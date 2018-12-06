package com.ljb.socket.android.utils

import android.content.Context
import com.ljb.socket.android.model.FileUploadResult
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.ljb.kt.client.HttpClient
import net.ljb.kt.utils.NetLog
import okhttp3.*
import java.io.*
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author:Ljb
 * Time:2018/9/19
 * There is a lot of misery in life
 **/
class FileUploadManager(var mContext: Context?) {

    companion object {
        const val FILE_SERVICE_HOST = "http://integer.wang/uploads.shtml"
    }


    interface FileUploadCallBack {
        fun onError(e: Throwable)
        fun onSuccess(urlList: List<String>)
    }

    interface FileDownCallBack {
        fun onError(e: Exception)
        fun onSuccess(path: String)
    }

    private val mRxLife = ArrayList<WeakReference<Disposable>>()

    fun downFile(url: String, savePath: String, downCall: FileDownCallBack) {
//        val request = Request.Builder().url(url).get().build()
//        val call = HttpClient.getLongHttpClient().newCall(request)
//        mCallList.add(call)
//        call.enqueue(object : Callback {
//            override fun onFailure(call: Call?, e: IOException) {
//                downCall.onError(e)
//            }
//
//            override fun onResponse(call: Call?, response: Response?) {
//                try {
//                    if (response != null && response.code() in 200..299) {
//                        var inputStream: InputStream? = null
//                        var outputStream: OutputStream? = null
//                        try {
//                            inputStream = response.body()!!.byteStream()
//                            outputStream = FileOutputStream(savePath)
//                            val buff = ByteArray(1024 * 1024)
//                            var downloaded: Long = 0
//                            var len = 0
//                            len = inputStream.read(buff)
//                            while (len != -1) {
//                                outputStream.write(buff, 0, len)
//                                downloaded += len.toLong()
//                                //更新UI进度
//                                len = inputStream.read(buff)
//                            }
//                        } catch (e: Exception) {
//                            downCall.onError(e)
//                        } finally {
//                            inputStream?.close()
//                            outputStream?.close()
//                        }
//                        downCall.onSuccess(savePath)
//                    }
//                } catch (e: Exception) {
//                    downCall.onError(e)
//                }
//            }
//        })
    }


    fun uploadVideoFile(path: String, uploadCall: FileUploadCallBack) {
        val file = File(path)
        val fileBody = RequestBody.create(MediaType.parse("audio/mp4"), file)
        val requestBody = MultipartBody.Builder()
                .addFormDataPart("fileType", "4")
                .addFormDataPart("fileExtName", "mp4")
                .addFormDataPart("file", file.nameWithoutExtension, fileBody)
        sendRequest(requestBody.build(), uploadCall)
    }

    fun uploadImgFile(paths: List<String>, uploadCall: FileUploadCallBack) {
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.ALTERNATIVE)

        for (path in paths) {
            val file = File(path)
            val newFile = File(FileUtils.getSmallPicDir(mContext!!), file.name)
            ImageUtils.compressImage(file, newFile, 720, 1280, 300)
            val fileBody = RequestBody.create(MediaType.parse("image/jpg"), newFile)
            requestBody.addFormDataPart("file", File(path).name, fileBody)
        }
        sendRequest(requestBody.build(), uploadCall)
    }

    private fun sendRequest(body: MultipartBody, uploadCall: FileUploadCallBack) {

        val request = Request.Builder()
                .url(FILE_SERVICE_HOST)
                .post(body)
                .build()

        val subscribe = Observable.create<ArrayList<String>> { it ->
            val response = HttpClient.getLongHttpClient().newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body()!!.string()
                val result = JsonParser.fromJsonArr(json, FileUploadResult::class.java)
                val urlList = ArrayList<String>()
                result.map { urlList.add("${it.serverUrl}${it.url}") }
                it.onNext(urlList)
            } else {
                it.onError(IllegalStateException(response.message()))
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    uploadCall.onSuccess(it)
                }, {
                    uploadCall.onError(it)
                })
        mRxLife.add(WeakReference(subscribe))
    }


    fun release() {
        mRxLife.map {
            RxUtils.dispose(it.get())
        }
        mRxLife.clear()
        mContext = null
    }

}

