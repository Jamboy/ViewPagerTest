package com.example.jambo.viewpagetest.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jambo.viewpagetest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/6/2.
 */
public class PageOneFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pager_content_one,container,false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.black);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListView.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,getData()));
        return view;
    }


    public List<String> getData(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 14; i++){
            list.add("" + i);
        }
        return list;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        },3000);
    }
}
