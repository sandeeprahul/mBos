package com.HnG.stock.mbos.activity;


import static android.util.Base64.DEFAULT;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.hardware.input.InputManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.HnG.stock.mbos.Adapter.CustomSpinnerAdapter;
import com.HnG.stock.mbos.Adapter.ExpandableListAdapter;
import com.HnG.stock.mbos.BuildConfig;
import com.HnG.stock.mbos.R;
import com.HnG.stock.mbos.database.EANDB;
import com.HnG.stock.mbos.database.FinalData;
import com.HnG.stock.mbos.database.HG_PIHV_DETAILS;
import com.HnG.stock.mbos.database.HG_PO_DETAILS;
import com.HnG.stock.mbos.database.HG_PO_HEADER;
import com.HnG.stock.mbos.database.HG_TRANS_IN_DETAILS;
import com.HnG.stock.mbos.database.InvDB;
import com.HnG.stock.mbos.database.TesterRegisterDB;
import com.HnG.stock.mbos.database.UserDB;
import com.HnG.stock.mbos.gettersetter.InvoiceDetails;
import com.HnG.stock.mbos.gettersetter.MenuModel;
import com.HnG.stock.mbos.gettersetter.PODetails;
import com.HnG.stock.mbos.gettersetter.POHeader;
import com.HnG.stock.mbos.helper.ApiCall;
import com.HnG.stock.mbos.helper.AppController;
import com.HnG.stock.mbos.helper.BaseActivity;
import com.HnG.stock.mbos.helper.FileUtils;
import com.HnG.stock.mbos.helper.InputDeviceState;
import com.HnG.stock.mbos.helper.Log;
import com.HnG.stock.mbos.interfaces.LogOutTimerUtil;
import com.HnG.stock.mbos.model.DeliveryData;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class MGINUploadInvoiceTest_MainCopy extends BaseActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, InputManager.InputDeviceListener, LogOutTimerUtil.LogOutListener {

    public static final String TAG = MGINUploadInvoiceTest_MainCopy.class.getSimpleName();

    private static final int IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private static final int REQUEST_CODE_SELECT_IMAGE = 101;
    public static final int GALLERY_REQUEST_CODE = 102;
    public static final int CUSTOM_REQUEST_CODE = 103;
    Uri croppedImagePath;
    final int CROP_PIC = 2;
    JSONObject jsonObject;
    String currentImagePath = "", timeStamp = "";
    Uri outPutfileUri;
    File file_pdf;

    ArrayList<Bitmap> img_bitmap_list = new ArrayList<>();

    Button next_btn, clear_btn, btn_capture_invoice, btn_upload_invoice, submit_btn;
    EditText poNumberEdt, invoiceNumberEdt, invoiceDateEdt, invoiceAmountEdt, invoiceVatAmountEdt, locationcode, inwardNumberEdit;
    EditText invoiceCGSTAmoutEdt, invoiceSGSTAmoutEdt, invoiceIGSTAmoutEdt, invoiceCESSAmoutEdt, etVendorCode;
    EditText etDeliveredBy, etDeliveredPersonPhno, etTcsPercentage, etTcsValue;
    ImageView imageView, capturedImg2;
    LinearLayout llManualGinNo, llInvoiceDetails, top_btn_layout;

    Calendar myCalendar = Calendar.getInstance();
    boolean poInvalid, invoiceInvalid;
    Menu optionsMenu;
    String poDate="", poValue;
    private ProgressDialog idialog = null, pDialog = null;

    String storeip = "", mInvoiceDate = "", mGINNo = "", mPendingCount = "", enableDisableButton = "N";
    int responseValue = 0;
    double gstAmt = 0.0, cgst = 0.0, sgst = 0.0, igst = 0.0, cess = 0.0;
    int apiCallingCnt = 0;
    boolean image2 = false;

    private InputManager mInputManager;
    private SparseArray<InputDeviceState> mInputDeviceStates;

    SharedPreferences sharedPreferences, sp;
    Gson gson;
    ProgressDialog progressDialog;

    List<String> imagePathList = new ArrayList<>();
    String mRequestBody = "", mBaseCostValue = "", mDocumentId = "", invoiceCaptureType = "";

    SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

    ExpandableListAdapter expandableListAdapter;
    ExpandableListView expandableListView;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();
    String einvoiceFlag;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    ViewGroup viewGroup;
    View dialogView;
    EditText irnNumber;
    String irnNumberValue = "";
    String EInvoiceFlag = "";
    String QrcodeFlag = "";
    TextView stockcheck_tv;
    int inputIRnNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mgin2);

        imageViewSh = findViewById(R.id.iv_sharpen_image);
        iv_pdf = findViewById(R.id.iv_pdf);
        stockcheck_tv = (TextView) findViewById(R.id.stockcheck_tv);
        stockcheck_tv.setVisibility(View.GONE);
       /* try {
            createPdf("");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //showCameraPreview();

        mInputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
        mInputDeviceStates = new SparseArray<>();

        sp = getSharedPreferences("LoginDetails", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE);
        gson = new Gson();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pre GIN");
//        toolbar.setTitle("Pre GIN Test");
        setSupportActionBar(toolbar);

        expandableListView = findViewById(R.id.expandableListView);
        prepareMenuData();
        populateExpandableList();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                //changed
               /* if (imm != null) {
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                }*/
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MGINUploadInvoiceTest_MainCopy.this);
        Menu nav_Menu = navigationView.getMenu();

        UserDB userDb = new UserDB(getApplicationContext());
        userDb.open();
        HashMap<String, String> dataexist = userDb.getUserIP("");
        if (dataexist.isEmpty()) {
            //userDb.insertUserIP(ipaddress);
        } else {
            storeip = dataexist.get("ipaddress");
        }
        Log.w(TAG, "StoreIp Address " + storeip);
        userDb.close();

        FinalData podb = new FinalData(getApplicationContext());
        podb.open();
        //podb.UpdatePO();
        podb.purgePOdata();
        podb.close();

        InvDB invDB = new InvDB(getApplicationContext());
        invDB.open();
        invDB.addColumn();
        invDB.addColumn1();
        invDB.close();

        HG_PO_DETAILS poDB = new HG_PO_DETAILS(getApplicationContext());
        poDB.open();
        poDB.addColumn();
        poDB.close();

        HG_PIHV_DETAILS hg_pihv_details = new HG_PIHV_DETAILS(getApplicationContext());
        hg_pihv_details.open();
        hg_pihv_details.close();

        HG_TRANS_IN_DETAILS hgTransInDetails = new HG_TRANS_IN_DETAILS(getApplicationContext());
        hgTransInDetails.open();
        hgTransInDetails.close();
