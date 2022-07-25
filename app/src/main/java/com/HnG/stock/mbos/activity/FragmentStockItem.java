package com.HnG.stock.mbos.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.HnG.stock.mbos.R;
import com.HnG.stock.mbos.database.EANDB;
import com.HnG.stock.mbos.database.EAN_MASTER_DB;
import com.HnG.stock.mbos.database.InvDB;
import com.HnG.stock.mbos.database.MASTER_DATA;
import com.HnG.stock.mbos.database.SKU_MASTER_DB;
import com.HnG.stock.mbos.database.UserDB;
import com.HnG.stock.mbos.gettersetter.InvoiceDetails;
import com.HnG.stock.mbos.gettersetter.MasterTable;
import com.HnG.stock.mbos.gettersetter.SKUMASTER;
import com.HnG.stock.mbos.helper.ApiCall;
import com.HnG.stock.mbos.helper.AppController;
import com.HnG.stock.mbos.helper.BaseActivity;
import com.HnG.stock.mbos.helper.Constants;
import com.HnG.stock.mbos.helper.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by mac on 7/24/18.
 */

public class FragmentStockItem extends Fragment {

    LinearLayout stockcheck_ll, stockdetails_ll;
    EditText storestockcheck_edt, location_code_edt, device_no_edt;
    EditText eansku_edt, skuname_edt, shelfno_edt, physicalqty_edt;
    Button submit_btn, save_btn,clear_btn;
    ListView prices_lv;
    String storeip = "";
    ProgressDialog progressDialog;
    Spinner spinner;
    private String skuCode;
    TextView price_tv;
    SharedPreferences sharedPreferences;
    List<String> prices = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    String SKU_LOC_NO,
            DAMAGED_QTY,
            EXPIRY_DATE,
            MRP,
            SKU_CODE,
            REF_BATCH,
            SKU_NAME;

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
        price_tv = (TextView) view.findViewById(R.id.price_tv);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        stockdetails_ll = (LinearLayout) view.findViewById(R.id.stockdetails_ll);
        stockcheck_ll = (LinearLayout) view.findViewById(R.id.stockcheck_ll);
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


