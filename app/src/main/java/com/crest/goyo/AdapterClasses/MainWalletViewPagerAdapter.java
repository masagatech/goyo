package com.crest.goyo.AdapterClasses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.crest.goyo.fragment.CuponWalletFragment;
import com.crest.goyo.fragment.MyWalletFragment;


/**
 * Created by jasson on 7/12/16.
 */

public class MainWalletViewPagerAdapter extends FragmentStatePagerAdapter {
    int tabCount;

    public MainWalletViewPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MyWalletFragment myWalletFragment = new MyWalletFragment();
                return myWalletFragment;
            case 1:
                CuponWalletFragment cuponWalletFragment = new CuponWalletFragment();
                return cuponWalletFragment;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}