/*
        FinalData fnDB = new FinalData(getApplicationContext());
        fnDB.open();
        fnDB.UpdateAllPushStatustoN();
        fnDB.close();
*/
        TesterRegisterDB testerRegisterDB = new TesterRegisterDB(this);
        testerRegisterDB.open();
        testerRegisterDB.purgePOdata();
        int pendingCount = testerRegisterDB.getPendingCount();
        testerRegisterDB.close();

        Log.w(TAG, "Pending Count ###### " + pendingCount);
        if (pendingCount > 0)
            showAlert("There are pending records in Free Gift and Tester Register! Please submit");

        initUI();

        setListners();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (image2) {
//                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//                    Bitmap bitmap2 = ((BitmapDrawable) capturedImg2.getDrawable()).getBitmap();
//
//                    Bitmap mergedImages = createSingleImageFromMultipleImages(bitmap, bitmap2);
//
//                    imageViewSh.setImageBitmap(mergedImages);
//                    imageViewSh.setVisibility(View.VISIBLE);
//
//                    imageView.setVisibility(View.GONE);
//                    capturedImg2.setVisibility(View.GONE);
//                    displayImageDup();
//
//                } else {
//                    displayImageDup();
//                }

                uploadImageToServerDup(file_pdf.getPath(), 1);


//                clear_btn.performClick();
                clearFileds();

            }
        });
        capturedImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image2 = true;
                if (isCameraPermissionGranted() && isWriteStoragePermissionGranted() && isReadStoragePermissionGranted())
                    captureImage();
            }
        });

        iv_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri path = Uri.fromFile(file_pdf);
                Log.e("test_file pdf", file_pdf.getPath());
                Uri path = FileProvider.getUriForFile(MGINUploadInvoiceTest_MainCopy.this, getApplicationContext().getPackageName() + ".fileprovider", file_pdf);
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfOpenintent.setDataAndType(path, "application/pdf");
                try {
                    startActivity(pdfOpenintent);
                } catch (ActivityNotFoundException e) {

                }
            }
        });

        stockcheck_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MGINUploadInvoiceTest_MainCopy.this, StockActivityTemp.class);
                startActivity(intent);
            }
        });


    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight() + secondImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, 0f, firstImage.getHeight(), null);
        return result;
    }

    public void initUI() {
        poNumberEdt = findViewById(R.id.htt_po_number);

        invoiceNumberEdt = findViewById(R.id.htt_invoice_no_edt);
        invoiceDateEdt = findViewById(R.id.htt_invoice_date_edt);
        invoiceAmountEdt = findViewById(R.id.htt_invoice_amount_edt);
        next_btn = findViewById(R.id.htt_next);
        clear_btn = findViewById(R.id.htt_clear);
        btn_capture_invoice = findViewById(R.id.btn_capture_invoice);
        btn_upload_invoice = findViewById(R.id.btn_upload_invoice);
        submit_btn = findViewById(R.id.submit_btn);
        invoiceVatAmountEdt = findViewById(R.id.htt_invoice_vat);
        locationcode = findViewById(R.id.htt_location_code);
        inwardNumberEdit = findViewById(R.id.po_inward_no);
        llManualGinNo = findViewById(R.id.ll_manual_gin);
        llInvoiceDetails = findViewById(R.id.ll_invoice_details);
        top_btn_layout = findViewById(R.id.top_btn_layout);
        imageView = findViewById(R.id.iv_display_image);
        capturedImg2 = findViewById(R.id.iv_display_image2);

        invoiceCGSTAmoutEdt = findViewById(R.id.htt_invoice_cgst);
        invoiceSGSTAmoutEdt = findViewById(R.id.htt_invoice_sgst);
        invoiceIGSTAmoutEdt = findViewById(R.id.htt_invoice_igst);
        invoiceCESSAmoutEdt = findViewById(R.id.htt_invoice_cess);
        etVendorCode = findViewById(R.id.et_vendorcode);

        etDeliveredBy = findViewById(R.id.et_delivered_by);
        etDeliveredPersonPhno = findViewById(R.id.et_delivered_person_phno);
        etTcsPercentage = findViewById(R.id.etTcsPercentage);
        etTcsValue = findViewById(R.id.etTcsValue);
        poNumberEdt.setVisibility(View.VISIBLE);
        top_btn_layout.setVisibility(View.GONE);
//        next_btn.setEnabled(false);
//        clear_btn.setEnabled(false);


        builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
        viewGroup = findViewById(android.R.id.content);
        dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_pre_gin_dialog, viewGroup, false);

        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        etTcsPercentage.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(12, 3)});

        findViewById(R.id.btn_select_pdf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (poNumberEdt.getText().toString().trim().isEmpty())
                    showAlert("Enter Po Number..");
                else {
                    if (locationcode.getText().toString().trim().isEmpty() && etVendorCode.getText().toString().trim().isEmpty()) {
                        imageView.setVisibility(View.GONE);
                        llInvoiceDetails.setVisibility(View.GONE);
                        checkGinAvailForPoNumber();
                    }
                    //llInvoiceDetails.setVisibility(View.GONE);


                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    String[] mimetypes = {"application/pdf"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    startActivityForResult(Intent.createChooser(intent, "Choose Pdf"), REQUEST_CODE_SELECT_IMAGE);
                }*/

                //  checkGinAvailForPoNumber();
                Toast.makeText(MGINUploadInvoiceTest_MainCopy.this, "Please wait...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"application/pdf"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(Intent.createChooser(intent, "Choose Pdf"), REQUEST_CODE_SELECT_IMAGE);

            }
        });
    }

    public void setListners() {

        next_btn.setOnClickListener(this);
        invoiceDateEdt.setOnClickListener(this);

        btn_capture_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //llInvoiceDetails.setVisibility(View.GONE);
                //imageView.setVisibility(View.VISIBLE);
               /* if (poNumberEdt.getText().toString().trim().isEmpty())
                    showAlert("Enter Po Number..");
                else {
                    if (locationcode.getText().toString().trim().isEmpty() && etVendorCode.getText().toString().trim().isEmpty()) {
                        imageView.setVisibility(View.GONE);
                        llInvoiceDetails.setVisibility(View.GONE);
                        checkGinAvailForPoNumber();
                    }


                }*/
                // checkGinAvailForPoNumber();
                hideKeyboard();
                chooseImage();
                clearFileds();
                img_bitmap_list.clear();
                //captureImage();
            }
        });

        btn_upload_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
                //llInvoiceDetails.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
            }
        });

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //llInvoiceDetails.setVisibility(View.GONE);
//                top_btn_layout.setVisibility(View.GONE);

                locationcode.setText("");
                locationcode.setEnabled(true);
                poNumberEdt.setText("");
                invoiceAmountEdt.setText("");
                invoiceDateEdt.setText("");
                invoiceAmountEdt.setText("");
                invoiceVatAmountEdt.setText("");
                invoiceNumberEdt.setText("");
                inwardNumberEdit.setText("");
                etVendorCode.setText("");
                etVendorCode.setEnabled(true);
                invoiceCGSTAmoutEdt.setText("0");
                invoiceSGSTAmoutEdt.setText("0");
                invoiceIGSTAmoutEdt.setText("0");
                invoiceCESSAmoutEdt.setText("0");
                etDeliveredBy.setText(null);
                etDeliveredPersonPhno.setText(null);
                llManualGinNo.setVisibility(View.GONE);
                poNumberEdt.requestFocus();

                mBaseCostValue = mDocumentId = invoiceCaptureType = "";
            }
        });

        poNumberEdt.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                locationcode.setText("");
                invoiceDateEdt.setText("");
                invoiceAmountEdt.setText("");
                invoiceVatAmountEdt.setText("");
                inwardNumberEdit.setText("");
                invoiceNumberEdt.setText("");
                mBaseCostValue = mDocumentId = invoiceCaptureType = "";
            }
        });
        locationcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    getPOmaster();
                    return true;
                }
                return false;
            }
        });

        invoiceCGSTAmoutEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString()) &&
                        !editable.toString().equalsIgnoreCase("0")) {
                    cgst = Double.parseDouble(editable.toString());
                    if (!TextUtils.isEmpty(invoiceIGSTAmoutEdt.getText().toString()) &&
                            !invoiceIGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))
                        invoiceIGSTAmoutEdt.setText("0");
                } else
                    cgst = 0.0;
            }
        });

        invoiceSGSTAmoutEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString()) &&
                        !editable.toString().equalsIgnoreCase("0")) {
                    sgst = Double.parseDouble(editable.toString());

                    if (!TextUtils.isEmpty(invoiceIGSTAmoutEdt.getText().toString()) &&
                            !invoiceIGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))
                        invoiceIGSTAmoutEdt.setText("0");
                } else
                    sgst = 0.0;

                gstAmt = sgst + cgst + cess;
                invoiceVatAmountEdt.setText(String.valueOf(gstAmt));
            }
        });

        invoiceIGSTAmoutEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString()) &&
                        !editable.toString().equalsIgnoreCase("0")) {

                    igst = Double.parseDouble(editable.toString());
                    invoiceCGSTAmoutEdt.setText("0");
                    invoiceSGSTAmoutEdt.setText("0");
                } else
                    igst = 0.0;

                gstAmt = igst + cess;
                invoiceVatAmountEdt.setText(String.valueOf(gstAmt));
            }
        });

        invoiceCESSAmoutEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString())) {
                    cess = Double.parseDouble(editable.toString());
                } else {
                    cess = 0.0;
                }

                gstAmt = sgst + cgst + igst + cess;
                invoiceVatAmountEdt.setText(String.valueOf(gstAmt));
            }
        });

        invoiceAmountEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    invoiceCGSTAmoutEdt.requestFocus();
                    invoiceCGSTAmoutEdt.setSelection(invoiceCGSTAmoutEdt.getText().toString().trim().length());
                    return true;
                }
                return false;
            }
        });

        invoiceCGSTAmoutEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    invoiceSGSTAmoutEdt.requestFocus();
                    invoiceSGSTAmoutEdt.setSelection(invoiceSGSTAmoutEdt.getText().toString().trim().length());
                    return true;
                }
                return false;
            }
        });

        invoiceSGSTAmoutEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    invoiceCESSAmoutEdt.requestFocus();
                    invoiceCESSAmoutEdt.setSelection(invoiceCESSAmoutEdt.getText().toString().trim().length());
                    return true;
                }
                return false;
            }
        });

        poNumberEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (!poNumberEdt.getText().toString().trim().equalsIgnoreCase("")) {
                        imageView.setVisibility(View.GONE);
                        //llInvoiceDetails.setVisibility(View.GONE);
                        mBaseCostValue = mDocumentId = invoiceCaptureType = "";
                        checkGinAvailForPoNumber();
                    } else
                        showAlert("Enter Po Number");
                    return true;
                }
                return false;
            }
        });

        poNumberEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    if (!poNumberEdt.getText().toString().trim().equalsIgnoreCase("")) {
                        imageView.setVisibility(View.GONE);
                        mBaseCostValue = mDocumentId = invoiceCaptureType = "";
                        // checkGinAvailForPoNumber();
                    }
                }
            }
        });

    }

    private void getPOmaster() {
//        locationcode.clearFocus();
        try {
            if (!poNumberEdt.getText().toString().trim().isEmpty()) {
//                String apiUrl = storeip + "/values/mgin_validate/" + poNumberEdt.getText().toString().trim();
                String apiUrl = "http://36.255.252.200" + "/VendorInvoice/api/Poheader/get_Poheader?PoNo=" + poNumberEdt.getText().toString().trim()
                        + "&LocationCode=" + locationcode.getText().toString();

                Log.w(TAG, "getPOmaster URL " + apiUrl);

                ApiCall.make(this, apiUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Log.e(TAG, "getPOmaster Response " + response);

                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("result").equalsIgnoreCase("Success")) {
                                        //showAlert(jsonObject.getString("Message"));
//                                        Log.e("getPOmaster",);

                                        JSONArray jsonPOmaster = jsonObject.getJSONArray("POmaster");

//                                        for (int i = 0; i < jsonPOmaster.length(); i++) {
                                        JSONObject jsonobjectPredict = jsonPOmaster.getJSONObject(0);

                                        Log.d(TAG, "getPOmaster jsonObj " + jsonobjectPredict.toString());


                                        etVendorCode.setText(jsonobjectPredict.getString("VENDOR_CODE"));

                                        invoiceNumberEdt.setText(jsonobjectPredict.getString("INVOICE_NO"));
                                        invoiceNumberEdt.setEnabled(false);

                                        invoiceAmountEdt.setText(jsonobjectPredict.getString("INVOICE_AMOUNT"));
                                        invoiceAmountEdt.setEnabled(false);

                                        invoiceDateEdt.setText(jsonobjectPredict.getString("INVOICE_DATE"));
                                        invoiceDateEdt.setEnabled(false);

                                        invoiceCGSTAmoutEdt.setText(jsonobjectPredict.getString("INVOICE_TAX_CGST"));
                                        invoiceCGSTAmoutEdt.setEnabled(false);

                                        invoiceSGSTAmoutEdt.setText(jsonobjectPredict.getString("INVOICE_TAX_SGST"));
                                        invoiceSGSTAmoutEdt.setEnabled(false);

                                        invoiceIGSTAmoutEdt.setText(jsonobjectPredict.getString("INVOICE_TAX_IGST"));
                                        invoiceIGSTAmoutEdt.setEnabled(false);

//                                            invoiceVatAmountEdt.setEnabled(false);
//                                            etDeliveredBy.setText(jsonobjectPredict.getString("ocr_text"));
//                                            etDeliveredBy.setEnabled(false);
                                        invoiceIGSTAmoutEdt.setText(jsonobjectPredict.getString("INVOICE_TAX_IGST"));
                                        invoiceIGSTAmoutEdt.setEnabled(false);
//                                        poNumberEdt.setText(jsonobjectPredict.getString("PO_NO"));

//                                            poNumberEdt.setEnabled(false);

                                        //poNumberEdt
                                        //IGST_Amount
//                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                showErrorType(error);
                            }
                        }
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Exception <<::>> " + ex.toString());
            showAlert(ex.toString());
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void clearFileds() {

        locationcode.setText("");
//        locationcode.setEnabled(true);
        poNumberEdt.setText("");
        invoiceAmountEdt.setText("");
        invoiceDateEdt.setText("");
        invoiceAmountEdt.setText("");
        invoiceVatAmountEdt.setText("");
        invoiceNumberEdt.setText("");
        inwardNumberEdit.setText("");
        etVendorCode.setText("");
        etVendorCode.setEnabled(true);
        invoiceCGSTAmoutEdt.setText("0");
        invoiceSGSTAmoutEdt.setText("0");
        invoiceIGSTAmoutEdt.setText("0");
        invoiceCESSAmoutEdt.setText("0");
        etDeliveredBy.setText(null);
        etDeliveredPersonPhno.setText(null);
        llManualGinNo.setVisibility(View.GONE);
//        poNumberEdt.requestFocus();
        capturedImg2.setImageDrawable(getResources().getDrawable(R.drawable.add_photo));

        mBaseCostValue = mDocumentId = invoiceCaptureType = "";
    }

    private void getManualInwardNo() {

        try {
            if (!poNumberEdt.getText().toString().trim().isEmpty()) {
                String apiUrl = storeip + "/values/mgin_validate/" + poNumberEdt.getText().toString().trim();

                Log.w(TAG, "getManualInwardNo URL " + apiUrl);

                ApiCall.make(this, apiUrl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Log.d(TAG, "checkManualGinNumber Response " + response);

                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                        //showAlert(jsonObject.getString("Message"));
                                        String ginNum = "";
                                        Pattern p = Pattern.compile("is " + "\\d+");
                                        Matcher m = p.matcher(jsonObject.getString("Message"));
                                        while (m.find()) {
                                            ginNum = m.group().replaceAll("[^0-9]", "");
                                            Log.e(TAG, "Gin NUmber " + ginNum);
                                        }

                                        showAlertDialog(jsonObject.getString("Message") +
                                                "\nDo you want to proceed to Gin processing?", ginNum);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(getApplicationContext(), "Error while checking for Inward number ", Toast.LENGTH_SHORT).show();
                                showErrorType(error);
                            }
                        }
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Exception <<::>> " + ex.toString());
            showAlert(ex.toString());
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void checkGinAvailForPoNumber() {

        pDialog = ProgressDialog.show(MGINUploadInvoiceTest_MainCopy.this, "",
                "Checking If Inward No already generated", true);
        pDialog.show();

        String apiUrl = storeip + "/values/mgin_validate/" + poNumberEdt.getText().toString().trim();

        ApiCall.make(this, apiUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pDialog.dismiss();

                        try {
                            Log.d(TAG, "checkGinAvailForPoNumber Response " + response);

                            JSONObject jsonObject = new JSONObject(response);
                            //jsonObject.getString("result").equalsIgnoreCase("Success")
                            //jsonObject.getString("StatusCode").equalsIgnoreCase("200")
                            if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                //showAlert(jsonObject.getString("Message"));

                                String ginNum = "";
                                Pattern p = Pattern.compile("is " + "\\d+");
                                Matcher m = p.matcher(jsonObject.getString("Message"));
                                while (m.find()) {
                                    ginNum = m.group().replaceAll("[^0-9]", "");
                                    Log.e(TAG, "Gin NUmber " + ginNum);
                                }

                                showAlertDialog(jsonObject.getString("Message") +
                                        "\nDo you want to proceed to Gin processing?", ginNum);

                                disableFields();


                            } else {
                                enableFields();

                                InvDB invDB = new InvDB(getApplicationContext());
                                invDB.open();
                                ArrayList<InvoiceDetails> Invoice = invDB.getInvoiceDetails(poNumberEdt.getText().toString().trim());
                                invDB.close();
                                if (Invoice.size() > 0) {
                                    View view = MGINUploadInvoiceTest_MainCopy.this.getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }

                                    //old 03/07/2022
                                    DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                    //new
//                                    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        Date date = sdf.parse(Invoice.get(0).getInvoiceDate());
                                        mInvoiceDate = new SimpleDateFormat("dd-MMM-yyyy").format(date);
                                        Log.e(TAG, "Invoice Data " + mInvoiceDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    locationcode.setText(Invoice.get(0).getLocCode());
                                    locationcode.setEnabled(false);
                                    //invoiceDateEdt.setText(Invoice.get(0).getInvoiceDate());
                                    //invoiceAmountEdt.setText(Invoice.get(0).getInvAmount());
                                    //invoiceVatAmountEdt.setText(Invoice.get(0).getInvVatAmount());
                                    inwardNumberEdit.setText(Invoice.get(0).getInwardNo());
                                    invoiceNumberEdt.setText(Invoice.get(0).getInvoiceNo());

                                    etVendorCode.setText(Invoice.get(0).getVendorCode());
                                    etVendorCode.setEnabled(false);

                                    etTcsPercentage.setText(Invoice.get(0).getTCS_PER());
                                    etTcsValue.setText(Invoice.get(0).getTCS_VAL());

                                    if (Invoice.get(0).getInvoiceTaxCGST() == null)
                                        invoiceCGSTAmoutEdt.setText("0");
                                    else
                                        invoiceCGSTAmoutEdt.setText(Invoice.get(0).getInvoiceTaxCGST());
                                    if (Invoice.get(0).getInvoiceTaxSGST() == null)
                                        invoiceSGSTAmoutEdt.setText("0");
                                    else
                                        invoiceSGSTAmoutEdt.setText(Invoice.get(0).getInvoiceTaxSGST());
                                    if (Invoice.get(0).getInvoiceTaxIGST() == null)
                                        invoiceIGSTAmoutEdt.setText("0");
                                    else
                                        invoiceIGSTAmoutEdt.setText(Invoice.get(0).getInvoiceTaxIGST());
                                    if (Invoice.get(0).getInvoiceTaxCESS() == null)
                                        invoiceCESSAmoutEdt.setText("0");
                                    else
                                        invoiceCESSAmoutEdt.setText(Invoice.get(0).getInvoiceTaxCESS());

                                }
                                else {

                                    locationcode.setText("");
                                    locationcode.setEnabled(true);
                                    etVendorCode.setText("");
                                    etVendorCode.setEnabled(true);

                                    invoiceDateEdt.setText("");
                                    invoiceAmountEdt.setText("");
                                    inwardNumberEdit.setText("");
                                    invoiceVatAmountEdt.setText("");
                                    invoiceNumberEdt.setText("");

                                    etTcsPercentage.setText("");
                                    etTcsValue.setText("");

                                    invoiceCGSTAmoutEdt.setText("0");
                                    invoiceSGSTAmoutEdt.setText("0");
                                    invoiceIGSTAmoutEdt.setText("0");
                                    invoiceCESSAmoutEdt.setText("0");

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String Url = storeip + "/values/hhtpo_details/" + poNumberEdt.getText().toString().trim();
                        makeFetchPOdetailsApiCall(Url);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error while checking for Inward number ", Toast.LENGTH_SHORT).show();
                        showErrorType(error);
                    }
                }
        );
    }

    private void getActionDetails() {
        String apiUrl = storeip + "/values/ocrStatus/";

        ApiCall.make(this, apiUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            Log.d(TAG, "getActionDetails Response " + response);

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("StatusCode").equalsIgnoreCase("200")) {
                                enableDisableButton = jsonObject.getString("flag");
                                Log.w(TAG, "EnableDisable Button Flag value <<::>> " + enableDisableButton);
                            } else
                                Log.w(TAG, "No Data Found!!");

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "getActionDetails Exception <<::>> " + e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e(TAG, "GetActionDetails Volley Error <<::>> " + error);
                        showErrorType(error);
                    }
                }
        );
    }

    public void captureImage() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;

            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (imageFile != null) {

                if (Build.VERSION.SDK_INT <= 19) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    outPutfileUri = Uri.fromFile(imageFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                    startActivityForResult(takePictureIntent, IMAGE_REQUEST);

                } else {
                    String AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider";

                    Uri imageUri = FileProvider.getUriForFile(this, AUTHORITY, imageFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(cameraIntent, IMAGE_REQUEST);
                }
            }
        }
    }

    Mat src, src_gray;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void displayImage(Uri imageUri) {

        Log.w(TAG, "Display Image###########");
        Log.e("TAG", "Display Image###########");

        try {
            File file = FileUtils.getFile(this, imageUri);
            InputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            File file1 = new File(currentImagePath.replaceAll("file:", ""));
            long length = file1.length();
            length = length / 1024;
            System.out.println("File Path : " + file1.getPath() + ", File size : " + length + " KB");

            if (length > 50000)
                Toast.makeText(this, "Image Size is too big.. ", Toast.LENGTH_SHORT).show();
            else {

                boolean isImageBlurred = isBlurredImage(bitmap);
                Log.w(TAG, "IS Image Blurred " + isImageBlurred);
                imageView.setImageBitmap(bitmap);
                if (isImageBlurred) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
                    builder.setTitle("HnGmBOS");
                    builder.setMessage("Captured photo is not clear. Please capture again and upload")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();

                                    imageView.setVisibility(View.VISIBLE);
                                    //imageView.setImageDrawable(null);
                                    //imageView.setVisibility(View.GONE);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                } else {

                    imagePathList.add(currentImagePath.replaceAll("file:", ""));

                    imageView.setVisibility(View.VISIBLE);

                    try {
                        Document document = new Document();

                        File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "InvoicePDF");

                        if (!folder_gui.exists()) {
                            folder_gui.mkdirs();
                        }

                        String directoryPath = Environment.getExternalStorageDirectory().toString() + File.separator + "InvoicePDF";

                        String path = directoryPath + "/Invoice_" + timeStamp + ".pdf";
                        PdfWriter.getInstance(document, new FileOutputStream(path)); //  Change pdf's name.

                        document.open();

//                        Image image = Image.getInstance(currentImagePath.replaceAll("file:", ""));  // Change image's name and extension.


                        //new method start
                        if (image2) {
                            Bitmap bm = ((BitmapDrawable) imageViewSh.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                            Image myImg = Image.getInstance(stream.toByteArray());
                            myImg.setAlignment(Image.MIDDLE);
                            document.add(myImg);
                            document.close();

//                            imageViewSh.
                        } else {
                            Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                            Image myImg = Image.getInstance(stream.toByteArray());
                            myImg.setAlignment(Image.MIDDLE);
                            document.add(myImg);
                            document.close();
                        }
                        //new method end

                        //old method start
//                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
//                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
//                        image.scalePercent(scaler);
//                        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                        //old method end

//                        document.add(image);
//                        document.close();

//                        new MyTask().execute(path);
//                        uploadImageToServer(path,0);
                        uploadImageToServerDup(path, 0);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Exception <<::>> " + e.toString());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception <<::>> " + e.toString());
        }

//        /*Log.w(TAG, "Current Image Path " + currentImagePath);
//        Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
//        imageView.setImageBitmap(bitmap);*/
//
//        /*src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
//        Utils.bitmapToMat(bitmap, src);
//        src_gray = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
//
//        Mat kernel = new Mat(3, 3, CvType.CV_16SC1);
//        Log.d(TAG, "imageType " + CvType.typeToString(src.type()) + "");
//        kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
//        Imgproc.filter2D(src, src, src_gray.depth(), kernel);
//
//        Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
//        Log.i(TAG, "imageType 2" + CvType.typeToString(src.type()) + "");
//        Utils.matToBitmap(src, processedImage);*/
//        //imageView.setImageBitmap(bitmap);
        Log.i(TAG, "process #### process done");
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void displayImageDup() {

        Log.w(TAG, "Display Image###########");
        Log.e("TAG", "Display Image###########");


        try {
            Document document = new Document();

            File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "InvoicePDF");

            if (!folder_gui.exists()) {
                folder_gui.mkdirs();
            }

            String directoryPath = Environment.getExternalStorageDirectory().toString() + File.separator + "InvoicePDF";

            String path = directoryPath + "/Invoice_" + timeStamp + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(path)); //  Change pdf's name.

            document.open();

//                        Image image = Image.getInstance(currentImagePath.replaceAll("file:", ""));  // Change image's name and extension.


            //new method start
            if (image2) {
                Bitmap bm = ((BitmapDrawable) imageViewSh.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Image myImg = Image.getInstance(stream.toByteArray());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 0) / myImg.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                myImg.scalePercent(scaler);
                myImg.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                document.add(myImg);
                document.close();

//                            imageViewSh.
            } else {
                Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                Image myImg = Image.getInstance(stream.toByteArray());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin() - 0) / myImg.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                myImg.scalePercent(scaler);
                myImg.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                document.add(myImg);
                document.close();
            }
            //new method end

            //old method start
//                        float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
//                                - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
//                        image.scalePercent(scaler);
//                        image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            //old method end

//                        document.add(image);
//                        document.close();

//                        new MyTask().execute(path);
//                        uploadImageToServer(path,0);
            uploadImageToServerDup(path, 0);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception <<::>> " + e.toString());
        }


