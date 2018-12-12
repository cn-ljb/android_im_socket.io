package com.ljb.socket.android.protocol.dao

import com.senyint.ihospital.user.kt.db.table.ImNewNumTable
import dao.ljb.kt.core.IDaoInterface
import io.reactivex.Observable

/**
 * Author:Ljb
 * Time:2018/11/10
 * There is a lot of misery in life
 **/
interface INewNumDaoProtocol : IDaoInterface {

    fun deleteNewNum(table: ImNewNumTable, fromId: String = "", conversation: String = ""): Observable<Boolean>

    fun insertNewNum(table: ImNewNumTable, num: Int, fromId: String = "", conversation: String = ""): Observable<Boolean>

    fun queryNewNum(table: ImNewNumTable, fromId: String = "", conversation: String = ""): Observable<Int>

    fun queryNewNumImpl(table: ImNewNumTable, fromId: String = "", conversation: String = ""): Int
}