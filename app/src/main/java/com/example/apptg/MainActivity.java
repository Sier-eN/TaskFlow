package com.example.apptg;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import Adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView btnavview;
    private ViewPager viewPager;

    // ánh xạ menuId <-> page
    private final Map<Integer, Integer> menuToPage = new HashMap<>();
    private final Map<Integer, Integer> pageToMenu = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhxa();
        initMaps();
        setUpViewPager();

        // BottomNavigation click
        btnavview.setOnNavigationItemSelectedListener(item -> {
            Integer page = menuToPage.get(item.getItemId());
            if (page != null) {
                viewPager.setCurrentItem(page, false); // vuốt mượt, không animation
                return true;
            }
            return false;
        });

        // Sync ViewPager -> BottomNavigation
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Integer menuId = pageToMenu.get(position);
                if (menuId != null) {
                    btnavview.setSelectedItemId(menuId);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        // set mặc định ở Home
        viewPager.setCurrentItem(menuToPage.get(R.id.home), false);
        btnavview.setSelectedItemId(R.id.home);
    }

    private void anhxa() {
        btnavview = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.view_pager);
    }

    private void initMaps() {
        // menuId -> page
        menuToPage.put(R.id.home, 0);
        menuToPage.put(R.id.baothuc, 1);
        menuToPage.put(R.id.thoigianbieu, 2);
//        menuToPage.put(R.id.themtgb, 3);

        // page -> menuId
        pageToMenu.put(0, R.id.home);
        pageToMenu.put(1, R.id.baothuc);
        pageToMenu.put(2, R.id.thoigianbieu);
//        pageToMenu.put(3, R.id.themtgb);
    }

    private void setUpViewPager() {
        ViewPagerAdapter viewPagerAdapter =
                new ViewPagerAdapter(getSupportFragmentManager(),
                        FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager.setAdapter(viewPagerAdapter);

        // giữ sẵn 3 fragment bên trái/phải để vuốt mượt
        viewPager.setOffscreenPageLimit(3);
    }
}
