package com.ljb.socket.android.protocol.dao.impl

import android.content.ContentValues
import android.database.Cursor
import com.ljb.socket.android.db.DatabaseSqlHelper
import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.protocol.dao.IContactProtocol
import com.ljb.socket.android.table.ContactTable
import dao.ljb.kt.core.BaseDaoProtocol
import net.ljb.kt.utils.NetLog

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
class ContactListProtocol : BaseDaoProtocol(), IContactProtocol {
    override fun queryContactById(contactTable: ContactTable, uid: String) = createObservable {
        queryContactByIdImpl(contactTable , uid)
    }

    override fun getAllContactList(contactTable: ContactTable) = createObservable {
        getAllContactListImpl(contactTable)
    }


    override fun insertContactList(table: ContactTable, contactList: List<UserBean>) = createObservable {
        insertContactListImpl(table, contactList)
    }

    private fun insertContactListImpl(table: ContactTable, contactList: List<UserBean>): Boolean {
        var result = false
        try {
            mSqliteDb.beginTransaction()
            for (contact in contactList) {
                val dbUserBean = queryContactByIdImpl(table, contact.uid)
                if (dbUserBean != null) {
                    updateContactImpl(table, contact)
                } else {
                    insertContactImpl(table, contact)
                }
            }
            mSqliteDb.setTransactionSuccessful()
            result = true
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            mSqliteDb.endTransaction()
        }
        return result
    }

    private fun updateContactImpl(table: ContactTable, contact: UserBean): Int {
        var num = 0
        try {
            val values: ContentValues = DatabaseSqlHelper.getContactContentValue(table, contact)
            num = mSqliteDb.update(table.getName(), values, "${table.COLUMN_UID} = ?", arrayOf(contact.uid))
        } catch (e: Exception) {
            NetLog.e(e)
        }
        return num
    }

    private fun insertContactImpl(table: ContactTable, contact: UserBean): Long {
        var num = -1L
        try {
            val values: ContentValues = DatabaseSqlHelper.getContactContentValue(table, contact)
            num = mSqliteDb.insert(table.getName(), null, values)
        } catch (e: Exception) {
            NetLog.e(e)
        }
        return num
    }

    override fun queryContactByIdImpl(table: ContactTable, uid: String): UserBean? {
        var bean: UserBean? = null
        var cursor: Cursor? = null
        try {
            cursor = mSqliteDb.rawQuery("select * from ${table.getName()} where ${table.COLUMN_UID} = ?", arrayOf(uid))
            if (cursor != null && cursor.moveToNext()) {
                bean = DatabaseSqlHelper.getContact(table, cursor)
            }
        } catch (e: java.lang.Exception) {
            NetLog.e(e)
        } finally {
            cursor?.close()
        }
        return bean
    }

    private fun getAllContactListImpl(table: ContactTable): List<UserBean> {
        var list = ArrayList<UserBean>()
        var cursor: Cursor? = null
        try {
            mSqliteDb.beginTransaction()
            cursor = mSqliteDb.rawQuery("select * from ${table.getName()}", null)
            while (cursor != null && cursor.moveToNext()) {
                val bean = DatabaseSqlHelper.getContact(table, cursor)
                list.add(bean)
            }
            mSqliteDb.setTransactionSuccessful()
        } catch (e: Exception) {
            NetLog.e(e)
        } finally {
            cursor?.close()
            mSqliteDb.endTransaction()
        }
        return list
    }
}