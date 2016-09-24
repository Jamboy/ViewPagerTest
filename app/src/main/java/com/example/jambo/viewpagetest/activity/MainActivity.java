package com.example.jambo.viewpagetest.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
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
import com.example.jambo.viewpagetest.db.SelectedCityDBManager;
import com.example.jambo.viewpagetest.mould.Weather;
import com.example.jambo.viewpagetest.util.HttpUtil;
import com.example.jambo.viewpagetest.util.WeatherList;
import java.util.ArrayList;
import java.util.List;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.example.jambo.viewpagetest.R.id.city;

public class MainActivity extends Activity
    implements NavigationView.OnNavigationItemSelectedListener, AMapLocationListener{
    private ViewPager mViewPager = null;
    private MainActivityAdapter mAdapter = null;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private RecyclerView mRecyclerView;
    private TextView mCityTV;
    private TextView mTimeTV;
    private Button mAddCity;
    private Observer<Weather> observer;
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private SharedPreferences mPreference;
    private WeatherAdapter mWeatherAdapter = null;
    private LinearLayout mFatherLinearLayout;
    private final String KEY = "1f93bec9ad304eb2ae641280bd65b9df";
    private List<Integer> list = new ArrayList();
    private SelectedCityDBManager selectedCityDBManager;
    private static final int LOCATION_REQUEST_CODE = 007;
    //SQLiteDatabase database = null;
    private boolean isLocation = false;
    private String location_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewId();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MainActivityAdapter();
        mViewPager.setAdapter(mAdapter);
        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        selectedCityDBManager = new SelectedCityDBManager(this);
        requestLocationPermission();
        loadCity();
        location_city = mPreference.getString("location_city","北京");
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST_CODE);
        }else{
            Toast.makeText(this,"hava permission",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    location();
                    isLocation = true;
                    Toast.makeText(this,"you get permission",Toast.LENGTH_LONG).show();
                //    权限被同意
                }else {
                    Toast.makeText(this,"you not get permission",Toast.LENGTH_LONG).show();
                //    被拒绝
                }
            }
        }
    }


    public void findViewId(){
        mNavigationView = (NavigationView)findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.city_manager:
                closeDrawerLayout();
                // TODO: 2016/9/23 把requestCode定义成静态
                startActivityForResult(new Intent(MainActivity.this,AddCityManager.class),2);
                break;
            case R.id.city_location:
                // TODO: 2016/9/24 这个方法块可以封装
                closeDrawerLayout();
                if (!selectedCityDBManager.isExisted(location_city)){
                    queryWeatherDataFromService(location_city);
                }else {
                    setCurrentPage(mAdapter.getViewForTag(location_city));
                }
                break;
            case R.id.about:
                removeView(mAdapter.getView(1));
                selectedCityDBManager.deleteCity("长沙");
                closeDrawerLayout();
                break;
            case R.id.activity_settings:
                closeDrawerLayout();
                Intent intent = new Intent(MainActivity.this,SearchCityActivity.class);
                startActivityForResult(intent,1);
                break;
            default:
                closeDrawerLayout();
        }
        return true;
    }

    public void initView(final String city_name, int city_id){
        LayoutInflater inflater = getLayoutInflater();
        View v0 = inflater.inflate(R.layout.show_weather_fragment,null);

        mFatherLinearLayout = (LinearLayout) v0.findViewById(R.id.father_linear_layout);
        mToolbar = (Toolbar) v0.findViewById(R.id.bar);
        mRecyclerView = (RecyclerView) v0.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mCityTV = (TextView) v0.findViewById(city);
        mTimeTV = (TextView) v0.findViewById(R.id.time);
        mAddCity = (Button) v0.findViewById(R.id.add_city);
        mAddCity.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchCityActivity.class);
                startActivityForResult(intent,1);
            }
        });

        ActionBarDrawerToggle mActionBar  = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open,R.string.close);
        mDrawerLayout.setDrawerListener(mActionBar);
        //ToDo 这里侧滑栏的点击接口是this也就当前
        /**
        mNavigationView.getMenu().add(R.id.city_group, Menu.NONE,Menu.NONE,"进入北京界面");

        对应Item的添加
        mNavigationView.getMenu().add(R.id.city_group, Menu.NONE,0,"进入北京界面").setIcon(R.drawable.place).setOnMenuItemClickListener(
            new MenuItem.OnMenuItemClickListener() {
                @Override public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(MainActivity.this,"you click me",Toast.LENGTH_SHORT).show();
                    closeDrawerLayout();
                    setCurrentPage(mAdapter.getView(1));
                    return true;
                }
            });
        获取到要删除的Item id 即可删除
        mNavigationView.getMenu().removeItem(R.id.location_now_city);
        */
