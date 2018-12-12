package com.codebear.keyboard.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.codebear.keyboard.R;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/7/4.
 */

public class RecordIndicator {

    private Vibrator vibrator;
    private TextView tv_hint_text;

    public enum RecordView {
        START_VIEW, CANCEL_VIEW, SHORT_VIEW, HINT_VIEW
    }

    private Context mContext;
    private static int[] amps = {R.mipmap.amp1, R.mipmap.amp2, R.mipmap.amp3, R.mipmap.amp4, R.mipmap.amp5, R.mipmap.amp6, R.mipmap.amp7};

    private Dialog recordDialog;
    private ViewFlipper viewFlipper;
    private ImageView volumeAnim;

    private boolean cancel_record = false;
    private boolean start_record = false;

    /**
     * 最短录音时长为1s
     */
    private int minRecordTime = 1000;
    //取消录音的状态值
    private static final int MSG_VOICE_STOP = 4;
    /**
     * 录音限制时间
     */
    private int mMaxRecordTimes = 59;

    public int getMaxRecorderTime() {
        return mMaxRecordTimes;
    }

    public void setMaxRecordTime(int time) {
        mMaxRecordTimes = time;
    }

    //是否正在 正在录音标记
    private boolean isRecording = false;

    //当前录音时长
    private float mTime = 0;

    //是否超时自动发送
    private boolean isOverTimeSend = false;

    //提醒倒计时
    private int mRemainedTime = 10;

    private Button recordButton;

    private AnimationDrawable anim = null;

    private OnRecordListener onRecordListener;

    public RecordIndicator(Context mContext) {
        this.mContext = mContext;
        initDialog();
    }

    private void initDialog() {
        View view = View.inflate(mContext, R.layout.dialog_record_indicator, null);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.vf_record);
        volumeAnim = (ImageView) view.findViewById(R.id.iv_record_amp);
        viewFlipper.setDisplayedChild(0);
        tv_hint_text = view.findViewById(R.id.tv_hint_text);

