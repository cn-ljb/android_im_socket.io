package com.ljb.socket.android.adapter.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ljb.socket.android.R

/**
 * Author:Ljb
 * Time:2018/12/12
 * There is a lot of misery in life
 **/
class UnknownHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvTime by lazy { itemView.findViewById<TextView>(R.id.tv_time) }
}
