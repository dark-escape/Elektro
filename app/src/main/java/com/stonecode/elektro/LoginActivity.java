package com.stonecode.elektro;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class LoginActivity extends AppCompatActivity {


    private ViewPager mPager;
    private TabLayout mTabLayout;
    public static final int NUM_PAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        mPager = (ViewPager) findViewById(R.id.pager);
        mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        mPager.setAdapter(new SignInUpAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mPager);
    }


    private class SignInUpAdapter extends FragmentStatePagerAdapter {

        public SignInUpAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SignInFragment();
                case 1:
                    return new SignUpFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SIGN IN";
                case 1:
                    return "SIGN UP";
            }
            return super.getPageTitle(position);
        }
    }
}
