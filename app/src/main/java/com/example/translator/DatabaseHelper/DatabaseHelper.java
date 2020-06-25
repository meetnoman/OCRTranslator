package com.example.translator.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.translator.Helper.LanguageRemindHelper;
import com.example.translator.LanguageInputActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(@Nullable Context context) {

        super(context, "fyp",null,7);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String historyTableQuery="create table history (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "text TEXT NOT NULL," +
            "translation TEXT," +
            "image BLOB,"+
            "inputLanguage TEXT,"+
            "outputLanguage TEXT,"+
             "dateTime TEXT)";

        String languageQuery="create table languageRemember (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "languageinput TEXT NOT NULL," +
                "languageoutput TEXT)";

        String languageDownload="create table languageDetail (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "language TEXT NOT NULL," +
                "languageCode TEXT NOT NULL," +
                "download INTEGER)";


        try {
            sqLiteDatabase.execSQL(historyTableQuery);
            sqLiteDatabase.execSQL(languageQuery);
            sqLiteDatabase.execSQL(languageDownload);
        }catch (SQLException ex){
            Log.i("Db Error",ex.getMessage());
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS history");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS languageRemember");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS languageDetail");
        this.onCreate(sqLiteDatabase);
    }

    public boolean storeTextExtraction(String text,  byte imageInByte[]){
        SQLiteDatabase sdb=this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("text",text);
        values.put("translation","");
        values.put("image", imageInByte);
        values.put("dateTime",getDateTime());

        try
        {
            sdb.insertOrThrow("history", null, values);
            return true;
         }
        catch(SQLiteException ex)
        {   return false;
         }

    }
    public boolean storeTextTranslation(String text, String translation,byte imageInByte[],String inputLanguage,String outputLanguage){
        SQLiteDatabase sdb=this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("text",text);
        values.put("translation",translation);
        values.put("image", imageInByte);
        values.put("inputLanguage",inputLanguage);
        values.put("outputLanguage",outputLanguage);
        values.put("dateTime",getDateTime());

        try
        {
            sdb.insertOrThrow("history", null, values);
            return true;
         }
        catch(SQLiteException ex)
        {   return false;
         }

    }



    public boolean storeEnterdTextTranslation(String text, String translation,String inputLanguage,String outputLanguage){
        SQLiteDatabase sdb=this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("text",text);
        values.put("translation",translation);
        values.put("inputLanguage",inputLanguage);
        values.put("outputLanguage",outputLanguage);
        values.put("dateTime",getDateTime());

        try
        {
            sdb.insertOrThrow("history", null, values);
            return true;
        }
        catch(SQLiteException ex)
        {   return false;
        }

    }









    public void storeLanguage(){
        SQLiteDatabase sdb=this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("languageinput", LanguageRemindHelper.getInstance().getInputLanguage());
        values.put("languageoutput", LanguageRemindHelper.getInstance().getOutputLanguage());

        try
        {
            sdb.insertOrThrow("history", null, values);
           // return true;
        }
        catch(SQLiteException ex)
        {   //return false;
        }
    }

    public void storeLanguageDetail(){
        SQLiteDatabase sdb=this.getWritableDatabase();

          ArrayList<String> allLanguagesCode=new ArrayList<>(Arrays.asList("en","ur","zh","ar","tr","ru","id","ja","it","hi","fa","es","af","de","fr"));
           ArrayList<String> allLanguagesAbbrevations=new ArrayList<>(Arrays.asList("English","Urdu","Chineese","Arabic","Turkish","Russian","Indonesian","Japanese","Italian"
                ,"Hindi","Persian","Spanish","Afrikaans","German","French"));


        ContentValues values = new ContentValues();



        for (int i=0;i<allLanguagesAbbrevations.size();i++)
        {
        values.put("language", allLanguagesAbbrevations.get(i) );
        values.put("languageCode", allLanguagesCode.get(i)  );
        values.put("download", "0");


        try
        {
            sdb.insertOrThrow("languageDetail", null, values);
            // return true;
        }
        catch(SQLiteException ex)
        {   //return false;
        }

        }


    }







    public Cursor getHistoryResult(){
     String query="Select * from history";
     SQLiteDatabase sdb=this.getReadableDatabase();
     Cursor cursor= sdb.rawQuery(query,null);

     return cursor;
    }

    public Cursor getLanguageDetail(){
        String query="Select * from languageDetail";
        SQLiteDatabase sdb=this.getReadableDatabase();
        Cursor cursor= sdb.rawQuery(query,null);

        return cursor;
    }




    public  boolean updateLanguageDetail(int key,int download) {

        try {

            SQLiteDatabase sdb = this.getWritableDatabase();
            sdb.execSQL("UPDATE languageDetail SET download = " + download + " WHERE id = " + key );
            return true;
        }catch (Exception e){
            Log.i("Db Error",e.getMessage());

            return false;
        }


    }

    public String deleteRecord(String tableName,String id) {
        SQLiteDatabase sdb = this.getWritableDatabase();

        try {
            //new String[]{String.valueOf(id)}
             int a=sdb.delete(tableName, "id="+id,null);
               return String.valueOf(a);
        }
    catch (Exception e){

            return e.getMessage();
    }

    }





    public  String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  'at' HH:mm ");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
         }

}
