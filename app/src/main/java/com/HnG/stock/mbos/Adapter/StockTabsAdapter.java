package com.HnG.stock.mbos.Adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.HnG.stock.mbos.activity.FragmentBatch;
import com.HnG.stock.mbos.activity.FragmentQuantity;
import com.HnG.stock.mbos.activity.FragmentStockItem;
import com.HnG.stock.mbos.activity.StockActivityTemp;



/**
 * Created by mac on 3/18/17.
 */

public class StockTabsAdapter extends FragmentStatePagerAdapter {



    public FragmentStockItem fragmnet;

//    public NewDashBoardFragment sr_fragmnet;


    public StockTabsAdapter(FragmentManager fm, StockActivityTemp activity) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {


    switch (position){


        case 0:{

            fragmnet = FragmentStockItem.newInstance(position);
            return fragmnet;
        }


        case 1:{

            FragmentQuantity fragmentQuantity = FragmentQuantity.newInstance(position);
            return fragmentQuantity;

        }

        case 2:{


            FragmentBatch fragmentBatch = FragmentBatch.newInstance(position);
            return fragmentBatch;
        }

       /* case 2:{


            NotificationFragment notificationFragment = NotificationFragment.newInstance(position);
            return notificationFragment;
        }

        case 3:{
            SettingsFragment settingsFragment  = SettingsFragment.newInstance(position);
            return settingsFragment;
        }*/




        default: {
            fragmnet = FragmentStockItem.newInstance(position);
            return fragmnet;
        }


    }




    }

    @Override
    public int getCount() {
        return 2;
    }
}
