package com.ljb.socket.android.widgets.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import com.ljb.socket.android.R

class LoadingDialog(context: Context, val isBack: Boolean = true) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(View.inflate(getContext(), R.layout.dialog_loading, null))
        setCancelable(isBack)
        setCanceledOnTouchOutside(isBack)
    }

}