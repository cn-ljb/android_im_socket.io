package com.ljb.socket.android.utils

import io.reactivex.disposables.Disposable

/**
 * Created by L on 2017/7/14.
 */
object RxUtils {
    
    fun dispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }
}