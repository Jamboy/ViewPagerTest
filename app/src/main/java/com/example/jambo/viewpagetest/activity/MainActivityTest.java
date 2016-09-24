package com.example.jambo.viewpagetest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.jambo.viewpagetest.R;
import com.example.jambo.viewpagetest.adapter.MainActivityAdapter;

/**
 * Created by Jambo on 2016/9/19.
 * 将这个活动作为Layout重置后的测试类，
 * 测试NavigationView是否为全局共有
 * 测试ViewPager添加View后的效果
 * 测试动态添加城市时NavigationView的Item同步更新
 * 测试
 *
 */
public class MainActivityTest extends Activity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private MainActivityAdapter adapter;
    private TextView testCity;
    private Button addCity;
    private Toolbar toolbar;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_mainactivity_layout_test);
        initView();
        addViewToViewPager("onCreate");


    }

    private void initView(){
        navigationView = (NavigationView) findViewById(R.id.new_layout_test_navigation);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.new_drawerLayout_test);
        viewPager = (ViewPager) findViewById(R.id.new_layout_test_pager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        adapter = new MainActivityAdapter();
        viewPager.setAdapter(adapter);
    }


    private void addViewToViewPager(String name){
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.new_show_weather_fragment_test,null);
        addCity = (Button) view.findViewById(R.id.add_city);
        addCity.setOnClickListener(this);
        testCity = (TextView)view.findViewById(R.id.city);
        testCity.setText(name);
        adapter.addView(view);
        toolbar.setTitle(name);
        adapter.notifyDataSetChanged();
        navigationView.getMenu()
            .add(R.id.city_group, Menu.NONE,Menu.NONE,name)
            .setIcon(R.drawable.place)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override public boolean onMenuItemClick(MenuItem item) {
                    //这里可以通过item.getTitle获取到当前点击的城市名
                    //在这里我要完成的是点击跳转到对应的界面setCurrentPage(对应的pagerIndex)
                    //根据城市名去数据库查询到对应的pagerIndex传入？
                    //viewPager.setCurrentItem();
                    Toast.makeText(MainActivityTest.this,item.getTitle().toString(),Toast.LENGTH_LONG).show();
                    return true;
                }
            });
    }

    @Override public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                //测试NavigationView的侧滑菜单点击
                addViewToViewPager("About");
                Toast.makeText(this,"you click about",Toast.LENGTH_SHORT).show();
            break;
            case R.id.city_manager:
                addViewToViewPager("CITY_MANAGER");
                Toast.makeText(this,"you click city_manager",Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_settings:
                Toast.makeText(this,"you click Settings",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_city:
                Intent intent = new Intent(this,SearchCityActivity.class);
                startActivityForResult(intent,1);
                Toast.makeText(this,"you click add_city",Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            if (data != null){
                String SelectCity = data.getStringExtra("SelectCity");
                if (SelectCity.length() > 0){
                    addViewToViewPager(SelectCity);

                    Log.d("MainActivityTest",SelectCity);
                }
            }
        }
    }
}
