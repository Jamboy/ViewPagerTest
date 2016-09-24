package com.example.jambo.viewpagetest.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jambo on 2016/6/2.
 */
public class MainActivityAdapter extends PagerAdapter {
    // TODO: 2016/9/22 最好是能写一个根据View的Title来删除视图的方法 

    List<View> mViewList = new ArrayList<>();

    /**
     * Called when the host view is attempting to determine if an item's position has changed
     * 这个方法是用来显示当前的页面，若当前view不存在则return POSITION_NONE，否则return 当前页面
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        int index = mViewList.indexOf(object);
        if (index == -1){
            return POSITION_NONE;
        }else
            return index;
    }

    /**
     *当ViewPager要显示界面的时候调用
     * 在容器中添加页面
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }

    /**
     * 在容器中删除页面
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    /**
     * 获取ViewPager页面的数量。
     * @return
     */

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 传入一个新的View
     * 然后再调用addView(将其添加值mViewList)
     * @param view
     * @return
     */
    public int addView(View view){
        return addView(view,mViewList.size());
    }

    public int addView(View view, int position){
        mViewList.add(position,view);
        return position;
    }

    /**
     * 将ViewPager的adpter设为空，然后再在mViewlist中删除此view后，
     * 再重设viewpager的adapter则不包含已删除的View
     * @param pager
     * @param view
     * @return
     */
    public int removeView(ViewPager pager, View view){
        return removeView(pager, mViewList.indexOf(view));
    }

    public int removeView(ViewPager pager, int position){
        pager.setAdapter(null);
        mViewList.remove(position);
        pager.setAdapter(this);

        return position;
    }


    public View getView(int position){
        return mViewList.get(position);
    }

    // TODO: 2016/9/22 根据Tag返回对应View 遍历所有的View的Tag 配对到的返回 然后Return

    public View getViewForTag(String tag){
        for(int i = 0; i < mViewList.size(); i++){
            if (mViewList.get(i).getTag().toString().equals(tag)){
                   return mViewList.get(i);
            }
        }
        return mViewList.get(0);
    }

}
