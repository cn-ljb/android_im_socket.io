package com.ljb.socket.android.table

import android.provider.BaseColumns
import com.ljb.socket.android.common.Constant
import com.ljb.socket.android.utils.SPUtils
import dao.ljb.kt.table.BaseTable

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
class ContactTable : BaseTable() {

    val COLUMN_ID = BaseColumns._ID
    val COLUMN_UID = "uid"
    val COLUMN_NAME = "name"
    val COLUMN_HEAD_IMG = "headImg"

    override fun createTableName(): String {
        val uid = SPUtils.getString(Constant.SPKey.KEY_UID)
        return "contact_list_$uid"
    }

    override fun createColumns(): Map<String, String> {
        val tableColumns = HashMap<String, String>()
        tableColumns[COLUMN_ID] = "integer primary key autoincrement"
        tableColumns[COLUMN_UID] = TYPE_TEXT
        tableColumns[COLUMN_NAME] = TYPE_TEXT
        tableColumns[COLUMN_HEAD_IMG] = TYPE_TEXT
        return tableColumns
    }

}
