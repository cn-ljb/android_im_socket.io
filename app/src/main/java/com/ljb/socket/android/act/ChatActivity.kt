package com.ljb.socket.android.act

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.Toast
import com.andview.refreshview.XRefreshView
import com.codebear.keyboard.CBEmoticonsKeyBoard
import com.codebear.keyboard.data.AppFuncBean
import com.codebear.keyboard.data.EmoticonsBean
import com.codebear.keyboard.utils.EmoticonsKeyboardUtils
import com.codebear.keyboard.widget.CBAppFuncView
import com.codebear.keyboard.widget.CBEmoticonsView
import com.codebear.keyboard.widget.FuncLayout
import com.codebear.keyboard.widget.RecordIndicator
import com.ljb.socket.android.R
import com.ljb.socket.android.adapter.ChatAdapter
import com.ljb.socket.android.chat.OnChatActionListener
import com.ljb.socket.android.chat.SimpleAudioRecordListener
import com.ljb.socket.android.common.act.BaseMvpFragmentActivity
import com.ljb.socket.android.contract.ChatContract
import com.ljb.socket.android.event.ChatNewEvent
import com.ljb.socket.android.model.ChatMessage
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.ChatPresenter
import com.ljb.socket.android.socket.SocketManager
import com.ljb.socket.android.utils.*
import com.ljb.socket.android.widgets.ChatRefreshViewHeader
import com.lqr.audio.AudioPlayManager
import com.lqr.audio.AudioRecordManager
import com.lqr.audio.IAudioPlayListener
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.layout_title.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

/**
 * Author:Ljb
 * Time:2018/12/11
 * There is a lot of misery in life
 **/
class ChatActivity : BaseMvpFragmentActivity<ChatContract.IPresenter>(), ChatContract.IView, OnChatActionListener, RecordIndicator.OnRecordListener, CBEmoticonsView.OnEmoticonClickListener {

