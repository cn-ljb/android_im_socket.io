package com.ljb.socket.android.presenter

import android.text.TextUtils
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.contract.SplashContract
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.presenter.base.BaseRxLifePresenter
import com.ljb.socket.android.utils.FileUploadManager
import com.ljb.socket.android.utils.JsonParser
import com.ljb.socket.android.utils.SPUtils
import com.ljb.socket.android.utils.UUIDUtils
import io.reactivex.Observable
import mvp.ljb.kt.presenter.getContextEx
import java.util.concurrent.TimeUnit

/**
 * Author:Ljb
 * Time:2018/12/5
 * There is a lot of misery in life
 **/
class SplashPresenter : BaseRxLifePresenter<SplashContract.IView>(), SplashContract.IPresenter {


    private val mFileUploadManager by lazy { FileUploadManager(getContextEx()) }

    override fun login(userName: String, headUrl: String) {
        //模拟登陆，返回一个user model
        val userBean = UserBean(UUIDUtils.getUUID8(), userName, headUrl)
        val userJson = JsonParser.toJson(userBean)
        SPUtils.putString(Constant.SPKey.KEY_USER, userJson)
        getMvpView().goHome()
    }

    override fun uploadImg(picturePath: String) {
        getMvpView().showLoadDialog()
        mFileUploadManager.uploadImgFile(listOf(picturePath), object : FileUploadManager.FileUploadCallBack {

            override fun onSuccess(urlList: List<String>) {
                getMvpView().dismissLoadDialog()
                if (urlList.isNotEmpty()) {
                    getMvpView().uploadImgSuccess(urlList[0])
                } else {
                    getMvpView().uploadImgError()
                }
            }

            override fun onError(e: Throwable) {
                getMvpView().dismissLoadDialog()
                getMvpView().uploadImgError()
            }

        })
    }

    override fun checkLoginStatus() {
        val userJson = SPUtils.getString(Constant.SPKey.KEY_USER)
        if (TextUtils.isEmpty(userJson)) {
            getMvpView().showLoginView()
        } else {
            Observable.timer(1500, TimeUnit.MILLISECONDS)
                    .subscribe { getMvpView().goHome() }
                    .bindRxLifeEx(RxLife.ON_DESTROY)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mFileUploadManager.release()
    }

}
