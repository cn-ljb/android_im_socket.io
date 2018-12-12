package com.ljb.socket.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.ljb.socket.android.BuildConfig
import java.io.File


/**
 * Created by L on 2017/8/1.
 */
object SystemUtils {


    fun openCallTel(context: Context, phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }


    fun openBrowser(context: Context, url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(url)
        intent.data = content_url
        context.startActivity(intent)
    }

    /**
     * 拍照
     * */
    fun openTakePic(act: Activity, requestCode: Int, takePicPath: String) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageUri: Uri
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            imageUri = FileProvider.getUriForFile(act.applicationContext, "${BuildConfig.APPLICATION_ID}.fileprovider", File(takePicPath))
        } else {
            imageUri = Uri.fromFile(File(takePicPath))
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        act.startActivityForResult(intent, requestCode)
    }

    /**
     * 打开图库
     */
    fun openPicLibForResult(context: Activity, requestCode: Int) {
        val i = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        context.startActivityForResult(i, requestCode)
    }

    /**
     * 获取图库返回的图片
     * */
    fun getPicLibResult(act: Activity, data: Intent): String {
        val selectedImage = data.data
        val picturePath: String
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = act.contentResolver.query(selectedImage,
                filePathColumn, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            picturePath = cursor.getString(columnIndex)
            cursor.close()
        } else {
            picturePath = selectedImage.encodedPath
        }
        return picturePath
    }

}