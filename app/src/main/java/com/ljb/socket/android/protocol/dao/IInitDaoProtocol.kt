package com.ljb.socket.android.protocol.dao

import dao.ljb.kt.core.IDaoInterface
import dao.ljb.kt.table.BaseTable
import io.reactivex.Observable

/**
 * Author:Ljb
 * Time:2018/11/9
 * There is a lot of misery in life
 **/
interface IInitDaoProtocol : IDaoInterface {

    fun initTable(): Observable<Boolean>

    fun createTableNotExists(table: BaseTable): Observable<Boolean>

    fun getCountForTable(table: BaseTable): Observable<Int>

}