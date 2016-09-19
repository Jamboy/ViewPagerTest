package com.example.jambo.viewpagetest.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/8/12.
 */
public class CityQuery  {
    public Context context;

    public CityQuery(Context context){
        this.context = context;
    }

    public List getCursor(SQLiteDatabase db){
        Cursor cursor = db.query("T_City",null,null,null, null,null,null);
        List cityNames = new ArrayList();
        while(cursor.moveToNext()){
            String city = cursor.getString(
                          cursor.getColumnIndex("CityName"))
                                .replace("市","")
                                .replace("省","")
                                .replace("土家族苗族自治州","")
                                .replace("自治区","")
                                .replace("特别行政区", "")
                                .replace("地区", "")
                                .replace("盟", "");

            cityNames.add(city);
        }
        return cityNames;
    }


    public List queryByLike(SQLiteDatabase db,String searcheFilter){
        Cursor cursor = db.query("T_City", null,"CityName like '%" + searcheFilter + "%'", null,null,null,null);
        List cityNames = new ArrayList();
        while(cursor.moveToNext()){
            String city = cursor.getString(
                cursor.getColumnIndex("CityName"))
                .replace("市","")
                .replace("省","")
                .replace("土家族苗族自治州","")
                .replace("自治区","")
                .replace("特别行政区", "")
                .replace("地区", "")
                .replace("盟", "");
            cityNames.add(city);
        }
        return cityNames;
    }
}
