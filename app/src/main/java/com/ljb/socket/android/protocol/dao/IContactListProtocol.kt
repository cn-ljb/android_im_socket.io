package com.ljb.socket.android.protocol.dao

import com.ljb.socket.android.model.UserBean
import com.ljb.socket.android.table.ContactTable
import dao.ljb.kt.core.IDaoInterface
import io.reactivex.Observable

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
interface IContactListProtocol : IDaoInterface {

    fun insertContactList(table: ContactTable, contactList: List<UserBean>): Observable<Boolean>

    fun getAllContactList(contactTable: ContactTable): Observable<List<UserBean>>

    fun queryContactByIdImpl(mContactTable: ContactTable, uid: String): UserBean?

}