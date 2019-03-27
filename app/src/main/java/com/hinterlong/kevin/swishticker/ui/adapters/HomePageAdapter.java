package com.hinterlong.kevin.swishticker.ui.adapters;

import android.content.Context;

import com.hinterlong.kevin.swishticker.R;
import com.hinterlong.kevin.swishticker.ui.modules.HistoryFragment;
import com.hinterlong.kevin.swishticker.ui.modules.TeamsFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomePageAdapter extends FragmentPagerAdapter {
    private static final int titles[] = {R.string.title_history, R.string.title_teams};
    private final Context context;

    public HomePageAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(titles[position]);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return new HistoryFragment();
            case 1:
                return new TeamsFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
