package com.ljb.socket.android.protocol.dao.impl

import android.database.Cursor
import com.ljb.socket.android.db.DatabaseSqlHelper
import com.ljb.socket.android.protocol.dao.IInitDaoProtocol
import com.ljb.socket.android.table.ContactTable
import com.ljb.socket.android.table.ImConversationTable
import com.senyint.ihospital.user.kt.db.table.ImNewNumTable
import dao.ljb.kt.core.BaseDaoProtocol
import dao.ljb.kt.table.BaseTable
import io.reactivex.Observable
import net.ljb.kt.utils.NetLog

/**
 * Author:Ljb
 * Time:2018/11/9
 * There is a lot of misery in life
 **/
class InitDaoProtocol : BaseDaoProtocol(), IInitDaoProtocol {


    override fun getCountForTable(table: BaseTable): Observable<Int> = createObservable {
        getCountForTableImpl(table)
    }


    override fun createTableNotExists(table: BaseTable): Observable<Boolean> = createObservable {
        createTableImpl(table)
    }

    override fun initTable(): Observable<Boolean> = createObservable {
        initTableImpl()
    }

    private fun getCountForTableImpl(table: BaseTable): Int {
        var result = 0
        var cursor: Cursor? = null
        try {
            cursor = mSqliteDb.rawQuery("select count(*) from ${table.getName()}", null)
            if (cursor != null && cursor.moveToNext()) {
                result = cursor.getInt(0)
            }
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            cursor?.close()
        }
        return result
    }

    private fun createTableImpl(table: BaseTable): Boolean {
        var result: Boolean
        try {
            val sql = DatabaseSqlHelper.getCreateTableSql(table)
            mSqliteDb.execSQL(sql)
            result = true
        } catch (e: Exception) {
            NetLog.e(e)
            result = false
        }
        return result
    }


    private fun initTableImpl(): Boolean {
        var result = false
        try {
            mSqliteDb.beginTransaction()

            //联系人表
            val contactTable = ContactTable()
            val patientTableSql = DatabaseSqlHelper.getCreateTableSql(contactTable)
            mSqliteDb.execSQL(patientTableSql)
            //新消息数量表
            val newNumTable = ImNewNumTable()
            val newNumTableSql = DatabaseSqlHelper.getCreateTableSql(newNumTable)
            mSqliteDb.execSQL(newNumTableSql)

            //会话表
            val conversationTable = ImConversationTable()
            val conversationTableSql = DatabaseSqlHelper.getCreateTableSql(conversationTable)
            mSqliteDb.execSQL(conversationTableSql)


            mSqliteDb.setTransactionSuccessful()
            result = true
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            mSqliteDb.endTransaction()
        }
        return result
    }


}