package com.example.jambo.viewpagetest.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.example.jambo.viewpagetest.R;
import com.example.jambo.viewpagetest.db.SelectedCityDBManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/8/9.
 * 主要实现功能：城市数据同步问题
 * 从数据库获取所有已添加的城市：（在何处查询城市，这个活动还是MainActivity查询后传过来《Intent传递数据的局限性》）
 * 遍历添加CheckBox 何时添加》
 * 添加一个delete city Button 根据CheckBox的状态是否显示
 * Button要实现的操作是 首先删除选中的CheckBox，获取到checkBox的title
 */
public class AddCityManager extends Activity implements View.OnClickListener{
    private ImageButton add_city;
    private Button now_city_off;
    private CheckBox hengyang_rb;
    private CheckBox beiijing_ck;
    private ImageButton back_btn;
    private SelectedCityDBManager selectedCityDBManager;
    private LinearLayout checkBox_layout;
    private Button deleteCity;
    private List<View> removeCheckBoxs;
    private ArrayList<String> removeCities;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager);
        selectedCityDBManager = new SelectedCityDBManager(this);
        Log.d("AddCity","oncreate");
        initView();
        loadCityCheckBox();
    }

    public void initView(){
        add_city = (ImageButton) findViewById(R.id.city_manager_add_city);
        add_city.setOnClickListener(this);
        back_btn = (ImageButton) findViewById(R.id.city_manager_btn_back);
        back_btn.setOnClickListener(this);
        checkBox_layout = (LinearLayout) findViewById(R.id.checkbox_layout);
        deleteCity = (Button) findViewById(R.id.city_manager_delete_city_button);
        deleteCity.setOnClickListener(this);
        deleteCity.setVisibility(View.INVISIBLE);
        removeCheckBoxs = new ArrayList<>();
        removeCities = new ArrayList<>();
    }


    /**
     *获取到所有的城市
     */

    private void loadCityCheckBox(){
        Cursor cursor = selectedCityDBManager.selectAllCity();
        if (cursor.moveToFirst()){
            do {
                String city_name = cursor.getString(cursor.getColumnIndex("name"));
                addCheckBox(city_name);
            }while (cursor.moveToNext());
        }
    }


    /**
     * 添加对应的CheckBox
     * @param name
     */

    private void addCheckBox(final String name){
        final CheckBox checkBox = new CheckBox(getApplicationContext());
        checkBox.setText(name);
        checkBox_layout.addView(checkBox);
   //checkBox_layout.removeView(checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                    if (checkBox.isChecked()){
                        deleteCity.setVisibility(View.VISIBLE);
                        removeCities.add(name);
                        //checkBox.setTag(name);
                        removeCheckBoxs.add(checkBox);
                    }
            }
        });
    }





    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.city_manager_btn_back:
                Intent intent = new Intent(this,MainActivity.class);
                intent.putStringArrayListExtra("removeCities",removeCities);
                setResult(2,intent);
                this.finish();
                break;
            case R.id.city_manager_add_city:
                Intent intent1 = new Intent(AddCityManager.this,SearchCityActivity.class);
                startActivity(intent1);
                break;
            case R.id.city_manager_delete_city_button:
                //删除操作
                for (View view : removeCheckBoxs){
                    checkBox_layout.removeView(view);
                    //String name = view.getTag().toString();
                    //selectedCityDBManager.deleteCity(name);
                }
                break;
        }
    }



}
