package com.example.sqlitewithstetho.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sqlitewithstetho.database.model.Note;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    //DB version
    private static final int DATABASE_VERSION = 1;

    //DB name

    private static final String DATABASE_NAME = "notes_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Note.CREATE_TABLE);

    }

    //Upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop table if exist
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);

        //create table again
        onCreate(db);
    }

    public long insertNote(String note) {

        //get writable database as we want to write data
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // id and timestamp will insert automaticaly
        values.put(Note.COLUMN_NOTE, note);

        //insert row
        long id = database.insert(Note.TABLE_NAME, null, values);
        database.close();

        // return newly inserted row id
        return id;
    }

    /*public Note getNote(long id) {
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_ID},
                Note.COLUMN_ID +  " = ? ",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        //prepare Note object

        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))
        );
        cursor.close();

        return note;
    }*/
    public Note getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Note.TABLE_NAME,
                new String[]{Note.COLUMN_ID, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP},
                Note.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Note note = new Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }
    public List<Note> getAllNotes() {

        List<Note> noteList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Note.TABLE_NAME + " ORDER BY " + Note.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding in list
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();

                note.setId(cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)));

                noteList.add(note);
            } while (cursor.moveToNext());
        }
        //close db connection
        database.close();

        return noteList;
    }

    public int getNoteCount(){

        String countQuery="SELECT * FROM " + Note.TABLE_NAME;

        SQLiteDatabase database=this.getReadableDatabase();

        Cursor cursor=database.rawQuery(countQuery,null);

        int count=cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateNote(Note note){

        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(Note.COLUMN_NOTE,note.getNote());

        //updating row
       int count= database.update(Note.TABLE_NAME,values,Note.COLUMN_ID+" = ? ",
                new String[]{String.valueOf(note.getNote())});

        return count;
    }

    public void deleteNote(Note note){

        SQLiteDatabase database=this.getWritableDatabase();
        database.delete(Note.TABLE_NAME,Note.COLUMN_ID+ " = ?",
                new String[]{String.valueOf(note.getId())});
        database.close();

    }



}
