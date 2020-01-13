package de.hpi3d.gamepgrog.trap.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import de.hpi3d.gamepgrog.trap.R;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};

    private static DisplayableListFragment clueFragment;
    private static DisplayableListFragment taskFragment;
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

        clueFragment = new DisplayableListFragment();
        Bundle clueBundle = new Bundle();
        clueBundle.putString(DisplayableListFragment.KEY_WHAT_TO_DISPLAY, DisplayableListFragment.DISPLAY_CLUES);
        clueFragment.setArguments(clueBundle);

        taskFragment = new DisplayableListFragment();
        Bundle taskBundle = new Bundle();
        taskBundle.putString(DisplayableListFragment.KEY_WHAT_TO_DISPLAY, DisplayableListFragment.DISPLAY_TASKS);
        taskFragment.setArguments(taskBundle);

    }

    @Override
    public Fragment getItem(int position) {
        if (position % 2 == 0) {
            return clueFragment;
        } else {
            return taskFragment;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}