        recordDialog = new Dialog(mContext, R.style.preview_dialog_style);
        recordDialog.setContentView(view);
    }

    private void show() {
        if (recordDialog != null && !recordDialog.isShowing()) {
            recordDialog.show();

        }
    }

    private void dismiss() {
        if (recordDialog != null && recordDialog.isShowing()) {
            recordDialog.dismiss();
        }
    }

    private void showView(RecordView recordView) {
        switch (recordView) {
            case START_VIEW:
                viewFlipper.setDisplayedChild(0);
                break;
            case CANCEL_VIEW:
                viewFlipper.setDisplayedChild(1);
                break;
            case SHORT_VIEW:
                viewFlipper.setDisplayedChild(2);
                break;

        }
    }

    public void setRecordDecibel(int decibelRank) {
        if (decibelRank == 0) {
            volumeAnim.setBackgroundResource(amps[0]);
        } else if (decibelRank == 1) {
            volumeAnim.setBackgroundResource(amps[1]);
        } else if (decibelRank == 2) {
            volumeAnim.setBackgroundResource(amps[2]);
        } else if (decibelRank == 3) {
            volumeAnim.setBackgroundResource(amps[3]);
        } else if (decibelRank == 4) {
            volumeAnim.setBackgroundResource(amps[4]);
        } else if (decibelRank == 5) {
            volumeAnim.setBackgroundResource(amps[5]);
        } else {
            volumeAnim.setBackgroundResource(amps[6]);
        }
    }

    public void setMinRecordTime(int minRecordTime) {
        this.minRecordTime = minRecordTime;
    }

    public void setRecordButton(Button recordButton) {
        this.recordButton = recordButton;
        listenerVoiceBtn();
    }

    // 三个状态
    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGE = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;


    public void onDestory() {
        mStateHandler.removeMessages(MSG_AUDIO_PREPARED);
        mStateHandler.removeMessages(MSG_VOICE_CHANGE);
        mStateHandler.removeMessages(MSG_DIALOG_DIMISS);
        mStateHandler.removeMessages(MSG_VOICE_STOP);
        mStateHandler = null;
        mContext = null;
    }

    @SuppressLint("HandlerLeak")
    private Handler mStateHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:  //启动计时
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGE:  //开始倒计时
                    //剩余10s
                    showRemainedTime();
                    break;
                case MSG_DIALOG_DIMISS:

                    break;
                case MSG_VOICE_STOP:   //超时自动发送
                    isOverTimeSend = true;
                    if (isOverTimeSend) {
                        onRecordListener.getRecordTime();
                        finishRecord();
                    }
                    break;

            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    private void listenerVoiceBtn() {
        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onRecordListener != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.i("ljb", "onTouch Down");
                            startRecord(true);
                            mStateHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            Log.i("ljb", "onTouch Move start");
                            if (start_record) {
                                Log.i("ljb", "start_record");
                                float x = event.getX();
                                float y = event.getY();
                                boolean cancelY;
                                boolean cancelX;
                                if (y < 0) {
                                    cancelY = (-y > recordButton.getHeight() * 4);
                                } else {
                                    cancelY = (y > recordButton.getHeight() * 1.5);
                                }
                                cancelX = (x < 0 || x > recordButton.getWidth());

                                cancel_record = cancelX || cancelY;

                                if (cancel_record) {
                                    recordButton.setText("松开手指，结束录音");
                                    cancelRecord(false);
                                } else {
                                    recordButton.setText("松开结束");
                                    startRecord(false);
                                }
                            }
                            Log.i("ljb", "onTouch Move end");
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            Log.i("ljb", "onTouch Up");
                            if (!isOverTimeSend) {   //抬起的时候，判断是否超时，，超时handler调用结束的方法，，没有继续向下执行
                                start_record = false;
                                recordButton.setText("按住录音");
                                long intervalTime = onRecordListener.getRecordTime();
                                if (cancel_record) {
                                    cancelRecord(true);
                                } else if (intervalTime < minRecordTime) {
                                    recordTooShort();
                                } else {
                                    finishRecord();
                                }
                            }

                            if (isOverTimeSend) {
                                start_record = false;
                                recordButton.setText("按住录音");
                                long intervalTime = onRecordListener.getRecordTime();
                                if (cancel_record) {
                                    cancelRecord(true);
                                } else if (intervalTime < minRecordTime) {
                                    recordTooShort();
                                } else {
                                    recordButton.setText("按住录音");
                                }
                            }
                            reset();
                            break;
                    }
                }
                return false;
            }
        });
    }

    private Runnable mGetVoiceLevelRunnable = new Runnable() {

        @Override
        public void run() {
            while (isRecording) {
                try {
                    //最长mMaxRecordTimes
                    if (mTime > mMaxRecordTimes) {
                        mStateHandler.sendEmptyMessage(MSG_VOICE_STOP);  //超时
                        return;
                    }

                    Thread.sleep(100);
                    mTime += 0.1f;
                    mStateHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //是否触发过震动
    boolean isShcok;

    private void showRemainedTime() {
        //倒计时
        int remainTime = (int) (mMaxRecordTimes - mTime);
        if (remainTime < mRemainedTime) {
            if (!isShcok) {
                isShcok = true;
                doShock();
            }
            tv_hint_text.setText("还可以说" + remainTime + "秒");
        }

    }

    /*
     * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
     * */
    @SuppressLint("MissingPermission")
    private void doShock() {
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 300};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }

    /**
     * 回复标志位以及状态
     */
    private void reset() {
        isRecording = false;  //是否正在录音
        mTime = 0;            //录制的时间
        isShcok = false;      //震动
        isOverTimeSend = false;  //是否超时
        tv_hint_text.setText("手指上滑，取消录音");
    }

    private DecibelThread decibelThread;

    private void startRecord(boolean realStart) {
        showView(RecordIndicator.RecordView.START_VIEW);
        if (realStart) {
            cancel_record = false;
            start_record = true;
            recordButton.setBackgroundResource(R.drawable.btn_voice_press);
            recordButton.setText("松开结束");
            onRecordListener.recordStart();
            show();
            decibelThread = new DecibelThread();
            decibelThread.start();

        }
    }


    private void cancelRecord(boolean dismiss) {
        if (decibelThread != null) {
            stopRecordAnimation();
            decibelThread.exit();
            decibelThread = null;
        }
        showView(RecordIndicator.RecordView.CANCEL_VIEW);
        if (dismiss) {
            onRecordListener.recordCancel();
            dismissRecordIndicator(600);
        }
    }

    private void finishRecord() {
        if (decibelThread != null) {
            stopRecordAnimation();
            decibelThread.exit();
            decibelThread = null;
        }
        onRecordListener.recordFinish();
        dismissRecordIndicator(200);
    }

    private void recordTooShort() {
        showView(RecordIndicator.RecordView.SHORT_VIEW);
        onRecordListener.recordCancel();
        dismissRecordIndicator(600);
    }

    private void dismissRecordIndicator(long time) {
        recordButton.setBackgroundResource(R.drawable.btn_voice_normal);
        recordButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, time);
    }


    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }

    /**
     * 录音接口
     */
    public interface OnRecordListener {
        /**
         * 开始录音
         */
        void recordStart();

        /**
         * 结束录音
         */
        void recordFinish();

        /**
         * 取消录音
         */
        void recordCancel();

        /**
         * 获取录音时长
         *
         * @return
         */
        long getRecordTime();

        /**
         * 获取分贝等级
         *
         * @return
         */
        int getRecordDecibel();
    }

    private class DecibelThread extends Thread {
        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            super.run();
            while (running) {
                if (onRecordListener == null || !running) {
                    break;
                }
                viewFlipper.post(new Runnable() {
                    @Override
                    public void run() {
                        setRecordDecibel(onRecordListener.getRecordDecibel());
                    }
                });
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 开始播放动画
     */
    private void startRecordAnimation() {
        anim = (AnimationDrawable) volumeAnim.getDrawable();
        anim.start();
    }

    /**
     * 动画停止
     */
    private void stopRecordAnimation() {
        if (null != anim) {
            anim.selectDrawable(0);
            anim.stop();
        }

    }


}
