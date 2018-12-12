package com.codebear.keyboard.utils;

import android.content.Context;

import com.codebear.keyboard.data.EmoticonsBean;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class ParseDataUtils {
    private static final String SP = "CBEMOTICONSKEYBOARD";

    public static EmoticonsBean parseDataFromFile(Context context, String zipFileName) {
        EmoticonsBean emoticonsBean = getEmoticonsBean(context, zipFileName);
        if (null != emoticonsBean) {
            return emoticonsBean;
        }
        String filePath = FileUtils.getFolderPath("emoticon");
        String xmlFilePath = filePath + "/" + zipFileName + "/" + zipFileName + ".xml";
        File file = new File(xmlFilePath);
        if (!file.exists()) {
            try {
                FileUtils.unzip(context.getAssets().open("emoticon/" + zipFileName + ".zip"), filePath);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        file = new File(xmlFilePath);
        if (file.exists()) {
            XmlUtil xmlUtil = new XmlUtil(context);
            emoticonsBean = xmlUtil.ParserXml(filePath + "/" + zipFileName, xmlUtil.getXmlFromSD(xmlFilePath));
            if (null != emoticonsBean) {
                String emoticonString = new Gson().toJson(emoticonsBean);
                context.getSharedPreferences(SP, Context.MODE_PRIVATE).edit().putString(zipFileName, emoticonString)
                        .apply();
            }
            return emoticonsBean;
        }
        return null;
    }

    public static EmoticonsBean getEmoticonsBean(Context context, String zipFileName) {
        String emoticonString = context.getSharedPreferences(SP, Context.MODE_PRIVATE).getString(zipFileName, null);
        if (null == emoticonString) {
            return null;
        }
        return new Gson().fromJson(emoticonString, EmoticonsBean.class);
    }
}
