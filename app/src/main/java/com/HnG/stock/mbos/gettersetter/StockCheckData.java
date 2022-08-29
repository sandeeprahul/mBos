package com.HnG.stock.mbos.gettersetter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StockCheckData {

    private String stockNo;
    private String locationCode;
    private String deviceNo;
    public ArrayList<SKUMASTER> detailsArray = new ArrayList<SKUMASTER>();

    public StockCheckData(JSONObject jsonObject) {
        try {
            stockNo = jsonObject.getString("stockNo");
            locationCode = jsonObject.getString("locationCode");
            deviceNo = jsonObject.getString("deviceNo");
            for (int i = 0; i < jsonObject.getJSONArray("detailsArray").length(); i++) {
                detailsArray.add(new SKUMASTER(

                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("stockChkNo"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("skuLOCNo"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("skuCode"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("skuName"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("deviceNo"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("refBatch"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("mrp"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("expiryDate"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("physicalQty"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("damagedQty"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("eanCode"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("bay_shelf_no"),
                        jsonObject.getJSONArray("detailsArray").getJSONObject(i).getString("location_code")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
