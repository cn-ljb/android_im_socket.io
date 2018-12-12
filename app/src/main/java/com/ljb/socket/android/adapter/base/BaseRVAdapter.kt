package com.ljb.socket.android.adapter.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Author:Ljb
 * Time:2018/12/11
 * There is a lot of misery in life
 **/
abstract class BaseRVAdapter<T>(val context: Context, val data: MutableList<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onItemClick(holder.itemView, position)
        }
        onBindViewData(holder, position)
    }

    abstract fun onBindViewData(holder: RecyclerView.ViewHolder, position: Int)

    override fun getItemCount() = data.size


}
