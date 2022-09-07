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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.HnG.stock.mbos.Adapter.QuantityHisAdapter;
import com.HnG.stock.mbos.R;
import com.HnG.stock.mbos.gettersetter.SKUMASTER;
import com.HnG.stock.mbos.helper.Log;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mac on 7/24/18.
 */

public class FragmentQuantity extends Fragment {

    EditText skucode_edt, batchcode_edt, physicalqty_edt, qty_edt, shelfno_edt;
    Button search_btn, update_btn, clear_btn;
    RecyclerView rv_data;
    RelativeLayout shelf_no_rl;
    ListView shelf_no_lv;
    String code = "";
    ArrayAdapter<String> arrayAdapter;
    List<String> shelf_no_list = new ArrayList<String>();

    String hasSku = "";
    TextView shelf_no_tv;
    ArrayList<SKUMASTER> skumasters = new ArrayList<SKUMASTER>();
    TextView tv_skuname;
    QuantityHisAdapter quantityHisAdapter;
    ArrayList<SKUMASTER> skumasterArrayList_ = new ArrayList<SKUMASTER>();

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

        skucode_edt = (EditText) view.findViewById(R.id.skueancode_edt);
        batchcode_edt = (EditText) view.findViewById(R.id.batchcode_edt);
        physicalqty_edt = (EditText) view.findViewById(R.id.physicalqty_edt);
        qty_edt = (EditText) view.findViewById(R.id.qty_edt);
        shelfno_edt = (EditText) view.findViewById(R.id.shelfno_edt);
        search_btn = (Button) view.findViewById(R.id.search_btn);
        shelf_no_lv = (ListView) view.findViewById(R.id.shelf_no_lv);
        shelf_no_rl = (RelativeLayout) view.findViewById(R.id.shelf_no_rl);
        update_btn = (Button) view.findViewById(R.id.update_btn);
        shelf_no_tv = (TextView) view.findViewById(R.id.shelf_no_tv);
        clear_btn = (Button) view.findViewById(R.id.clear_btn);
        rv_data = (RecyclerView) view.findViewById(R.id.rv_data);
        tv_skuname = (TextView) view.findViewById(R.id.tv_skuname);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        rv_data.setLayoutManager(linearLayoutManager1);
        quantityHisAdapter = new QuantityHisAdapter(getActivity(), skumasterArrayList_);
        rv_data.setAdapter(quantityHisAdapter);

