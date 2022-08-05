package com.HnG.stock.mbos.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.HnG.stock.mbos.Adapter.StockTabsAdapter;
import com.HnG.stock.mbos.R;
import com.google.android.material.tabs.TabLayout;

public class StockActivityTemp extends AppCompatActivity {
    ViewPager stock_viewpager;
    StockTabsAdapter stockTabsAdapter;
    TabLayout tablayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockcheck);

        stock_viewpager = (ViewPager) findViewById(R.id.stock_viewpager);
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        stockTabsAdapter = new StockTabsAdapter(getSupportFragmentManager(), this);
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                stock_viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        stock_viewpager.setOffscreenPageLimit(1);
        stock_viewpager.setAdapter(stockTabsAdapter);
//        stock_viewpager.set(false);

        stock_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                tablayout.setScrollPosition(position,0,false);
            }

            @Override
            public void onPageSelected(int position) {
//                tablayout.setScrollPosition(position,0,false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        tablayout.setupWithViewPager(stock_viewpager);


    }

    @Override
    public void onBackPressed() {
        finishActivity();
        super.onBackPressed();
    }

    public void finishActivity(){
        Intent intent  = new Intent(StockActivityTemp.this,MGINUploadInvoiceTest.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
