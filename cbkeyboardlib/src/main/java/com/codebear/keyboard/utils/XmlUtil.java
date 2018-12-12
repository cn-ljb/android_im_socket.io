package com.codebear.keyboard.utils;

import android.content.Context;
import android.util.Xml;

import com.codebear.keyboard.data.EmoticonsBean;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/30.
 */

public class XmlUtil {
    Context mContext;

    public XmlUtil(Context context) {
        this.mContext = context;
    }

    public InputStream getXmlFromAssets(String xmlName) {
        try {
            InputStream inStream = this.mContext.getResources().getAssets().open(xmlName);
            return inStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public InputStream getXmlFromSD(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return new FileInputStream(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EmoticonsBean ParserXml(String filePath, InputStream inStream) {
        String emoticonKey = "Emoticon";
        boolean isEmoticon = false;

        EmoticonsBean emoticons = new EmoticonsBean();
        ArrayList<EmoticonsBean> emoticonsList = new ArrayList<>();
        emoticons.setEmoticonsBeanList(emoticonsList);
        EmoticonsBean temp = null;

        if (null != inStream) {
            XmlPullParser pullParser = Xml.newPullParser();
            try {
                pullParser.setInput(inStream, "UTF-8");
                int event = pullParser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            String sTagName = pullParser.getName();
                            if (emoticonKey.equals(sTagName)) {
                                isEmoticon = true;
                                temp = new EmoticonsBean();
                            }

                            if (isEmoticon) {
                                //Emoticon
                                switch (sTagName) {
                                    case "id":
                                        temp.setParentId(emoticons.getId());
                                        temp.setId(pullParser.nextText());
                                        break;
                                    case "name":
                                        temp.setName(pullParser.nextText());
                                        break;
                                    case "iconUri":
                                        String uri = filePath + "/";
                                        if (!"".equals(emoticons.getId())) {
                                            uri += emoticons.getId() + "/";
                                        }
                                        temp.setIconUri(uri + pullParser.nextText());
                                        break;
                                    case "iconType":
                                        temp.setIconType(pullParser.nextText());
                                        break;
                                }
                            } else {
                                //EmoticonSet
                                switch (sTagName) {
                                    case "id":
                                        emoticons.setId(pullParser.nextText());
                                        break;
                                    case "name":
                                        emoticons.setName(pullParser.nextText());
                                        break;
                                    case "iconUri":
                                        String uri = filePath + "/";
                                        if (!"".equals(emoticons.getId())) {
                                            uri += emoticons.getId() + "/";
                                        }
                                        emoticons.setIconUri(uri + pullParser.nextText());
                                        break;
                                    case "iconType":
                                        emoticons.setIconType(pullParser.nextText());
                                        break;
                                    case "showName":
                                        String showName = pullParser.nextText();
                                        emoticons.setShowName(Integer.parseInt(showName) != 0);
                                        break;
                                    case "bigEmoticon":
                                        String bigEmoticon = pullParser.nextText();
                                        emoticons.setBigEmoticon(Integer.parseInt(bigEmoticon) != 0);
                                        break;
                                    case "rol":
                                        String rol = pullParser.nextText();
                                        emoticons.setRol(Integer.parseInt(rol));
                                        break;
                                    case "row":
                                        String row = pullParser.nextText();
                                        emoticons.setRow(Integer.parseInt(row));
                                        break;
                                    case "showDel":
                                        String showDel = pullParser.nextText();
                                        emoticons.setShowDel(Integer.parseInt(showDel) != 0);
                                        break;
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            String eTagName = pullParser.getName();
                            if (isEmoticon && emoticonKey.equals(eTagName)) {
                                isEmoticon = false;
                                emoticonsList.add(temp);
                            }
                            break;
                        default:
                            break;
                    }
                    event = pullParser.next();
                }
                return emoticons;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return emoticons;
    }
}
