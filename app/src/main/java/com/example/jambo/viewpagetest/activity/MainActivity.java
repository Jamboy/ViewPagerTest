package com.example.jambo.viewpagetest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.jambo.viewpagetest.R;
import com.example.jambo.viewpagetest.adapter.MainActivityAdapter;
import com.example.jambo.viewpagetest.adapter.WeatherAdapter;
import com.example.jambo.viewpagetest.mould.Weather;
import com.example.jambo.viewpagetest.util.HttpUtil;
import com.example.jambo.viewpagetest.util.WeatherList;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends FragmentActivity implements NavigationView.OnNavigationItemSelectedListener, AMapLocationListener{
    private ViewPager mViewPager = null;
    private MainActivityAdapter mAdapter = null;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private RecyclerView mRecyclerView;
    private TextView mCityTV;
    private TextView mTimeTV;
    private Observer<Weather> observer;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private Boolean isLocation = false;
    private SharedPreferences mPreference;
    private WeatherAdapter mWeatherAdapter;
    private LinearLayout mFatherLinearLayout;

    private final String KEY = "1f93bec9ad304eb2ae641280bd65b9df";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MainActivityAdapter();
        mViewPager.setAdapter(mAdapter);
        initView();
        location();
        queryWeatherDataFromService("北京");
        addCity();
    }


    /**
     * 一个添加ViewItem的方法
     */

    public void addCity(){
        initView();
        queryWeatherDataFromService("长沙");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.city_manager:
                initView();
                break;
            case R.id.about:
                Toast.makeText(MainActivity.this,"About me?", Toast.LENGTH_SHORT).show();
                String city = mPreference.getString("location_city","北京");
                queryWeatherDataFromService(city);
                Log.d("city",city);
                break;
            case R.id.activity_settings:
//                removeView(getCurrentPage());
                startActivity(new Intent(MainActivity.this,Setting.class));
                break;
            case R.id.beijing:
                startActivity(new Intent(MainActivity.this,test.class));
                break;
        }
        closeDrawerLayout();
        return true;
    }

    public void initView(){
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        LayoutInflater inflater = getLayoutInflater();
        View v0 = inflater.inflate(R.layout.show_weather_fragment,null);

        mFatherLinearLayout = (LinearLayout) v0.findViewById(R.id.father_linear_layout);
        mToolbar = (Toolbar) v0.findViewById(R.id.bar);
        mDrawerLayout = (DrawerLayout) v0.findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) v0.findViewById(R.id.navigation_view);

        mRecyclerView = (RecyclerView) v0.findViewById(R.id.recycler_view);
        mCityTV = (TextView) v0.findViewById(R.id.city);
        mTimeTV = (TextView) v0.findViewById(R.id.time);

        mFatherLinearLayout.setBackgroundResource(R.drawable.hua);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mWeatherAdapter);
        ActionBarDrawerToggle mActionBar  = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open,R.string.close);
        mDrawerLayout.setDrawerListener(mActionBar);
        mNavigationView.setNavigationItemSelectedListener(this);
        mActionBar.syncState();

        addView(v0);
        mAdapter.notifyDataSetChanged();
    }


    public void queryWeatherDataFromService(String city_name){
        Log.d("city_name",city_name);
        HttpUtil.getWeatherApi().getWeather(city_name,KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<WeatherList, Weather>() {
                    @Override
                    public Weather call(WeatherList weatherList) {
                        return weatherList.mWeathers.get(0);
                    }
                })
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        Log.d("queryDate", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError",e.getMessage());
                    }

                    @Override
                    public void onNext(Weather weather) {
                        Log.d("onNext",weather.basic.city);
//                        WeatherAdapter mAdapter = new WeatherAdapter(MainActivity.this,weather);
                        mCityTV.setText(weather.basic.city);
                        mTimeTV.setText(convertTime(weather.basic.update.loc));
                        Log.d("onNext",convertTime(weather.basic.update.loc));
                        WeatherAdapter mWeatherAdapter = new WeatherAdapter(MainActivity.this,weather);
                        mRecyclerView.setAdapter(mWeatherAdapter);
                        //ToDo add new method
                    }
                });
    }


    public String convertTime(String time){
        String [] times = time.split(" ");
        return times[1];
    }




    public void addView(View newPage){
        int pageIndex = mAdapter.addView(newPage);
        Log.d("pagerIndex",pageIndex + "");
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(pageIndex);
    }


    public void removeView(View defuntPage){
        int pagerIndex = mAdapter.removeView(mViewPager,defuntPage);
        if (pagerIndex == mAdapter.getCount()){
            pagerIndex--;
        }
        mViewPager.setCurrentItem(pagerIndex);
    }

    public View getCurrentPage(){
        return mAdapter.getView(mViewPager.getCurrentItem());
    }

    public void setCurrentPage(View pageToShow){
        mViewPager.setCurrentItem(mAdapter.getItemPosition(pageToShow),true);
    }


    public boolean isDrawerLayoutClose(){
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }


    public void closeDrawerLayout(){
        if (mDrawerLayout != null){
            mDrawerLayout.closeDrawers();
        }
    }

    @Override
    public void onBackPressed() {
            if (isDrawerLayoutClose()){
                closeDrawerLayout();
                Log.d("close","close");
            }else {
                super.onBackPressed();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔 单位毫秒
        mLocationOption.setInterval(24 * 3600 * 1000);
        //给定位客户端对象设置定位参 数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                //aMapLocation.getLatitude();//获取纬度
                //aMapLocation.getLongitude();//获取经度
                //aMapLocation.getAccuracy();//获取精度信息
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //Date date = new Date(aMapLocation.getTime());
                //df.format(date);//定位时间
                //aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                //aMapLocation.getCountry();//国家信息
                //aMapLocation.getProvince();//省信息
                //aMapLocation.getCity();//城市信息
                //aMapLocation.getDistrict();//城区信息
                //aMapLocation.getStreet();//街道信息
                //aMapLocation.getStreetNum();//街道门牌号信息
                //aMapLocation.getCityCode();//城市编码
                //aMapLocation.getAdCode();//地区编码
                String location_city = aMapLocation.getCity().replace("市", "")
                        .replace("省", "")
                        .replace("土家族苗族自治州", "")
                        .replace("自治区", "")
                        .replace("特别行政区", "")
                        .replace("地区", "")
                        .replace("盟", "");
                mPreference.edit().putString("location_city", location_city).commit();
                isLocation = true;
                Log.d(location_city, "当前城市");
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" +
                        aMapLocation.getErrorInfo());
            }
        }
    }
}
