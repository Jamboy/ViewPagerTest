package com.example.jambo.viewpagetest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import com.example.jambo.viewpagetest.R;

/**
 * Created by Jambo on 2016/8/9.
 */
public class AddCityManager extends Activity implements View.OnClickListener{
    private ImageButton add_city;
    private Button now_city_off;
    private CheckBox hengyang_rb;
    private CheckBox beiijing_ck;
    private ImageButton back_btn;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager);
        Log.d("AddCity","oncreate");
        initView();
    }

    public void initView(){
        add_city = (ImageButton) findViewById(R.id.city_manager_add_city);
        add_city.setOnClickListener(this);
        //now_city_off = (Button) findViewById(R.id.city_now_off);
        //now_city_off.setOnClickListener(this);
        //hengyang_rb = (CheckBox) findViewById(R.id.hengyang_ck);
        //hengyang_rb.setOnClickListener(this);
        //beiijing_ck = (CheckBox) findViewById(R.id.beijing_ischecked);
        //beiijing_ck.setOnClickListener(this);
        back_btn = (ImageButton) findViewById(R.id.city_manager_btn_back);
    }


    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.city_manager_btn_back:
                this.finish();
                break;
            case R.id.city_manager_add_city:
                Intent intent = new Intent(AddCityManager.this,SearchCityActivity.class);
                startActivity(intent);
                break;
            //case R.id.city_now_off:
            //    Toast.makeText(this,"you click city_now_off",Toast.LENGTH_SHORT).show();
            //    break;
            //case R.id.hengyang_ck:
            //    if (hengyang_rb.isChecked()){
            //        Toast.makeText(this,"you click hengyang and this is checked",Toast.LENGTH_SHORT).show();
            //    }
            //    break;
            //case R.id.beijing_ischecked:
            //    if (beiijing_ck.isChecked()){
            //        Toast.makeText(this,"you click beijing and this is checked",Toast.LENGTH_SHORT).show();
            //    }
            //    break;
        }

    }
}
