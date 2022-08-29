package com.HnG.stock.mbos.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import androidx.fragment.app.Fragment;

import com.HnG.stock.mbos.R;
import com.HnG.stock.mbos.gettersetter.SKUMASTER;
import com.HnG.stock.mbos.helper.Log;
import com.google.gson.Gson;

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
    String hasSku = "";
    ArrayList<SKUMASTER> skumasters = new ArrayList<SKUMASTER>();
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
        if (Integer.parseInt(physicalqty_edt.getText().toString()) > Integer.parseInt(qty_edt.getText().toString())) {
            showAlertDialog("-Ve Quanity should not be less than actual Quantity");
        } else {

        }

    }

    public void showAlertDialog(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("HnGmBOS");
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
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
                    hasSku = String.valueOf(i);
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

    public void saveDetails_temp() {


        ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "",
                "Please wait..", true);
        progressDialog.show();

        String lastSavedSku = getDetails();


        try {
            JSONArray jsonArray = new JSONArray(lastSavedSku);
            int phyqty = 0;
//                Log.e("SKUMASTER", jsonArray.toString());
            ArrayList<SKUMASTER> temp = new ArrayList<>();

//                String hasSku = getPos(jsonArray);

            if (!hasSku.equals("")) {


                skumasters.clear();

                for (int k = 0; k < jsonArray.length(); k++) {
                    skumasters.add(new SKUMASTER(jsonArray.getJSONObject(k)));
                }

                int position = Integer.parseInt(hasSku);

//                    for (int i = 0; i < jsonArray.length(); i++) {
                temp.add(new SKUMASTER(jsonArray.getJSONObject(position)));
                phyqty = Integer.parseInt(jsonArray.getJSONObject(position).getString("physicalQty")) + Integer.parseInt(physicalqty_edt.getText().toString());
//                        phyqty += Integer.parseInt(physicalqty_edt.getText().toString());

                String sPhyqty = String.valueOf(phyqty);


                skumasters.set(position, new SKUMASTER(jsonArray.getJSONObject(position).getString("stockChkNo"), jsonArray.getJSONObject(position).getString("skuLOCNo"), jsonArray.getJSONObject(position).getString("skuCode"), jsonArray.getJSONObject(position).getString("skuName"), jsonArray.getJSONObject(position).getString("deviceNo"),
                        "", jsonArray.getJSONObject(position).getString("mrp"), "", sPhyqty,
                        "", jsonArray.getJSONObject(position).getString("eanCode"), jsonArray.getJSONObject(position).getString("bay_shelf_no"), jsonArray.getJSONObject(position).getString("location_code")));


                Gson gson = new Gson();
                String json = gson.toJson(skumasters);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("stock", "");
                editor.apply();
                editor.putString("stock", json);
                editor.apply();

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        progressDialog.dismiss();


        showAlertDialog("Details saved");
        clearFields();
//        lastsku_btn.performClick();

    }

    private void clearFields() {
        skucode_edt.getText().clear();
        batchcode_edt.getText().clear();
        tv_skuname.setText("");
        physicalqty_edt.getText().clear();
        qty_edt.getText().clear();
        skucode_edt.requestFocus();
    }


}
