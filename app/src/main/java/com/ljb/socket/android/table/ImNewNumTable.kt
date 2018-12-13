package com.senyint.ihospital.user.kt.db.table

import android.provider.BaseColumns
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.utils.SPUtils
import dao.ljb.kt.table.BaseTable

/**
 * Author:Ljb
 * Time:2018/11/9
 * There is a lot of misery in life
 **/

class ImNewNumTable : BaseTable() {


    val COLUMN_ID = BaseColumns._ID
    val COLUMN_NEW_NUM = "newNum"
    val COLUMN_CONVERSATION = "conversation"


    override fun createColumns(): Map<String, String> {
        val tableColumns = HashMap<String, String>()
        tableColumns[COLUMN_ID] = "integer primary key autoincrement"
        tableColumns[COLUMN_NEW_NUM] = TYPE_INTEGER
        tableColumns[COLUMN_CONVERSATION] = TYPE_TEXT
        return tableColumns
    }

    override fun createTableName(): String {
        val uid = SPUtils.getString(Constant.SPKey.KEY_UID)
        return "im_new_num_$uid"
    }


}