        ArrayAdapter<String> arrayAdapterr = new ArrayAdapter<String>(getActivity(), R.layout.item_pricelistview, R.id.textView, prices);

        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, prices);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(arrayAdapter);
        prices_lv.setAdapter(arrayAdapterr);
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

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails(storeip);
            }
        });
        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prices.clear();
                device_no_edt.getText().clear();
                storestockcheck_edt.getText().clear();
                eansku_edt.getText().clear();
                skuname_edt.getText().clear();
                physicalqty_edt.getText().clear();
                physicalqty_edt.getText().clear();
                price_tv.setText("");
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storestockcheck_edt.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter Stock check number", Toast.LENGTH_SHORT).show();

                } else if (device_no_edt.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Enter Device number", Toast.LENGTH_SHORT).show();

                } else {
                    getStockDetails(storeip);

                }

            }
        });

        eansku_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    fetchSKUData(eansku_edt.getText().toString());
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


        return view;
    }


    private void saveDetails(String storeip) {

        try {
            progressDialog = ProgressDialog.show(getActivity(), "",
                    "Logging In.. Please wait..", true);
            progressDialog.show();


            final JSONObject jsonObject = new JSONObject();


            JSONArray hg_hht_headerArray = new JSONArray();

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("StkTakeNo", storestockcheck_edt.getText().toString());
            jsonObject1.put("Skulocstockno",SKU_LOC_NO );
            jsonObject1.put("Product_Code", eansku_edt.getText().toString());
            jsonObject1.put("Product_Name", SKU_NAME);
            jsonObject1.put("Active", "1");
            jsonObject1.put("MRP", prices.get(0));
            jsonObject1.put("Expiry_Date", "");
            jsonObject1.put("Physical_Qty", "");
            jsonObject1.put("Damaged_Qty", "");
            jsonObject1.put("device_no", device_no_edt.getText().toString());
            jsonObject1.put("Ean_Code", "");

            hg_hht_headerArray.put(jsonObject1);

            jsonObject.put("result", "Success");
            jsonObject.put("hg_hht_header", hg_hht_headerArray);

            Log.e("Queryparams",jsonObject.toString());

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
                            Toast.makeText(getActivity(),Jsonobj.getString("message"),Toast.LENGTH_LONG).show();
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


        progressDialog = ProgressDialog.show(getContext(), "",
                "Please wait ..\nDownloading stock Details", true);
        progressDialog.show();

        String apiUrl = storeip + "/StockCheck/get_eanmaster_for_hht?StockCheckNo=" + storestockcheck_edt.getText().toString() + "&DeviceNo=" + device_no_edt.getText().toString();
        Log.d("getStockDetails: ", apiUrl);
        ApiCall.make(getActivity(), apiUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getStockDetails: ", response);

                        progressDialog.dismiss();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equalsIgnoreCase("Success")) {
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

                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                        showErrorType(error);
                    }
                }
        );
    }

    private void getSkuMaster(String storeip) {

        progressDialog = ProgressDialog.show(getContext(), "",
                "Please wait ..\nDownloading stock Details", true);
        progressDialog.show();

        String apiUrl = storeip + "/StockCheck/get_skumaster_for_hht?StockCheckNo=" + storestockcheck_edt.getText().toString() + "&DeviceNo=" + device_no_edt.getText().toString();
        Log.d("getSkuMaster: ", apiUrl);
        ApiCall.make(getActivity(), apiUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("getSkuMaster: ", response);

                        progressDialog.dismiss();

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
                                stockcheck_ll.setVisibility(View.GONE);
                                stockdetails_ll.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivity(), jsonObject.getString("result").toString(), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                        showErrorType(error);
                    }
                }
        );
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

    public void fetchSKUData(String eanCode) {

        ProgressDialog progressDialog = ProgressDialog.show(getContext(), "",
                "Please wait ..", true);
        progressDialog.show();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ArrayList<SKUMASTER> skuDataList = new ArrayList<SKUMASTER>();

        try {
            JSONArray jsonArray = new JSONArray(sharedPreferences.getString("skumaster", "0"));
            Log.e("fetchSKUData", "" + jsonArray.length());
//            jsonArray.toString().
            for (int i = 0; i < jsonArray.length(); i++) {

                if (jsonArray.getJSONObject(i).getString("SKU_CODE").equals(eanCode)) {
                    Log.e("PresentSKU", "true = " + i);
                    skuname_edt.setText(jsonArray.getJSONObject(i).getString("SKU_NAME"));
                    prices.add(jsonArray.getJSONObject(i).getString("MRP"));
                    SKU_LOC_NO = jsonArray.getJSONObject(i).getString("SKU_LOC_NO");
                    DAMAGED_QTY = jsonArray.getJSONObject(i).getString("DAMAGED_QTY");
                    EXPIRY_DATE = jsonArray.getJSONObject(i).getString("EXPIRY_DATE");
                    MRP = jsonArray.getJSONObject(i).getString("MRP");
                    SKU_CODE = jsonArray.getJSONObject(i).getString("SKU_CODE");
                    REF_BATCH = jsonArray.getJSONObject(i).getString("REF_BATCH");
                    SKU_NAME = jsonArray.getJSONObject(i).getString("SKU_NAME");
                }


            }

           /* if (skuDataList.contains(eanCode)) {
                Log.e("fetchSKUData", "true");

            }
            else{
                Log.e("fetchSKUData", "false");

            }*/
/*
            for (SKUMASTER d : skuDataList) {
                if (d.getSkuCode() != null && d.getSkuCode().contains(eanCode)) {
                    Log.e("fetchSKUData", "true");

                }
                else{
                    Log.e("fetchSKUData", "false");

                }
                //something here
            }*/
            progressDialog.dismiss();


        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }


       /* if (skuDataList.size() != 0) {

            skuname_edt.setText(skuDataList.get(0).getSkuName());
//            etSkuCode.setText(skuDataList.get(0).getSKUcode());

//            fetchBrandCode(skuDataList.get(0).getSKUcode());
        } else {
//            showAlertDialog("Invalid Sku Code");
        }*/
    }



/*    public void getDetailsFromDB_Temp(){
        try {

            SKU_MASTER_DB skudb = new SKU_MASTER_DB(getActivity());
            skudb.open();

            ArrayList<SKUMASTER> eanDataList = skudb.getSkuLocDetails(eansku_edt.getText().toString().trim().replaceFirst("^0+(?!$)", ""));

            if (eanDataList == null || eanDataList.size() <= 0) {

                fetchSKUData(eansku_edt.getText().toString().trim().replaceFirst("^0+(?!$)", ""));

            } else {


                skuCode = eanDataList.get(0).getSkuCode();


                fetchSKUData(eansku_edt.getText().toString().trim().replaceFirst("^0+(?!$)", ""));

            }

        } catch (Exception ex) {
            ex.printStackTrace();
//            Log.e(TAG, "fetchDataBySkuCode Exception Occured " + ex.getLocalizedMessage());
            showAlertDialog("Something went wrong");
        }
    }

    private void fetchSKUData_Temp(String skuCode) {

        SKU_MASTER_DB skudb = new SKU_MASTER_DB(getActivity());
        skudb.open();

        ArrayList<SKUMASTER> skuDataList = skudb.getSkuDetails(skuCode);
        skudb.close();


        if (skuDataList.size() != 0) {

            skuname_edt.setText(skuDataList.get(0).getSkuName());
//            etSkuCode.setText(skuDataList.get(0).getSKUcode());

//            fetchBrandCode(skuDataList.get(0).getSKUcode());
        } else {
//            showAlertDialog("Invalid Sku Code");
        }
    }*/


}
