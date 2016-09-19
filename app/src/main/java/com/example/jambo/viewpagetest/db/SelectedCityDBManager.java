package com.example.jambo.viewpagetest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jambo on 2016/9/18.
 */
public class SelectedCityDBManager extends SQLiteOpenHelper {
    private static final String DBNAME = "CityManager.db";
    private static final int DBVERSION = 1;
    private static final String TABLENAME = "CityManager";

    public static final String CREATE_CITYMANAGER = "create table CityManager("
        + "name text primary key,"
        + "pageIndex integer,"
        + "cityId integer)";

    private Context mContext;
    public SelectedCityDBManager(Context context){
        super(context,DBNAME,null,DBVERSION);
        mContext = context;
    }


    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CITYMANAGER);
    }


    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int addCity(SQLiteDatabase db, String name, int pageIndex, int cityId){
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("pageIndex",pageIndex);
        values.put("cityId",cityId);
        int resutl = (int)db.insert(TABLENAME,null,values);
        return resutl;
    }

    public void deleteCity(SQLiteDatabase db, String name){
        db.delete(TABLENAME, "name = ?", new String[]{name});
    }

}
