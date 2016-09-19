package com.example.jambo.viewpagetest.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import java.util.List;

/**
 * Created by Jambo on 2016/9/12.
 */
public class BaseActivity extends FragmentActivity {

    private static final String TAG = "BaseActivity";


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0){
            index --;
            if (fragmentManager.getFragments() == null || index < 0 || index >= fragmentManager.getFragments().size()){
                Log.w(TAG,"Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
                return;
            }
            Fragment fragment = fragmentManager.getFragments().get(index);
            if (fragment == null){
                Log.w(TAG,"Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
            }else {
                handleResult(fragment,requestCode,resultCode,data);
            }
        }
    }


    /**
     * 递归调用，对所有的子fragment生效
     * @param fragment
     * @param requestCode
     * @param resultCode
     * @param data
     */

    private void handleResult(Fragment fragment, int requestCode, int resultCode, Intent data){
        fragment.onActivityResult(requestCode & 0xffff,resultCode,data);
        List<Fragment> fragments = fragment.getChildFragmentManager().getFragments();
        if (fragments != null){
            for (Fragment fm : fragments){
                if (fm != null){
                    handleResult(fm,requestCode,resultCode,data);
                }
            }
        }
    }
}
