package com.galaxy.keyboard.helper;

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.galaxy.keyboard.model.PhraseModel
import com.galaxy.keyboard.model.UnicodeModel

class GalaxyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE_ENTRIES)
//        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    @Throws(SQLiteConstraintException::class)
    fun insertPrediction(phraseModel: PhraseModel): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(PREDICTION_FIELD_INPUT_LETTER, phraseModel.mInput)
        values.put(PREDICTION_FIELD_PREDICT_LETTER, phraseModel.mPredict)
        values.put(PREDICTION_FIELD_IS_USER_TEXT, phraseModel.mIsUserText)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(PREDICTION_TABLE_NAME, null, values)

        return newRowId > 0
    }

    @Throws(SQLiteConstraintException::class)
    fun deletePrediction(selId: Int) {
        // Gets the data repository in write mode
        val db = writableDatabase
        db.execSQL("UPDATE " +
                PREDICTION_TABLE_NAME
                + " SET " + PREDICTION_FIELD_IS_EXIST + " = 0" + " WHERE " + PREDICTION_FIELD_ID + " = ?", arrayOf(selId)
        )
    }

    fun increasePredictionFrequency(selId: Int) {
        val db = writableDatabase
        db.execSQL("UPDATE " +
                PREDICTION_TABLE_NAME
                + " SET " + PREDICTION_FIELD_FREQUENCY + " = " + PREDICTION_FIELD_FREQUENCY + " + " + 1
                + " WHERE " + PREDICTION_FIELD_ID + " = ?", arrayOf(selId)
        )
    }

    fun increaseFollowerFrequency(selId: Int) {
        val db = writableDatabase
        db.execSQL("UPDATE " +
                FOLLOWER_TABLE_NAME
                + " SET " + FOLLOWER_FIELD_FREQUENCY + " = " + FOLLOWER_FIELD_FREQUENCY + " + " + 1
                + " WHERE " + FOLLOWER_FIELD_ID + " = ?", arrayOf(selId)
        )
    }

    @SuppressLint("Range")
    fun readUnicodeFromSancode(input: String): UnicodeModel {
        if (input.isEmpty()) {
            return UnicodeModel(0, input, input)
        }
        val db = writableDatabase
        var cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM "
                    + UNICODE_TABLE_NAME + " WHERE "
                    + UNICODE_FIELD_SANCODE + " = '"
                    + input + "' AND is_exist = 1 ORDER BY id ASC", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
//            db.execSQL(SQL_CREATE_ENTRIES)
            return UnicodeModel(0, input, input)
        }

        var unicode = UnicodeModel(0, input, input)

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                unicode = UnicodeModel(
                    cursor.getInt(cursor.getColumnIndex(UNICODE_FIELD_ID)),
                    input,
                    cursor.getString(cursor.getColumnIndex(UNICODE_FIELD_UNICODE))
                )
                cursor.moveToNext()
            }
        }
        return unicode
    }

    @SuppressLint("Range")
    fun readPrediction(input: String, isUserText: Int): ArrayList<PhraseModel> {
        if (input.isEmpty() && isUserText == 0) {
            return ArrayList()
        }
        val predicts = ArrayList<PhraseModel>()
        val db = writableDatabase
        var cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM "
                    + PREDICTION_TABLE_NAME + " WHERE "
                    + PREDICTION_FIELD_INPUT_LETTER + " LIKE '"
                    + input + "%' AND " + PREDICTION_FIELD_IS_USER_TEXT + " = " + isUserText + " AND is_exist = 1 ORDER BY frequency DESC", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
//            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                predicts.add(PhraseModel(
                    cursor.getInt(cursor.getColumnIndex(PREDICTION_FIELD_ID)),
                    input,
                    cursor.getString(cursor.getColumnIndex(PREDICTION_FIELD_PREDICT_LETTER)),
                    cursor.getInt(cursor.getColumnIndex(PREDICTION_FIELD_FREQUENCY)),
                    cursor.getInt(cursor.getColumnIndex(PREDICTION_FIELD_IS_USER_TEXT)))
                )
                cursor.moveToNext()
            }
        }
        return predicts
    }

    @SuppressLint("Range")
    fun readFollower(input: String): ArrayList<PhraseModel> {
        if (input.isEmpty()) {
            return ArrayList()
        }
        val predicts = ArrayList<PhraseModel>()
        val db = writableDatabase
        var cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM "
                    + FOLLOWER_TABLE_NAME + " WHERE "
                    + FOLLOWER_FIELD_KEYWORD + " = '"
                    + input + "' AND is_exist = 1 ORDER BY frequency DESC", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
//            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                predicts.add(PhraseModel(
                    cursor.getInt(cursor.getColumnIndex(FOLLOWER_FIELD_ID)),
                    input,
                    cursor.getString(cursor.getColumnIndex(FOLLOWER_FIELD_FOLLOW_LETTER)),
                    cursor.getInt(cursor.getColumnIndex(FOLLOWER_FIELD_FREQUENCY))),
                )
                cursor.moveToNext()
            }
        }
        return predicts
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "ayemin.db"

        private val UNICODE_TABLE_NAME: String = "tbl_unicode"
        private val UNICODE_FIELD_ID: String = "id"
        private val UNICODE_FIELD_SANCODE: String = "sancode"
        private val UNICODE_FIELD_UNICODE: String = "unicode"

        private val PREDICTION_TABLE_NAME: String = "tbl_prediction"
        private val PREDICTION_FIELD_ID: String = "id"
        private val PREDICTION_FIELD_INPUT_LETTER: String = "input_letter"
        private val PREDICTION_FIELD_PREDICT_LETTER: String = "predict_letter"
        private val PREDICTION_FIELD_FREQUENCY: String = "frequency"
        private val PREDICTION_FIELD_IS_USER_TEXT: String = "is_user_text"
        private val PREDICTION_FIELD_IS_EXIST: String = "is_exist"

        private val FOLLOWER_TABLE_NAME: String = "tbl_follower"
        private val FOLLOWER_FIELD_ID: String = "id"
        private val FOLLOWER_FIELD_KEYWORD: String = "keyword"
        private val FOLLOWER_FIELD_FOLLOW_LETTER: String = "follow_letter"
        private val FOLLOWER_FIELD_FREQUENCY: String = "frequency"

        private val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PREDICTION_TABLE_NAME + " (" +
                    PREDICTION_FIELD_ID + " INTEGER PRIMARY KEY," +
                    PREDICTION_FIELD_INPUT_LETTER + " TEXT," +
                    PREDICTION_FIELD_PREDICT_LETTER + " TEXT," +
                    PREDICTION_FIELD_FREQUENCY + " INTEGER)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PREDICTION_TABLE_NAME
    }

}