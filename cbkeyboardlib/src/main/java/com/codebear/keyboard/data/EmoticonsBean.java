package com.codebear.keyboard.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.codebear.keyboard.R;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 * <p>
 * Created by CodeBear on 2017/6/28.
 */

public class EmoticonsBean implements Parcelable {
    public static final String DEL = "del";

    private String parentTag;
    private String parentId;
    private String id;
    private String name;
    private Object iconUri;
    private String iconType;
    private boolean showName = false;
    private boolean bigEmoticon = false;

    private int rol = -1;
    private int row = -1;

    private boolean showDel = false;

    private List<EmoticonsBean> emoticonsBeanList = new ArrayList<>();

    public EmoticonsBean() {
    }

    public EmoticonsBean(boolean isDel) {
        if(isDel) {
            id = DEL;
            iconUri = R.mipmap.ic_del;
        }
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getIconUri() {
        return iconUri;
    }

    public void setIconUri(Object iconUri) {
        this.iconUri = iconUri;
    }

    public String getIconType() {
        return iconType;
    }

    public void setIconType(String iconType) {
        this.iconType = iconType;
    }

    public List<EmoticonsBean> getEmoticonsBeanList() {
        return emoticonsBeanList;
    }

    public void setEmoticonsBeanList(List<EmoticonsBean> emoticonsBeanList) {
        this.emoticonsBeanList = emoticonsBeanList;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public boolean isShowDel() {
        return showDel;
    }

    public void setShowDel(boolean showDel) {
        this.showDel = showDel;

    }

    public boolean isShowName() {
        return showName;
    }

    public void setShowName(boolean showName) {
        this.showName = showName;
    }

    public boolean isBigEmoticon() {
        return bigEmoticon;
    }

    public void setBigEmoticon(boolean bigEmoticon) {
        this.bigEmoticon = bigEmoticon;
        if (rol == -1 || row == -1) {
            if (bigEmoticon) {
                rol = 4;
            } else {
                rol = 7;
            }
            row = 3;
        }
    }

    public String getParentTag() {
        return parentTag;
    }

    public void setParentTag(String parentTag) {
        this.parentTag = parentTag;
    }

    protected EmoticonsBean(Parcel in) {
        parentTag = in.readString();
        parentId = in.readString();
        id = in.readString();
        name = in.readString();
        iconType = in.readString();
        showName = in.readByte() != 0;
        bigEmoticon = in.readByte() != 0;
        rol = in.readInt();
        row = in.readInt();
        showDel = in.readByte() != 0;
        emoticonsBeanList = in.createTypedArrayList(EmoticonsBean.CREATOR);
    }

    public static final Creator<EmoticonsBean> CREATOR = new Creator<EmoticonsBean>() {
        @Override
        public EmoticonsBean createFromParcel(Parcel in) {
            return new EmoticonsBean(in);
        }

        @Override
        public EmoticonsBean[] newArray(int size) {
            return new EmoticonsBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(parentTag);
        parcel.writeString(parentId);
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(iconType);
        parcel.writeByte((byte) (showName ? 1 : 0));
        parcel.writeByte((byte) (bigEmoticon ? 1 : 0));
        parcel.writeInt(rol);
        parcel.writeInt(row);
        parcel.writeByte((byte) (showDel ? 1 : 0));
        parcel.writeTypedList(emoticonsBeanList);
    }
}
