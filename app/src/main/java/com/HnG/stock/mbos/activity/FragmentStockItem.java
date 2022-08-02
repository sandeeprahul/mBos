package com.HnG.stock.mbos.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.HnG.stock.mbos.R;
import com.HnG.stock.mbos.database.UserDB;
import com.HnG.stock.mbos.gettersetter.SKUMASTER;
import com.HnG.stock.mbos.gettersetter.StockCheckData;
import com.HnG.stock.mbos.helper.ApiCall;
import com.HnG.stock.mbos.helper.AppController;
import com.HnG.stock.mbos.helper.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by mac on 7/24/18.
 */

public class FragmentStockItem extends Fragment {

    ArrayList<SKUMASTER> skumasters = new ArrayList<SKUMASTER>();
    ArrayList<SKUMASTER> skumasterArrayList = new ArrayList<SKUMASTER>();


    RelativeLayout price_rl;
    LinearLayout stockcheck_ll, stockdetails_ll, storedata_popup_ll;
    LinearLayout dowloadpopup_ll;
    EditText storestockcheck_edt, location_code_edt, device_no_edt;
    EditText eansku_edt, skuname_edt, shelfno_edt, physicalqty_edt;
    Button submit_btn, save_btn, clear_btn, exit_btn, lastsku_btn, search_btn, upload_btn;
    Button closepopup_btn;
    ListView prices_lv, stockno_lv;
    String storeip = "";
    ProgressDialog progressDialog;
    Spinner spinner;
    TextView price_tv, txtstatus;
    SharedPreferences sharedPreferences;
    List<String> prices = new ArrayList<String>();
    List<String> stockData = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    String SKU_LOC_NO,
            DAMAGED_QTY,
            EXPIRY_DATE,
            MRP,
            SKU_CODE,
            REF_BATCH,
            SKU_NAME,
            EAN_CODE;
    String localEanData,localSkuData;
    ArrayAdapter<String> arrayAdapterrStock;

    public static FragmentStockItem newInstance(int someInt) {
        FragmentStockItem myFragment = new FragmentStockItem();

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
        View view = inflater.inflate(R.layout.stocktake_item, container, false);

        prices_lv = (ListView) view.findViewById(R.id.prices_lv);
        stockno_lv = (ListView) view.findViewById(R.id.stockno_lv);
        price_tv = (TextView) view.findViewById(R.id.price_tv);
        txtstatus = (TextView) view.findViewById(R.id.txtstatus);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        dowloadpopup_ll = (LinearLayout) view.findViewById(R.id.dowloadpopup_ll);
        storedata_popup_ll = (LinearLayout) view.findViewById(R.id.storedata_popup_ll);
        stockdetails_ll = (LinearLayout) view.findViewById(R.id.stockdetails_ll);
        stockcheck_ll = (LinearLayout) view.findViewById(R.id.stockcheck_ll);
        price_rl = (RelativeLayout) view.findViewById(R.id.price_rl);
        eansku_edt = (EditText) view.findViewById(R.id.eansku_edt);
        shelfno_edt = (EditText) view.findViewById(R.id.shelfno_edt);
        physicalqty_edt = (EditText) view.findViewById(R.id.physicalqty_edt);
        skuname_edt = (EditText) view.findViewById(R.id.skuname_edt);
        device_no_edt = (EditText) view.findViewById(R.id.device_no_edt);
        location_code_edt = (EditText) view.findViewById(R.id.location_code_edt);
        storestockcheck_edt = (EditText) view.findViewById(R.id.storestockcheck_edt);
        submit_btn = (Button) view.findViewById(R.id.submit_btn);
        save_btn = (Button) view.findViewById(R.id.save_btn);
        clear_btn = (Button) view.findViewById(R.id.clear_btn);
        exit_btn = (Button) view.findViewById(R.id.exit_btn);
        lastsku_btn = (Button) view.findViewById(R.id.lastsku_btn);
        search_btn = (Button) view.findViewById(R.id.search_btn);
        upload_btn = (Button) view.findViewById(R.id.upload_btn);
        closepopup_btn = (Button) view.findViewById(R.id.closepopup_btn);


        ArrayAdapter<String> arrayAdapterr = new ArrayAdapter<String>(getActivity(), R.layout.item_pricelistview, R.id.textView, prices);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, prices);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(arrayAdapter);
        prices_lv.setAdapter(arrayAdapterr);


//         = new ArrayAdapter<String>(getActivity(), R.layout.item_pricelistview, R.id.textView, stockData);
        arrayAdapterrStock = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stockData);
        arrayAdapterrStock.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(arrayAdapter);
        stockno_lv.setAdapter(arrayAdapterrStock);


