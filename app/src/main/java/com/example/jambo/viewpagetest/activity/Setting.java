package com.example.jambo.viewpagetest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.jambo.viewpagetest.R;

/**
 * Created by Jambo on 2016/6/7.
 */
public class Setting extends Activity implements View.OnClickListener {

    private Button add_city;
    private Button city_now;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager);
        init();
    }


    public void init(){
        add_city = (Button) findViewById(R.id.button_city_add);
        city_now = (Button) findViewById(R.id.button_city_now);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_city_add:
//              弹出输入框可检索  添加对应城市 并处理数据？是在这里处理？
                break;
            case R.id.button_city_now:
//              跳转到当前定位的城市界面
                break;
        }
    }
}
