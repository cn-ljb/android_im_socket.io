package com.ljb.socket.android.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Author:Ljb
 * Time:2018/12/6
 * There is a lot of misery in life
 **/
class UserBean(val uid: String, var name: String, var headUrl: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(headUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserBean> {
        override fun createFromParcel(parcel: Parcel): UserBean {
            return UserBean(parcel)
        }

        override fun newArray(size: Int): Array<UserBean?> {
            return arrayOfNulls(size)
        }
    }
}
