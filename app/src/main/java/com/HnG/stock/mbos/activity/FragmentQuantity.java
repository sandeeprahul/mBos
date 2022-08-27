package com.HnG.stock.mbos.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.HnG.stock.mbos.R;
import com.HnG.stock.mbos.gettersetter.SKUMASTER;
import com.HnG.stock.mbos.helper.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created by mac on 7/24/18.
 */

public class FragmentQuantity extends Fragment {

    EditText skucode_edt, batchcode_edt, physicalqty_edt, qty_edt;
    Button search_btn, update_btn, clear_btn;
    ListView lv_data;
    TextView tv_skuname;
    ProgressDialog progressDialog;
    ArrayList<SKUMASTER> skumasterArrayList = new ArrayList<>();


    public static FragmentQuantity newInstance(int someInt) {
        FragmentQuantity myFragment = new FragmentQuantity();

        Bundle args = new Bundle();
        args.putInt("someInt", someInt);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.quantity_item, container, false);

        skucode_edt = (EditText) view.findViewById(R.id.skucode_edt);
        batchcode_edt = (EditText) view.findViewById(R.id.batchcode_edt);
        physicalqty_edt = (EditText) view.findViewById(R.id.physicalqty_edt);
        qty_edt = (EditText) view.findViewById(R.id.qty_edt);
        search_btn = (Button) view.findViewById(R.id.search_btn);
        update_btn = (Button) view.findViewById(R.id.update_btn);
        clear_btn = (Button) view.findViewById(R.id.clear_btn);
        lv_data = (ListView) view.findViewById(R.id.lv_data);
        tv_skuname = (TextView) view.findViewById(R.id.tv_skuname);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skucode_edt.getText().toString().equals("")) {
                    customToast("Please enter Sku Code");
                } else if (batchcode_edt.getText().toString().equals("")) {
                    customToast("Please enter MRP");
                } else {
                    findDetails();
                }
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skucode_edt.getText().toString().equals("")) {
                    customToast("Please enter Sku Code");
                } else if (batchcode_edt.getText().toString().equals("")) {
                    customToast("Please enter MRP");
                } else if (physicalqty_edt.getText().toString().equals("")) {
                    customToast("Please enter Quantity");
                } else {
                    updateqty();
                }
            }
        });


        return view;
    }

    public void updateqty() {

    }

    public void findDetails() {
        Log.e("findDetails", skucode_edt.getText().toString() + ", " + batchcode_edt.getText().toString());
        String json = getDetails();

        try {
            JSONArray jsonArray = new JSONArray(json);
            ArrayList<SKUMASTER> temp = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                temp.add(new SKUMASTER(jsonArray.getJSONObject(i)));

                if (skucode_edt.getText().toString().equals(jsonArray.getJSONObject(i).getString("skuCode")) && batchcode_edt.getText().toString().equals(jsonArray.getJSONObject(i).getString("mrp"))) {
                    tv_skuname.setText(jsonArray.getJSONObject(i).getString("skuName"));
                    qty_edt.setText(jsonArray.getJSONObject(i).getString("physicalQty"));
                }
            }
            skumasterArrayList = temp;

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    String json = "";

    public String getDetails() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        json = prefs.getString("stock", "");
        /*Type type = new TypeToken<ArrayList<SKUMASTER>>() {
        }.getType();
        return gson.fromJson(json, type);*/

        return json;

    }

    public void customToast(String msg) {
        Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.custom_background);
        toast.show();
    }


}