        skucode_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(skucode_edt.getWindowToken(), 0);
                    findSkucode();
                }
            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skucode_edt.getText().toString().equals("")) {
                    customToast("Please enter Sku Code");
                } else if (batchcode_edt.getText().toString().equals("")) {
                    customToast("Please enter MRP");
                } else {
                    if (!code.equals("")) {
                        Log.e("customToast", code);
                        findDetails(0);
                    }
                }
            }
        });

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFields();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skucode_edt.getText().toString().equals("")) {
                    customToast("Please enter Sku Code");
                } else if (batchcode_edt.getText().toString().equals("")) {
                    customToast("Please enter MRP");
                } else if (shelfno_edt.getText().toString().equals("Bay/Shelf No")) {
                    customToast("Please select Bay/Shelf No");
                } else if (physicalqty_edt.getText().toString().equals("")) {
                    customToast("Please enter Updated Qty");
                } else if (physicalqty_edt.getText().toString().equals("0")||physicalqty_edt.getText().toString().equals("00")||physicalqty_edt.getText().toString().equals("000")) {
                    customToast("Please enter Updated qty");

                } else {
                    updateqty();
                }
            }
        });

        shelf_no_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shelf_no_list.size() == 1) {
                    shelf_no_lv.setVisibility(View.VISIBLE);
                } else if (shelf_no_list.size() > 1) {
                    shelf_no_lv.setVisibility(View.VISIBLE);
                }
            }
        });

        shelf_no_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), shelf_no_list.get(position), Toast.LENGTH_SHORT).show();
                shelf_no_tv.setText(shelf_no_list.get(position));
                shelf_no_lv.setVisibility(View.GONE);
                findDetails(1);
            }
        });

        shelf_no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shelf_no_rl.performClick();
            }
        });

        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_pricelistview, R.id.textView, shelf_no_list);
        shelf_no_lv.setAdapter(arrayAdapter);


        return view;
    }

    public void updateqty() {
        updatePhyQty();

        /*if (Integer.parseInt(physicalqty_edt.getText().toString()) > Integer.parseInt(qty_edt.getText().toString())) {
            showAlertDialog("-Ve Quanity should not be less than actual Quantity");
        } else if (Integer.parseInt(physicalqty_edt.getText().toString()) == Integer.parseInt(qty_edt.getText().toString())) {
            showAlertDialog("-Ve Quanity should not be equal than actual Quantity");
        } else {
//            showUpdateAlertDialog("");
            updatePhyQty();
        }*/

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

    public void showUpdateAlertDialog(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("HnGmBOS");
                builder.setMessage(message)
                        .setCancelable(false).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }


    public void findSkucode() {
        Log.e("findSkucode", "findSkucode");
        try {
            String json = getDetails();
            JSONArray jsonArray = new JSONArray(json);
//            ArrayList<SKUMASTER> temp = new ArrayList<>();

            boolean sku = true;
            for (int i = 0; i < jsonArray.length(); i++) {


//                temp.add(new SKUMASTER(jsonArray.getJSONObject(i)));

                if (skucode_edt.getText().toString().equals(jsonArray.getJSONObject(i).getString("eanCode"))) {


                    skucode_edt.setText(jsonArray.getJSONObject(i).getString("skuCode"));
                    code = jsonArray.getJSONObject(i).getString("skuCode");
                    sku = false;
                    Log.e("findSkucode", code);
                } /*else {
                    customToast("No details found");
                }*/
/*
                if (!sku){
                    if (skucode_edt.getText().toString().equals(jsonArray.getJSONObject(i).getString("skuCode"))){
                        skucode_edt.setText(jsonArray.getJSONObject(i).getString("eanCode"));
                        code = jsonArray.getJSONObject(i).getString("skuCode");
                        Log.e("findSkucode",code);

                    }
                }*/

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void findDetails(int type) {

        skumasterArrayList_.clear();


        String phyTemp = "";

        Log.e("findDetails", skucode_edt.getText().toString() + ", " + batchcode_edt.getText().toString());
        String json = getDetails();

        try {
            JSONArray jsonArray = new JSONArray(json);
            ArrayList<SKUMASTER> temp = new ArrayList<>();
            String price = "";
            if (batchcode_edt.getText().toString().contains(".")) {
                price = batchcode_edt.getText().toString();
            } else {
                price = batchcode_edt.getText().toString() + ".00";
            }

            if (type == 0) {
                shelf_no_list.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.e("findDetailsArray", jsonArray.toString());
                    hasSku = String.valueOf(i);


                    temp.add(new SKUMASTER(jsonArray.getJSONObject(i)));


                    if (skucode_edt.getText().toString().equals(jsonArray.getJSONObject(i).getString("skuCode")) && price.equals(jsonArray.getJSONObject(i).getString("mrp"))) {
//                        hasSku = String.valueOf(i);
//                    Integer.parseInt(jsonArray.getJSONObject(i).getString("bay_shelf_no"))>1

                        shelf_no_list.add(jsonArray.getJSONObject(i).getString("bay_shelf_no"));

                    /*if (jsonArray.getJSONObject(i).getJSONArray("jsonArrayQty").length() > 1) {
                        ArrayList<String> listdata = new ArrayList<String>();
                        JSONArray jArray_ = jsonArray.getJSONObject(i).getJSONArray("jsonArrayQty");
                        if (jArray_ != null) {
                            for (int j = 0; j < jArray_.length(); j++) {
//                                listdata.add(jArray_.getString(i));

                                SKUMASTER tempSku = new SKUMASTER(temp.get(i).stockChkNo, temp.get(i).skuLOCNo, temp.get(i).skuCode, temp.get(i).skuName, temp.get(i).deviceNo,
                                        "", temp.get(i).mrp, "", temp.get(i).jsonArrayQty.get(j).toString(),
                                        "", temp.get(i).eanCode, temp.get(i).bay_shelf_no, temp.get(i).location_code, temp.get(i).jsonArrayQty);
*//*                                SKUMASTER tempSku = new SKUMASTER(jsonArray.getJSONObject(i).getString("stockChkNo"), jsonArray.getJSONObject(i).getString("skuLOCNo"), jsonArray.getJSONObject(i).getString("skuCode"), jsonArray.getJSONObject(i).getString("skuName"), jsonArray.getJSONObject(i).getString("deviceNo"),
                                        "", jsonArray.getJSONObject(i).getString("mrp"), "", jsonArray.getJSONObject(i).getString("physicalQty"),
                                        "", jsonArray.getJSONObject(i).getString("eanCode"), jsonArray.getJSONObject(i).getString("bay_shelf_no"), jsonArray.getJSONObject(i).getString("location_code"), listdata);*//*
                                skumasterArrayList_.add(tempSku);
                                Gson gson = new Gson();
                                String jsonss = gson.toJson(skumasterArrayList_);
                                Log.e("jsonss", jsonss);
                            }
                        }
                    }*/


                        tv_skuname.setText(jsonArray.getJSONObject(i).getString("skuName"));
//                    qty_edt.setText(jsonArray.getJSONObject(i).getString("physicalQty"));
                        phyTemp = jsonArray.getJSONObject(i).getString("physicalQty");
                    } /*else {
                    customToast("No details found");
                }*/


                }

                if (shelf_no_list.size() == 1) {

                    shelf_no_tv.setText(shelf_no_list.get(0));
                    qty_edt.setText(phyTemp);
                    skucode_edt.setEnabled(false);
                    physicalqty_edt.requestFocus();

                } else if (shelf_no_list.size() > 1) {
                    skucode_edt.setEnabled(false);

//                shelf_no_list.size();
                    try {
                        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    shelf_no_lv.setVisibility(View.VISIBLE);
                } else {
                    customToast("No details found");

                }


                arrayAdapter.notifyDataSetChanged();
                skumasterArrayList = temp;

                quantityHisAdapter = new QuantityHisAdapter(getActivity(), skumasterArrayList_);
                quantityHisAdapter.notifyDataSetChanged();
                rv_data.setAdapter(quantityHisAdapter);
            } else if (type == 1) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    Log.e("findDetailsArray", jsonArray.toString());

                    temp.add(new SKUMASTER(jsonArray.getJSONObject(i)));


                    if (skucode_edt.getText().toString().equals(jsonArray.getJSONObject(i).getString("skuCode")) && price.equals(jsonArray.getJSONObject(i).getString("mrp")) && shelf_no_tv.getText().toString().equals(jsonArray.getJSONObject(i).getString("bay_shelf_no"))) {
                        hasSku = String.valueOf(i);
//                    Integer.parseInt(jsonArray.getJSONObject(i).getString("bay_shelf_no"))>1

//                        shelf_no_list.add(jsonArray.getJSONObject(i).getString("bay_shelf_no"));

                    /*if (jsonArray.getJSONObject(i).getJSONArray("jsonArrayQty").length() > 1) {
                        ArrayList<String> listdata = new ArrayList<String>();
                        JSONArray jArray_ = jsonArray.getJSONObject(i).getJSONArray("jsonArrayQty");
                        if (jArray_ != null) {
                            for (int j = 0; j < jArray_.length(); j++) {
//                                listdata.add(jArray_.getString(i));

                                SKUMASTER tempSku = new SKUMASTER(temp.get(i).stockChkNo, temp.get(i).skuLOCNo, temp.get(i).skuCode, temp.get(i).skuName, temp.get(i).deviceNo,
                                        "", temp.get(i).mrp, "", temp.get(i).jsonArrayQty.get(j).toString(),
                                        "", temp.get(i).eanCode, temp.get(i).bay_shelf_no, temp.get(i).location_code, temp.get(i).jsonArrayQty);
*//*                                SKUMASTER tempSku = new SKUMASTER(jsonArray.getJSONObject(i).getString("stockChkNo"), jsonArray.getJSONObject(i).getString("skuLOCNo"), jsonArray.getJSONObject(i).getString("skuCode"), jsonArray.getJSONObject(i).getString("skuName"), jsonArray.getJSONObject(i).getString("deviceNo"),
                                        "", jsonArray.getJSONObject(i).getString("mrp"), "", jsonArray.getJSONObject(i).getString("physicalQty"),
                                        "", jsonArray.getJSONObject(i).getString("eanCode"), jsonArray.getJSONObject(i).getString("bay_shelf_no"), jsonArray.getJSONObject(i).getString("location_code"), listdata);*//*
                                skumasterArrayList_.add(tempSku);
                                Gson gson = new Gson();
                                String jsonss = gson.toJson(skumasterArrayList_);
                                Log.e("jsonss", jsonss);
                            }
                        }
                    }*/


//                        tv_skuname.setText(jsonArray.getJSONObject(i).getString("skuName"));
                        qty_edt.setText(jsonArray.getJSONObject(i).getString("physicalQty"));
                        phyTemp = jsonArray.getJSONObject(i).getString("physicalQty");

                        physicalqty_edt.requestFocus();

                    } /*else {
                    customToast("No details found");
                }*/


                }
            }


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

    public void updatePhyQty() {


        ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "",
                "Please wait..", true);
        progressDialog.show();

        String lastSavedSku = getDetails();


        try {
            JSONArray jsonArray = new JSONArray(lastSavedSku);
            ArrayList<SKUMASTER> temp = new ArrayList<>();

//String hasSku = getPos(jsonArray);
            int upDatedPhy = 0;

            if (!hasSku.equals("")) {


                skumasters.clear();

                for (int k = 0; k < jsonArray.length(); k++) {
                    skumasters.add(new SKUMASTER(jsonArray.getJSONObject(k)));
                }

                int position = Integer.parseInt(hasSku);

//                    for (int i = 0; i < jsonArray.length(); i++) {
                temp.add(new SKUMASTER(jsonArray.getJSONObject(position)));
//                upDatedPhy = physicalqty_edt.getText().toString();
//                upDatedPhy = Integer.parseInt(jsonArray.getJSONObject(position).getString("physicalQty")) - Integer.parseInt(physicalqty_edt.getText().toString());
//                        phyqty += Integer.parseInt(physicalqty_edt.getText().toString());

                String sPhyqty = physicalqty_edt.getText().toString();
//                String sPhyqty = String.valueOf(upDatedPhy);

                ArrayList<String> listdata = new ArrayList<String>();
                JSONArray jArray = jsonArray.getJSONObject(position).getJSONArray("jsonArrayQty");
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        listdata.add(jArray.getString(i));
                    }
                }
                listdata.add("-" + physicalqty_edt.getText().toString());

                skumasters.set(position, new SKUMASTER(jsonArray.getJSONObject(position).getString("stockChkNo"), jsonArray.getJSONObject(position).getString("skuLOCNo"), jsonArray.getJSONObject(position).getString("skuCode"), jsonArray.getJSONObject(position).getString("skuName"), jsonArray.getJSONObject(position).getString("deviceNo"),
                        "", jsonArray.getJSONObject(position).getString("mrp"), "", sPhyqty,
                        "", jsonArray.getJSONObject(position).getString("eanCode"), jsonArray.getJSONObject(position).getString("bay_shelf_no"), jsonArray.getJSONObject(position).getString("location_code"), listdata));
              /*  skumasters.set(position, new SKUMASTER(temp.get(position).stockChkNo, temp.get(position).skuLOCNo,temp.get(position).skuCode,temp.get(position).skuName,temp.get(position).deviceNo,
                        "",temp.get(position).mrp, "", sPhyqty,
                        "",temp.get(position).eanCode,temp.get(position).bay_shelf_no,temp.get(position).location_code,temp.get(position).jsonArrayQty));
*/


                Gson gson = new Gson();
                String json = gson.toJson(skumasters);
                Log.e("updatePhyQty", json);

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("stock", "");
                editor.apply();
                editor.putString("stock", json);
                editor.apply();

            }

            skumasterArrayList_.clear();
            quantityHisAdapter.notifyDataSetChanged();


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
        skucode_edt.setEnabled(true);
        skucode_edt.requestFocus();
        shelf_no_tv.setText("Bay/Shelf No: ");
        shelf_no_lv.setVisibility(View.GONE);
    }


}
