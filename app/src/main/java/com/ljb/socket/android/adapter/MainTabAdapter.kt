package com.ljb.socket.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ljb.socket.android.R
import com.ljb.socket.android.model.TabBean
import com.ljb.socket.android.widgets.TabGroupView

/**
 * Created by L on 2017/7/12.
 */
class MainTabAdapter(private val mContext: Context, val mData: List<TabBean>) : TabGroupView.TabAdapter() {

    override fun getCount() = mData.size

    override fun createTabView(position: Int, parent: ViewGroup?): View {
        return LayoutInflater.from(mContext).inflate(R.layout.bottom_tab_defalut, parent, false)
    }

    override fun bindData(position: Int, itemView: View) {
        val itemBean = mData[position]
        itemView.findViewById<ImageView>(R.id.bottom_tab_icon).setImageResource(itemBean.iconResID)
        itemView.findViewById<TextView>(R.id.bottom_tab_text).setText(itemBean.textResID)
        val tvRead = itemView.findViewById<TextView>(R.id.tv_bottom_red)
        if (itemBean.newNum > 0) {
            tvRead.visibility = View.VISIBLE
            tvRead.text = if (itemBean.newNum > 99) "99" else itemBean.newNum.toString()
        } else {
            tvRead.visibility = View.GONE
        }
    }

}