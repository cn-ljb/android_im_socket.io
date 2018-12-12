package com.ljb.socket.android.utils

import android.content.Context
import com.ljb.socket.android.model.FileUploadResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.ljb.kt.client.HttpClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.WeakReference

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
        fun onError(e: Throwable)
        fun onSuccess(path: String)
    }

    private val mRxLife = ArrayList<WeakReference<Disposable>>()

    fun downFile(url: String, savePath: String, downCall: FileDownCallBack) {
        val request = Request.Builder().url(url).get().build()
        val call = HttpClient.getLongHttpClient().newCall(request)
        val subscribe = Observable.create<String> {
            val response = call.execute()
            try {
                if (response.isSuccessful) {
                    var inputStream: InputStream? = null
                    var outputStream: OutputStream? = null
                    try {
                        inputStream = response.body()!!.byteStream()
                        outputStream = FileOutputStream(savePath)
                        val buff = ByteArray(1024 * 1024)
                        var downloaded: Long = 0
                        var len = 0
                        len = inputStream.read(buff)
                        while (len != -1) {
                            outputStream.write(buff, 0, len)
                            downloaded += len.toLong()
                            //更新UI进度
                            len = inputStream.read(buff)
                        }
                    } catch (e: Exception) {
                        it.onError(e)
                    } finally {
                        inputStream?.close()
                        outputStream?.close()
                    }
                    it.onNext(savePath)
                }
            } catch (e: Exception) {
                it.onError(e)
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    downCall.onSuccess(it)
                }, {
                    downCall.onError(it)
                })

        mRxLife.add(WeakReference(subscribe))
    }


    fun uploadVideoFile(path: String, uploadCall: FileUploadCallBack) {
        val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.ALTERNATIVE)

        val file = File(path)
        val fileBody = RequestBody.create(MediaType.parse("audio/mp4"), file)
        requestBody.addFormDataPart("mp3", File(path).name, fileBody)
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
            requestBody.addFormDataPart("img", File(path).name, fileBody)
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

