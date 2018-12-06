package com.ljb.socket.android.utils

import android.content.Context
import android.os.Environment
import com.bumptech.glide.Glide
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.math.BigDecimal


object FileUtils {

    private val ROOT_DIR = "android_socket_io"

    fun getLogDir(context: Context): String = getDir(context, "log")

    fun getDownloadDir(context: Context): String = getDir(context, "download")

    fun getPicDir(context: Context): String = getDir(context, "pic")

    fun getSmallPicDir(context: Context): String = getDir(context, "pic" + File.separator + "small")

    fun getVoiceDir(context: Context) = getDir(context, "voice")

    fun getPicClipDir(context: Context): String = getDir(context, "clip_pic")


    fun getRecordDir(context: Context): String = getDir(context, "record")

    /**
     * 根据手机状�?自动挑�?存储介质（SD or 手机内部存储）

     * @param string
     * *
     * @return
     */
    private fun getDir(context: Context, string: String): String {
        return if (isSDAvailable()) {
            getSDDir(string)
        } else {
            getDataDir(context, string)
        }
    }

    /**
     * 判断sd卡是否可以用

     * @return
     */
    private fun isSDAvailable(): Boolean =
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * 获取到手机内存的目录
     */
    private fun getDataDir(context: Context, string: String): String {
        // data/data/包名/cache
        val path = context.cacheDir.absolutePath + File.separator + string
        val file = File(path)
        if (!file.exists()) {
            return if (file.mkdirs()) {
                file.absolutePath
            } else {
                ""
            }
        }
        return file.absolutePath
    }

    /**
     * 获取到sd卡的目录

     * @param key_dir
     * *
     * @return
     */
    private fun getSDDir(key_dir: String): String {
        val sb = StringBuilder()
        val absolutePath = Environment.getExternalStorageDirectory()
                .absolutePath// /mnt/sdcard
        sb.append(absolutePath)
        sb.append(File.separator)
                .append(ROOT_DIR)
                .append(File.separator)
                .append(key_dir)

        val filePath = sb.toString()
        val file = File(filePath)
        if (!file.exists()) {
            if (file.mkdirs()) {
                return file.absolutePath
            } else {
                return ""
            }
        }

        return file.absolutePath
    }

    /**
     * 删除文件或目录
     */
    fun deleteAll(file: File) {
        if (file.isFile || file.list()!!.isEmpty()) {
            file.delete()
        } else {
            val files = file.listFiles()
            for (i in files!!.indices) {
                deleteAll(files[i])
                files[i].delete()
            }

            if (file.exists()) { // 如果文件本身就是目录 ，就要删除目�?
                file.delete()
            }
        }
    }


    fun getCacheSize(context: Context): String {
        val cacheSize = getFolderSize(File(getDataDir(context, "")))
        var externalCacheSize: Long = 0
        if (isSDAvailable()) {
            externalCacheSize = getFolderSize(File(getSDDir("")))
        }

        return getFormatSize((cacheSize + externalCacheSize).toDouble())
    }

    /**
     * 清除缓存
     */
    fun clearCache(context: Context) {
        val cacheFile = File(getDataDir(context, ""))
        if (cacheFile.exists()) {
            deleteAll(cacheFile)
        }

        if (isSDAvailable()) {
            val sdCacheFile = File(getSDDir(""))
            if (sdCacheFile.exists()) {
                deleteAll(sdCacheFile)
            }
        }

        //Glide图片缓存目录
        val photoCacheDir = Glide.getPhotoCacheDir(context)
        if (photoCacheDir != null && photoCacheDir.exists()) {
            deleteAll(photoCacheDir)
        }
    }

    /**
     * 获取文件大小

     * @param file
     * *
     * @return
     */
    fun getFolderSize(file: File?): Long {
        var size: Long = 0
        if (file != null && file.exists()) {
            try {
                val fileList = file.listFiles()
                for (i in fileList!!.indices) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory) {
                        size += getFolderSize(fileList[i])
                    } else {
                        size += fileList[i].length()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return size
    }

    /**
     * 格式化文件大小

     * @param size
     * *
     * @return
     */
    fun getFormatSize(size: Double): String {
        if (0.0 == size) {
            return "0K"
        }

        val kiloByte = size / 1024
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .stripTrailingZeros().toPlainString() + "K"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .stripTrailingZeros().toPlainString() + "M"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .stripTrailingZeros().toPlainString() + "G"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString() + "T"
    }


    /**
     * 读取一个流中的内容
     * */
    fun readDataFromInputStream(input: InputStream): String {
        val bis = BufferedInputStream(input)
        var str = ""
        val buf = ByteArray(1024 * 100)
        var tempStr = ""
        try {
            while (true) {
                val len = bis.read(buf)
                if (len == -1) {
                    break
                } else {
                    tempStr = String(buf, 0, len)
                    str += tempStr
                }
            }
        } catch (e: Exception) {
            bis.close()
        }
        return str
    }


}
