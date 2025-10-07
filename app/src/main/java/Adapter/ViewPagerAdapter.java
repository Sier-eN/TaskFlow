package Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import Fragments.BamGioFragment;
import Fragments.BaothucFragment;
import Fragments.DanhSachTGBFragment;
import Fragments.HenGioFragment;
import Fragments.HomeFragment;
import Fragments.ThemTGBFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new BaothucFragment();
            case 2:
                return new HenGioFragment();
            case 3:
                return new BamGioFragment();
            case 4:
                return new DanhSachTGBFragment();
            case 5:
                return new ThemTGBFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}
