package com.ljb.socket.android.adapter

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.ljb.socket.android.R
import com.ljb.socket.android.adapter.base.BaseRVAdapter
import com.ljb.socket.android.adapter.holder.*
import com.ljb.socket.android.chat.OnChatActionListener
import com.ljb.socket.android.img.ImageLoader
import com.ljb.socket.android.model.*
import com.ljb.socket.android.utils.EmoticonFilterUtils
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.TimeUtils
import com.ljb.socket.android.utils.UIUtils
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Author:Ljb
 * Time:2018/12/11
 * There is a lot of misery in life
 **/
class ChatAdapter(context: Context, val fromUser: UserBean, val toUser: UserBean, data: MutableList<ChatMessage>) : BaseRVAdapter<ChatMessage>(context, data) {

    companion object {
        /**
         * 未知
         */
        private val TYPE_UNKNOWN = 0
        /**
         * 文本
         */
        private val TYPE_RECEIVER_TXT = 1
        private val TYPE_SEND_TXT = 2
        /**
         * 图片
         */
        private val TYPE_SEND_IMAGE = 3
        private val TYPE_RECEIVER_IMAGE = 4
        /**
         * 语音
         */
        private val TYPE_SEND_VOICE = 5
        private val TYPE_RECEIVER_VOICE = 6

    }


    private var mChatActionLis: OnChatActionListener? = null