//        /*Log.w(TAG, "Current Image Path " + currentImagePath);
//        Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
//        imageView.setImageBitmap(bitmap);*/
//
//        /*src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
//        Utils.bitmapToMat(bitmap, src);
//        src_gray = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC1);
//
//        Mat kernel = new Mat(3, 3, CvType.CV_16SC1);
//        Log.d(TAG, "imageType " + CvType.typeToString(src.type()) + "");
//        kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
//        Imgproc.filter2D(src, src, src_gray.depth(), kernel);
//
//        Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
//        Log.i(TAG, "imageType 2" + CvType.typeToString(src.type()) + "");
//        Utils.matToBitmap(src, processedImage);*/
//        //imageView.setImageBitmap(bitmap);
        Log.i(TAG, "process #### process done");
    }

    private class MyTask extends AsyncTask<okhttp3.Request, Void, String> {


        @Override
        protected String doInBackground(okhttp3.Request... requests) {

            try {
                OkHttpClient client = new OkHttpClient();
                client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                okhttp3.Response response = client.newCall(requests[0]).execute();
                jsonObject = new JSONObject(getResponse(response));


            } catch (Exception e) {
                e.printStackTrace();
            }

            if (jsonObject.length() == 0)
                return "0";
            else
                return "1";
        }

        protected void onPreExecute() {
            //any specific setup before you start copy() method ,  runs on UI
            // thread
            progressDialog = ProgressDialog.show(MGINUploadInvoiceTest_MainCopy.this, "",
                    "Please wait..", true);
            progressDialog.show();
            Log.e("onPreExecute", "onPreExecute");

        }


        protected void onProgressUpdate(Void... params) {
            //any event you wanna perform , while the task is in progress, runs on
            //UI main thread
            Log.e("onProgressUpdate", "onProgressUpdate");
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }

        protected void onPostExecute(String params) {
            //the event you wanna perform once your copy() method is complete, runs
            //on UI main thread
            Log.e("onPostExecute", "onPostExecute");

            image2 = false;
            imageView.setVisibility(View.GONE);
            capturedImg2.setVisibility(View.GONE);
            imageViewSh.setVisibility(View.GONE);
            submit_btn.setVisibility(View.GONE);
            iv_pdf.setVisibility(View.GONE);
            img_bitmap_list.clear();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (params.equals("1")) {
                try {
                    if (jsonObject.getString("message").equalsIgnoreCase("Success")) {
//                progressDialog.dismiss();

                        JSONArray jsonArray = jsonObject.getJSONArray("result");

                        JSONObject jsonObject2 = jsonArray.getJSONObject(0);

                        if (jsonObject2.getString("message").equalsIgnoreCase("Success")) {
                            Log.e("jsonObject2", jsonObject2.toString());

                            JSONArray jsonArrayPrediction = jsonObject2.getJSONArray("prediction");

                            for (int i = 0; i < jsonArrayPrediction.length(); i++) {
                                JSONObject jsonobjectPredict = jsonArrayPrediction.getJSONObject(i);

                                if (jsonobjectPredict.getString("label").equalsIgnoreCase("invoice_date")) {


                        /* SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
                         SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                         Date date;
                         try {

                             String ocr_date_text ="23-Dec-2020";
                             date = originalFormat.parse(ocr_date_text);
                             System.out.println("Old Format :   " + originalFormat.format(date));
                             System.out.println("New Format :   " + targetFormat.format(date));

                             invoiceDateEdt.setText(targetFormat.format(date));
                             invoiceDateEdt.setEnabled(false);
                             invoiceDateEdt.setFocusable(false);

                         } catch (ParseException ex) {
                             // Handle Exception.
                             ex.getMessage();
                         }*/

                                    invoiceDateEdt.setText(jsonobjectPredict.getString("ocr_text"));
                                    invoiceDateEdt.setEnabled(false);
                                } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("invoice_no")) {
                                    invoiceNumberEdt.setText(jsonobjectPredict.getString("ocr_text"));
                                    invoiceNumberEdt.setEnabled(false);
                                } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("INVOICE_AMOUNT")) {
                                    invoiceAmountEdt.setText(jsonobjectPredict.getString("ocr_text"));
                                    invoiceAmountEdt.setEnabled(false);
                                } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("SGST_Amount")) {
                                    invoiceSGSTAmoutEdt.setText(jsonobjectPredict.getString("ocr_text"));
                                    invoiceSGSTAmoutEdt.setEnabled(false);
                                } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("CGST_Amount")) {
                                    invoiceCGSTAmoutEdt.setText(jsonobjectPredict.getString("ocr_text"));
                                    invoiceCGSTAmoutEdt.setEnabled(false);
                                } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("Total_Tax_Amount")) {
                                    invoiceVatAmountEdt.setText(jsonobjectPredict.getString("ocr_text"));
                                    invoiceVatAmountEdt.setEnabled(false);
                                } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("Vendor_Name")) {
                                    etDeliveredBy.setText(jsonobjectPredict.getString("ocr_text"));
                                    etDeliveredBy.setEnabled(false);
                                } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("IGST_Amount")) {
                                    invoiceIGSTAmoutEdt.setText(jsonobjectPredict.getString("ocr_text"));
                                    invoiceIGSTAmoutEdt.setEnabled(false);
//                                    invoiceIGSTAmoutEdt
                                } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("PO_number")) {
                                    poNumberEdt.setText(jsonobjectPredict.getString("ocr_text"));
                                    poNumberEdt.setEnabled(false);
//                                    invoiceIGSTAmoutEdt
                                }
                                //poNumberEdt
                                //IGST_Amount
                            }
                        }

                /*JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonArray = jsonObject1.getJSONArray("document");

                JSONObject jsonObject2 = jsonArray.getJSONObject(0);
                String docID = jsonObject2.getString("doc_id");
                Log.w(TAG, "Document ID " + docID);

                apiCallingCnt = 0;

                String docURl = "https://app.docsumo.com/api/v1/eevee/apikey/data/" + docID + "/";
                sendImageDocumentID(docURl, docID);*/
                    } else {
//                progressDialog.dismiss();
                        Log.e("Else", jsonObject.getString("message"));
                        showAlert(jsonObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("onPostExecute", "else");
            }


        }

    }


    private void pickFromGallery() {
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private void uploadImageToServer(String path, int fileType) {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();

        progressDialog = ProgressDialog.show(MGINUploadInvoiceTest_MainCopy.this, "",
                "Processing Document.. Please wait..", true);
        progressDialog.show();

        Log.e("uploadImageToServer", path);

        try {
            Log.e("uploadImageToServer", "try");

            FileInputStream fileInputStream = null;
            byte[] bytesArray = null;

            try {
                File file = new File(path);
                bytesArray = new byte[(int) file.length()];

                //read file into bytes[]
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bytesArray);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            String bytesEncoded = Base64.encodeToString(bytesArray, 0);

            String directoryPath = Environment.getExternalStorageDirectory().toString() + File.separator + "InvoicePDF/AAInvoice_.pdf";

            File logfile = Environment.getExternalStorageDirectory();
            File myFile = new File(logfile.getAbsolutePath() + "/mBOSlog/");

            if (!myFile.exists()) {
                myFile.mkdir();
            }
            File mLogFile = new File(myFile, "Base65.txt");
            if (!mLogFile.exists()) {
                try {
                    mLogFile.createNewFile();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else {
                mLogFile.delete();
                try {
                    mLogFile.createNewFile();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
//            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();

            final FileWriter fileOut = new FileWriter(mLogFile, true);
            fileOut.append(bytesEncoded);
            fileOut.close();
//            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            Log.e(TAG, "LOG FILE###############");

            final File dwldsPath = new File(directoryPath);
            byte[] pdfAsBytes = Base64.decode(bytesEncoded, 0);
            FileOutputStream os;
            os = new FileOutputStream(dwldsPath, false);
            os.write(pdfAsBytes);
            os.flush();
            os.close();


            progressDialog.show();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Log.w(TAG, "PATH##### " + path);


            OkHttpClient client = new OkHttpClient();
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            MediaType MEDIA_TYPE;
            if (fileType == 0) {
                MEDIA_TYPE = MediaType.parse("image/jpeg");
            } else {
                MEDIA_TYPE = MediaType.parse("application/pdf");
            }


            RequestBody requestBody = new MultipartBody.Builder()

                    .setType(MultipartBody.FORM)

                    .addFormDataPart("file", path, RequestBody.create(MEDIA_TYPE, new File(path)))

                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("https://app.nanonets.com/api/v2/OCR/Model/f4195852-b39f-4034-8fff-856292453e69/LabelFile/")

                    .post(requestBody)

                    .addHeader("Authorization", Credentials.basic("Wn5YE5EFTkHwzXs_Gln8UKaZ174L7zJv", ""))

                    .build();


            okhttp3.Response response = client.newCall(request).execute();


            JSONObject jsonObject = new JSONObject(getResponse(response));
            Log.e("uploadImageToServer", "JSON Object " + jsonObject);


            if (jsonObject.getString("message").equalsIgnoreCase("Success")) {
                progressDialog.dismiss();

                JSONArray jsonArray = jsonObject.getJSONArray("result");

                JSONObject jsonObject2 = jsonArray.getJSONObject(0);

                if (jsonObject2.getString("message").equalsIgnoreCase("Success")) {
                    Log.e("jsonObject2", jsonObject2.toString());

                    JSONArray jsonArrayPrediction = jsonObject2.getJSONArray("prediction");

                    for (int i = 0; i < jsonArrayPrediction.length(); i++) {
                        JSONObject jsonobjectPredict = jsonArrayPrediction.getJSONObject(i);

                        if (jsonobjectPredict.getString("label").equalsIgnoreCase("invoice_date")) {


                        /* SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
                         SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                         Date date;
                         try {

                             String ocr_date_text ="23-Dec-2020";
                             date = originalFormat.parse(ocr_date_text);
                             System.out.println("Old Format :   " + originalFormat.format(date));
                             System.out.println("New Format :   " + targetFormat.format(date));

                             invoiceDateEdt.setText(targetFormat.format(date));
                             invoiceDateEdt.setEnabled(false);
                             invoiceDateEdt.setFocusable(false);

                         } catch (ParseException ex) {
                             // Handle Exception.
                             ex.getMessage();
                         }*/

                            invoiceDateEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceDateEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("invoice_no")) {
                            invoiceNumberEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceNumberEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("INVOICE_AMOUNT")) {
                            invoiceAmountEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceAmountEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("SGST_Amount")) {
                            invoiceSGSTAmoutEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceSGSTAmoutEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("CGST_Amount")) {
                            invoiceCGSTAmoutEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceCGSTAmoutEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("Total_Tax_Amount")) {
                            invoiceVatAmountEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceVatAmountEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("Vendor_Name")) {
                            etDeliveredBy.setText(jsonobjectPredict.getString("ocr_text"));
                            etDeliveredBy.setEnabled(false);
                        }
                    }
                }

                /*JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonArray = jsonObject1.getJSONArray("document");

                JSONObject jsonObject2 = jsonArray.getJSONObject(0);
                String docID = jsonObject2.getString("doc_id");
                Log.w(TAG, "Document ID " + docID);

                apiCallingCnt = 0;

                String docURl = "https://app.docsumo.com/api/v1/eevee/apikey/data/" + docID + "/";
                sendImageDocumentID(docURl, docID);*/
            } else {
                progressDialog.dismiss();
                Log.e("Else", jsonObject.getString("message"));
                showAlert(jsonObject.getString("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.toString());
            Log.e("UISException", e.toString());

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }

    }

    private void uploadImageToServerDup(String path, int fileType) {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();

        progressDialog = ProgressDialog.show(MGINUploadInvoiceTest_MainCopy.this, "",
                "Processing Document.. Please wait..", true);
        progressDialog.show();

        Log.e("uploadImageToServerDup", path);

        try {
            Log.e("uploadImageToServerDup", "try");

            FileInputStream fileInputStream = null;
            byte[] bytesArray = null;

            try {
                File file = new File(path);
                bytesArray = new byte[(int) file.length()];

                //read file into bytes[]
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bytesArray);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            String bytesEncoded = Base64.encodeToString(bytesArray, 0);

            String directoryPath = Environment.getExternalStorageDirectory().toString() + File.separator + "InvoicePDF/AAInvoice_.pdf";

            File logfile = Environment.getExternalStorageDirectory();
            File myFile = new File(logfile.getAbsolutePath() + "/mBOSlog/");

            if (!myFile.exists()) {
                myFile.mkdir();
            }
            File mLogFile = new File(myFile, "Base65.txt");
            if (!mLogFile.exists()) {
                try {
                    mLogFile.createNewFile();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } else {
                mLogFile.delete();
                try {
                    mLogFile.createNewFile();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
//            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();

            final FileWriter fileOut = new FileWriter(mLogFile, true);
            fileOut.append(bytesEncoded);
            fileOut.close();
//            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            Log.e(TAG, "LOG FILE###############");
//
//            final File dwldsPath = new File(directoryPath);
//            byte[] pdfAsBytes = Base64.decode(bytesEncoded, 0);
//            FileOutputStream os;
//            os = new FileOutputStream(dwldsPath, false);
//            os.write(pdfAsBytes);
//            os.flush();
//            os.close();


            progressDialog.show();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Log.e("PATH", "PATH##### " + path);


            OkHttpClient client = new OkHttpClient();
            client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            MediaType MEDIA_TYPE;
            if (fileType == 0) {
                MEDIA_TYPE = MediaType.parse("image/jpeg");
            } else {
                MEDIA_TYPE = MediaType.parse("application/pdf");
            }
            RequestBody requestBody = new MultipartBody.Builder()

                    .setType(MultipartBody.FORM)

                    .addFormDataPart("file", path, RequestBody.create(MEDIA_TYPE, file_pdf))

                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("https://app.nanonets.com/api/v2/OCR/Model/f4195852-b39f-4034-8fff-856292453e69/LabelFile/")

                    .post(requestBody)

                    .addHeader("Authorization", Credentials.basic("Wn5YE5EFTkHwzXs_Gln8UKaZ174L7zJv", ""))

                    .build();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            new MyTask().execute(request);


        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.toString());
            Log.e("UISException", e.toString());

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }

    }


    private void uploadPdfToServer(String path) {

        Log.e("uploadPdfToServer", path);

        try {

            OkHttpClient client = new OkHttpClient();

            final MediaType MEDIA_TYPE_JPG = MediaType.parse("application/pdf");


            RequestBody requestBody = new MultipartBody.Builder()

                    .setType(MultipartBody.FORM)

                    .addFormDataPart("file", path, RequestBody.create(MEDIA_TYPE_JPG, new File(path)))

                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url("https://app.nanonets.com/api/v2/OCR/Model/f4195852-b39f-4034-8fff-856292453e69/LabelFile/")

                    .post(requestBody)

                    .addHeader("Authorization", Credentials.basic("Wn5YE5EFTkHwzXs_Gln8UKaZ174L7zJv", ""))

                    .build();


            okhttp3.Response response = client.newCall(request).execute();

            JSONObject jsonObject = new JSONObject(getResponse(response));
            Log.e("uploadImageToServer", "JSON Object " + jsonObject);


            if (jsonObject.getString("message").equalsIgnoreCase("Success")) {
                progressDialog.dismiss();

                JSONArray jsonArray = jsonObject.getJSONArray("result");

                JSONObject jsonObject2 = jsonArray.getJSONObject(0);

                if (jsonObject2.getString("message").equalsIgnoreCase("Success")) {
                    Log.e("jsonObject2", jsonObject2.toString());

                    JSONArray jsonArrayPrediction = jsonObject2.getJSONArray("prediction");

                    for (int i = 0; i < jsonArrayPrediction.length(); i++) {
                        JSONObject jsonobjectPredict = jsonArrayPrediction.getJSONObject(i);

                        if (jsonobjectPredict.getString("label").equalsIgnoreCase("invoice_date")) {


                        /* SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
                         SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                         Date date;
                         try {

                             String ocr_date_text ="23-Dec-2020";
                             date = originalFormat.parse(ocr_date_text);
                             System.out.println("Old Format :   " + originalFormat.format(date));
                             System.out.println("New Format :   " + targetFormat.format(date));

                             invoiceDateEdt.setText(targetFormat.format(date));
                             invoiceDateEdt.setEnabled(false);
                             invoiceDateEdt.setFocusable(false);

                         } catch (ParseException ex) {
                             // Handle Exception.
                             ex.getMessage();
                         }*/

                            invoiceDateEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceDateEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("invoice_no")) {
                            invoiceNumberEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceNumberEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("INVOICE_AMOUNT")) {
                            invoiceAmountEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceAmountEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("SGST")) {
                            invoiceSGSTAmoutEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceSGSTAmoutEdt.setEnabled(false);
                        } else if (jsonobjectPredict.getString("label").equalsIgnoreCase("INVOICE_TAX_AMT")) {
                            invoiceVatAmountEdt.setText(jsonobjectPredict.getString("ocr_text"));
                            invoiceVatAmountEdt.setEnabled(false);
                        }
                    }
                }

                /*JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonArray = jsonObject1.getJSONArray("document");

                JSONObject jsonObject2 = jsonArray.getJSONObject(0);
                String docID = jsonObject2.getString("doc_id");
                Log.w(TAG, "Document ID " + docID);

                apiCallingCnt = 0;

                String docURl = "https://app.docsumo.com/api/v1/eevee/apikey/data/" + docID + "/";
                sendImageDocumentID(docURl, docID);*/
            } else {
                progressDialog.dismiss();
                Log.e("Else", jsonObject.getString("message"));
                showAlert(jsonObject.getString("message"));

            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(e.toString());
            Log.e("UISException", e.toString());

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }


    private void sendImageDocumentID(final String docURl, final String documentId) {

        try {

            Log.w(TAG, "DODUMENT URl " + docURl);

            mBaseCostValue = mDocumentId = invoiceCaptureType = "";
            RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, docURl, null,
                    new Response.Listener<JSONObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.w(TAG, "RESPONSE DOC ID ###### " + response.toString());
                            Log.w(TAG, "apiCallingCnt<<<::>>>> " + apiCallingCnt);
                            try {

                                if (!response.getString("status_code").equalsIgnoreCase("200")) {

                                    Log.w(TAG, "Error Response " + response.toString());
                                    //Toast.makeText(MGINUploadInvoiceTest.this, response.toString(), Toast.LENGTH_SHORT).show();

                                    if (apiCallingCnt < 5) {

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Do something after 10sec
                                                apiCallingCnt++;
                                                sendImageDocumentID(docURl, documentId);
                                            }
                                        }, 15000);

                                    } else {
                                        showAlert("Time Out Error.. Unable to process the document");
                                        if (progressDialog != null && progressDialog.isShowing())
                                            progressDialog.dismiss();
                                    }

                                } else {
                                    if (progressDialog != null && progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    Log.w(TAG, "Data Fetched Successfully!!!!!");

                                    Toast.makeText(MGINUploadInvoiceTest_MainCopy.this, response.toString(), Toast.LENGTH_SHORT).show();

                                    JSONObject jsonObject = new JSONObject(response.getString("data"));
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("Invoice Detail");

                                    llInvoiceDetails.setVisibility(View.VISIBLE);

                                    Iterator<String> keys = jsonObject1.keys();

                                    HashMap<String, String> data = new HashMap<>();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        Log.v(TAG, "********************");
                                        Log.v(TAG, "Invoice key " + key);

                                        JSONObject innerJObject = jsonObject1.getJSONObject(key);
                                        Iterator<String> innerKeys = innerJObject.keys();

                                        String value = innerJObject.getString("value");
                                        Log.v(TAG, "key = " + key + " = " + value);

                                        data.put(key, value);
                                    }
                                    data.put("docId", documentId);
                                    setProcessedData(data);
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.e(TAG, "Exception Localied Message############## " + ex.getLocalizedMessage());
                                Toast.makeText(MGINUploadInvoiceTest_MainCopy.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                if (progressDialog != null && progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                            showVolleyErrorAlert(error, "GET DOCUMENT ERROR");
                        }
                    }) {
                /** Passing some request headers* */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    //headers.put("Content-Type", "application/json");
                    headers.put("apiKey", "xiPqZkpz3kkMh59jWWzy0lVYqsU3wfGgSRTgkiDYB43gD9JWMbbtSSYLJ96n");
                    return headers;
                }

            };
            requestQueue.add(jsonObjReq);

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Exception <<::>> " + ex.toString());
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private String getResponse(okhttp3.Response response) {
        try {
            return response.body().string();
        } catch (Exception e) {

            Log.e(TAG, "Exception <<::>> " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    private void uploadProcessedData() {
        try {

            String InvoiceNumber = invoiceNumberEdt.getText().toString();
            String poNumber = poNumberEdt.getText().toString();
            String invoiceDate = invoiceDateEdt.getText().toString();
            String invoiceAmount = invoiceAmountEdt.getText().toString();
            String cgst = invoiceCGSTAmoutEdt.getText().toString();
            String sgst = invoiceSGSTAmoutEdt.getText().toString();
            String igst = invoiceIGSTAmoutEdt.getText().toString();
            String cess = invoiceCESSAmoutEdt.getText().toString();
            String totalTaxVal = invoiceVatAmountEdt.getText().toString();
            String vendorCode = etVendorCode.getText().toString().trim();
            String locationCode = locationcode.getText().toString().trim();

            if (invoiceCaptureType.equalsIgnoreCase("OCR")) {
                mRequestBody = "{\"InvoiceNumber\":\"" + InvoiceNumber + "\"," +
                        "\"PoNumber\":\"" + poNumber + "\"," +
                        "\"InvoiceDate\":\"" + invoiceDate + "\"," +
                        "\"InvoiceAmount\":\"" + invoiceAmount + "\"," +
                        "\"CGST\":\"" + cgst + "\"," +
                        "\"SGST\":\"" + sgst + "\"," +
                        "\"IGST\":\"" + igst + "\"," +
                        "\"CESS\":\"" + cess + "\"," +
                        "\"TotalTaxValue\":\"" + totalTaxVal + "\"," +
                        "\"File\":\"" + readFromFile() + "\"," +
                        "\"BaseCostValue\":\"" + mBaseCostValue.replaceAll(",", "") + "\" ," +
                        "\"LocationCode\":\"" + locationCode + "\" ," +
                        "\"DocumentId\":\"" + mDocumentId + "\" ," +
                        "\"inv_capture_type\":\"" + invoiceCaptureType + "\" ," +
                        "\"VendorCode\":\"" + vendorCode + "\" }";
            } else {
                mRequestBody = "{\"InvoiceNumber\":\"" + InvoiceNumber + "\"," +
                        "\"PoNumber\":\"" + poNumber + "\"," +
                        "\"InvoiceDate\":\"" + invoiceDate + "\"," +
                        "\"InvoiceAmount\":\"" + invoiceAmount + "\"," +
                        "\"CGST\":\"" + cgst + "\"," +
                        "\"SGST\":\"" + sgst + "\"," +
                        "\"IGST\":\"" + igst + "\"," +
                        "\"CESS\":\"" + cess + "\"," +
                        "\"TotalTaxValue\":\"" + totalTaxVal + "\"," +
                        "\"File\":\"" + "" + "\"," +
                        "\"BaseCostValue\":\"" + "" + "\" ," +
                        "\"LocationCode\":\"" + locationCode + "\" ," +
                        "\"DocumentId\":\"" + mDocumentId + "\" ," +
                        "\"inv_capture_type\":\"" + invoiceCaptureType + "\" ," +
                        "\"VendorCode\":\"" + vendorCode + "\" }";
            }

            Log.w(TAG, "mRequestBody############## ");
            Log.w(TAG, "Invoice Number <<::>> " + InvoiceNumber);
            Log.w(TAG, "Po Number <<::>> " + poNumber);
            Log.w(TAG, "Invoice Date <<::>> " + invoiceDate);
            Log.w(TAG, "Invoice Amount <<::>> " + invoiceAmount);
            Log.w(TAG, "CGST <<::>> " + cgst);
            Log.w(TAG, "SGST <<::>> " + sgst);
            Log.w(TAG, "IGST <<::>> " + igst);
            Log.w(TAG, "CESS <<::>> " + cess);
            Log.w(TAG, "totalTaxVal <<::>> " + totalTaxVal);
            Log.w(TAG, "Location Code <<::>> " + locationCode);
            Log.w(TAG, "Document ID <<::>> " + mDocumentId);
            Log.w(TAG, "invoice capture typp <<::>> " + invoiceCaptureType);
            Log.w(TAG, "Vendor code <<::>> " + vendorCode);

            mRequestBody = mRequestBody.replaceAll("\\s", "");

            String url = "http://36.255.252.212/api/Uploaddoc/Upload";
            RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {

                    try {
                        Log.i(TAG, "UPLOAD DOC DETAILS Api Call response<<::>>>> " + response);
                        JSONObject Jsonobj = new JSONObject(response);

                    } catch (Exception e) {

                        Log.d(TAG, "Exception Occured during Document data Upload " + e.getLocalizedMessage());
                        e.printStackTrace();

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showVolleyErrorAlert(error, "Upload PDF document");
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
            ex.printStackTrace();
            Log.e(TAG, "Error<<::>> " + ex.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setProcessedData(HashMap<String, String> data) {

        String poNum = poNumberEdt.getText().toString().trim();
        if (data.get("PO No") != null && !data.get("PO No").equalsIgnoreCase(poNum))
            showAlert("Invoice PO Number doesn't match with the entered PO Number");

        invoiceNumberEdt.setText(data.get("Invoice No"));

        try {
            String mCGST = data.containsKey("CGST") ? data.get("CGST").replace(",", "") : "0";
            invoiceCGSTAmoutEdt.setText(mCGST.replaceAll("\\s", ""));
        } catch (Exception e) {
            Log.e(TAG, "setProcessedData Exception CGST <<::>> " + e.toString());
            e.printStackTrace();
        }

        try {
            String mSGST = data.containsKey("SGST") ? data.get("SGST").replace(",", "") : "0";
            invoiceSGSTAmoutEdt.setText(mSGST.replaceAll("\\s", ""));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setProcessedData Exception SGST <<::>> " + e.toString());
        }

        try {
            String mIGST = data.containsKey("IGST") ? data.get("IGST").replace(",", "") : "0";
            invoiceIGSTAmoutEdt.setText(mIGST.replaceAll("\\s", ""));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setProcessedData Exception IGST <<::>> " + e.toString());
        }

        try {
            String mCESS = data.containsKey("CESS") ? data.get("CESS").replace(",", "") : "0";
            invoiceCESSAmoutEdt.setText(mCESS.replaceAll("\\s", ""));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setProcessedData Exception CESS<<::>> " + e.toString());
        }

        try {
            String totalVal = data.get("Total Value").replace("*", "");
            invoiceAmountEdt.setText(totalVal.replace(",", ""));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "setProcessedData Exception Total Value <<::>> " + e.toString());
        }

        invoiceVatAmountEdt.setText(data.get("Total Tax value").replace(",", ""));

        mBaseCostValue = data.get("Base Cost value");
        mDocumentId = data.get("docId");
        invoiceCaptureType = "OCR";

        if (data.containsKey("Invoice date") && !Objects.requireNonNull(data.get("Invoice date")).isEmpty()) {

            if (data.get("Invoice date").contains("-")) {
                String[] dateSplit = data.get("Invoice date").split("-");
                String date = null;
                if (dateSplit[0].length() == 1)
                    date = "0" + data.get("Invoice date");
                else
                    date = data.get("Invoice date");

                Log.w(TAG, "Length ######## " + dateSplit[0].length());
                Log.w(TAG, "Invoice Date " + date);
                try {
                    invoiceDateEdt.setText(convertDataFormat(date));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "IfLoop setProcessData Invoice Date <<::>> " + ex.toString());
                }

            } else if (data.get("Invoice date").contains("/")) {

                String date1 = data.get("Invoice date");
                boolean containsAlpha = isAlpha(date1);
                Log.w(TAG, "Does contains Alphabet " + containsAlpha);
                if (!containsAlpha) {
                    Log.w(TAG, "Getting the correct month####### " + date1);
                    Date myDate = null;

                    try {
                        myDate = sdf1.parse(date1);

                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e(TAG, "ElseLoop setProcessData Invoice Date <<::>> " + e.toString());
                    }

                    SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
                    mInvoiceDate = timeFormat.format(myDate);
                    invoiceDateEdt.setText(mInvoiceDate);
                }
            }
        }

        //uploadProcessedData();
    }

    private boolean isAlpha(String s) {

        Pattern p = Pattern.compile("^[a-zA-Z]*$");
        return p.matcher(s).find();
    }

    final static int KERNAL_WIDTH = 3;
    final static int KERNAL_HEIGHT = 3;

    int[][] kernalBlur = {
            {0, -1, 0},
            {-1, 5, -1},
            {0, -1, 0}
    };
    Bitmap afterSharpen;
    ImageView imageViewSh, iv_pdf;

    Mat imageMat, matImageGrey, laplacianImage, laplacianImage8bit;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i("OpenCV", "OpenCV loaded successfully");
                imageMat = new Mat();
                matImageGrey = new Mat();
                laplacianImage = new Mat();
                laplacianImage8bit = new Mat();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private synchronized boolean isBlurredImage(Bitmap image) {
        try {
            if (image != null) {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inDither = true;
                opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
                int l = CvType.CV_8UC1;

                imageMat = new Mat();
                Utils.bitmapToMat(image, imageMat);
                matImageGrey = new Mat();
                Imgproc.cvtColor(imageMat, matImageGrey, Imgproc.COLOR_BGR2GRAY);

                Mat dst2 = new Mat();
                Utils.bitmapToMat(image, dst2);

                laplacianImage = new Mat();
                dst2.convertTo(laplacianImage, l);
                Imgproc.Laplacian(matImageGrey, laplacianImage, CvType.CV_8U);
                laplacianImage8bit = new Mat();
                laplacianImage.convertTo(laplacianImage8bit, l);
                System.gc();

                Bitmap bmp = Bitmap.createBitmap(laplacianImage8bit.cols(),
                        laplacianImage8bit.rows(), Bitmap.Config.ARGB_8888);

                Utils.matToBitmap(laplacianImage8bit, bmp);

                int[] pixels = new int[bmp.getHeight() * bmp.getWidth()];
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
                        bmp.getHeight());
                if (bmp != null)
                    if (!bmp.isRecycled()) {
                        bmp.recycle();

                    }
                int maxLap = -16777216;

                for (int i = 0; i < pixels.length; i++) {
                    if (pixels[i] > maxLap) {
                        maxLap = pixels[i];
                    }
                }
                int soglia = -6118750;
                if (maxLap < soglia || maxLap == soglia) {
                    return true;
                } else
                    return false;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        } catch (OutOfMemoryError e) {
            return false;
        }
    }

    private File getImageFile() throws IOException {
        String imageName = "jpg_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/mBOSPictures");
        if (!storageDir.exists()) {
            File wallpaperDirectory = new File("/sdcard/mBOSPictures/");
            wallpaperDirectory.mkdirs();
        }

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = "file:" + imageFile.getAbsolutePath();
        Log.w(TAG, "ImageFile " + imageName);
        return imageFile;
    }

    private void disableFields() {

        locationcode.setEnabled(false);
        invoiceDateEdt.setEnabled(false);
        invoiceAmountEdt.setEnabled(false);
        inwardNumberEdit.setEnabled(false);
        invoiceVatAmountEdt.setEnabled(false);
        invoiceNumberEdt.setEnabled(false);

        etVendorCode.setEnabled(false);
        invoiceCGSTAmoutEdt.setEnabled(false);
        invoiceSGSTAmoutEdt.setEnabled(false);
        invoiceIGSTAmoutEdt.setEnabled(false);
        invoiceCESSAmoutEdt.setEnabled(false);
        etDeliveredBy.setEnabled(false);
        etDeliveredPersonPhno.setEnabled(false);
        etTcsPercentage.setEnabled(false);
        etTcsValue.setEnabled(false);

        invoiceCGSTAmoutEdt.setText("0");
        invoiceSGSTAmoutEdt.setText("0");
        invoiceIGSTAmoutEdt.setText("0");
        invoiceCESSAmoutEdt.setText("0");
    }

    private void enableFields() {

        locationcode.setEnabled(true);
        invoiceDateEdt.setEnabled(true);
        invoiceAmountEdt.setEnabled(true);
        inwardNumberEdit.setEnabled(true);
        invoiceVatAmountEdt.setEnabled(true);
        invoiceNumberEdt.setEnabled(true);

        etVendorCode.setEnabled(true);
        invoiceCGSTAmoutEdt.setEnabled(true);
        invoiceSGSTAmoutEdt.setEnabled(true);
        invoiceIGSTAmoutEdt.setEnabled(true);
        invoiceCESSAmoutEdt.setEnabled(true);
        etDeliveredBy.setEnabled(true);
        etDeliveredPersonPhno.setEnabled(true);
        etTcsPercentage.setEnabled(true);
        etTcsValue.setEnabled(true);

        invoiceCGSTAmoutEdt.setText("0");
        invoiceSGSTAmoutEdt.setText("0");
        invoiceIGSTAmoutEdt.setText("0");
        invoiceCESSAmoutEdt.setText("0");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.htt_next:
                Log.e("htt_next", "htt_next");
                Log.d(TAG, "IS Everything valid " + validate());

                /*mGINNo = "12";
                responseValue = 3;
                showAlertDialog("Your Manual Inward number for the PO Number " + poNumberEdt.getText().toString() + " is " + mGINNo +
                        "\nDo you want to proceed to Gin processing?", mGINNo, responseValue);*/

                if (validate()) {

                    if (poInvalid) {
                        Log.w(TAG, "PO is invalid");
                        Toast toast = Toast.makeText(getApplicationContext(), "PO is invalid", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        View view = toast.getView();
                        view.setBackgroundResource(R.drawable.custom_background);
                        toast.show();
                        poNumberEdt.requestFocus();
                    } else {

                        Log.d(TAG, "TCS Percentage <<::>> " + etTcsPercentage.getText().toString());
                        Log.d(TAG, "TCS Amount <<::>> " + etTcsValue.getText().toString());

                        Spanned message = Html.fromHtml("<b>" + "Invoice Number -" + "</b>" + invoiceNumberEdt.getText().toString().trim() + "<br>" +
                                "<b>" + "Invoice Date - " + "</b>" + invoiceDateEdt.getText().toString().trim() + "<br>" +
                                "<b>" + "Invoice Amount - " + "</b>" + invoiceAmountEdt.getText().toString().trim() + "<br>" +
                                "<b>" + "Total Tax Amount - " + "</b>" + invoiceVatAmountEdt.getText().toString().trim() + "<br>" +
                                "<b>" + "TCS Percentage - " + "</b>" + (etTcsPercentage.getText().toString().trim().isEmpty() ? "0" : etTcsPercentage.getText().toString().trim()) + "<br>" +
                                "<b>" + "TCS Amount - " + "</b>" + (etTcsValue.getText().toString().trim().isEmpty() ? "0" : etTcsValue.getText().toString().trim()) + "<br>" +
                                "<b>" + "IRN - " + irnNumberValue + "<br><br>" +

                                "<b>" + "Kindly verify above information and proceed" + "</b>");

                        Log.w(TAG, "Message#####\n " + message);

                        AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
                        builder.setTitle("HnGmBOS");
                        builder.setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        Log.d(TAG, "Everything is filled == SUCCESS");
                                        if (invoiceCaptureType.isEmpty())
                                            invoiceCaptureType = "Manual";
                                        uploadProcessedData();

                                        HashMap<String, String> inv_data = new HashMap<String, String>();

                                        inv_data.put("POno", poNumberEdt.getText().toString().trim());
                                        inv_data.put("LocCode", locationcode.getText().toString().trim());
                                        inv_data.put("POExpiry", "");
                                        inv_data.put("InvNo", invoiceNumberEdt.getText().toString().trim());
                                        inv_data.put("InvDate", invoiceDateEdt.getText().toString().trim());
                                        inv_data.put("InvAmt", invoiceAmountEdt.getText().toString().trim());
                                        inv_data.put("InvVatAmt", invoiceVatAmountEdt.getText().toString().trim());
                                        inv_data.put("VendorCode", etVendorCode.getText().toString().trim());
                                        inv_data.put("invoiceTaxCGST", invoiceCGSTAmoutEdt.getText().toString().trim());
                                        inv_data.put("invoiceTaxSGST", invoiceSGSTAmoutEdt.getText().toString().trim());
                                        inv_data.put("invoiceTaxIGST", invoiceIGSTAmoutEdt.getText().toString().trim());
                                        inv_data.put("invoiceTaxCESS", invoiceCESSAmoutEdt.getText().toString().trim());

                                        if (etTcsPercentage.getText().toString().trim().isEmpty())
                                            inv_data.put("TCS_PER", "0");
                                        else
                                            inv_data.put("TCS_PER", etTcsPercentage.getText().toString().trim());

                                        if (etTcsValue.getText().toString().trim().isEmpty())
                                            inv_data.put("TCS_VAL", "0");
                                        else
                                            inv_data.put("TCS_VAL", etTcsValue.getText().toString().trim());

                                        Log.d(TAG, "Invoice data to be inserted <<::>> " + inv_data.toString());

                                        InvDB Invoice = new InvDB(getApplicationContext());
                                        Invoice.open();
                                        ArrayList<InvoiceDetails> Invoices = Invoice.getInvoiceDetails(poNumberEdt.getText().toString().trim());
                                        if (Invoices.size() > 0)
                                            Invoice.deleteInvoice(poNumberEdt.getText().toString().trim());
                                        Invoice.insertInvoiceDetails(inv_data);
                                        Invoice.close();

                                        Log.d(TAG, "JSON Date sending " + invoiceDateEdt.getText().toString().trim());
                                        Log.d(TAG, "Invoice Date " + mInvoiceDate);

                                        try {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("PO_NO", poNumberEdt.getText().toString().trim());
                                            jsonObject.put("LOCATION_CODE", locationcode.getText().toString());
                                            jsonObject.put("VENDOR_CODE", etVendorCode.getText().toString());
                                            jsonObject.put("INVOICE_NO", invoiceNumberEdt.getText().toString().trim());
                                            jsonObject.put("INVOICE_DATE", mInvoiceDate);
                                            jsonObject.put("INVOICE_AMOUNT", invoiceAmountEdt.getText().toString().trim());
                                            jsonObject.put("INVOICE_TAX_AMOUNT", invoiceVatAmountEdt.getText().toString().trim());
                                            jsonObject.put("INVOICE_TAX_CGST", invoiceCGSTAmoutEdt.getText().toString().trim());
                                            jsonObject.put("INVOICE_TAX_SGST", invoiceSGSTAmoutEdt.getText().toString().trim());
                                            jsonObject.put("INVOICE_TAX_IGST", invoiceSGSTAmoutEdt.getText().toString().trim());
                                            jsonObject.put("INVOICE_TAX_CESS", invoiceSGSTAmoutEdt.getText().toString().trim());
                                            jsonObject.put("CREATED_BY", sp.getString("Username", ""));
//                                            jsonObject.put("TCS_VAL", etTcsValue.getText().toString().trim());
//                                            jsonObject.put("TCS_PER", "0");
                                            //jsonObject.put("MobileNumber", etDeliveredPersonPhno.getText().toString().trim());
//                                            jsonObject.put("DeliveredBy", etDeliveredBy.getText().toString().trim());
                                            postData(jsonObject);

                                        } catch (JSONException jex) {
                                            jex.printStackTrace();
                                        }

                                 /*       try {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("poNo", poNumberEdt.getText().toString().trim());
                                            jsonObject.put("location_code", locationcode.getText().toString());
                                            jsonObject.put("vendorCode", etVendorCode.getText().toString());
                                            jsonObject.put("invoiceNo", invoiceNumberEdt.getText().toString().trim());
                                            jsonObject.put("invoiceDate", mInvoiceDate);
                                            jsonObject.put("invoiceAmount", invoiceAmountEdt.getText().toString().trim());
                                            jsonObject.put("invoiceTaxAmount", invoiceVatAmountEdt.getText().toString().trim());
                                            jsonObject.put("invoiceTaxCGST", invoiceCGSTAmoutEdt.getText().toString().trim());
                                            jsonObject.put("invoiceTaxSGST", invoiceSGSTAmoutEdt.getText().toString().trim());


                                            if (EInvoiceFlag.isEmpty())
                                                jsonObject.put("E_INVOICE_FLAG", "");
                                            else
                                                jsonObject.put("E_INVOICE_FLAG", EInvoiceFlag);

                                            if (QrcodeFlag.isEmpty())
                                                jsonObject.put("QRCODE_FLAG", "");
                                            else
                                                jsonObject.put("QRCODE_FLAG", QrcodeFlag);

                                            if (irnNumberValue.isEmpty())
                                                jsonObject.put("IRN_NO", "");
                                            else
                                                jsonObject.put("IRN_NO", irnNumberValue);
                                            if (TextUtils.isEmpty(invoiceIGSTAmoutEdt.getText().toString().trim()))
                                                jsonObject.put("invoiceTaxIGST", "0");
                                            else
                                                jsonObject.put("invoiceTaxIGST", invoiceIGSTAmoutEdt.getText().toString().trim());
                                            if (TextUtils.isEmpty(invoiceCESSAmoutEdt.getText().toString()))
                                                jsonObject.put("invoiceTaxCESS", "0");
                                            else
                                                jsonObject.put("invoiceTaxCESS", invoiceCESSAmoutEdt.getText().toString().trim());

                                            jsonObject.put("DeliveredBy", etDeliveredBy.getText().toString().trim());
                                            jsonObject.put("MobileNumber", etDeliveredPersonPhno.getText().toString().trim());
                                            jsonObject.put("CreatedBy", sp.getString("Username", ""));

                                            if (etTcsPercentage.getText().toString().trim().isEmpty())
                                                jsonObject.put("TCS_PER", "0");
                                            else
                                                jsonObject.put("TCS_PER", etTcsPercentage.getText().toString().trim());

                                            if (etTcsPercentage.getText().toString().trim().isEmpty())
                                                jsonObject.put("TCS_VAL", "0");
                                            else
                                                jsonObject.put("TCS_VAL", etTcsValue.getText().toString().trim());


                                            postData(jsonObject);

                                        }
                                        catch (JSONException jex) {
                                            jex.printStackTrace();
                                            Log.w(TAG, "JsonException Occured ## " + jex.getLocalizedMessage());
                                        }*/

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } else {
                    //Toast toast = Toast.makeText(getApplicationContext(), "All fields are mandatory!", Toast.LENGTH_SHORT);
                    //toast.setGravity(Gravity.CENTER,0,0);
                    //View view = toast.getView();
                    //view.setBackgroundResource(R.drawable.custom_background);
                    //toast.show();
                    //View view = toast.getView();
                    //view.setBackgroundResource(R.drawable.custom_background);

                }
                break;

            case R.id.htt_invoice_date_edt:
                myCalendar = Calendar.getInstance();
                int year1 = myCalendar.get(Calendar.YEAR);
                int month = myCalendar.get(Calendar.MONTH);
                int day = myCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                                          int dayOfMonth) {

                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        alertDialog.dismiss();

                        String myFormat = "dd-MM-yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        mInvoiceDate = simpleDateFormat.format(myCalendar.getTime());
                        invoiceDateEdt.setText(sdf.format(myCalendar.getTime()));
                        Log.d(TAG, "Invoice Date in update label<<::>> " + invoiceDateEdt.getText().toString());
                        invoiceAmountEdt.requestFocus();
                    }

                }, year1, month, day);
                Calendar maxDate = Calendar.getInstance();
                maxDate.set(Calendar.HOUR_OF_DAY, 23);
                maxDate.set(Calendar.MINUTE, 59);
                maxDate.set(Calendar.SECOND, 59);
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
                datePickerDialog.show();

                /*DatePickerDialog datePickerDialog = new DatePickerDialog(MGINUploadInvoiceTest.this, datelistener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));*/


                break;
        }
    }

    private void postData(final JSONObject jsonObject) {

        Log.w(TAG, "JSONObject to Post " + jsonObject.toString());
        Log.w(TAG, "Ready to POST Data");

        final String mRequestBody = jsonObject.toString();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
//        String URL = storeip + "/values/pregin";
        String URL = "http://36.255.252.200/VendorInvoice/api/Poheader/";

        Log.w(TAG, "Got the URL TO post data " + URL);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {

                try {
                    Log.i(TAG, "Posting PreGin Api Call response: " + response);
                    JSONObject Jsonobj = new JSONObject(response);

                  /*  if (Jsonobj.getString("StatusCode").equalsIgnoreCase("200")) {

                        toastSuccess(Jsonobj.getString("Message"));
                        //llManualGinNo.setVisibility(View.VISIBLE);
                        mGINNo = Jsonobj.getString("pre_gin_no");
                        mPendingCount = Jsonobj.getString("pending_count");
                        inwardNumberEdit.setText(mGINNo);
                        inwardNumberEdit.setEnabled(false);

                        //showAlert("Your Manual Inward number for the PO Number " + poNumberEdt.getText().toString() + " is " + mGINNo);

                        showAlertDialog("Your Manual Inward number for the PO Number " + poNumberEdt.getText().toString() + " is " + mGINNo +
                                "\nDo you want to proceed to Gin processing?", mGINNo, Integer.parseInt(mPendingCount));

                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), Jsonobj.getString("Message"), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        View view = toast.getView();
                        view.setBackgroundResource(R.drawable.custom_background);
                        toast.show();

                        getManualInwardNo();
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                    showFailedAlert(e.toString());
                    Log.e(TAG, "Exception postData pregin <<::>> " + e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                idialog.dismiss();
                showVolleyErrorAlert(error, "postData");
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
    }

    List<String> reasonList = new ArrayList<>();
    CustomSpinnerAdapter customSpinnerAdapter;

    private void captureReason(final String gNo) {

        Log.w(TAG, "Capture Reason why vendor not picked");
        getReasons();

        Log.w(TAG, "REason list size " + reasonList.size());

        final Dialog CouponDialog = new Dialog(this);
        CouponDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final EditText EtDlgDeliveryBy, EtDlgDeliveredPersonPhNo, EtDlgOtherComments;
        final Spinner SpDlgReason;
        final Button Cancel, BtnDlgSave;

        CouponDialog.setContentView(R.layout.capture_delivery_details);
        EtDlgDeliveryBy = CouponDialog.findViewById(R.id.et_dlg_delivered_by);
        EtDlgDeliveredPersonPhNo = CouponDialog.findViewById(R.id.et_dlg_delivered_person_phno);
        EtDlgOtherComments = CouponDialog.findViewById(R.id.et_dlg_comments);
        SpDlgReason = CouponDialog.findViewById(R.id.dlg_reason_spinner);
        Cancel = CouponDialog.findViewById(R.id.btn_dlg_cancel);
        BtnDlgSave = CouponDialog.findViewById(R.id.btn_dlg_save);

        customSpinnerAdapter = new CustomSpinnerAdapter(CouponDialog.getContext(), reasonList);
        SpDlgReason.setAdapter(customSpinnerAdapter);
        customSpinnerAdapter.notifyDataSetChanged();

        SpDlgReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.w(TAG, "Selected Reason " + SpDlgReason.getSelectedItem().toString());
                //Toast.makeText(CouponDialog.getContext(), SpDlgReason.getSelectedItem().toString() + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userChoice.isEmpty())
                    userChose(userChoice, userChoice.equalsIgnoreCase("Yes") ? gNo : "");
                CouponDialog.dismiss();
            }
        });

        BtnDlgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (TextUtils.isEmpty(EtDlgDeliveryBy.getText().toString().trim()))
                        showAlert("Enter Delivery By");
                    else if (TextUtils.isEmpty(EtDlgDeliveredPersonPhNo.getText().toString().trim()))
                        showAlert("Enter Delivered By Person Phone Number");
                    else {
                        DeliveryData deliveryData = new DeliveryData();
                        deliveryData.setDeliveredBy(EtDlgDeliveryBy.getText().toString().trim());
                        deliveryData.setDeliveredByPhNo(EtDlgDeliveredPersonPhNo.getText().toString().trim());
                        deliveryData.setReason(SpDlgReason.getSelectedItem().toString());
                        deliveryData.setComments(EtDlgOtherComments.getText().toString().trim());

                        Log.w(TAG, "GIN No " + gNo);
                        Log.w(TAG, "Delivery data " + gson.toJson(deliveryData));

                        savePendingVendorDeliveryData(gNo, gson.toJson(deliveryData));

                    /*String json = gson.toJson(deliveryData);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("DeliveryData", json);
                    editor.apply();*/

                        //Toast.makeText(MGINUploadInvoiceTest.this, "Data Saved", Toast.LENGTH_SHORT).show();

                    /* if (!userChoice.isEmpty())
                    userChose(userChoice, userChoice.equalsIgnoreCase("Yes") ? gNo : "");*/

                        CouponDialog.dismiss();
                    }
                } catch (Exception e) {
                    CouponDialog.dismiss();
                    e.printStackTrace();
                    Log.w(TAG, "Exception occured while trying to save reason for pending venodor " + e.getLocalizedMessage());
                }
            }
        });

        /*String json = sharedPreferences.getString("DeliveryData", "");
        if (json.isEmpty()) {
            Toast.makeText(CouponDialog.getContext(), "There is something error", Toast.LENGTH_LONG).show();
        } else {
            Type type = new TypeToken<DeliveryData>() {
            }.getType();
            DeliveryData arrPackageData = gson.fromJson(json, type);
        }*/

        CouponDialog.setCancelable(false);
        CouponDialog.show();
        Window window = CouponDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void savePendingVendorDeliveryData(final String gNo, final String requestBody) {

        Log.w(TAG, "DATA TO BE POST FOR REJECTION " + requestBody);

        try {

            RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
            String URL = storeip + "/Values/pregin_Rejection";
            Log.w(TAG, "Got the URL TO post data " + URL);

            JSONObject jsonObject = new JSONObject(requestBody);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject Jsonobj) {

                            try {
                                Log.i(TAG, "Api Call response pregin_Rejection : " + Jsonobj.toString());

                                if (Jsonobj.getString("StatusCode").equalsIgnoreCase("200")) {

                                    toastSuccess(Jsonobj.getString("Message"));
                                    //Toast.makeText(MGINUploadInvoiceTest.this, "Data Saved", Toast.LENGTH_SHORT).show();

                                    Log.w(TAG, "userChoice " + userChoice);
                                    if (!userChoice.isEmpty())
                                        userChose(userChoice, userChoice.equalsIgnoreCase("Yes") ? gNo : "");

                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), Jsonobj.getString("Message"), Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                                    View view = toast.getView();
                                    view.setBackgroundResource(R.drawable.custom_background);
                                    toast.show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, "savePendingVendorDeliveryData POST Exception <<::>> " + e.toString());
                                showAlert(e.toString());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showVolleyErrorAlert(error, "postData");
                }
            });
            requestQueue.add(jsonObjectRequest);

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Exception <<::>> " + ex.toString());
        }
    }

    private void getReasons() {

        try {

            reasonList = new ArrayList<>();

            RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
            String URL = storeip + "/Values/rejection_reasons";
            Log.w(TAG, "Got the URL TO GET data " + URL);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {

                    try {
                        Log.i(TAG, "Api Call response: " + response);
                        String res = response.replaceAll("\"", "");
                        //res = res.replaceAll("'", "\"");

                        Log.i(TAG, "Api Call response1 : " + res);

                        JSONObject Jsonobj = new JSONObject(res);
                        Log.i(TAG, response);

                        if (Jsonobj.getString("StatusCode").equalsIgnoreCase("200")) {

                            int count = Jsonobj.getInt("Count");
                            for (int i = 1; i <= count; i++) {
                                reasonList.add(Jsonobj.getString("Reason" + i));
                            }
                            customSpinnerAdapter.notifyDataSetChanged();

                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), Jsonobj.getString("Message"), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            View view = toast.getView();
                            view.setBackgroundResource(R.drawable.custom_background);
                            toast.show();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Exception <<::>> " + e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    idialog.dismiss();
                    showVolleyErrorAlert(error, "postData");
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    6000, 3,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "Exception <<::>> " + ex);
        }
    }

    public boolean validate() {

        Log.w(TAG, "Begining to validate");
        Log.d(TAG, "Invoice Data for validation <<::>> " + mInvoiceDate);
        Log.e(TAG, "Check if both are same " + !invoiceCGSTAmoutEdt.getText().toString().trim().
                equalsIgnoreCase(invoiceSGSTAmoutEdt.getText().toString().trim()));

        if (poNumberEdt.getText().toString().trim().equalsIgnoreCase("") ||
                invoiceNumberEdt.getText().toString().trim().equalsIgnoreCase("") ||
                invoiceDateEdt.getText().toString().trim().equalsIgnoreCase("") ||
                invoiceAmountEdt.getText().toString().trim().equalsIgnoreCase("") ||
                etVendorCode.getText().toString().trim().equalsIgnoreCase("")) {

            toastError("All fields are mandatory");
            return false;

        } else if (validateInvoiceDate(invoiceDateEdt.getText().toString())) {//validateInvoiceDate(mInvoiceDate)

            //   toastError("Invoice date cannot be greater than current date");
            return false;

        } /*else if (invoiceVatAmountEdt.getText().toString().equalsIgnoreCase("0")) {

            toastError("GST Amount cannot be Zero.. Please verify");
            return false;

        }*/ else if (invoiceVatAmountEdt.getText().toString().trim().isEmpty()) {

            toastError("GST Amount cannot be empty.. Please verify");
            return false;

        } else if ((!TextUtils.isEmpty(invoiceCESSAmoutEdt.getText().toString()) &&
                !invoiceCESSAmoutEdt.getText().toString().equalsIgnoreCase("0")) &&
                (TextUtils.isEmpty(invoiceCGSTAmoutEdt.getText().toString()) ||
                        invoiceCGSTAmoutEdt.getText().toString().equalsIgnoreCase("0")) &&
                (TextUtils.isEmpty(invoiceIGSTAmoutEdt.getText().toString()) ||
                        invoiceIGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))) {

            if (invoiceCGSTAmoutEdt.getText().toString().equalsIgnoreCase("0")) {
                invoiceCGSTAmoutEdt.requestFocus();
                invoiceCGSTAmoutEdt.setSelection(invoiceCGSTAmoutEdt.getText().toString().length());
            } else {
                invoiceIGSTAmoutEdt.requestFocus();
                invoiceIGSTAmoutEdt.setSelection(invoiceIGSTAmoutEdt.getText().toString().length());
            }
            toastError("Enter CGST or IGST.. Both cannot be empty at the same time");
            return false;

        } /*else if ((TextUtils.isEmpty(invoiceCGSTAmoutEdt.getText().toString()) ||
                invoiceCGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))
                &&
                (TextUtils.isEmpty(invoiceSGSTAmoutEdt.getText().toString()) ||
                        invoiceSGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))
                &&
                (TextUtils.isEmpty(invoiceIGSTAmoutEdt.getText().toString()) ||
                        invoiceIGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))) {

            invoiceIGSTAmoutEdt.requestFocus();
            toastError("IGST amount is Invalid or Empty");

            return false;

        }*/ else if ((!TextUtils.isEmpty(invoiceCGSTAmoutEdt.getText().toString()) &&
                !invoiceCGSTAmoutEdt.getText().toString().equalsIgnoreCase("0")) &&
                (TextUtils.isEmpty(invoiceSGSTAmoutEdt.getText().toString()) ||
                        invoiceSGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))) {

            invoiceSGSTAmoutEdt.requestFocus();
            invoiceSGSTAmoutEdt.setSelection(invoiceSGSTAmoutEdt.getText().toString().length());
            toastError("SGST amount is Invalid or Empty");

            return false;

        } else if ((!TextUtils.isEmpty(invoiceSGSTAmoutEdt.getText().toString()) &&
                !invoiceSGSTAmoutEdt.getText().toString().equalsIgnoreCase("0")) &&
                (TextUtils.isEmpty(invoiceCGSTAmoutEdt.getText().toString()) ||
                        invoiceCGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))) {

            invoiceSGSTAmoutEdt.requestFocus();
            invoiceSGSTAmoutEdt.setSelection(invoiceSGSTAmoutEdt.getText().toString().length());
            toastError("CGST amount is Invalid or Empty");

            return false;

        } else if (!invoiceCGSTAmoutEdt.getText().toString().trim().
                equalsIgnoreCase(invoiceSGSTAmoutEdt.getText().toString().trim())) {
            toastError("CGST and SGST amount cannot be different");
            return false;
        } else if (((
                (!TextUtils.isEmpty(invoiceCGSTAmoutEdt.getText().toString()) &&
                        !invoiceCGSTAmoutEdt.getText().toString().equalsIgnoreCase("0"))
                        &&
                        (!TextUtils.isEmpty(invoiceSGSTAmoutEdt.getText().toString()) &&
                                !invoiceSGSTAmoutEdt.getText().toString().equalsIgnoreCase("0")))
                ||
                (!TextUtils.isEmpty(invoiceIGSTAmoutEdt.getText().toString()) &&
                        !invoiceIGSTAmoutEdt.getText().toString().equalsIgnoreCase("0")))
                && (TextUtils.isEmpty(invoiceVatAmountEdt.getText().toString()) ||
                invoiceVatAmountEdt.getText().toString().equalsIgnoreCase("0"))) {

            invoiceVatAmountEdt.requestFocus();
            invoiceVatAmountEdt.setSelection(invoiceVatAmountEdt.getText().toString().length());
            toastError("GST amount is Invalid or Empty");

            return false;

        } else if (TextUtils.isEmpty(etDeliveredBy.getText().toString().trim())) {

            etDeliveredBy.requestFocus();
            toastError("Enter Delivered By");
            return false;

        } else if (TextUtils.isEmpty(etDeliveredPersonPhno.getText().toString().trim())) {

            etDeliveredPersonPhno.requestFocus();
            toastError("Enter Delivered By Person Phone Number");
            return false;

        } else if (etDeliveredPersonPhno.getText().toString().trim().length() < 10) {

            etDeliveredPersonPhno.requestFocus();
            toastError("Invalid Phone Number");
            return false;

        } else if (!TextUtils.isEmpty(invoiceVatAmountEdt.getText().toString()) &&
                !invoiceVatAmountEdt.getText().toString().equalsIgnoreCase("0")) {

            double gstAmt = 0.0;
            if (TextUtils.isEmpty(invoiceCESSAmoutEdt.getText().toString()))
                invoiceCESSAmoutEdt.setText("0");

            if (!invoiceCGSTAmoutEdt.getText().toString().equalsIgnoreCase("0") &&
                    !invoiceSGSTAmoutEdt.getText().toString().equalsIgnoreCase("0")) {

                gstAmt = Double.parseDouble(invoiceCGSTAmoutEdt.getText().toString()) +
                        Double.parseDouble(invoiceSGSTAmoutEdt.getText().toString()) +
                        Double.parseDouble(invoiceCESSAmoutEdt.getText().toString());
                if ((gstAmt < Double.parseDouble(invoiceVatAmountEdt.getText().toString())) ||
                        (gstAmt > Double.parseDouble(invoiceVatAmountEdt.getText().toString()))) {
                    showAlert("CGST and SGST or CESS amount doesn't match with the Entered GST amount");
                    return false;
                }
            } else if (!invoiceIGSTAmoutEdt.getText().toString().equalsIgnoreCase("0")) {

                gstAmt = Double.parseDouble(invoiceIGSTAmoutEdt.getText().toString()) +
                        Double.parseDouble(invoiceCESSAmoutEdt.getText().toString());

                if ((gstAmt < Double.parseDouble(invoiceVatAmountEdt.getText().toString())) ||
                        (gstAmt > Double.parseDouble(invoiceVatAmountEdt.getText().toString()))) {
                    showAlert("IGST or CESS amount doesn't match with the Entered GST amount");
                    return false;
                }
            }
            return true;
        } /*else if (etTcsPercentage.getText().toString().isEmpty()) {
            showAlert("TCS Percentage cannot be Empty");
            return false;
        } else if (etTcsValue.getText().toString().isEmpty()) {
            showAlert("TCS value cannot be Empty");
            return false;
        }*/ else {
            return true;
        }
    }


    public boolean validateInvoiceDate(String invoicedt) {

        Log.d(TAG, "validateInvoiceDate <<::>> " + invoicedt);
        try {
            // If you already have date objects then skip 1

            //1
            // Create 2 dates starts
            //date format changed
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = new SimpleDateFormat("dd-MMM-yyyy").format(Calendar.getInstance().getTime());
            Date currdate = sdf.parse(currentDate);
            Date date2 = sdf.parse(invoicedt);
            Date poDatesdf = sdf.parse(poDate);

            Log.d("Date1", sdf.format(currdate));
            Log.d("Date2", sdf.format(date2));

            // Create 2 dates ends
            //1

            // Date object is having 3 methods namely after,before and equals for comparing
            // after() will retrn true if and only if date1 is after date 2
            if (currdate.after(date2) || currdate.equals(date2)) {

                if (date2.after(poDatesdf) || date2.equals(poDatesdf)) {
                    invoiceInvalid = false;
                } else {
                    invoiceInvalid = true;
                    Toast toast = Toast.makeText(getApplicationContext(), "Invoice date cannot be less  than po date", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = toast.getView();
                    view.setBackgroundResource(R.drawable.custom_background);
                    toast.show();
                }


            } else {
                invoiceInvalid = true;
                Toast toast = Toast.makeText(getApplicationContext(), "Invoice date cannot be greater than current date", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                View view = toast.getView();
                view.setBackgroundResource(R.drawable.custom_background);
                toast.show();
            }

            //equals() returns true if both the dates are equal

            //retrn true;
        } catch (ParseException ex) {
            ex.printStackTrace();
            Log.w(TAG, "DataParse Exception " + ex);
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
            invoiceInvalid = true;
        }
        return invoiceInvalid;

    }

    private void makeFetchPOdetailsApiCall(String urlPath) {

        Log.d(TAG, "URL PAth of PO Details " + urlPath);

        idialog = ProgressDialog.show(MGINUploadInvoiceTest_MainCopy.this, "",
                "Fetching PO details", true);

        ApiCall.make(this, urlPath, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            Log.w(TAG, "PO Details Response ====> " + response);

                            JSONObject responseObject = new JSONObject(response);
                            String status = responseObject.getString("result");
                            if (status.equalsIgnoreCase("success")) {

                                if (enableDisableButton.equalsIgnoreCase("Y"))
                                    top_btn_layout.setVisibility(View.VISIBLE);
                                else
                                    top_btn_layout.setVisibility(View.GONE);

                                // PO Header Data
                                idialog.dismiss();

                                HashMap<String, String> po_header_data = new HashMap<String, String>();
                                JSONArray poheader = responseObject.getJSONArray("Poheader");
                                Log.d("PO DATA ==", poheader.toString());

                                JSONObject row = poheader.getJSONObject(0);
                                po_header_data.put("PO_No", row.getString("po_no"));
                                po_header_data.put("LocationCode", row.getString("location_code"));
                                po_header_data.put("PO_Date", row.getString("PO_Date"));
                                poDate = row.getString("PO_Date");
                                po_header_data.put("PO_ExpiryDate", row.getString("po_expiry_date"));
                                po_header_data.put("PO_Value", row.getString("po_value"));
                                po_header_data.put("PO_VAT", row.getString("VAT"));

                                einvoiceFlag = row.getString("einvoiceFlag");


                                if (row.getString("einvoiceFlag").equalsIgnoreCase("Y") && !TextUtils.isEmpty(mInvoiceDate)) {
                                    alertDialog.dismiss();
                                    invoiceDateEdt.setText(mInvoiceDate);

                                } else {

                                    if (row.getString("einvoiceFlag").equalsIgnoreCase("Y")) {
                                        alertDialog.show();
                                        invoiceDateEdt.setText(mInvoiceDate);
                                    /*AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest.this);
                                    ViewGroup viewGroup = findViewById(android.R.id.content);
                                    View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_pre_gin_dialog, viewGroup, false);
                                    builder.setView(dialogView);
                                    alertDialog = builder.create();
                                    alertDialog.show();*/
                                        Button httClear = dialogView.findViewById(R.id.htt_clear);
                                        Button httNext = dialogView.findViewById(R.id.htt_next);
                                        irnNumber = dialogView.findViewById(R.id.irn_number);
                                       /* ArrayList<InputFilter> curInputFilters = new ArrayList<InputFilter>(Arrays.asList(irnNumber.getFilters()));
                                        curInputFilters.add(0, new AlphaNumericInputFilter());
                                        curInputFilters.add(1, new InputFilter.AllCaps());
                                        InputFilter[] newInputFilters = curInputFilters.toArray(new InputFilter[curInputFilters.size()]);
                                        irnNumber.setFilters(newInputFilters);*/

                                        irnNumber.setFilters(new InputFilter[]{
                                                new InputFilter() {
                                                    public CharSequence filter(CharSequence src, int start,
                                                                               int end, Spanned dst, int dstart, int dend) {
                                                        if (src.equals("")) { // for backspace
                                                            return src;
                                                        }
                                                        if (src.toString().matches("[a-zA-Z 0-9]+")) {
                                                            return src;
                                                        }
                                                        return "";
                                                    }
                                                },
                                                new InputFilter.LengthFilter(5)
                                        });


                                        final CheckBox check_Qr_Code = dialogView.findViewById(R.id.check_qr_code);
                                        httNext.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (!check_Qr_Code.isChecked()) {

                                                    Toast.makeText(getApplicationContext(), "Please Check QR Code Invoice",
                                                            Toast.LENGTH_SHORT).show();
                                               /* String irnNumberValue = irnNumber.getText().toString();

                                                 inputIRnNumber = Integer.parseInt(irnNumberValue);*/


                                                } else if (irnNumber.getText().toString().trim().length() < 5) {
                                                    Toast.makeText(getApplicationContext(), "Please Enter Valid 5 Digits IRN Number",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Success",
                                                            Toast.LENGTH_SHORT).show();


                                                    irnNumberValue = irnNumber.getText().toString();
                                                    QrcodeFlag = "Y";
                                                    EInvoiceFlag = "Y";
                                                    alertDialog.dismiss();

                                                }
                                            }
                                        });

                                        httClear.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                irnNumber.setText("");
                                                // alertDialog.dismiss();

                                            }
                                        });
                                    }

                                }



                               /* if (invoiceNumberEdt.getText().toString().trim().equalsIgnoreCase("") || !invoiceNumberEdt.getText().toString().trim().equalsIgnoreCase("") ){
                                    alertDialog.dismiss();
                                }*/


                                HG_PO_HEADER po_header = new HG_PO_HEADER(getApplicationContext());
                                po_header.open();
                                //po_header.deleteUserTable();
                                ArrayList<POHeader> Header = po_header.getPOHeaderValues(poNumberEdt.getText().toString().trim());
                                if (Header.size() > 0)
                                    po_header.deletePOheader(poNumberEdt.getText().toString().trim());
                                po_header.insertPoHeader(po_header_data);
                                po_header.close();

                                //PO Details
                                JSONArray podetails = responseObject.getJSONArray("podetails");
                                JSONObject jsonObject = podetails.getJSONObject(0);
                                HG_PO_DETAILS po_detail = new HG_PO_DETAILS(getApplicationContext());
                                po_detail.open();

                                ArrayList<PODetails> Detail = po_detail.getPODetails("", poNumberEdt.getText().toString().trim());
                                if (Detail.size() > 0) {
                                    po_detail.deletePOdetail(poNumberEdt.getText().toString().trim());
                                    etVendorCode.setText(Detail.get(0).getVendorCode());
                                    etVendorCode.setEnabled(false);
                                } else {
                                    etVendorCode.setText(jsonObject.getString("vendor_code"));
                                    etVendorCode.setEnabled(false);
                                }
                                po_detail.insertBulkPODetails(podetails, poNumberEdt.getText().toString().trim());
                                po_detail.close();

                                locationcode.setText(row.getString("location_code"));
                                locationcode.setEnabled(false);
                                //invoiceAmountEdt.setText(row.getString("po_value"));
                                //invoiceVatAmountEdt.setText(row.getString("VAT"));

                                Toast.makeText(getApplicationContext(), "PO details downloaded successfully",
                                        Toast.LENGTH_SHORT).show();

                                String url = storeip + "/values/hhtpo_eandetails/" + poNumberEdt.getText().toString().trim(); // url
                                makeFetchEANdetailsApiCall(url);


                            } else {
                                idialog.dismiss();
                                String msg = responseObject.getString("message");
                                toastError(msg);

                                //clear_btn.performClick();
                            }
                        } catch (JSONException e) {
                            Log.d("JSON EXception :::: ", e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        idialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error while fetching PO details",
                                Toast.LENGTH_SHORT).show();
                        showVolleyErrorAlert(error, "makeFetchPOdetailsApiCall");

                    }
                }
        );

    }

    public static class AlphaNumericInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            // Only keep characters that are alphanumeric
            StringBuilder builder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (Character.isLetterOrDigit(c)) {
                    builder.append(c);
                }
            }

            // If all characters are valid, return null, otherwise only return the filtered characters
            boolean allCharactersValid = (builder.length() == end - start);
            return allCharactersValid ? null : builder.toString();
        }
    }

    private void makeFetchEANdetailsApiCall(String urlPath) {

        idialog = ProgressDialog.show(MGINUploadInvoiceTest_MainCopy.this, "",
                "Fetching EAN details", true);
        ApiCall.make(this, urlPath, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String status = responseObject.getString("result");
                            Log.d("json response", status);

                            if (status.equalsIgnoreCase("success")) {

                                JSONArray EanData = responseObject.getJSONArray("data");
                                EANDB eandb = new EANDB(getApplicationContext());
                                eandb.open();
                                eandb.deleteAllEAN();
                                eandb.insertBulkEANDetails(EanData);
                                eandb.close();
                                Toast toast = Toast.makeText(getApplicationContext(), "EAN details downloaded successfully", Toast.LENGTH_SHORT);
                                toast.show();
                                idialog.dismiss();

                                String url = storeip + "/values/pending_vendorreturn?id=" + poNumberEdt.getText().toString().trim();
                                makeVendorReturnPendingApiCall(url);


                            } else {
                                idialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), "Error while fetching EAN details", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            //Log.d("PO Header", json.toString()); // get the response
                        } catch (JSONException e) {
                            Log.d("JSON EXception :::: ", e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        idialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error while fetching EAN details",
                                Toast.LENGTH_SHORT).show();

                        showVolleyErrorAlert(error, "makeFetchEANdetailsApiCall");

                    }
                }
        );

    }

    private void makeVendorReturnPendingApiCall(String urlPath) {

        idialog = ProgressDialog.show(MGINUploadInvoiceTest_MainCopy.this, "",
                "Fetching pending vendor return details", true);
        ApiCall.make(this, urlPath, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String status = responseObject.getString("result");
                            Log.d("json response", status);

                            if (status.equalsIgnoreCase("Succes ")) {
                                idialog.dismiss();

                                int count = Integer.parseInt(responseObject.getString("count"));
                                if (count > 0)
                                    showFailedAlert("There are pending vendor return for this vendor, Please handover.");


                            } else {
                                idialog.dismiss();
                                Toast toast = Toast.makeText(getApplicationContext(), "Error while fetching pending vendor return details", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        } catch (JSONException e) {
                            Log.d(TAG, "JSON EXception :::: " + e);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        idialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error while fetching pending vendor return details",
                                Toast.LENGTH_SHORT).show();

                        showVolleyErrorAlert(error, "makeVendorReturnPendingApiCall");

                    }
                }
        );

    }

    private void showVolleyErrorAlert(VolleyError error, String functionName) {
        try {
            if (error instanceof TimeoutError) {
                showFailedAlert("Time out error occurred.Please click on OK and try again");
                Log.e(TAG, "Time out error occurred.");

            } else if (error instanceof NoConnectionError) {
                showFailedAlert("Network error occurred.Please click on OK and try again");
                Log.e(TAG, "Network error occurred.");

            } else if (error instanceof AuthFailureError) {
                showFailedAlert("Authentication error occurred.Please click on OK and try again");
                Log.e(TAG, "Authentication error occurred.");
            } else if (error instanceof ServerError) {
                showFailedAlert("Server error occurred.Please click on OK and try again");
                Log.e(TAG, "Server error occurred.");

            } else if (error instanceof NetworkError) {
                showFailedAlert("Network error occurred.Please click on OK and try again");
                Log.e(TAG, "Network error occurred.");

            } else if (error instanceof ParseError) {
                showFailedAlert("An error occurred.Please click on OK and try again");
                Log.e(TAG, "An error occurred.");
            } else {
                showFailedAlert("An error occurred.Please click on OK and try again");
                Log.e(TAG, "An error occurred.");
            }
        } catch (Exception e) {
            Log.e(TAG, "------------------------- " + functionName + " -------------------------");
            Log.e(TAG, "Exception <<::>> " + e);
            e.printStackTrace();
        }
        //End
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ipsetting) {
            Intent ip = new Intent(MGINUploadInvoiceTest_MainCopy.this, IPsettingActivity.class);
            startActivity(ip);
            finish();
            //SharedPreferences sp = getSharedPreferences("LoginDetails", MODE_PRIVATE);
            //sp.edit().putBoolean("Login", false).commit();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        View view = MGINUploadInvoiceTest_MainCopy.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    Intent in2;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
       /* switch (item.getItemId()) {

            case R.id.nav_tester_free_gift_expiry_check:
                startActivity(new Intent(MGINUploadInvoiceTest.this, TestersFreeGiftExpiryCheckActivity.class));
                break;

            case R.id.nav_bank_deposit_register:
                startActivity(new Intent(MGINUploadInvoiceTest.this, BankDepositGridActivity.class));
                break;

            case R.id.nav_free_tester_generator:
                startActivity(new Intent(MGINUploadInvoiceTest.this, FreeGiftTestRegisterActivity.class));
                break;

            case R.id.nav_generator:
                startActivity(new Intent(MGINUploadInvoiceTest.this, GeneratorRegisterListActivity.class));
                break;

            case R.id.nav_store_electricity:
                startActivity(new Intent(MGINUploadInvoiceTest.this, ElectricityDetailsActivity.class));
                break;

            case R.id.nav_expiry_check:
                Intent intent = new Intent(MGINUploadInvoiceTest.this, ExpiryCheckActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_phiv:
                in2 = new Intent(MGINUploadInvoiceTest.this, PIHVactivity.class);
                startActivity(in2);
                finish();
                break;

            case R.id.nav_GIN:
                in2 = new Intent(MGINUploadInvoiceTest.this, POdetailsActivity.class);
                //  Intent intent = new Intent(LoginActivity.this, MGINUploadInvoiceTest.class);
                in2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in2);
                break;

            case R.id.nav_mGIN:
                *//*Intent in2 = new Intent(MGINUploadInvoiceTest.this, MGINUploadInvoiceTest.class);
                startActivity(in2);
                finish();*//*
                break;

            case R.id.nav_download:
                in2 = new Intent(MGINUploadInvoiceTest.this, MasterDownloadActivity.class);
                startActivity(in2);
                finish();
                break;

            case R.id.nav_apk_download:
                in2 = new Intent(MGINUploadInvoiceTest.this, APKdownloadActivity.class);
                startActivity(in2);
                finish();
                break;

            case R.id.nav_transfer_in:
                in2 = new Intent(MGINUploadInvoiceTest.this, TransferInActivity.class);
                startActivity(in2);
                finish();
                break;

            case R.id.nav_transfer:
                in2 = new Intent(MGINUploadInvoiceTest.this, TransferActivity.class);
                startActivity(in2);
                finish();
                break;

            case R.id.nav_purchase_return:
                in2 = new Intent(MGINUploadInvoiceTest.this, PurchaseReturnActivity.class);
                startActivity(in2);
                finish();
                break;

            case R.id.nav_shelfTicket:
                in2 = new Intent(MGINUploadInvoiceTest.this, ShelfTicketActivity.class);
                startActivity(in2);
                finish();
                break;

            case R.id.nav_prodFeedback:
                in2 = new Intent(MGINUploadInvoiceTest.this, ProductFeedbackActivity.class);
                startActivity(in2);
                finish();
                break;

            case R.id.nav_logout:
                View view = MGINUploadInvoiceTest.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                new AlertDialog.Builder(MGINUploadInvoiceTest.this)
                        .setMessage("Are you sure you want to Logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent in = new Intent(MGINUploadInvoiceTest.this, LoginActivity.class);
                                startActivity(in);
                                finish();
                                SharedPreferences sp = getSharedPreferences("LoginDetails", MODE_PRIVATE);
                                sp.edit().putBoolean("Login", false).apply();

                            }
                        }).setNegativeButton("No", null)
                        .show();
                break;
        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Request Code <<::>> " + requestCode);
        Log.d(TAG, "REsult Code <<::>> " + resultCode);
        Log.d(TAG, "Build.VERSION.SDK_INT " + Build.VERSION.SDK_INT);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_REQUEST) {

                //displayImage();

                if (Build.VERSION.SDK_INT <= 19) {
                    String uri = outPutfileUri.toString();
                    Log.e(TAG, "uri-: >>>> " + uri);
                    Toast.makeText(this, outPutfileUri.toString(), Toast.LENGTH_LONG).show();

                    try {

                        File file = new File(new URI(uri));
                        currentImagePath = file.getAbsolutePath();
                        Log.d(TAG, "Captured Scratch Card CurrentImagePath <<::>>> " + currentImagePath);

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outPutfileUri);
                        Drawable d = new BitmapDrawable(getResources(), bitmap);

                        imageView.setImageBitmap(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "Captured Scratch Card Exception " + e);
                    }
                } else {
                    Log.w(TAG, "Current Image Path " + currentImagePath);
                    Uri uri = null;
                    uri = Uri.parse(currentImagePath);
                    openCropActivity(uri, uri);
                }
            }
            // user is returning from cropping the image
            else if (requestCode == UCrop.REQUEST_CROP) {

                Log.w(TAG, "Cropped Image ############");
                Uri uri = UCrop.getOutput(data);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    img_bitmap_list.add(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final CharSequence[] optionsChoose = new
                        CharSequence[]{"Add More Images", "Done"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
                builder.setTitle("Add More Photos!");
                builder.setItems(optionsChoose, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (optionsChoose[item].equals("Add More Images")) {
                            if (isCameraPermissionGranted() && isWriteStoragePermissionGranted() && isReadStoragePermissionGranted())
                                captureImage();

                        } else if (optionsChoose[item].equals("Done")) {
                            Log.w(TAG, "PDF document ############   " + String.valueOf(img_bitmap_list.size()));
                            createPDF();
                        }
//                        else if (optionsChoose[item].equals("Cancel")) {
//                            dialog.dismiss();
//                        }
                    }
                });
                builder.show();

//                Log.e("displayImage", uri.getPath());
//                Log.e("displayImage", uri.getEncodedPath());
//
//                if (!image2) {
//                    Picasso.get().load(uri).into(imageView);
//                    imageView.setVisibility(View.VISIBLE);
//                    capturedImg2.setVisibility(View.VISIBLE);
//                } else {
//                    Picasso.get().load(uri).into(capturedImg2);
//                    capturedImg2.setVisibility(View.VISIBLE);
//                }
//                submit_btn.setVisibility(View.VISIBLE);
//                displayImage(uri);
//                croppedImagePath = uri;


            } else if (requestCode == GALLERY_REQUEST_CODE) {

                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                String imagePath = FileUtils.getPath(this, selectedImage);
                Log.w(TAG, "Gallery imagePath PATH 1######## " + imagePath);

                Uri uri = null;
                currentImagePath = "file:" + imagePath;
                uri = Uri.parse(currentImagePath);

                openCropActivity(uri, uri);

            } else if (requestCode == REQUEST_CODE_SELECT_IMAGE) {

                String template_file_uri = null;
                String extension = null;
                String getRealPathFromURI = null;
                try {
                    Log.e("OAR@try", "");
                    String path = null;
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {

                        Uri selectedFileUri = data.getData();
                        String mimeType = getContentResolver().getType(selectedFileUri);
                        Log.e(TAG, "onActivityResult() REQUEST_CODE_SELECT_IMAGE RESULT_OK uri : " + selectedFileUri + " mimetype " + mimeType);
                        Log.e(TAG, ":::>>selectedFileUri::: " + selectedFileUri);
                        int lastDot = selectedFileUri.toString().lastIndexOf('.');
                        if (lastDot == -1) {
                            // No dots - what do you want to do?
                            showAlert("Please select only pdf file !!!");
                        } else {
                            extension = selectedFileUri.toString().substring(lastDot);
                            Log.e(TAG, "extension: " + extension);
                        }

                        if (extension.equals(".pdf") || mimeType.equals("application/pdf")) {
                            template_file_uri = selectedFileUri.toString();
                            //displayFromUri(selectedFileUri);
                            getRealPathFromURI = FileUtils.getRealPathFromURI(this, selectedFileUri);

                        } else {
                            Log.e(TAG, "else ext: " + extension);
                            showAlert("Please select only pdf file !!!");
                            template_file_uri = null;
                        }
                        Log.e(TAG, "::::>>>getRealPathFromURI::: " + getRealPathFromURI);

//                        uploadImageToServer(getRealPathFromURI,0);
                        uploadImageToServerDup(getRealPathFromURI, 0);

                    } else {


                       /* Uri selectedFileUri = data.getData();
                        Log.e("selectedFileUri", selectedFileUri.toString());
                        final String docId = DocumentsContract.getDocumentId(selectedFileUri);
                        Log.e("docId@selectedFileUri", docId);*/


                        try {
                            Toast.makeText(MGINUploadInvoiceTest_MainCopy.this, "Please wait...", Toast.LENGTH_LONG).show();

                            Uri selectedFileUri = data.getData();
                            Log.e("selectedFileUri", selectedFileUri.toString());
                            getFileNameByUri(MGINUploadInvoiceTest_MainCopy.this, selectedFileUri);
//                            getFilePath(selectedFileUri);

//                            String path1 = FileUtils.getPath(this, selectedFileUri);


                       /*     String s = getStringPdf(selectedFileUri);
                            Log.e("getStringPdf", "PATH 1######## " + s);
                            saveBase64toFile(s);
                            readFromFilePrivate();*/

                            /*  String path1 = FileUtils.getPath(this, selectedFileUri);
                            Log.e(TAG, "PATH 1######## " + path1);
                            Toast.makeText(this, "PDF Path " + path1, Toast.LENGTH_SHORT).show();
                            uploadImageToServer(path1);*/


//  File dir = Environment.getExternalStorageDirectory();
//                            File yourFile = new File(dir, path+ finalDisplayName );
                        } catch (NumberFormatException e) {
                            Log.e("numberFormatException", e.toString());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onActivity Exception <<::>> " + e);
                }
            }

        }

    }

    private File getOutputFile() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/pdfs");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        boolean isFolderCreated = true;


        if (isFolderCreated) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            String imageFileName = "PDF_" + timeStamp;

            return new File(myDir, imageFileName + ".pdf");
        } else {
            Toast.makeText(this, "Folder is not created", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPDF() {
//        final File file = new File(uploadFolder, "AnswerSheet_" + queId + ".pdf");
        file_pdf = getOutputFile();

        final ProgressDialog dialog = ProgressDialog.show(this, "", "Generating PDF...");
        dialog.show();
        new Thread(() -> {
            Bitmap bitmap;
            PdfDocument document = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                document = new PdfDocument();
            }
            //  int height = 842;
            //int width = 595;
            int height = 1010;
            int width = 714;
            int reqH, reqW;
            reqW = width;

            for (int i = 0; i < img_bitmap_list.size(); i++) {
                //  bitmap = BitmapFactory.decodeFile(array.get(i));
                Bitmap resized = Bitmap.createScaledBitmap(img_bitmap_list.get(i), width, height, true);


                reqH = width * resized.getHeight() / resized.getWidth();
                Log.e("reqH", "=" + reqH);
                if (reqH < height) {
                    //  bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
                } else {
                    reqH = height;
                    reqW = height * resized.getWidth() / resized.getHeight();
                    Log.e("reqW", "=" + reqW);
                    //   bitmap = Bitmap.createScaledBitmap(bitmap, reqW, reqH, true);
                }
                // Compress image by decreasing quality
                // ByteArrayOutputStream out = new ByteArrayOutputStream();
                //  bitmap.compress(Bitmap.CompressFormat.WEBP, 50, out);
                //    bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
                //bitmap = bitmap.copy(Bitmap.Config.RGB_565, false);
                //Create an A4 sized page 595 x 842 in Postscript points.
                //PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(reqW, reqH, 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                Log.e("PDF", "pdf = " + resized.getWidth() + "x" + resized.getHeight());
                canvas.drawBitmap(resized, 0, 0, null);

                document.finishPage(page);
            }

            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file_pdf);
                document.writeTo(fos);
                document.close();
                fos.close();

                Log.e("file_pathh", "#############" + file_pdf.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                dialog.dismiss();
                iv_pdf.setVisibility(View.VISIBLE);
                submit_btn.setVisibility(View.VISIBLE);

            });
        }).start();
    }


    public void getFileNameByUri(Context context, Uri uri) {
        // The temp file could be whatever you want

        if (uri.getScheme().toLowerCase().startsWith("file")) {
            Log.e("startswith", "file");
//            String realPath = ImageFilePath.getPath(context, uri);
//                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());

//            android.util.Log.i("file", "onActivityResult: file path : " + realPath);
//            fileSelected(new File(realPath));

        } else {

            String fileName = getFileName(uri);
            File temp_file = new File(getCacheDir().getPath() + "/" + fileName);
            Log.e("getFileNameByUri", temp_file.getPath());

            try {
                File fileCopy = copyToTempFile(uri, temp_file);
//                fileSelected(fileCopy);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            uploadImageToServer(temp_file.getPath(),1);
            uploadImageToServerDup(temp_file.getPath(), 1);


        }
    }

    private File copyToTempFile(Uri uri, File tempFile) throws IOException {
        // Obtain an input stream from the uri
        InputStream inputStream = getContentResolver().openInputStream(uri);

        if (inputStream == null) {
            throw new IOException("Unable to obtain input stream from URI");
        }

        // Copy the stream to the temp file
        FileUtils.copy(inputStream, tempFile);


        return tempFile;
    }

    private String getFileName(Uri uri) throws IllegalArgumentException {
        // Obtain a cursor with information regarding this uri
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            throw new IllegalArgumentException("Can't obtain file name, cursor is empty");
        }
        cursor.moveToFirst();
        String fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
        cursor.close();
        return fileName;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveBase64toFile(String s) {

        try {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MGINUploadInvoiceTest_MainCopy.this.openFileOutput(timeStamp + "_invoice.pdf", Context.MODE_PRIVATE));
            outputStreamWriter.write(s);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private void readFromFilePrivate() {

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File file = new File(MGINUploadInvoiceTest_MainCopy.this.getFilesDir(), timeStamp + "_invoice.pdf");
        Log.e("file.getPath()", file.getPath());
        Log.e("file.getPath()", file.getAbsolutePath());

        uploadPdfToServer(file.getPath());
//        uploadImageToServer(file.getPath());
    }


    public String getStringPdf(Uri filepath) {
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            inputStream = getContentResolver().openInputStream(filepath);

            byte[] buffer = new byte[1024];
            byteArrayOutputStream = new ByteArrayOutputStream();

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        byte[] pdfByteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(pdfByteArray, DEFAULT);
    }

    /*   public String getPDFPath(Uri uri){

           final String id = DocumentsContract.getDocumentId(uri);
           final Uri contentUri = ContentUris.withAppendedId(
                   Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

           String[] projection = { MediaStore.Images.Media.DATA };
           Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
           int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
           cursor.moveToFirst();
           return cursor.getString(column_index);
       }*/
    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        /*UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorAccent));
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(100, 100)
                .withAspectRatio(5f, 5f)
                .start(this);*/

        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);
        options.setMaxBitmapSize(10000);

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .withAspectRatio(3, 4)
                .start(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            View view = MGINUploadInvoiceTest_MainCopy.this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this)
                    .setMessage("Are you sure you want to Logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent in = new Intent(MGINUploadInvoiceTest_MainCopy.this, LoginActivity.class);
                            startActivity(in);
                            finish();
                            SharedPreferences sp = getSharedPreferences("LoginDetails", MODE_PRIVATE);
                            sp.edit().putBoolean("Login", false).apply();

                        }
                    }).setNegativeButton("No", null)
                    .show();


        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void showFailedAlert(final String msg) {

        MGINUploadInvoiceTest_MainCopy.this.runOnUiThread(new Runnable() {
            public void run() {
                //Log.e(TAG,msg);
                AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
                builder.setTitle("HnGmBOS");
                builder.setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //on click event
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void showAlert(final String message) {

        Log.w(TAG, "ShowAlert Diloag message " + message);

        MGINUploadInvoiceTest_MainCopy.this.runOnUiThread(new Runnable() {
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
                builder.setTitle("HnGmBOS");
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                //clear_btn.performClick();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    String userChoice = "";

    private void showAlertDialog(final String message, final String gNo,
                                 final int responseValue) {

        Log.w(TAG, "Show Alert Dialog Message " + message);
        MGINUploadInvoiceTest_MainCopy.this.runOnUiThread(new Runnable() {
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
                builder.setTitle("HnGmBOS");
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                userChoice = "Yes";
                                pendingVendorDelivery(gNo, responseValue);
                                //userChose("Yes", gNo);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                userChoice = "No";
                                //userChose("No", "");
                                pendingVendorDelivery("", responseValue);
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void showAlertDialog(final String message, final String gNo) {
        MGINUploadInvoiceTest_MainCopy.this.runOnUiThread(new Runnable() {
            public void run() {

                AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
                builder.setTitle("HnGmBOS");
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                userChoice = "Yes";
                                userChose("Yes", gNo);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                userChoice = "No";
                                userChose("No", "");
                                dialogInterface.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    void pendingVendorDelivery(final String gNo, final int responseValue) {

        Log.w(TAG, "pendingVendorDelivery ginNo " + gNo + "  responseCnt " + responseValue);

        if (responseValue > 0) {

            Log.w(TAG, "Pending vendor count > 0. Prompting to user Are the vendor picked");

            AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
            builder.setTitle("HnGmBOS");
            //builder.setMessage("There are " + responseValue + " pending vendor return . Are the vendor picked ?")
            builder.setMessage("There are " + responseValue + " Vendor Returns pending, Please confirm whether vendor is taking it now ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            if (!userChoice.isEmpty())
                                userChose(userChoice, userChoice.equalsIgnoreCase("Yes") ? gNo : "");

                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.w(TAG, "Vendor not picked ginNo " + gNo);
                            captureReason(gNo);
                            dialogInterface.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else
            userChose(userChoice, userChoice.equalsIgnoreCase("Yes") ? gNo : "");
    }

    void userChose(String choice, String ginNo) {

        Log.w(TAG, "User choice to proceed to PODetails " + choice + " GinNo " + ginNo);
        if (choice.equalsIgnoreCase("Yes")) {
            Intent intent = new Intent(MGINUploadInvoiceTest_MainCopy.this, POdetailsActivity.class);
            intent.putExtra("gin_no", ginNo);
            startActivity(intent);
        } else if (choice.equalsIgnoreCase("No")) {
            Log.d(TAG, "User does not want to proceed to Gin");
        }
    }

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private void showCameraPreview() {

        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                requestCameraPermission();
        }
    }

    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted1");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted1");
            return true;
        }
    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted2");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted2");
            return true;
        }
    }

    public boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted4");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked4");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 4);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted4");
            return true;
        }
    }

    /**
     * Requests the {@link Manifest.permission#CAMERA} permission.
     * If an additional rationale should be displayed, the user has to launch the request from
     * a SnackBar that includes additional information.
     */
    private void requestCameraPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage(R.string.camera_access_required)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MGINUploadInvoiceTest_MainCopy.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    PERMISSION_REQUEST_CAMERA);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();

        } else {
            //Toast.makeText(this, R.string.camera_unavailable, Toast.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        }
    }

    private void toastError(String message) {
        Log.e(TAG, "Toast Error message " + message);
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = toast.getView();
        view.setBackgroundResource(R.drawable.custom_background);
        toast.show();
    }

    private void toastSuccess(String message) {
        Log.e(TAG, "Toast Success message " + message);
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = toast.getView();
        view.setPadding(15, 4, 15, 4);
        view.setBackgroundResource(R.drawable.custom_success_background);
        toast.show();
    }

    @Override
    public void onInputDeviceAdded(int i) {
        Log.e(TAG, "Input device added " + i);
    }

    @Override
    public void onInputDeviceRemoved(int i) {
        Log.e(TAG, "Input device removed " + i);
    }

    @Override
    public void onInputDeviceChanged(int i) {
        Log.e(TAG, "Input device changed " + i);
    }


    @Override
    protected void onResume() {
        super.onResume();

        getActionDetails();

        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

        // Register an input device listener to watch when input devices are
        // added, removed or reconfigured.
        mInputManager.registerInputDeviceListener(this, null);

        // Query all input devices.
        // We do this so that we can see them in the log as they are enumerated.
        int[] ids = mInputManager.getInputDeviceIds();
        for (int id : ids) {
            getInputDeviceState(id);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Remove the input device listener when the activity is paused.
        mInputManager.unregisterInputDeviceListener(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Update device state for visualization and logging.
        InputDeviceState state = getInputDeviceState(event.getDeviceId());

        if (state != null) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (state.onKeyDown(event)) {
                        Log.e(TAG, "Action Down " + state);
                    }
                    break;

                case KeyEvent.ACTION_UP:
                    if (state.onKeyUp(event)) {
                        Log.e(TAG, "Action UP " + state);
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private InputDeviceState getInputDeviceState(int deviceId) {
        InputDeviceState state = mInputDeviceStates.get(deviceId);
        if (state == null) {
            final InputDevice device = mInputManager.getInputDevice(deviceId);
            if (device == null) {
                return null;
            }
            state = new InputDeviceState(device);
            mInputDeviceStates.put(deviceId, state);
            //Log.i(TAG, "Device enumerated: " + state.getDevice());
        }
        return state;
    }

    private void showErrorType(VolleyError error) {
        try {
            if (error instanceof TimeoutError) {
                showFailedAlert("Time out error occurred.");
                Log.e(TAG, "Time out error occurred.");

            } else if (error instanceof NoConnectionError) {
                showFailedAlert("Network error occurred.");
                Log.e(TAG, "Network error occurred.");

            } else if (error instanceof AuthFailureError) {
                showFailedAlert("Authentication error occurred.");
                Log.e(TAG, "Authentication error occurred.");

            } else if (error instanceof ServerError) {
                showFailedAlert("Server error occurred.");
                Log.e(TAG, "Server error occurred.");

            } else if (error instanceof NetworkError) {
                showFailedAlert("Network error occurred.");
                Log.e(TAG, "Network error occurred.");

            } else if (error instanceof ParseError) {
                showFailedAlert("An error occurred.");
                Log.e(TAG, "An error occurred.");
            } else {
                showFailedAlert("An error occurred.");
                Log.e(TAG, "An error occurred.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Exception <<::>> " + e);
        }
    }


    private void chooseImage() {
        final CharSequence[] optionsChoose = new
                CharSequence[]{"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this);
        builder.setTitle("Add Photo!");
        builder.setItems(optionsChoose, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (optionsChoose[item].equals("Take Photo")) {
                    if (isCameraPermissionGranted() && isWriteStoragePermissionGranted() && isReadStoragePermissionGranted())
                        captureImage();

                } else if (optionsChoose[item].equals("Choose from Gallery")) {
                    if (isReadStoragePermissionGranted() && isWriteStoragePermissionGranted())
                        pickFromGallery();

                } else if (optionsChoose[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    chooseImage();
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    chooseImage();
                }
                break;

            case 4:
                Log.d(TAG, "Camera Permission granted");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    chooseImage();
                }
                break;

        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String convertDataFormat(String date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        String[] myData = date.split("-");
        //date = myData[0] + "-" + validMonth(myData[1]) + "-" + myData[2];
        Log.w(TAG, "Getting the correct month####### " + date);
        Date myDate = null;

        try {
            myDate = dateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        mInvoiceDate = timeFormat.format(myDate);
        return timeFormat.format(myDate);
    }

    private String validMonth(String month) {

        String monthName = month.toUpperCase();
        String mName = "";

        try {

            switch (monthName.charAt(0)) {

                case 'J':
                    if (monthName.charAt(1) == 'A')
                        mName = "Jan";
                    else if (monthName.charAt(1) == 'U') {
                        if (monthName.charAt(2) == 'N')
                            mName = "Jun";
                        else if (monthName.charAt(2) == 'L')
                            mName = "Jul";
                    } else {
                        if (monthName.charAt(2) == 'N')
                            mName = "Jun";
                        else if (monthName.charAt(2) == 'L')
                            mName = "Jul";
                    }
                    break;

                case 'F':
                    mName = "Feb";
                    break;

                case 'M':
                    if (monthName.charAt(2) == 'R')
                        mName = "Mar";
                    else if (monthName.charAt(2) == 'Y')
                        mName = "May";
                    break;

                case 'A':
                    if (monthName.charAt(1) == 'P')
                        mName = "Apr";
                    else if (monthName.charAt(1) == 'U')
                        mName = "Aug";
                    else {
                        if (monthName.charAt(2) == 'R')
                            mName = "Apr";
                        else if (monthName.charAt(2) == 'G')
                            mName = "Aug";
                    }
                    break;

                case 'S':
                    mName = "Sep";

                case 'N':
                    mName = "Nov";

                case 'D':
                    mName = "Dec";
            }
            return mName;
        } catch (Exception e) {
            e.printStackTrace();
            return month;
        }
    }

    public static void longLog(String str) {
        if (str.length() > 4000) {
            Log.d("D:/", str.substring(0, 4000));
            longLog(str.substring(4000));
        } else
            Log.d("D:/", str);
    }

    private String readFromFile() {

        File logfile = Environment.getExternalStorageDirectory();
        File myFile = new File(logfile.getAbsolutePath() + "/mBOSlog/");

        if (!myFile.exists()) {
            myFile.mkdir();
        }
        File file = new File(myFile, "Base65.txt");
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        return text.toString().replaceAll("\\s", "");
    }

    private void prepareMenuData() {

        /*MenuModel menuModel = new MenuModel("Home Screen", true, false, "HomeScreen", 0); //Menu of Expiry Check
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            Log.d(TAG, "Home Screen");
            childList.put(menuModel, null);
        }*/

        MenuModel menuModel = new MenuModel("Transaction", true, true, "", 0); //Menu of Android Tutorial. No sub menus
        headerList.add(menuModel);

        List<MenuModel> childModelsList = new ArrayList<>();

        MenuModel childModel = new MenuModel("GIN", false, false,
                "POdetailsActivity", R.drawable.gin);
        childModelsList.add(childModel);

        childModel = new MenuModel("Pre GIN", false, false,
                "MGINUploadInvoiceTest", R.drawable.gin);
        childModelsList.add(childModel);

        /*childModel = new MenuModel("Pre GIN Test", false, false,
                "MGINUploadInvoiceTest", R.drawable.gin);
        childModelsList.add(childModel);*/

        childModel = new MenuModel("PIHV", false, false,
                "PIHVactivity", R.drawable.pihv);
        childModelsList.add(childModel);

        childModel = new MenuModel("Transfer IN", false, false,
                "TransferInActivity", R.drawable.in);
        childModelsList.add(childModel);

        childModel = new MenuModel("Transfer Out", false, false,
                "TransferActivity", R.drawable.out);
        childModelsList.add(childModel);

        childModel = new MenuModel("Purchase Return", false, false,
                "PurchaseReturnActivity", R.drawable.retrn);
        childModelsList.add(childModel);

        /*childModel = new MenuModel("Shelf Ticketing", false, false,
                "ShelfTicketActivity", R.drawable.shelf);
        childModelsList.add(childModel);*/

        if (menuModel.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel, childModelsList);
        }

        /*childModelsList = new ArrayList<>();
        menuModel = new MenuModel("Customer", true, true, "", 0); //Menu of Customer
        headerList.add(menuModel);

        childModel = new MenuModel("Product Feedback", false, false,
                "ProductFeedbackActivity", R.drawable.feedbck);
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel, childModelsList);
        }*/

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("Masters", true, true, "", 0); //Menu of Master
        headerList.add(menuModel);
        childModel = new MenuModel("Download Data", false, false,
                "MasterDownloadActivity", R.drawable.dwnld);
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            Log.d("API123", "here");
            childList.put(menuModel, childModelsList);
        }

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("Application", true, true, "", 0); //Menu of Application
        headerList.add(menuModel);
        childModel = new MenuModel("Download APK", false, false,
                "APKdownloadActivity", R.drawable.dwnld);
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            Log.d(TAG, "Application menu here");
            childList.put(menuModel, childModelsList);
        }

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("Registers", true, true, "", 0); //Menu of Registers
        headerList.add(menuModel);

        childModel = new MenuModel("Free Gift Tester Register", false, false,
                "FreeGiftTestRegisterActivity", R.drawable.ic_register);
        childModelsList.add(childModel);

        childModel = new MenuModel("Bank Deposit Register", false, false,
                "BankDepositGridActivity", R.drawable.ic_register);
        childModelsList.add(childModel);

        childModel = new MenuModel("Generator Register", false, false,
                "GeneratorRegisterListActivity", R.drawable.ic_register);
        childModelsList.add(childModel);

        childModel = new MenuModel("Store Electricity", false, false,
                "ElectricityDetailsActivity", R.drawable.ic_electricity_bill);
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            Log.d(TAG, "Registers menu here");
            childList.put(menuModel, childModelsList);
        }

      /*  menuModel = new MenuModel("Customer Related", true, false, "Customer", R.drawable.ic_customer_feedback); //Menu of Expiry Check
        headerList.add(menuModel);*/

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("Expiry Check", true, true, "", 0); //Menu of Expiry Check
        headerList.add(menuModel);

        childModel = new MenuModel("Testers Free Gift Expiry Check", false, false,
                "TestersFreeGiftExpiryCheckActivity", R.drawable.ic_register);
        childModelsList.add(childModel);

        childModel = new MenuModel("Product Expiry Check", false, false,
                "ExpiryCheckActivity", R.drawable.ic_expiry);
        childModelsList.add(childModel);

        if (menuModel.hasChildren) {
            Log.d(TAG, "Expiry Check menu here");
            childList.put(menuModel, childModelsList);
        }
//        headerList.add(menuModel);

        menuModel = new MenuModel("Stock Check", true, false, "StockCheck", 0);
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            Log.d(TAG, "Stock Check");
            childList.put(menuModel, null);
        }

        childModelsList = new ArrayList<>();
        menuModel = new MenuModel("LogOut", true, false, "LogOut", 0); //Menu of Expiry Check
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            Log.d(TAG, "LogOut");
            childList.put(menuModel, null);
        }
    }

    private void populateExpandableList() {

        Log.w(TAG, "HeaderList Size " + headerList.size());
        Log.w(TAG, "ChildList Size " + childList.size());

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (headerList.get(groupPosition).isGroup) {
                    if (!headerList.get(groupPosition).hasChildren) {

                        if (headerList.get(groupPosition).className.equalsIgnoreCase("HomeScreen")) {

                            startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, HomeScreenActivity.class));
                            finish();

                        } else if (headerList.get(groupPosition).className.equalsIgnoreCase("LogOut")) {

                            View view = MGINUploadInvoiceTest_MainCopy.this.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

                            new AlertDialog.Builder(MGINUploadInvoiceTest_MainCopy.this)
                                    .setMessage("Are you sure you want to Logout?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            Intent in = new Intent(MGINUploadInvoiceTest_MainCopy.this, LoginActivity.class);
                                            startActivity(in);
                                            finish();
                                            SharedPreferences sp = getSharedPreferences("LoginDetails", MODE_PRIVATE);
                                            sp.edit().putBoolean("Login", false).apply();

                                        }
                                    }).setNegativeButton("No", null)
                                    .show();
                        } else if (headerList.get(groupPosition).className.equalsIgnoreCase("Customer")) {

                            startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, CustomerRelatedActivity.class));
                            finish();
                        } else if (headerList.get(groupPosition).className.equalsIgnoreCase("StockCheck")) {
                            startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, StockActivityTemp.class));
                            finish();
                        }
                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                    }

                }

                return false;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childList.get(headerList.get(groupPosition)) != null) {
                    MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);

                    Log.w(TAG, "Class Name <<:::>> " + model.className);

                    if (model.className.length() > 0) {
                        switch (model.className) {

                            case "POdetailsActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, POdetailsActivity.class));
                                break;

                            case "PIHVactivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, PIHVactivity.class));
                                finish();
                                break;

                            case "TransferInActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, TransferInActivity.class));
                                finish();
                                break;

                            case "TransferActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, TransferActivity.class));//transferOUT
                                finish();
                                break;

                            case "PurchaseReturnActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, PurchaseReturnActivity.class));
                                finish();
                                break;

                            case "ShelfTicketActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, ShelfTicketActivity.class));
                                finish();
                                break;

                            case "ProductFeedbackActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, ProductFeedbackActivity.class));
                                finish();
                                break;

                            case "MasterDownloadActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, MasterDownloadActivity.class));
                                finish();
                                break;

                            case "APKdownloadActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, APKdownloadActivityTemp.class));
//                                startActivity(new Intent(MGINUploadInvoiceTest.this, APKdownloadActivity.class));
                                finish();
                                break;

                            case "BankDepositGridActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, BankDepositGridActivity.class));
                                break;

                            case "GeneratorRegisterListActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, GeneratorRegisterListActivity.class));
                                break;

                            case "ElectricityDetailsActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, ElectricityDetailsActivity.class));
                                break;

                            case "FreeGiftTestRegisterActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, FreeGiftTestRegisterActivity.class));
                                break;

                            case "TestersFreeGiftExpiryCheckActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, TestersFreeGiftExpiryCheckActivity.class));
                                break;

                            case "ExpiryCheckActivity":
                                startActivity(new Intent(MGINUploadInvoiceTest_MainCopy.this, ExpiryCheckActivity.class));
                                break;
                        }
                        DrawerLayout drawer = findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        //onBackPressed();
                    }
                }

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimerUtil.startLogoutTimer(this, this);
        Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimerUtil.startLogoutTimer(this, this);
        //Log.e(TAG, "User interacting with screen");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogOutTimerUtil.stopLogoutTimer();
        Log.e(TAG, "onStop () &&& Stop timer");
    }

    @Override
    public void doLogout() {
        Log.d(TAG, "User is inactive for last 5 mins");
        Toast.makeText(this, "user is inactive from last 5 minutes", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MGINUploadInvoiceTest_MainCopy.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    static class DecimalDigitsInputFilter implements InputFilter {
        private Pattern mPattern;

        DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }

}

//2021-09-28 11:01:03.854 9675-9675/com.HnG.stock.mbos E/jsonObject2: {"message":"Success","input":"\/storage\/emulated\/0\/InvoicePDF\/Invoice_20210928_110036.pdf","prediction":[{"id":"","label":"Invoice_Amount","xmin":2011,"ymin":2083,"xmax":2186,"ymax":2110,"score":0.42674237,"ocr_text":"-3230.12"}],"page":0,"request_file_id":"a0fe0779-82da-46fa-951a-7824aeba3d34","filepath":"uploadedfiles\/f4195852-b39f-4034-8fff-856292453e69\/PredictionImages\/1343265023-1.jpeg","id":"40a48fd4-201d-11ec-8a65-92312b031665","rotation":360}