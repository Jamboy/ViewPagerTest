package com.example.jambo.viewpagetest.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Jambo on 2016/9/18.
 */
public class SelectedCityDBManager extends SQLiteOpenHelper {
    private static final String DBNAME = "CityManager.db";
    private static final int DBVERSION = 1;
    private static final String TABLENAME = "CityManager";

    public static final String CREATE_CITYMANAGER = "create table CityManager("
        + "pagerIndex INTEGER PRIMARY KEY,"
        + "cityId int,"
        + "name text NOT NULL)";

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

    public int addCity(String name,int city_id){
        if (!isExisted(name)){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name",name);
            values.put("cityId",city_id);
            int result = (int)db.insert(TABLENAME,null,values);
            return result;
        }else{
            return 0;
        }
    }


    public void deleteCity(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLENAME, "name = ?", new String[]{name});
    }

    public Cursor selectAllCity(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(TABLENAME,null,null,null, null,null, "pagerIndex" + " ASC");
        //return db.query(TABLENAME,null,null,null,null,null,null);
    }

    /**
     *判断数据库中是否存在该城市，如果cursor不为空则存在，返回true。
     *
     */
    public boolean isExisted(String cityName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLENAME,null,"name = ?", new String[]{cityName},null,null,null);
        if (cursor.moveToFirst()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            Log.d("DBManager",name);
            return true;
        }
        return false;
    }

    public int queryIdForName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor  = db.query(TABLENAME,new String[]{"cityId"},"name = ?",new String[]{name},null,null,null,null);
        if (cursor.moveToFirst()){
            int id = cursor.getInt(cursor.getColumnIndex("cityId"));
            return id;
        }
        return 0;
    }

}