//        arrayAdapter.notifyDataSetChanged();

//        prices.add("Price");

        UserDB userDb = new UserDB(getActivity());
        userDb.open();
        HashMap<String, String> dataexist = userDb.getUserIP("");
        if (dataexist.isEmpty()) {
            //userDb.insertUserIP(ipaddress);
        } else {
            storeip = dataexist.get("ipaddress");
        }
        userDb.close();



 /*       new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 2 seconds
            }
        }, 2000);*/

        new getpreviousDetails().execute();
        new getLocalEanMaster().execute();
        new getLocalSkuMaster().execute();


        storedata_popup_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        price_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price_tv.performClick();
            }
        });
        closepopup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPrevSavedData();
//                storedata_popup_ll.setVisibility(View.GONE);
//                getStockDetails(storeip);
            }
        });


        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDetails(storeip);
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shelfno_edt.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_LONG).show();
                } else if (eansku_edt.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_LONG).show();
                } else if (skuname_edt.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_LONG).show();
                } else if (physicalqty_edt.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_LONG).show();
                } else if (prices.size() == 0) {
                    Toast.makeText(getActivity(), "Please fill all the details", Toast.LENGTH_LONG).show();
                } else {
                    saveDetails();

                }
            }
        });
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogExit("Are your sure?");
//                getActivity().finish();
//                try {
//                    ((StockActivityTemp) getActivity()).finishActivity();
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storestockcheck_edt.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter Stock check number", Toast.LENGTH_SHORT).show();

                } else if (location_code_edt.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter Location Code", Toast.LENGTH_SHORT).show();

                } else if (device_no_edt.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter Device number", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Please wait..", Toast.LENGTH_SHORT).show();

//                    findStockNoDetails();
                    getStockDetails(storeip);
                }

            }
        });
        lastsku_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastsku();
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                findStockNoDetails();
                if (!skuname_edt.getText().toString().equals("")) {
                    searchEan();
                } else {
                    Toast.makeText(getActivity(), "Please enter code to search", Toast.LENGTH_LONG).show();
                }
            }
        });

        eansku_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    fetchSKUData(eansku_edt.getText().toString());
                    EAN_CODE = eansku_edt.getText().toString();
                }
            }
        });


        price_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prices_lv.setVisibility(View.VISIBLE);
            }
        });


        prices_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), prices.get(position), Toast.LENGTH_SHORT).show();
                price_tv.setText(prices.get(position));
                prices_lv.setVisibility(View.GONE);

            }
        });
        /* spinner.setOnClickListener(price_tv.performClick());*/


      /*  storedata_popup_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storedata_popup_ll.setVisibility(View.GONE);
            }
        });*/

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                price_tv.setText(prices.get(position));

