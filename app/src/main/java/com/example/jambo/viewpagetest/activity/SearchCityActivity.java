package com.example.jambo.viewpagetest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import com.example.jambo.viewpagetest.R;
import com.example.jambo.viewpagetest.db.CityQuery;
import com.example.jambo.viewpagetest.db.DBManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/6/7.
 */
public class SearchCityActivity extends FragmentActivity implements SearchView.OnCloseListener,
    SearchView.OnQueryTextListener,View.OnClickListener,ListView.OnItemClickListener{

    private ImageButton back_btn;
    private SearchView searchView;
    private ListView listView;
    private DBManager dbManager;
    private CityQuery cityQuery;
    private ArrayAdapter<String> mAdatpter;
    private List<String> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serachview);
        dbManager = new DBManager(this);
        dbManager.openDatabase();
        cityQuery = new CityQuery(this);
        init();
    }


    public void init(){
        back_btn = (ImageButton) findViewById(R.id.btn_back);
        back_btn.setOnClickListener(this);
        searchView = (SearchView) findViewById(R.id.search_view);
        listView = (ListView) findViewById(R.id.auto_list_view);

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);


        //Todo 设置进入这个活动时，将焦点默认放在searchview的输入框中。
        //searchView.requestFocus();
        //searchView.onActionViewExpanded();
        searchView.setFocusable(true);
        searchView.setQueryHint("请输入要查询的城市");
        List<String> city = cityQuery.getCursor(dbManager.getDatabase());
        if (city.size() > 0){
            for (String cityName : city){
                dataList.add(cityName);
            }
            mAdatpter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,dataList);
        }
        listView.setAdapter(mAdatpter);
        listView.setOnItemClickListener(this);
        listView.setTextFilterEnabled(true);
    }



    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                this.finish();
                break;
        }
    }


    @Override public boolean onClose() {
        if (searchView.getQuery().length() > 0){
            searchView.setQuery("",false);
        }else {
            this.finish();
        }
        return false;
    }


    @Override public boolean onQueryTextSubmit(String query) {
        //点击搜索按钮时激发此方法
        // ToDo 点击搜索时跳转回MainActivity并将选取的CityName传过去
        //Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("SelectCity",query);
        setResult(1,intent);
        this.finish();
        return true;
    }


    @Override public boolean onQueryTextChange(String newText) {
                //if (TextUtils.isEmpty(newText)){
                //    listView.clearTextFilter();
                //}else {
                //    listView.setFilterText(newText);
                //}
        List<String> list = cityQuery.queryByLike(dbManager.getDatabase(),newText);
        if (list.size() > 0){
            dataList.clear();
            for (String city : list){
                dataList.add(city);
            }
            mAdatpter.notifyDataSetChanged();
        }
        return true;
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        dbManager.closeDatabase();
    }


    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedCity = (String)listView.getItemAtPosition(position);
            searchView.setQuery(selectedCity,true);
    }
}