/**
        for (int i = 0; i < list.size(); i++) {
            mNavigationView.getMenu()
                .add(R.id.city_group, list.get(i), Menu.NONE, city_name)
                .setIcon(R.drawable.place)
                .setOnMenuItemClickListener(
                    new MenuItem.OnMenuItemClickListener() {
                        @Override public boolean onMenuItemClick(MenuItem item) {
                            closeDrawerLayout();
                            int currentPageIndex = mPreference.getInt("current_page_index", 0);
                            setCurrentPage(mAdapter.getView(currentPageIndex));
                            return true;
                        }
                    });
        }
 */
        //Cursor cursor = database.query("CityManager",null,null,null,null,null,null);
        //if (cursor.moveToFirst()){
        //    do {
        //        String name = cursor.getString(cursor.getColumnIndex("name"));
        //        int id = cursor.getInt(cursor.getColumnIndex("cityId"));
        //        int index = cursor.getInt(cursor.getColumnIndex("pageIndex"));
        //        final int i =
                if (!city_name.equals(location_city)){
                mNavigationView.getMenu()
                    .add(R.id.city_group,city_id,Menu.NONE,city_name)
                    .setIcon(R.drawable.place)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override public boolean onMenuItemClick(MenuItem item) {
                            closeDrawerLayout();
                            // TODO: 2016/9/22 在这个方法里我唯一可以获取到的值是item.getTitle
                            //添加一个List  计数器，因为每菜单的Menu添加的顺序是和View添加顺序一致的，所以在检测到每当添加一个View的时候
                            //在写一个方法，根据Menu的name获取到对应的id并返回，然后再设置到CurrentPage
                            //然后每个
                            int itemId = item.getItemId();
                            String itemTitle = item.getTitle().toString();
                            //String pagerTitle = mAdapter.getPageTitle(1).toString();
                            //String pagerTag = mAdapter.getView(1).getTag().toString();
                            Log.d("MainActivity:getMenu","ItemId :" + itemId + "");
                            Log.d("MainActivity:getMenu","ItemTitle :" + itemTitle + "");
                            setCurrentPage(mAdapter.getViewForTag(city_name));
                            return true;
                        }
                    });
                }

        mActionBar.syncState();
        // TODO: 2016/9/18  这里可以将cityName及对应的pagerIndex传入数据库
        v0.setTag(city_name);
        int pagerIndex = addView(v0);
        Log.d("initView",pagerIndex + "");
        mAdapter.notifyDataSetChanged();

        int result = selectedCityDBManager.addCity(city_name,city_id);
        Log.d("MainActivity:initView",result + "");

    }


    public void queryWeatherDataFromService(final String city_name){
        //Log.d("city_name",city_name);
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
                        // TODO: 2016/9/19 如果在这里添加城市及id
                        int city_id = Integer.parseInt(weather.basic.id.replace("CN",""));
                        Log.d("onNext : weather.basic",city_id + "");
                        initView(city_name,city_id);
                        mCityTV.setText(weather.basic.city);
                        mTimeTV.setText(convertTime(weather.basic.update.loc));
                        Log.d("onNext",convertTime(weather.basic.update.loc));
                        mWeatherAdapter = new WeatherAdapter(MainActivity.this,weather);
                        mRecyclerView.setAdapter(mWeatherAdapter);
                        //ToDo add new method
                    }
                });
    }


    public String convertTime(String time){
        String [] times = time.split(" ");
        return times[1];
    }


    public int addView(View newPage){
        int pageIndex = mAdapter.addView(newPage);
        Log.d("pagerIndex",pageIndex + "");
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(pageIndex);
        //mPreference.edit().putInt("current_page_index",pageIndex).commit();
        return pageIndex;
    }


    // TODO: 2016/9/18   removeView改成传Position   mAdapter.removeView 将传View实例的方法删除即可
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
        if (isDrawerLayoutClose()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    public void onBackPressed() {
        closeDrawerLayout();
        super.onBackPressed();
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
                mPreference.edit().putString("location_city",location_city).apply();
                Log.d(location_city, "onLocationChanged:");
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" +
                        aMapLocation.getErrorInfo());
            }
        }
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    String SelectCity = data.getStringExtra("SelectCity");

                    if (!selectedCityDBManager.isExisted(SelectCity)) {
                        queryWeatherDataFromService(SelectCity);
                    } else {
                        setCurrentPage(mAdapter.getViewForTag(SelectCity));
                    }
                } else {
                    return;
                }
                break;
            case 2:
                if (data != null){
                    ArrayList<String> removeCities = data.getStringArrayListExtra("removeCities");
                    for (String name : removeCities){
                        int removeItemId = selectedCityDBManager.queryIdForName(name);
                        removeView(mAdapter.getViewForTag(name));
                        mNavigationView.getMenu().removeItem(removeItemId);
                        selectedCityDBManager.deleteCity(name);
                    }
                }else{
                    return;
                }
                break;
        }

            // TODO: 2016/9/19  在重构MainActivity的Layout后在此更新NavigationView的Item mNavigationView.getMenu.add();但是这里无法为每个Item添加唯一的id 传一个?
            // TODO: 2016/9/19 在添加城市时，将所选的index传过来？ 无法完成
            // TODO: 2016/9/22 先将传过来的城市与数据库中的进行匹对，若有则跳转到对应的View，没有则添加

            /**
             * 以上也就是我需要将查询所有城市的操作进行封装，
             * 然后逐一取出匹对。 最好是在数据中能存入一个与ViewPagerIndex相对应的id 比如自增长的Id与ViewPgaerIndex，
             *
             */
            //initView(SelectCity);

            //mFatherLinearLayout.setBackgroundColor(Color.BLUE);
    }


    //应用初始化时从数据库中查询城市并加载 （Q:每次打开程序都会去服务器把所有数据加载一遍，虽然是可以起到及时更新数据的作用，但每次都去加载是否会浪费流量呢）
    //这个更新功能的开启应该在设置里交给用户，而非在每次打开应用时重新加载，因为有时即使数据没有更新，也会重新加载一遍数据
    //怎样解决:是保存天气数据然后从本地加载呢？还是保存什么？
    //在OnCreate中加载所有城市时是无序的，每次添加View的顺序都不一样
    private void loadCity(){
        Cursor cursor = selectedCityDBManager.selectAllCity();
        if (cursor.moveToFirst()){
            do {
                String city_name = cursor.getString(cursor.getColumnIndex("name"));
  Log.d("MainActivity",city_name);
  int pager_index = cursor.getInt(cursor.getColumnIndex("pagerIndex"));
  //int city_id = cursor.getInt(cursor.getColumnIndex("cityId"));
                queryWeatherDataFromService(city_name);
  Log.d("MainActivity",pager_index + "");
  //Log.d("MainActivity",city_id + "");
            }while (cursor.moveToNext());
        }else {
            queryWeatherDataFromService("北京");
            cursor.close();
        }
    }
}
