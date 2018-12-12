package com.codebear.keyboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codebear.keyboard.emoji.DefaultEmojiFilter;
import com.codebear.keyboard.emoji.ZsFilter;
import com.codebear.keyboard.interfaces.IEmoticonsView;
import com.codebear.keyboard.utils.EmoticonsKeyboardUtils;
import com.codebear.keyboard.widget.AutoHeightLayout;
import com.codebear.keyboard.widget.EmoticonsEditText;
import com.codebear.keyboard.widget.FuncLayout;
import com.codebear.keyboard.widget.RecordIndicator;

/**
 * description:
 * <p>
 * 参照w446108264提供的XhsEmoticonsKeyboard开源键盘解决方案
 * github:https://github.com/w446108264/XhsEmoticonsKeyboard
 * <p>
 * Created by CodeBear on 2017/6/28.
 */

public class CBEmoticonsKeyBoard extends AutoHeightLayout implements View.OnClickListener, EmoticonsEditText
        .OnBackKeyClickListener, FuncLayout.OnFuncChangeListener {
    public static final int FUNC_TYPE_EMOTION = -1;
    public static final int FUNC_TYPE_APPS = -2;

    protected LayoutInflater mInflater;

    protected ImageView mBtnVoiceOrText;
    protected Button mBtnVoice;
    protected EmoticonsEditText mEtChat;
    protected ImageView mBtnFace;
    protected RelativeLayout mRlInput;
    protected ImageView mBtnMultimedia;
    protected Button mBtnSend;
    protected FuncLayout funFunction;

    protected boolean mDispatchKeyEventPreImeLock = false;

    private IEmoticonsView iEmoticonView;

    /**
     * 用于记录软键盘关闭是手动关闭还是点击功能按钮
     */
    private int clickFunc = 1;

    private RecordIndicator recordIndicator;
    private boolean initRecordIndicator = false;
    private View mRlBottomKeyboard;
    private View mVLineBottom;

    public CBEmoticonsKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateKeyboardBar();
        initView();
        initFuncView();
    }

    protected void inflateKeyboardBar() {
        mInflater.inflate(R.layout.cb_view_keyboard, this);
    }

    protected View inflateFunc() {
        return mInflater.inflate(R.layout.cb_view_app_func_default, null);
    }

    public void setKeyBoardVisibility(int vis) {
        mRlBottomKeyboard.setVisibility(vis);
        mVLineBottom.setVisibility(vis);
        funFunction.setVisibility(vis);
    }

    protected void initView() {
        mRlBottomKeyboard = findViewById(R.id.rl_bottom_keyboard);
        mVLineBottom = findViewById(R.id.view_line_bottom_keyboard);
        mBtnVoiceOrText = (ImageView) findViewById(R.id.iv_voice_or_text);
        mBtnVoice = (Button) findViewById(R.id.btn_voice);
        mEtChat = (EmoticonsEditText) findViewById(R.id.et_chat);
        mBtnFace = (ImageView) findViewById(R.id.iv_face);
        mRlInput = (RelativeLayout) findViewById(R.id.rl_input);
        mBtnMultimedia = (ImageView) findViewById(R.id.iv_multimedia);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        funFunction = (FuncLayout) findViewById(R.id.fun_function);

        mBtnVoiceOrText.setOnClickListener(this);
        mBtnFace.setOnClickListener(this);
        mBtnMultimedia.setOnClickListener(this);
        mEtChat.setOnBackKeyClickListener(this);
        funFunction.setOnFuncChangeListener(this);

        if (recordIndicator != null && !initRecordIndicator) {
            initRecordIndicator = true;
            recordIndicator.setRecordButton(mBtnVoice);
        }
    }

    protected void initFuncView() {
        initEmoticonFuncView();
        initAppFuncView();
        initEditView();
    }

    protected void initEmoticonFuncView() {
        View keyboardView = inflateFunc();
        funFunction.addFuncView(FUNC_TYPE_EMOTION, keyboardView);
    }

    protected void initAppFuncView() {
        View keyboardView = inflateFunc();
        funFunction.addFuncView(FUNC_TYPE_APPS, keyboardView);
    }

    protected void initEditView() {
        mEtChat.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mEtChat.isFocused()) {
                    mEtChat.setFocusable(true);
                    mEtChat.setFocusableInTouchMode(true);
                }
                return false;
            }
        });

        mEtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    mBtnSend.setVisibility(VISIBLE);
                    mBtnMultimedia.setVisibility(GONE);
                } else {
                    mBtnMultimedia.setVisibility(VISIBLE);
                    mBtnSend.setVisibility(GONE);
                }
            }
        });
        mEtChat.addEmoticonFilter(new ZsFilter());
    }

    public void setEmoticonFuncView(IEmoticonsView emoticonView) {
        if (null != emoticonView && null != emoticonView.getView()) {
            this.iEmoticonView = emoticonView;
            funFunction.addFuncView(FUNC_TYPE_EMOTION, emoticonView.getView());
        }
    }

    public void setAppFuncView(View appFuncView) {
        if (null != appFuncView) {
            funFunction.addFuncView(FUNC_TYPE_APPS, appFuncView);
        }
    }

    public void reset() {
        clickFunc = 1;
        EmoticonsKeyboardUtils.closeSoftKeyboard(this);
        funFunction.hideAllFuncView();
        mBtnFace.setImageResource(R.drawable.icon_btn_face_bg);
    }

    protected void showVoice() {
        mRlInput.setVisibility(GONE);
        mBtnVoice.setVisibility(VISIBLE);
        mBtnSend.setVisibility(GONE);
        mBtnMultimedia.setVisibility(VISIBLE);
        reset();
    }

    protected void checkVoice() {
        if (mBtnVoice.isShown()) {
            mBtnVoiceOrText.setImageResource(R.drawable.icon_input_normal);
        } else {
            mBtnVoiceOrText.setImageResource(R.drawable.icon_voice_normal);
        }
    }

    protected void showText() {
        mRlInput.setVisibility(VISIBLE);
        mBtnFace.setVisibility(VISIBLE);
        mBtnVoice.setVisibility(GONE);
        if (!TextUtils.isEmpty(mEtChat.getText().toString())) {
            mBtnSend.setVisibility(VISIBLE);
            mBtnMultimedia.setVisibility(GONE);
        }
    }

    protected void toggleFuncView(int key) {
        showText();
        funFunction.toggleFuncView(key, isSoftKeyboardPop(), mEtChat);
    }

    @Override
    public void onFuncChange(int key) {
        if (FUNC_TYPE_EMOTION == key) {
            mBtnFace.setImageResource(R.drawable.icon_input_normal);
            this.iEmoticonView.openView();
        } else {
            mBtnFace.setImageResource(R.drawable.icon_btn_face_bg);
        }
        checkVoice();
    }

    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        funFunction.updateHeight(height);
    }

    @Override
    public void onSoftPop(int height) {
        super.onSoftPop(height);
        funFunction.setVisibility(true);
        onFuncChange(funFunction.DEF_KEY);
        clickFunc = 0;
        if (mEtChat.hasFocus()) {
            mEtChat.setBackgroundResource(R.drawable.edit_shape);
        } else {
            mEtChat.setBackgroundResource(R.drawable.edit_shape);
        }
    }

    @Override
    public void onSoftClose() {
        super.onSoftClose();
        if (clickFunc == 0) {
            reset();
        } else {
            onFuncChange(funFunction.getCurrentFuncKey());
        }
        if (mEtChat.hasFocus()) {
            mEtChat.setBackgroundResource(R.drawable.edit_shape);
        } else {
            mEtChat.setBackgroundResource(R.drawable.edit_shape);
        }
    }

    @Override
    public void onNavBarPop(int height) {
        super.onNavBarPop(height);
        funFunction.updateNavHeight(height);
    }

    @Override
    public void onNavBarClose() {
        super.onNavBarClose();
        funFunction.updateNavHeight(0);
    }

    public void addOnFuncKeyBoardListener(FuncLayout.OnFuncKeyBoardListener l) {
        funFunction.addOnKeyBoardListener(l);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_voice_or_text) {
            if (mRlInput.isShown()) {
                mBtnVoiceOrText.setImageResource(R.drawable.icon_input_normal);
                showVoice();
            } else {
                showText();
                mBtnVoiceOrText.setImageResource(R.drawable.icon_voice_normal);
                EmoticonsKeyboardUtils.openSoftKeyboard(mEtChat);
            }
        } else if (i == R.id.iv_face) {
            clickFunc = FUNC_TYPE_EMOTION;
            toggleFuncView(FUNC_TYPE_EMOTION);
        } else if (i == R.id.iv_multimedia) {
            clickFunc = FUNC_TYPE_APPS;
            toggleFuncView(FUNC_TYPE_APPS);
        }
    }

    @Override
    public void onBackKeyClick() {
        if (funFunction.isShown()) {
            mDispatchKeyEventPreImeLock = true;
            reset();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("dispatchKeyEvent", "" + clickFunc);
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (clickFunc == 0) {
                    reset();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            return false;
        }
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext())) {
            return;
        }
        super.requestChildFocus(child, focused);
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext()) && funFunction.isShown()) {
                    reset();
                    return true;
                }
            default:
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    boolean isFocused;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        isFocused = mEtChat.getShowSoftInputOnFocus();
                    } else {
                        isFocused = mEtChat.isFocused();
                    }
                    if (isFocused) {
                        mEtChat.onKeyDown(event.getKeyCode(), event);
                    }
                }
                return false;
        }
    }

    public EmoticonsEditText getEtChat() {
        return mEtChat;
    }

    public Button getBtnSend() {
        return mBtnSend;
    }

    public void delClick() {
        int action = KeyEvent.ACTION_DOWN;
        int code = KeyEvent.KEYCODE_DEL;
        KeyEvent event = new KeyEvent(action, code);
        mEtChat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
    }

    public void setRecordIndicator(RecordIndicator recordIndicator) {
        this.recordIndicator = recordIndicator;
        if (mBtnVoice != null && !initRecordIndicator) {
            recordIndicator.setRecordButton(mBtnVoice);
        }
    }
}