//                spinner.setSelection(position);
                Log.e("onItemSelected", "" + position);
                Object item = parent.getItemAtPosition(position);
                if (item != null) {
                    Toast.makeText(getContext(), prices.get(position), Toast.LENGTH_SHORT).show();

                }

                Toast.makeText(getContext(), "Selected", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("onNothingSelected", "onNothingSelected");

            }
        });

        stockno_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fillStockdata(position);
            }
        });


        return view;
    }

    public void clearPrevSavedData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("stock", "");
        editor.putString("stock", "");
        editor.apply();
        showAlertDialog("Previous data cleared");
        storedata_popup_ll.setVisibility(View.GONE);

    }


    public void fillStockdata(int position) {
        String sku = getDetails();
        try {

            //"damagedQty":"","deviceNo":"1","eanCode":"8908002760439","expiryDate":"","mrp":"125.00",
            // "physicalQty":"1","refBatch":"","skuCode":"531461",
            // "skuLOCNo":"54384","skuName":"HG DISPOSABLE RAZOR WOMEN PK 3","stockChkNo":"57"}
            JSONArray jsonArray = new JSONArray(sku);
            for (int i = 0; i < jsonArray.length(); i++) {
                skuname_edt.setText(jsonArray.getJSONObject(position).getString("skuName"));
                eansku_edt.setText(jsonArray.getJSONObject(position).getString("skuCode"));
                device_no_edt.setText(jsonArray.getJSONObject(position).getString("deviceNo"));
                physicalqty_edt.setText(jsonArray.getJSONObject(position).getString("physicalQty"));
                shelfno_edt.setText(jsonArray.getJSONObject(position).getString("bay_shelf_no"));
                storestockcheck_edt.setText(jsonArray.getJSONObject(position).getString("stockChkNo"));
                location_code_edt.setText(jsonArray.getJSONObject(position).getString("location_code"));
                price_tv.setText(jsonArray.getJSONObject(position).getString("mrp"));

                prices.add(jsonArray.getJSONObject(position).getString("mrp"));
                SKU_LOC_NO = jsonArray.getJSONObject(position).getString("stockChkNo");
                DAMAGED_QTY = jsonArray.getJSONObject(position).getString("damagedQty");
                EXPIRY_DATE = jsonArray.getJSONObject(position).getString("expiryDate");
                MRP = jsonArray.getJSONObject(position).getString("MRP");
                SKU_CODE = jsonArray.getJSONObject(position).getString("skuCode");
                EAN_CODE = jsonArray.getJSONObject(position).getString("ean");
                REF_BATCH = jsonArray.getJSONObject(position).getString("refBatch");
                SKU_NAME = jsonArray.getJSONObject(position).getString("skuName");


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        storedata_popup_ll.setVisibility(View.GONE);
        stockdetails_ll.setVisibility(View.VISIBLE);


    }

    public void fillSearchData(int position) {
        String sku = getDetails();
        try {

            //"damagedQty":"","deviceNo":"1","eanCode":"8908002760439","expiryDate":"","mrp":"125.00",
            // "physicalQty":"1","refBatch":"","skuCode":"531461",
            // "skuLOCNo":"54384","skuName":"HG DISPOSABLE RAZOR WOMEN PK 3","stockChkNo":"57"}
            JSONArray jsonArray = new JSONArray(sku);
            for (int i = 0; i < jsonArray.length(); i++) {
                skuname_edt.setText(jsonArray.getJSONObject(position).getString("skuName"));
                eansku_edt.setText(jsonArray.getJSONObject(position).getString("skuCode"));
                device_no_edt.setText(jsonArray.getJSONObject(position).getString("deviceNo"));
                physicalqty_edt.setText(jsonArray.getJSONObject(position).getString("physicalQty"));
                shelfno_edt.setText(jsonArray.getJSONObject(position).getString("bay_shelf_no"));
                storestockcheck_edt.setText(jsonArray.getJSONObject(position).getString("stockChkNo"));
                location_code_edt.setText(jsonArray.getJSONObject(position).getString("location_code"));
                price_tv.setText(jsonArray.getJSONObject(position).getString("mrp"));

                prices.add(jsonArray.getJSONObject(position).getString("mrp"));
                SKU_LOC_NO = jsonArray.getJSONObject(position).getString("stockChkNo");
                DAMAGED_QTY = jsonArray.getJSONObject(position).getString("damagedQty");
                EXPIRY_DATE = jsonArray.getJSONObject(position).getString("expiryDate");
                MRP = jsonArray.getJSONObject(position).getString("MRP");
                SKU_CODE = jsonArray.getJSONObject(position).getString("skuCode");
                EAN_CODE = jsonArray.getJSONObject(position).getString("ean");
                REF_BATCH = jsonArray.getJSONObject(position).getString("refBatch");
                SKU_NAME = jsonArray.getJSONObject(position).getString("skuName");


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        storedata_popup_ll.setVisibility(View.GONE);


    }

    private void getLastsku() {
        String sku = getDetails();
        Log.e("getLastsku", sku);
        try {

            //"damagedQty":"","deviceNo":"1","eanCode":"8908002760439","expiryDate":"","mrp":"125.00",
            // "physicalQty":"1","refBatch":"","skuCode":"531461",
            // "skuLOCNo":"54384","skuName":"HG DISPOSABLE RAZOR WOMEN PK 3","stockChkNo":"57"}
            JSONArray jsonArray = new JSONArray(sku);
            Log.e("JALastsku", jsonArray.getJSONObject(0).toString());
//            for (int i = 0; i < jsonArray.length(); i++) {
            skuname_edt.setText(jsonArray.getJSONObject(jsonArray.length() - 1).getString("skuName"));
            eansku_edt.setText(jsonArray.getJSONObject(jsonArray.length() - 1).getString("skuCode"));
            device_no_edt.setText(jsonArray.getJSONObject(jsonArray.length() - 1).getString("deviceNo"));
            physicalqty_edt.setText(jsonArray.getJSONObject(jsonArray.length() - 1).getString("physicalQty"));
            shelfno_edt.setText(jsonArray.getJSONObject(jsonArray.length() - 1).getString("bay_shelf_no"));
            storestockcheck_edt.setText(jsonArray.getJSONObject(jsonArray.length() - 1).getString("stockChkNo"));
            location_code_edt.setText(jsonArray.getJSONObject(jsonArray.length() - 1).getString("location_code"));
            price_tv.setText(jsonArray.getJSONObject(jsonArray.length() - 1).getString("mrp"));

            prices.add(jsonArray.getJSONObject(jsonArray.length() - 1).getString("mrp"));
            SKU_LOC_NO = jsonArray.getJSONObject(jsonArray.length() - 1).getString("stockChkNo");
            DAMAGED_QTY = jsonArray.getJSONObject(jsonArray.length() - 1).getString("damagedQty");
            EXPIRY_DATE = jsonArray.getJSONObject(jsonArray.length() - 1).getString("expiryDate");
            MRP = jsonArray.getJSONObject(jsonArray.length() - 1).getString("MRP");
            SKU_CODE = jsonArray.getJSONObject(jsonArray.length() - 1).getString("skuCode");
            EAN_CODE = jsonArray.getJSONObject(jsonArray.length() - 1).getString("ean");
            REF_BATCH = jsonArray.getJSONObject(jsonArray.length() - 1).getString("refBatch");
            SKU_NAME = jsonArray.getJSONObject(jsonArray.length() - 1).getString("skuName");


//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*SKUMASTER temp = skumasterArrayList.get(skumasterArrayList.size()-1);
        storestockcheck_edt.setText(temp.);*/

    }

    private void clearFields() {
        prices_lv.clearChoices();
        prices.clear();
//        device_no_edt.getText().clear();
//        storestockcheck_edt.getText().clear();
        eansku_edt.getText().clear();
        skuname_edt.getText().clear();
        physicalqty_edt.getText().clear();
//        location_code_edt.getText().clear();
        price_tv.setText("Price");
        shelfno_edt.getText().clear();
        SKU_CODE = "";
        EAN_CODE = "";
        SKU_LOC_NO = "";
        DAMAGED_QTY = "";
        EXPIRY_DATE = "";
        MRP = "";
        REF_BATCH = "";
        MRP = "";

    }

    @SuppressLint("StaticFieldLeak")
    public class getpreviousDetails extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            String hasJson = "0";
//            findStockNoDetails();
            stockData.clear();
            stockno_lv.clearChoices();
            if (getDetails() != null) {

                String sku = getDetails();
                try {
                    JSONArray jsonArray = new JSONArray(sku);
                    Log.e("JALastsku", jsonArray.getJSONObject(0).toString());
                    if (jsonArray.length() != 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stockData.add("Location code: " + jsonArray.getJSONObject(i).getString("stockChkNo") + " : SkuCode: " +
                                    jsonArray.getJSONObject(i).getString("skuCode"));
                       /* if (jsonArray.getJSONObject(i).getString("stockChkNo").equals(storestockcheck_edt.getText().toString())) {

                        }*/
                        }
                        arrayAdapterrStock.notifyDataSetChanged();
//                    storedata_popup_ll.setVisibility(View.VISIBLE);
//                    stockno_lv.setVisibility(View.VISIBLE);

                    }

                    if (stockData.size() > 0) {
//                        storedata_popup_ll.setVisibility(View.VISIBLE);
                        hasJson = "1";

//                    stockno_lv.setVisibility(View.VISIBLE);
                    } else {
                        Log.e("findStockNoDetails", "stockData.size()<0");

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
//            getStockDetails(storeip);
                Log.e("findStockNoDetails", "getStockDetails");

            }
            return hasJson;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("1")) {
                storedata_popup_ll.setVisibility(View.VISIBLE);

            }
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class getLocalEanMaster extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String value = "0";
            if (prefs.getString("eanmaster", null) != null || !prefs.getString("eanmaster", null).equals("")) {
                value = prefs.getString("eanmaster", null);
            }

            return value;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
           localEanData = s;
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class getLocalSkuMaster extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String value = "0";
            if (prefs.getString("skumaster", null) != null || !prefs.getString("skumaster", null).equals("")) {
                value = prefs.getString("skumaster", null);
            }

            return value;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            localSkuData = s;
            super.onPostExecute(s);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    public void saveDetails() {

        skumasters.add(new SKUMASTER(storestockcheck_edt.getText().toString(), SKU_LOC_NO, SKU_CODE, SKU_NAME, device_no_edt.getText().toString(),
                "", price_tv.getText().toString(), "", physicalqty_edt.getText().toString(),
                "", eansku_edt.getText().toString(), shelfno_edt.getText().toString(), location_code_edt.getText().toString()));

        Gson gson = new Gson();
        String json = gson.toJson(skumasters);
        Log.e("saveDetails()", json);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("stock", "");
        editor.putString("stock", json);
        editor.apply();
//        ArrayList<SKUMASTER> skumasterArrayList = new ArrayList<SKUMASTER>();
//        skumasterArrayList = getDetails();

//        getDetails();

        new getLocalEanMaster().execute();
        new getLocalSkuMaster().execute();

        showAlertDialogWithClosebtn("Details saved");
    }

    public String getDetails() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString("stock", null);
        /*Type type = new TypeToken<ArrayList<SKUMASTER>>() {
        }.getType();
        return gson.fromJson(json, type);*/

        return json;

    }


    private void uploadDetails(String storeip) {

        try {
            progressDialog = ProgressDialog.show(getActivity(), "",
                    "Please wait.. Saving details", true);
            progressDialog.show();


            final JSONObject jsonObject = new JSONObject();


            JSONArray hg_hht_headerArray = new JSONArray();

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("StkTakeNo", storestockcheck_edt.getText().toString());
            jsonObject1.put("Skulocstockno", SKU_LOC_NO);
            jsonObject1.put("Product_Code", eansku_edt.getText().toString());
            jsonObject1.put("Product_Name", SKU_NAME);
            jsonObject1.put("Active", "1");
            jsonObject1.put("MRP", price_tv.getText().toString());
            jsonObject1.put("Expiry_Date", "");
            jsonObject1.put("Physical_Qty", physicalqty_edt.getText().toString());
            jsonObject1.put("Damaged_Qty", "");
            jsonObject1.put("device_no", device_no_edt.getText().toString());
            jsonObject1.put("Ean_Code", EAN_CODE);

            hg_hht_headerArray.put(jsonObject1);

            jsonObject.put("result", "Success");
            jsonObject.put("hg_hht_header", hg_hht_headerArray);


            Log.e("Queryparams", jsonObject.toString());
//             previousData = hg_hht_headerArray.toString();

            final String mRequestBody = jsonObject.toString();


            RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
            String URL = storeip + "/StockCheck/hht_Response";


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {

                    try {
                        Log.i("saveDetails", "Api Call response: " + response);
                        JSONObject Jsonobj = new JSONObject(response);

                        if (Jsonobj.getString("result").equalsIgnoreCase("Success")) {
                            Toast.makeText(getActivity(), Jsonobj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();

                    } catch (Exception e) {

                        Log.d("TAG", "Exception Occured during login " + e.getLocalizedMessage());
                        e.printStackTrace();
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
//                    volleyError(error);
                    Log.e("onErrorResponse", error.toString());

                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    6000,
                    3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);

        } catch (Exception ex) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            ex.printStackTrace();
            Log.e("userLogin", ex.getMessage());
        }
    }


    private void getStockDetails(String storeip) {


      /*  progressDialog = ProgressDialog.show(getContext(), "",
                "Please wait ..\nDownloading EanMaster details", true);
        progressDialog.show();*/
        dowloadpopup_ll.setVisibility(View.VISIBLE);
        txtstatus.setText("Downloading EanMaster details");

        String apiUrl = storeip + "/StockCheck/get_eanmaster_for_hht?StockCheckNo=" + storestockcheck_edt.getText().toString() +
                "&DeviceNo=" + device_no_edt.getText().toString() +
                "&LocationCode=" + location_code_edt.getText().toString();
        Log.d("getStockDetails: ", apiUrl);
        ApiCall.make(getActivity(), apiUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getStockDetails: ", response);

//                        progressDialog.dismiss();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equals("Success")) {
                                Toast.makeText(getActivity(), "Please wait..\nDownloading SkuMaster details", Toast.LENGTH_SHORT).show();
                                //showAlert(jsonObject.getString("Message"));
                                JSONArray EanData = jsonObject.getJSONArray("eanmaster");
                               /* EAN_MASTER_DB eandb = new EAN_MASTER_DB(getActivity());
                                eandb.open();
                                eandb.deleteAllEAN();
                                eandb.insertBulkEANDetails(EanData);
                                eandb.close();*/
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("eanmaster", "");
                                editor.putString("eanmaster", EanData.toString());
                                editor.apply();
//
                                getSkuMaster(storeip);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        showAlertDialog(error.getMessage());
                        dowloadpopup_ll.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void getSkuMaster(String storeip) {

        /*progressDialog = ProgressDialog.show(getContext(), "",
                "Please wait ..\nDownloading SkuMaster details", true);
        progressDialog.show();*/
//        dowloadpopup_ll.setVisibility(View.VISIBLE);
        txtstatus.setText("Downloading SkuMaster details");

        String apiUrl = storeip + "/StockCheck/get_skumaster_for_hht?StockCheckNo=" + storestockcheck_edt.getText().toString() +
                "&DeviceNo=" + device_no_edt.getText().toString() +
                "&LocationCode=" + location_code_edt.getText().toString();
        Log.d("getSkuMaster: ", apiUrl);
        ApiCall.make(getActivity(), apiUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getSkuMaster: ", response);

//                        progressDialog.dismiss();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equalsIgnoreCase("Success")) {
                                //showAlert(jsonObject.getString("Message"));
                                JSONArray skumaster = jsonObject.getJSONArray("skumaster");

                              /*  SKU_MASTER_DB skutdb = new SKU_MASTER_DB(getActivity());
                                skutdb.open();
//                                skutdb.deleteMasterTables();
                                skutdb.insertBulkSKUMASTERData(skumaster);
                                skutdb.close();*/
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("skumaster", "");
                                editor.putString("skumaster", skumaster.toString());
                                editor.apply();
                                Toast toast = Toast.makeText(getActivity(), "Stock details downloaded successfully", Toast.LENGTH_SHORT);
                                toast.show();
                                dowloadpopup_ll.setVisibility(View.GONE);
//                                stockcheck_ll.setVisibility(View.GONE);
                                stockdetails_ll.setVisibility(View.VISIBLE);
//                                findStockNoDetails();
                            } else {
                                showAlertDialog(jsonObject.getString("result"));
                                Toast.makeText(getActivity(), jsonObject.getString("result").toString(), Toast.LENGTH_SHORT).show();
                                dowloadpopup_ll.setVisibility(View.GONE);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        showAlertDialog(error.getMessage());
                        dowloadpopup_ll.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    //hold
    public void findStockNoDetails() {

        stockData.clear();
        stockno_lv.clearChoices();
        if (getDetails() != null) {

            String sku = getDetails();
            try {
                JSONArray jsonArray = new JSONArray(sku);
                Log.e("JALastsku", jsonArray.getJSONObject(0).toString());
                if (jsonArray.length() != 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stockData.add("Location code: " + jsonArray.getJSONObject(i).getString("stockChkNo") + " ,SkuCode: " +
                                jsonArray.getJSONObject(i).getString("skuCode"));
                       /* if (jsonArray.getJSONObject(i).getString("stockChkNo").equals(storestockcheck_edt.getText().toString())) {

                        }*/
                    }
                    arrayAdapterrStock.notifyDataSetChanged();
//                    storedata_popup_ll.setVisibility(View.VISIBLE);
//                    stockno_lv.setVisibility(View.VISIBLE);

                }

                if (stockData.size() > 0) {
                    storedata_popup_ll.setVisibility(View.VISIBLE);

//                    stockno_lv.setVisibility(View.VISIBLE);
                } else {
                    Log.e("findStockNoDetails", "stockData.size()<0");

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
//            getStockDetails(storeip);
            Log.e("findStockNoDetails", "getStockDetails");

        }

    }

    public void searchEan() {
//        stockData.clear();
//        stockno_lv.clearChoices();
        String sku = getDetails();

        try {
            JSONArray jsonArray = new JSONArray(sku);
//            Log.e("JALastsku", jsonArray.getJSONObject(0).toString());
            if (jsonArray.length() != 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    if (jsonArray.getJSONObject(i).getString("eanCode").equals(eansku_edt.getText().toString())) {
                        fillSearchData(i);
                    }
                }
//                storedata_popup_ll.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    public void showAlertDialogWithClosebtn(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("HnGmBOS");
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                clearFields();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void showAlertDialogExit(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("HnGmBOS");
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                try {
                                    ((StockActivityTemp) getActivity()).finishActivity();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }


    public void fetchSKUData(String eanCode) {
        prices.clear();
        prices_lv.clearChoices();
        price_tv.setText("Price");
//        clear_btn.performClick();

        ProgressDialog progressDialog = ProgressDialog.show(getContext(), "",
                "Please wait ..", true);
        progressDialog.show();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        try {
            JSONArray jsonArray = new JSONArray(sharedPreferences.getString("skumaster", "0"));
            JSONArray jsonArrayEanMaster = new JSONArray(sharedPreferences.getString("eanmaster", "0"));

            for (int i = 0; i < jsonArrayEanMaster.length(); i++) {

                if (jsonArrayEanMaster.getJSONObject(i).getString("ean_code").equals(eanCode)) {
                    SKU_CODE = jsonArrayEanMaster.getJSONObject(i).getString("sku_code");
                }

            }
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("SKU_CODE").equals(SKU_CODE)) {
                    skuname_edt.setText(jsonArray.getJSONObject(i).getString("SKU_NAME"));
                    SKU_NAME = jsonArray.getJSONObject(i).getString("SKU_NAME");
                    prices.add(jsonArray.getJSONObject(i).getString("MRP"));
                    price_tv.setText(jsonArray.getJSONObject(i).getString("MRP"));
                    SKU_LOC_NO = jsonArray.getJSONObject(i).getString("SKU_LOC_NO");
                    DAMAGED_QTY = jsonArray.getJSONObject(i).getString("DAMAGED_QTY");
                    EXPIRY_DATE = jsonArray.getJSONObject(i).getString("EXPIRY_DATE");
                    MRP = jsonArray.getJSONObject(i).getString("MRP");
//                    SKU_CODE = jsonArray.getJSONObject(i).getString("SKU_CODE");
                    REF_BATCH = jsonArray.getJSONObject(i).getString("REF_BATCH");
                }
            }

            Log.e("EANCODE_SKUCODE", SKU_CODE + " " + EAN_CODE);
           /* if (SKU_CODE==null){
                Toast.makeText(getActivity(),"No details found for: "+SKU_CODE,Toast.LENGTH_LONG).show();
                showAlertDialog("No details found for: "+SKU_CODE);
            }
            if (EAN_CODE==null){
                Toast.makeText(getActivity(),"No details found for: "+EAN_CODE,Toast.LENGTH_LONG).show();
                showAlertDialog("No details found for: "+EAN_CODE);
            }*/

            progressDialog.dismiss();


        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }

    }

    public void saveStockLocDev() {
        try {
            String jso = getDetailsStoreCheck();
            JSONObject jsonObject = new JSONObject(jso);
            if (jsonObject.length() != 0) {

                ArrayList<StockCheckData> stockCheckDataArrayList = new ArrayList<>();
                stockCheckDataArrayList.add(new StockCheckData(jsonObject));
                if (jsonObject.getString("stockNo").equals(storestockcheck_edt.getText().toString())) {

              /*  if (jsonObject.getJSONArray("detailsArray").length()!=0){
                    ArrayList<SKUMASTER> arrayList = new ArrayList<>();
                    arrayList.add(new SKUMASTER(storestockcheck_edt.getText().toString(), SKU_LOC_NO, SKU_CODE, SKU_NAME, device_no_edt.getText().toString(),
                            "", price_tv.getText().toString(), "", physicalqty_edt.getText().toString(),
                            "", eansku_edt.getText().toString(),shelfno_edt.getText().toString(),location_code_edt.getText().toString()));

                }*/
                }
            }


            JSONObject stockJson = new JSONObject();
            JSONArray detailsArray = new JSONArray();

            stockJson.put("stockNo", storestockcheck_edt.getText().toString());
            stockJson.put("locationCode", location_code_edt.getText().toString());
            stockJson.put("deviceNo", device_no_edt.getText().toString());
            stockJson.put("detailsArray", detailsArray);
            Gson gson = new Gson();
            String json = gson.toJson(stockJson);
            Log.e("saveStockLocDev()", json);


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("stock", "");
            editor.putString("saveStockLocDev", json);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        ArrayList<SKUMASTER> skumasterArrayList = new ArrayList<SKUMASTER>();
//        skumasterArrayList = getDetails();

//        getDetails();
    }

    public String getDetailsStoreCheck() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString("saveStockLocDev", null);
        /*Type type = new TypeToken<ArrayList<SKUMASTER>>() {
        }.getType();
        return gson.fromJson(json, type);*/

        return json;

    }

}

//upload btn/confirm btn
//remove prev
//ean -> sku -> batch/price
//1-Aug
//new list details
//clear lst 4 fields on savebtn click
//exit alert
//looping issue
