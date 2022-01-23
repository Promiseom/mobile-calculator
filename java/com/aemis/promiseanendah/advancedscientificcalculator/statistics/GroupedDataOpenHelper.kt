package com.aemis.promiseanendah.advancedscientificcalculator.statistics

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GroupedDataOpenHelper(context: Context, tableName: String, createTableSQL: String) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        val TAG = "grouped data db"
        val DB_NAME = "my_database"
        val DB_VERSION = 1
    }

    private val DB_TABLE_NAME = tableName
    private val CREATE_TABLE_STRING: String = createTableSQL

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_STRING)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $DB_TABLE_NAME")
        onCreate(db)
    }
}