    companion object {
        const val MAX_VOICE_TIME = 60
        const val KEY_TO_USER = "key_to_user"
        const val KEY_LOC_USER = "key_from_USER"

        const val CODE_REQ_TAKE_PIC = 0x111
        const val CODE_REQ_PIC_LIB = 0x222

        const val CODE_PERMISSION_IM = 0x1000
        const val CODE_PERMISSION_TAKE_PIC = 0x1001
        const val CODE_PERMISSION_AUDIO = 0x1002

        const val OPTION_ID_TAKE_PIC = 1
        const val OPTION_ID_PIC_LIB = 2

        fun startActivity(context: Context, locUser: UserBean, toUser: UserBean) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(KEY_TO_USER, toUser)
            intent.putExtra(KEY_LOC_USER, locUser)
            context.startActivity(intent)
        }
    }

    protected lateinit var mLocUser: UserBean
    protected lateinit var mToUser: UserBean

    protected lateinit var mConversation: String

    protected lateinit var mEventBus: EventBus

    private var mRvView: RecyclerView? = null
    private var mKbView: CBEmoticonsKeyBoard? = null

    protected lateinit var mChatAdapter: ChatAdapter
    private var mIndex = 0

    private var mTakePicPath: String = ""

    private var mRecordIndicator: RecordIndicator? = null

    private var mStartRecorderMp3Time: Long = 0L
    private var mLvAudio: Int = 0

    private lateinit var mAudioRecordManager: AudioRecordManager

    override fun getLayoutId() = R.layout.activity_chat

    override fun registerPresenter() = ChatPresenter::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEventBus = EventBus.getDefault()
        mEventBus.register(this)
    }

    override fun onDestroy() {
        mEventBus.unregister(this)
        mRecordIndicator?.onDestory()
        SocketManager.clearCallBack()
        mAudioRecordManager.releaseAll()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (mIndex != 0) {
            scrollToPosition(mIndex)
        }
        getPresenter().notifyNewNum(mConversation)
    }

    override fun init(savedInstanceState: Bundle?) {
        val toUser = intent.getParcelableExtra<UserBean>(KEY_TO_USER)
        val locUser = intent.getParcelableExtra<UserBean>(KEY_LOC_USER)
        if (toUser == null || locUser == null || TextUtils.isEmpty(toUser.uid) || TextUtils.isEmpty(locUser.uid)) {
            finish()
            return
        }

        mLocUser = locUser
        mToUser = toUser

        mConversation = ChatUtils.createConversation(getTopic(), mLocUser.uid, mToUser.uid)

        mAudioRecordManager = AudioRecordManager(this)

        getPresenter().setTopic(getTopic())

        requestPermissions()
    }


    override fun initView() {
        tv_title.text = if (TextUtils.isEmpty(mToUser.name)) "正在聊天..." else mToUser.name
        iv_back.setOnClickListener { back() }
        initRecyclerView(rv_chat)
        initRefreshView(refresh_view)
        initKeyBoard(kb_bar)
        initOptions()
    }


    override fun initData() {
        getPresenter().initChatData(mConversation)
    }

    override fun setChatHistory(isLoadMore: Boolean, data: List<ChatMessage>) {
        mChatAdapter.data.addAll(0, data)
        mChatAdapter.notifyDataSetChanged()
        if (isLoadMore) {
            refresh_view.stopRefresh()
            rv_chat.scrollToPosition(0)
            if (data.isEmpty()) {
                Toast.makeText(this, R.string.not_has_chat_history, Toast.LENGTH_SHORT).show()
            }
        } else {
            scrollToBottom()
        }
    }


    private fun initOptions() {
        val appFuncBeanList = ArrayList<AppFuncBean>()
        appFuncBeanList.add(AppFuncBean(OPTION_ID_PIC_LIB, R.drawable.icon_photo, getString(R.string.pic_lib)))
        appFuncBeanList.add(AppFuncBean(OPTION_ID_TAKE_PIC, R.drawable.icon_camera, getString(R.string.take_pic)))
        val cbAppFuncView = CBAppFuncView(this)
        cbAppFuncView.setAppFuncBeanList(appFuncBeanList)
        kb_bar.setAppFuncView(cbAppFuncView)
        cbAppFuncView.setOnAppFuncClickListener { emoticon ->
            when (emoticon.id) {
                OPTION_ID_PIC_LIB -> optionPicLib()
                OPTION_ID_TAKE_PIC -> optionTakePic()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    protected fun initKeyBoard(kb_bar: CBEmoticonsKeyBoard) {
        mKbView = kb_bar
        kb_bar.addOnFuncKeyBoardListener(object : FuncLayout.OnFuncKeyBoardListener {
            override fun onFuncPop(height: Int) {
                scrollToBottom()
            }

            override fun onFuncClose() {
            }
        })
        kb_bar.btnSend.setOnClickListener {
            val text = kb_bar.etChat.text.toString()
            kb_bar.etChat.setText("")
            onSendTextClick(text)
        }
        kb_bar.etChat.setOnSizeChangedListener { _, _, _, _ -> scrollToBottom() }
        mRvView?.setOnTouchListener { _, _ ->
            kb_bar.reset()
            false
        }

        mRecordIndicator = RecordIndicator(this)
        kb_bar.setRecordIndicator(mRecordIndicator)
        mRecordIndicator!!.setOnRecordListener(this)
        mRecordIndicator!!.setMaxRecordTime(MAX_VOICE_TIME)

        val cbEmoticonsView = CBEmoticonsView(this)
        cbEmoticonsView.init(supportFragmentManager)
        kb_bar.setEmoticonFuncView(cbEmoticonsView)
        cbEmoticonsView.addEmoticonsWithName(arrayOf("default"))
        cbEmoticonsView.setOnEmoticonClickListener(this)

        //初始化录音工具
        mAudioRecordManager.maxVoiceDuration = mRecordIndicator!!.maxRecorderTime
        mAudioRecordManager.setAudioSavePath(FileUtils.getVoiceDir(this))
        mAudioRecordManager.audioRecordListener = object : SimpleAudioRecordListener() {

            override fun onFinish(audioPath: Uri, duration: Int) {
                Log.i("voice", "$duration :: " + audioPath.path)
                if (duration > 1) {
                    sendMp3Msg(audioPath.path, duration.toLong())
                }
            }

            override fun onAudioDBChanged(db: Int) {
                mLvAudio = db / 5
            }
        }
    }


    protected fun initRecyclerView(rvView: RecyclerView) {
        mRvView = rvView
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.isAutoMeasureEnabled = true
        rvView.layoutManager = linearLayoutManager
        rvView.isNestedScrollingEnabled = false
        val itemAnimator = rvView.itemAnimator
        if (itemAnimator is SimpleItemAnimator) {
            itemAnimator.supportsChangeAnimations = false
        }
        mChatAdapter = ChatAdapter(this, mLocUser, mToUser, mutableListOf())
        mChatAdapter.setAppActionListener(this)
        rvView.adapter = mChatAdapter
    }

    protected open fun initRefreshView(refresh_view: XRefreshView) {
        refresh_view.setCustomHeaderView(ChatRefreshViewHeader(this))
        refresh_view.setPinnedTime(1000)
        refresh_view.pullLoadEnable = false
        refresh_view.enableReleaseToLoadMore(false)
        refresh_view.setMoveForHorizontal(true)
        refresh_view.setAutoLoadMore(false)
        refresh_view.enableRecyclerViewPullUp(false)
        refresh_view.enablePullUpWhenLoadCompleted(false)
        refresh_view.setXRefreshViewListener(object : XRefreshView.SimpleXRefreshListener() {

            override fun onRefresh(isPullDown: Boolean) {
                getPresenter().getChatHistory()
            }
        })
    }


    protected fun optionTakePic() {
        PermissionUtils.requestPermission(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                , CODE_PERMISSION_TAKE_PIC) { _, result ->
            if (result.isNotEmpty() && result[0] == PackageManager.PERMISSION_GRANTED) {
                mTakePicPath = ImageUtils.getImageFile(this@ChatActivity).absolutePath
                SystemUtils.openTakePic(this@ChatActivity, CODE_REQ_TAKE_PIC, mTakePicPath)
            }
        }
    }


    protected fun optionPicLib() {
        SystemUtils.openPicLibForResult(this, CODE_REQ_PIC_LIB)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK && data == null) return
        when (requestCode) {
            CODE_REQ_TAKE_PIC -> takePicResult(mTakePicPath)
            CODE_REQ_PIC_LIB -> picLibResult(data!!)
        }
    }

    protected fun picLibResult(data: Intent) {
        val path = SystemUtils.getPicLibResult(this, data)
        sendImgMsg(path)
    }

    protected fun takePicResult(path: String) {
        if (TextUtils.isEmpty(path)) return
        val file = File(path)
        val newFile = File(FileUtils.getSmallPicDir(this), file.name)
        ImageUtils.compressImage(file, newFile, 400, 800, 300)
        sendImgMsg(newFile.absolutePath)
    }


    override fun recordStart() {
        PermissionUtils.requestPermission(this, arrayOf(Manifest.permission.RECORD_AUDIO),
                CODE_PERMISSION_AUDIO) { _, result ->
            if (result.isNotEmpty() && result[0] == PackageManager.PERMISSION_GRANTED) {
                mStartRecorderMp3Time = System.currentTimeMillis()
                mAudioRecordManager.startRecord()
            }
        }
    }

    override fun recordFinish() {
        mAudioRecordManager.stopRecord()
        mAudioRecordManager.destroyRecord()
    }

    override fun recordCancel() {
        mAudioRecordManager.stopRecord()
        mAudioRecordManager.destroyRecord()
        mAudioRecordManager.deleteAudioFile()
    }

    override fun getRecordTime(): Long {
        return System.currentTimeMillis() - mStartRecorderMp3Time
    }

    override fun getRecordDecibel(): Int {
        return mLvAudio
    }


    override fun addChatMessage2UI(chatMessage: ChatMessage) {
        mChatAdapter.data.add(chatMessage)
        scrollToBottom()
    }

    override fun notifyChatMessageStatus(chatMessage: ChatMessage, status: Int) {
        val position = mChatAdapter.data.indexOf(chatMessage)
        if (position != -1) {
            mChatAdapter.data[position].status = status
            mChatAdapter.notifyItemChanged(position)
        }
    }


    protected fun scrollToBottom() {
        if (mChatAdapter.itemCount == 0) return
        mRvView?.scrollToPosition(mChatAdapter.itemCount - 1)
    }

    protected fun scrollToPosition(position: Int) {
        if (mChatAdapter.itemCount == 0) return
        mRvView?.smoothScrollToPosition(position)
    }


    override fun onBackPressed() {
        back()
    }

    private fun back() {
        finish()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (EmoticonsKeyboardUtils.isFullScreen(this) && mKbView != null) {
            val isConsum = mKbView!!.dispatchKeyEventInFullScreen(event)
            return if (isConsum) isConsum else super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }

    private fun requestPermissions() {
        val arr = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WAKE_LOCK)

        PermissionUtils.requestPermission(this, arr, CODE_PERMISSION_IM) { permissions, result ->
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun sendImgMsg(path: String) {
        if (TextUtils.isEmpty(path)) return
        getPresenter().sendImgMsg(path, mLocUser.uid, mToUser.uid)
    }

    private fun sendMp3Msg(path: String, time: Long) {
        if (TextUtils.isEmpty(path)) return
        getPresenter().sendMp3Msg(path, time, mLocUser.uid, mToUser.uid)
    }

    override fun onEmoticonClick(emoticon: EmoticonsBean, isDel: Boolean) {
        if (mKbView == null) return
        if (isDel) {
            mKbView!!.delClick()
        } else {
            if ("default" == emoticon.parentTag) {
                val content = emoticon.name
                if (TextUtils.isEmpty(content)) {
                    return
                }
                val index = mKbView!!.etChat.selectionStart
                val editable = mKbView!!.etChat.text
                editable.insert(index, content)
            }
        }
    }

    private fun onSendTextClick(text: String) {
        if (TextUtils.isEmpty(text)) return
        getPresenter().sendTextMsg(text, mLocUser.uid, mToUser.uid)
    }

    override fun onChatVoiceClick(position: Int, voiceUrl: String, animView: ImageView) {
        mIndex = position
        val chatMessage = mChatAdapter.data[position]
        if (voiceUrl.startsWith("http") || voiceUrl.startsWith("https")) {
            //远程文件
            val filePath = FileUtils.getVoiceDir(this) + File.separator + chatMessage.pid + ".mp3"
            if (File(filePath).exists()) {
                //文件已存在
                playVoice(position, filePath, animView)
            } else {
                getPresenter().downFile(voiceUrl, filePath, position, animView)
            }
        } else {
            //本地文件
            playVoice(position, voiceUrl, animView)
        }
    }

    override fun onChatHeadImgClick(position: Int, imageView: ImageView) {
        mIndex = position
        val fromId = mChatAdapter.data[position].fromId
        if (fromId == mLocUser.uid) {
            openPhotoListPage(0, arrayListOf(mLocUser.headUrl))
        } else {
            openPhotoListPage(0, arrayListOf(mToUser.headUrl))
        }
    }

    override fun onChatImageClick(position: Int, imageView: ImageView) {
        mIndex = position
        getPresenter().gatAllChatPic(mChatAdapter.data[position].pid)
    }


    override fun playVoice(position: Int, path: String, animView: ImageView) {
        val voiceUri = Uri.fromFile(File(path))
        val anim = animView.drawable as AnimationDrawable
        AudioPlayManager.getInstance().stopPlay()
        AudioPlayManager.getInstance().startPlay(this, voiceUri, object : IAudioPlayListener {
            override fun onStart(var1: Uri?) {
                anim.start()
            }

            override fun onStop(var1: Uri?) {
                anim.selectDrawable(0)
                anim.stop()
                mChatAdapter.data[position].read = ChatMessage.MSG_VOICE_HAS_READ
                getPresenter().setVoiceIsRead(mChatAdapter.data[position])
            }

            override fun onComplete(var1: Uri?) {
                anim.selectDrawable(0)
                anim.stop()
                mChatAdapter.data[position].read = ChatMessage.MSG_VOICE_HAS_READ
                getPresenter().setVoiceIsRead(mChatAdapter.data[position])
            }
        })
    }

    override fun openPhotoListPage(index: Int, picList: ArrayList<String>) {
        PhotoListActivity.statrtActivity(this, picList, index)
    }


    private fun getTopic(): String {
        return "chat"
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onNewMsgEvent(chatNewEvent: ChatNewEvent) {
        responseChatMessage(chatNewEvent.chatMessage)
    }

    /**
     * 响应接收到的数据
     * */
    open fun responseChatMessage(chatMessage: ChatMessage) {
        if (chatMessage.conversation == mConversation) {
            val bodyType = chatMessage.bodyType
            mChatAdapter.data.add(chatMessage)
            scrollToBottom()
            getPresenter().notifyNewNum(mConversation)
        }
    }


}
