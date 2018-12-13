package com.ljb.socket.android.act

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.ljb.socket.android.R
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.common.act.BaseMvpActivity
import com.ljb.socket.android.contract.LoginContract
import com.ljb.socket.android.img.ImageLoader
import com.ljb.socket.android.presenter.LoginPresenter
import com.ljb.socket.android.utils.PermissionUtils
import com.ljb.socket.android.utils.SystemUtils
import com.ljb.socket.android.widgets.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Author:Ljb
 * Time:2018/12/5
 * There is a lot of misery in life
 **/
class LoginActivity : BaseMvpActivity<LoginContract.IPresenter>(), LoginContract.IView {


    private var mHeadUrl = ""
    private var mUserName = ""
    private lateinit var mLoadingDialog: LoadingDialog

    override fun getLayoutId() = R.layout.activity_login

    override fun registerPresenter() = LoginPresenter::class.java


    override fun init(savedInstanceState: Bundle?) {
        requestInitPermission()
    }


    override fun initView() {
        mLoadingDialog = LoadingDialog(this)
        iv_head.setOnClickListener { openPicLib() }
        btn_login.setOnClickListener { login() }
    }

    override fun initData() {
        getPresenter().checkLoginStatus()
    }

    private fun login() {
        val userName = et_username.text.toString().trim()
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, R.string.input_username, Toast.LENGTH_SHORT).show()
            return
        }
        //test
//        mHeadUrl = "https://avatars2.githubusercontent.com/u/10775316?s=400&u=1c02314e0bf6a7cf695152cbd42b3fa7ab9a8f49&v=4"
        if (TextUtils.isEmpty(mHeadUrl)) {
            Toast.makeText(this, R.string.input_head_img, Toast.LENGTH_SHORT).show()
            return
        }

        mUserName = userName
        getPresenter().login(mUserName, mHeadUrl)
    }

    private fun openPicLib() {
        SystemUtils.openPicLibForResult(this, Constant.ReqCode.CODE_PIC_LIB)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && Activity.RESULT_OK == resultCode) {
            when (requestCode) {
                Constant.ReqCode.CODE_PIC_LIB -> onPicLibResult(data)
            }
        }
    }

    private fun onPicLibResult(data: Intent) {
        val picPath = SystemUtils.getPicLibResult(this, data)
        getPresenter().uploadImg(picPath)
        ImageLoader.load(this, picPath, iv_head, ImageLoader.getCircleRequest())
    }

    override fun uploadImgSuccess(url: String) {
        mHeadUrl = url
    }

    override fun uploadImgError() {
        iv_head.setImageResource(R.drawable.icon_add_img)
        Toast.makeText(this, R.string.upload_img_error, Toast.LENGTH_SHORT).show()
    }

    override fun goHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun requestInitPermission() {
        val arr = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE)
        PermissionUtils.requestPermission(this, arr, Constant.PermissionCode.CODE_INIT) { permissions, result ->
            // permission result
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun showLoadDialog() {
        mLoadingDialog.show()
    }

    override fun dismissLoadDialog() {
        mLoadingDialog.dismiss()
    }

    override fun showLoginView() {
        ll_login.visibility = View.VISIBLE
    }

}

