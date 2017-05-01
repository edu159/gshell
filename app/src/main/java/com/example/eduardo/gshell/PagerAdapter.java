package com.example.eduardo.gshell;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private Server server;
    public TabFragment1 file_explorer_fragment;
    public TabFragment2 job_monitor_fragment;
    public TabFragment3 terminal_fragment;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Server server) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.server = server;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                file_explorer_fragment = new TabFragment1(this.server);
                return file_explorer_fragment;
            case 1:
                job_monitor_fragment = new TabFragment2();
                return job_monitor_fragment;
            case 2:
                terminal_fragment = new TabFragment3();
                return terminal_fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}