    private var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        val chatMessage = data[position]
        val isLoc = fromUser.uid == chatMessage.fromId
        return when (chatMessage.bodyType) {
            ChatMessage.MSG_BODY_TYPE_TEXT -> if (isLoc) TYPE_SEND_TXT else TYPE_RECEIVER_TXT
            ChatMessage.MSG_BODY_TYPE_IMAGE -> if (isLoc) TYPE_SEND_IMAGE else TYPE_RECEIVER_IMAGE
            ChatMessage.MSG_BODY_TYPE_VOICE -> if (isLoc) TYPE_SEND_VOICE else TYPE_RECEIVER_VOICE
            else -> TYPE_UNKNOWN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SEND_TXT -> getSendTxtHolder(parent)
            TYPE_RECEIVER_TXT -> getReceiverTxtHolder(parent)
            TYPE_SEND_IMAGE -> getSendImgHolder(parent)
            TYPE_RECEIVER_IMAGE -> getReceiverImgHolder(parent)
            TYPE_SEND_VOICE -> getSendVoiceHolder(parent)
            TYPE_RECEIVER_VOICE -> getReceiverVoiceHolder(parent)
            else -> getUnknownHolder(parent)
        }
    }

    private fun getReceiverVoiceHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_chat_received_voice, parent, false)
        return ReceiverVoiceHolder(view)
    }

    private fun getSendVoiceHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_chat_send_voice, parent, false)
        return SendVoiceHolder(view)
    }

    private fun getReceiverImgHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_chat_received_image, parent, false)
        return ReceiverImgHolder(view)
    }

    private fun getSendImgHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_chat_send_image, parent, false)
        return SendImgHolder(view)
    }

    private fun getReceiverTxtHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_chat_received_message, parent, false)
        return ReceiverTxtHolder(view)
    }

    private fun getSendTxtHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_chat_send_message, parent, false)
        return SendTxtHolder(view)
    }

    private fun getUnknownHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = mLayoutInflater.inflate(R.layout.item_unknown, parent, false)
        return UnknownHolder(view)
    }


    override fun onBindViewData(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (holder) {
            is SendTxtHolder -> handleSendTxt(holder, item)
            is ReceiverTxtHolder -> handleReceiverTxt(holder, item)
            is SendImgHolder -> handleSendImg(holder, item)
            is ReceiverImgHolder -> handleReceiverImg(holder, item)
            is SendVoiceHolder -> handleSendVoice(holder, item)
            is ReceiverVoiceHolder -> handleReceiverVoice(holder, item)
            is UnknownHolder -> handleUnknown(holder, item)
        }
    }

    private fun handleReceiverVoice(holder: ReceiverVoiceHolder, item: ChatMessage) {
        setTimeView(holder.adapterPosition, holder.tvTime, item.time)
        setHeadView(holder.adapterPosition, holder.ivAvatar, item.fromId)
        setVoiceContent(holder.adapterPosition, holder.layoutVoice, holder.ivVoice, holder.tvVoiceTime, holder.ivRead, item)
    }

    private fun handleSendVoice(holder: SendVoiceHolder, item: ChatMessage) {
        setTimeView(holder.adapterPosition, holder.tvTime, item.time)
        setHeadView(holder.adapterPosition, holder.ivAvatar, item.fromId)
        setStatusView(holder.loadView, holder.ivFail, item.status)
        setVoiceContent(holder.adapterPosition, holder.layoutVoice, holder.ivVoice, holder.tvVoiceTime, null, item)
    }

    private fun setVoiceContent(position: Int, layoutVoice: View, ivVoice: ImageView, tvVoiceTime: TextView, ivRead: View?, item: ChatMessage) {
        if (ivRead != null && toUser.uid == item.fromId) {
            if (item.read == ChatMessage.MSG_VOICE_HAS_READ) {
                ivRead.visibility = View.GONE
            } else {
                ivRead.visibility = View.VISIBLE
            }
        }

        val bodyVoice = JsonParser.fromJsonObj(item.body, BodyVoice::class.java)
        tvVoiceTime.text = bodyVoice.voiceTime
        layoutVoice.setOnClickListener {
            mChatActionLis?.onChatVoiceClick(position, bodyVoice.voiceUrl, ivVoice)
            if (ivRead != null && toUser.uid == item.fromId) {
                ivRead.visibility = View.GONE
            }
        }
    }

    private fun handleReceiverImg(holder: ReceiverImgHolder, item: ChatMessage) {
        setTimeView(holder.adapterPosition, holder.tvTime, item.time)
        setHeadView(holder.adapterPosition, holder.ivAvatar, item.fromId)
        setImgContent(holder.adapterPosition, holder.ivPicture, item.body)
    }

    private fun handleSendImg(holder: SendImgHolder, item: ChatMessage) {
        setTimeView(holder.adapterPosition, holder.tvTime, item.time)
        setHeadView(holder.adapterPosition, holder.ivAvatar, item.fromId)
        setStatusView(holder.loadView, holder.ivFail, item.status)
        setImgContent(holder.adapterPosition, holder.ivPicture, item.body)
    }


    private fun handleReceiverTxt(holder: ReceiverTxtHolder, item: ChatMessage) {
        setTimeView(holder.adapterPosition, holder.tvTime, item.time)
        setHeadView(holder.adapterPosition, holder.ivAvatar, item.fromId)
        setTxtContent(holder.tvMessage, item.body)
    }

    private fun handleSendTxt(holder: SendTxtHolder, item: ChatMessage) {
        setTimeView(holder.adapterPosition, holder.tvTime, item.time)
        setHeadView(holder.adapterPosition, holder.ivAvatar, item.fromId)
        setStatusView(holder.loadView, holder.ivFail, item.status)
        setTxtContent(holder.tvMessage, item.body)
    }

    private fun setImgContent(position: Int, ivPicture: ImageView, body: String) {
        val bodyImg = JsonParser.fromJsonObj(body, BodyImg::class.java)
        ivPicture.setOnClickListener {
            mChatActionLis?.onChatImageClick(position, ivPicture)
        }
        Glide.with(context).asBitmap().load(bodyImg.url).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {

                val screenWidth = UIUtils.getScreenWidth(context)
                val width = screenWidth / 3
                val layoutParams = ivPicture.getLayoutParams()
                layoutParams.width = width
                val r = 1.0f * bitmap.height / bitmap.width
                layoutParams.height = (width * r).toInt()
                ivPicture.layoutParams = layoutParams

                val roundRequest = ImageLoader.getRoundRequest(UIUtils.dip2px(context, 5f), RoundedCornersTransformation.CornerType.ALL)
                ImageLoader.load(context, bitmap, ivPicture, roundRequest)
            }
        })
    }


    private fun setTxtContent(tvMessage: TextView, txtBody: String) {
        val bodyTxt = JsonParser.fromJsonObj(txtBody, BodyTxt::class.java)
        EmoticonFilterUtils.spannableEmoticonFilter(tvMessage, bodyTxt.text)
    }

    private fun handleUnknown(holder: UnknownHolder, item: ChatMessage) {
        setTimeView(holder.adapterPosition, holder.tvTime, item.time)
    }

    private fun setStatusView(loadView: View, failView: View, status: Int) {
        when (status) {
            ChatMessage.MSG_STATUS_SEND_SUCCESS -> {
                loadView.visibility = View.GONE
                failView.visibility = View.GONE
            }
            ChatMessage.MSG_STATUS_SEND_ERROR -> {
                failView.visibility = View.VISIBLE
                loadView.visibility = View.GONE
            }
            ChatMessage.MSG_STATUS_SEND_ING -> {
                loadView.visibility = View.VISIBLE
                failView.visibility = View.GONE
            }
        }
    }

    private fun setHeadView(position: Int, ivAvatar: ImageView, fromId: String) {
        ivAvatar.setOnClickListener {
            mChatActionLis?.onChatHeadImgClick(position, ivAvatar)
        }
        if (fromUser.uid == fromId) {
            loadHeadImg(fromUser.headUrl, ivAvatar)
        } else {
            loadHeadImg(toUser.headUrl, ivAvatar)
        }
    }

    private fun loadHeadImg(headImg: String, ivHeadImg: ImageView) {
        if (TextUtils.isEmpty(headImg)) {
            ivHeadImg.setImageResource(R.drawable.icon_normal_img)
        } else {
            ImageLoader.load(context, headImg, ivHeadImg, ImageLoader.getCircleRequest())
        }
    }

    private fun setTimeView(position: Int, tvTime: TextView, time: Long) {
        if (position == 0 || time - data[position - 1].time > 300000) {
            tvTime.visibility = View.VISIBLE
            tvTime.text = TimeUtils.formatChatMessageTime(time)
        } else {
            tvTime.visibility = View.GONE
        }
    }


    fun setAppActionListener(listener: OnChatActionListener) {
        mChatActionLis = listener
    }
}
