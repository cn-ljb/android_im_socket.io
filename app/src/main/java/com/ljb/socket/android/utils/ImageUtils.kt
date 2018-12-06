package com.ljb.socket.android.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by L on 2017/8/5.
 */
object ImageUtils {


    /**
     * 根据当前时间生成一张图片地址
     * @return
     */
    fun getImageFile(c: Context): File {
        //图片根目录
        val imagePath = FileUtils.getPicDir(c)
        //文件名
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(Date())
        //新建文件
        val imageFile = File(imagePath + File.separator + "IMG_" + timeStamp + ".jpg")
        return imageFile
    }


    fun reverseImg(imgStr: String): String {
        var result = ""
        if (imgStr.isBlank()) {
            result = imgStr
        } else {
            val imgList = parseImage(imgStr)
            imgList.reverse()
            imgList.map { result = jointImg(result, it) }
        }
        return result
    }

    /**
     * 根据position替换对应图片字符串
     *
     * @return  返回处理之后的图片字符串
     * */
    fun replaceImg(parentImgStr: String, subImgStr: String, position: Int): String {
        var result = ""
        if (parentImgStr.isBlank()) {
            result = subImgStr
        } else {
            val imgList = parseImage(parentImgStr)
            if (imgList.size > position) {
                imgList.removeAt(position)
                imgList.add(position, subImgStr)
                imgList.map { result = jointImg(result, it) }
            } else {
                result = jointImg(parentImgStr, subImgStr)
            }
        }
        return result
    }


    /**
     * 拼接图片
     * */
    fun jointImg(parentImgStr: String, subImgStr: String): String {
        var resultImgStr = parentImgStr
        if (resultImgStr.isBlank()) {
            resultImgStr = subImgStr
        } else {
            resultImgStr += ",$subImgStr"
        }
        return resultImgStr
    }

    /**
     * 解析图片
     * */
    fun parseImage(imgStr: String): ArrayList<String> {
        val list = ArrayList<String>()
        if (imgStr.contains(",")) {
            imgStr.split(",").map { list.add(it) }
        } else {
            list.add(imgStr)
        }
        return list
    }

    /**
     * 读取图片的旋转的角度

     * @param imagePath
     * *            图片绝对路径
     * *
     * @return 图片的旋转角度
     */
    fun getBitmapDegree(imagePath: String): Int {
        var degree = 0
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            val exifInterface = ExifInterface(imagePath)
            // 获取图片的旋转信息
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    /**
     * 将图片按照某个角度进行旋转

     * @param bm
     * *            需要旋转的图片
     * *
     * @param degree
     * *            旋转角度
     * *
     * @return 旋转后的图片
     */
    fun rotateBitmapByDegree(bm: Bitmap, degree: Int): Bitmap {
        var returnBm: Bitmap? = null
        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }

        if (returnBm == null) {
            returnBm = bm
        }
        if (bm != returnBm) {
            bm.recycle()
        }
        return returnBm
    }

    /**
     * 质量压缩到本地
     * @param targetSize 目标大小（单位：kb）
     * *
     */
    fun compressImage(file: File, outFile: File, maxWidth: Int, maxHeight: Int, targetSize: Int): Boolean {
        val out: FileOutputStream
        try {
            //先进行了内存压缩后，再进行质量压缩 ， 防止一张大图就崩的可能性
            var scaledBitmap = getScaledBitmap(file, maxWidth, maxHeight)
            out = FileOutputStream(outFile.absolutePath)

            //修正图片方向
            val bitmapDegree = getBitmapDegree(file.absolutePath)
            if (bitmapDegree != 0) {
                scaledBitmap = rotateBitmapByDegree(scaledBitmap, bitmapDegree)
            }

            val baos = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            var options = 80
            while (baos.toByteArray().size / 1024 > targetSize) {
                baos.reset()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, options, baos)
                options -= 10
                if (options < 0) {
                    options = 10
                    break
                }
            }
            //压缩到本地
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, options, out)
            return true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * 将图片文件以目标宽高进行压缩
     * @param file
     * *
     * @param maxWidth
     * *
     * @param maxHeight
     * *
     * @return
     */
    fun getScaledBitmap(file: File, maxWidth: Int, maxHeight: Int): Bitmap {
        return getScaledBitmap(file.absolutePath, maxWidth, maxHeight)
    }

    /**
     * 将图片路径以目标宽高进行压缩
     * @param filePath
     * *
     * @param maxWidth
     * *
     * @param maxHeight
     * *
     * @return
     */
    fun getScaledBitmap(filePath: String, maxWidth: Int, maxHeight: Int): Bitmap {
        val decodeOptions = BitmapFactory.Options()
        val bitmap: Bitmap

        decodeOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, decodeOptions)
        val actualWidth = decodeOptions.outWidth
        val actualHeight = decodeOptions.outHeight

        val desiredWidth = getResizedDimension(maxWidth, maxHeight,
                actualWidth, actualHeight)
        val desiredHeight = getResizedDimension(maxHeight, maxWidth,
                actualHeight, actualWidth)

        // Decode to the nearest power of two scaling factor.
        decodeOptions.inJustDecodeBounds = false
        // decodeOptions.inPreferQualityOverSpeed = PREFER_QUALITY_OVER_SPEED;
        decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight)
        val tempBitmap = BitmapFactory.decodeFile(filePath, decodeOptions)
        // If necessary, scale down to the maximal acceptable size.
        if (tempBitmap != null && (tempBitmap.width > desiredWidth || tempBitmap.height > desiredHeight)) {
            bitmap = Bitmap.createScaledBitmap(tempBitmap,
                    desiredWidth, desiredHeight, true)
            tempBitmap.recycle()
        } else {
            bitmap = tempBitmap
        }
        return bitmap
    }

    /**
     * 计算长宽比
     * @param maxWidth 目标宽度
     * *
     * @param maxHeight 目标高度
     * *
     * @param actualWidth 实际宽度
     * *
     * @param actualHeight 实际高度
     */
    fun getResizedDimension(maxWidth: Int, maxHeight: Int, actualWidth: Int,
                            actualHeight: Int): Int {

        if (maxWidth == 0 && maxHeight == 0) {
            return actualWidth
        }

        //目标宽度为0 取高度比
        if (maxWidth == 0) {
            val ratio = maxHeight.toDouble() / actualHeight.toDouble()
            return (actualWidth * ratio).toInt()
        }

        //目标高度为0 取宽度比
        if (maxHeight == 0) {
            return maxWidth
        }

        //目标与实际都不为0  ， 计算合适比率
        val ratio = actualHeight.toDouble() / actualWidth.toDouble()
        var resized = maxWidth
        if (resized * ratio > maxHeight) {
            resized = (maxHeight / ratio).toInt()
        }
        return resized
    }

    /**

     * 通过长宽比，返回合适的压缩比
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.

     * @param actualWidth Actual width of the bitmap
     * *
     * @param actualHeight Actual height of the bitmap
     * *
     * @param desiredWidth Desired width of the bitmap
     * *
     * @param desiredHeight Desired height of the bitmap
     */
    fun findBestSampleSize(
            actualWidth: Int, actualHeight: Int, desiredWidth: Int, desiredHeight: Int): Int {
        val wr = actualWidth.toDouble() / desiredWidth
        val hr = actualHeight.toDouble() / desiredHeight
        val ratio = Math.min(wr, hr)
        var n = 1.0f
        while (n * 2 <= ratio) {
            n *= 2f
        }
        return n.toInt()
    }


}