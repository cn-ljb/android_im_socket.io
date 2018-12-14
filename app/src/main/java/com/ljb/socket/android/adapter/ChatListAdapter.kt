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
import com.ljb.socket.android.model.ConversationBean
import com.ljb.socket.android.utils.ChatUtils
import com.ljb.socket.android.utils.TimeUtils

/**
 * Author:Ljb
 * Time:2018/12/13
 * There is a lot of misery in life
 **/
class ChatListAdapter(context: Context, data: MutableList<ConversationBean>) : BaseRVAdapter<ConversationBean>(context, data) {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onBindViewData(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (holder is ChatHolder) {
            ImageLoader.load(context, item.user.headUrl, holder.ivHead, ImageLoader.getCircleRequest())
            holder.tvTime.text = TimeUtils.formatChatMessageTime(item.chatMessage.time)
            holder.tvName.text = item.user.name
            ChatUtils.setTextViewBody(holder.tvMsg, item.chatMessage)
            if (item.newNum > 0) {
                holder.tvRed.visibility = View.VISIBLE
                holder.tvRed.text = if (item.newNum < 99) item.newNum.toString() else "99"
            } else {
                holder.tvRed.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_conversation, parent, false)
        return ChatHolder(view)
    }

    class ChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime by lazy { itemView.findViewById<TextView>(R.id.tv_time) }
        val tvRed by lazy { itemView.findViewById<TextView>(R.id.tv_red) }
        val tvName by lazy { itemView.findViewById<TextView>(R.id.tv_name) }
        val tvMsg by lazy { itemView.findViewById<TextView>(R.id.tv_msg) }
        val ivHead by lazy { itemView.findViewById<ImageView>(R.id.iv_head) }
    }
}
