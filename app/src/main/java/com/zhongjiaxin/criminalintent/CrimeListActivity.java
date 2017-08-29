package com.zhongjiaxin.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by zhaokaiwen on 2017/8/24.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
