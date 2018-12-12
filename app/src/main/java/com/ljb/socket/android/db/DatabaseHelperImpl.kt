package com.ljb.socket.android.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import dao.ljb.kt.core.DatabaseHelper

/**
 * Author:Ljb
 * Time:2018/12/7
 * There is a lot of misery in life
 **/
class DatabaseHelperImpl(context: Context, dbName: String, version: Int) : DatabaseHelper(context, dbName, version) {


    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}
