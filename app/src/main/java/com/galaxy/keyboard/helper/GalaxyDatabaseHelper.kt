package com.galaxy.keyboard.helper;

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.galaxy.keyboard.model.PhraseModel
import java.util.ArrayList

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

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(PREDICTION_TABLE_NAME, null, values)

        return newRowId > 0
    }

    fun increasePredictionFrequency(predict: String, frequency: Int) {
        val db = writableDatabase
        db.rawQuery("UPDATE " +
                PREDICTION_TABLE_NAME
                + " SET " + PREDICTION_FIELD_FREQUENCY + " = " + (frequency + 1)
                + " WHERE " + PREDICTION_FIELD_PREDICT_LETTER + " = '" + predict + "'", null)
        Log.e("___Increasing Word___", "UPDATE " +
                PREDICTION_TABLE_NAME
                + " SET " + PREDICTION_FIELD_FREQUENCY + " = " + (frequency + 1)
                + " WHERE " + PREDICTION_FIELD_PREDICT_LETTER + " = '" + predict + "'")
    }

    @Throws(SQLiteConstraintException::class)
    fun deletePrediction(input: String): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = PREDICTION_FIELD_INPUT_LETTER + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(input)
        // Issue SQL statement.
        db.delete(PREDICTION_TABLE_NAME, selection, selectionArgs)

        return true
    }

    @SuppressLint("Range")
    fun readPrediction(input: String): ArrayList<PhraseModel> {
        if (input.isEmpty()) {
            return ArrayList()
        }
        val predicts = ArrayList<PhraseModel>()
        val db = writableDatabase
        var cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM "
                    + PREDICTION_TABLE_NAME + " WHERE "
                    + PREDICTION_FIELD_INPUT_LETTER + " LIKE '"
                    + input + "%' ORDER BY frequency, id DESC", null)
        } catch (e: SQLiteException) {
            // if table not yet present, create it
//            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                predicts.add(PhraseModel(input,
                    cursor.getString(cursor.getColumnIndex(PREDICTION_FIELD_PREDICT_LETTER)),
                    cursor.getInt(cursor.getColumnIndex(PREDICTION_FIELD_FREQUENCY))))
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
                    + FOLLOWER_FIELD_KEYWORD + "='"
                    + input + "' ORDER BY frequency, id DESC", null)
            Log.e("Prediction ", "SELECT * FROM "
                    + FOLLOWER_TABLE_NAME + " WHERE "
                    + FOLLOWER_FIELD_FOLLOW_LETTER + "='"
                    + input + "' ORDER BY frequency, id DESC")
        } catch (e: SQLiteException) {
            // if table not yet present, create it
//            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                predicts.add(PhraseModel(input,
                    cursor.getString(cursor.getColumnIndex(FOLLOWER_FIELD_FOLLOW_LETTER)),
                    cursor.getInt(cursor.getColumnIndex(FOLLOWER_FIELD_FREQUENCY))))
                cursor.moveToNext()
            }
        }
        return predicts
    }

    @SuppressLint("Range")
    fun readAllPredictions(): ArrayList<PhraseModel> {
        val predicts = ArrayList<PhraseModel>()
        val db = writableDatabase
        var cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM " + PREDICTION_TABLE_NAME + " ORDER BY frequency, id DESC", null)
        } catch (e: SQLiteException) {
//            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        var input: String
        var predict: String
        var freq: Int

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                input = cursor.getString(cursor.getColumnIndex(PREDICTION_FIELD_INPUT_LETTER))
                predict = cursor.getString(cursor.getColumnIndex(PREDICTION_FIELD_PREDICT_LETTER))
                freq = cursor.getInt(cursor.getColumnIndex(PREDICTION_FIELD_FREQUENCY))

                predicts.add(PhraseModel(input, predict, freq))
                cursor.moveToNext()
            }
        }
        return predicts
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "ayemin.db"

        private val PREDICTION_TABLE_NAME: String = "tbl_prediction"
        private val PREDICTION_FIELD_ID: String = "id"
        private val PREDICTION_FIELD_INPUT_LETTER: String = "input_letter"
        private val PREDICTION_FIELD_PREDICT_LETTER: String = "predict_letter"
        private val PREDICTION_FIELD_FREQUENCY: String = "frequency"

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