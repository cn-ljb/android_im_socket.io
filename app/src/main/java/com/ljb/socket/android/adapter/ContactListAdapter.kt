package com.ljb.socket.android.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ljb.socket.android.R
import com.ljb.socket.android.adapter.base.BaseRVAdapter
import com.ljb.socket.android.img.ImageLoader
import com.ljb.socket.android.model.UserBean

/**
 * Author:Ljb
 * Time:2018/12/11
 * There is a lot of misery in life
 **/
class ContactListAdapter(context: Context, data: MutableList<UserBean>) : BaseRVAdapter<UserBean>(context, data) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_item_contact, parent, false)
        return ContactItemHolder(itemView)
    }

    override fun onBindViewData(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (holder is ContactItemHolder) {
            ImageLoader.load(context, item.headUrl, holder.ivHead, ImageLoader.getCircleRequest())
            holder.tvName.text = item.name
        }
    }


    class ContactItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHead by lazy { view.findViewById<ImageView>(R.id.iv_head) }
        val tvName by lazy { view.findViewById<TextView>(R.id.tv_name) }
    }

}
