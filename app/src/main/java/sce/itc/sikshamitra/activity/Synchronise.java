package sce.itc.sikshamitra.activity;


import static sce.itc.sikshamitra.helper.ConstantField.ACTION_URL;
import static sce.itc.sikshamitra.helper.ConstantField.NETWORK_URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.CommunicationOn;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.NetworkUtils;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.helper.ProcessResponse;
import sce.itc.sikshamitra.model.CommunicationSend;
import sce.itc.sikshamitra.model.Image;
import sce.itc.sikshamitra.model.RetailOutReachModel;
import sce.itc.sikshamitra.model.SendProductModel;
import sce.itc.sikshamitra.model.Session;
import sce.itc.sikshamitra.model.User;


public class Synchronise extends AppCompatActivity {
    private static final String TAG = Synchronise.class.getName();
    private MaterialButton btnDownload, btnBack;
    private DatabaseHelper dbHelper;
    private final int maxErrorsAllowed = 100;
    Date lastCommDate;
    private int errorCount = 0;
    private boolean loginMode = false;
    private ProgressDialog progressDialog;
    private final Context mContext = this;
    private int page = 0;
    private String alertMsg = "";
    private TextView txtPendingMessage;
    private TextView txtMessage;
    private AppCompatTextView txtVersion;
    //backup
    private ProgressBar progressbar;
    boolean dbExportSuccess = false;
    private String pendingMessage = "";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronise);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.showOverflowMenu();
        initialWidgets();
        //fetch pending messages
        fetchMessages();
        clickEvent();
    }

    private void initialWidgets() {
        dbHelper = DatabaseHelper.getInstance(Synchronise.this);
        dbHelper.getUser(PreferenceCommon.getInstance().getUserGUID());
        btnDownload = findViewById(R.id.btn_download);
        btnBack = findViewById(R.id.btn_back);
        progressbar = findViewById(R.id.progress);
        txtPendingMessage = findViewById(R.id.pending_message);
        txtMessage = findViewById(R.id.message);
        txtVersion = findViewById(R.id.label_version);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Data..");
        progressDialog.setTitle("Please Wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        long diff = 0;


        //If coming from signin page,then only download will visible.
        // If background service is working ,then only back button will
        // visible and background messagen will show.
        btnDownload.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        txtMessage.setText(R.string.datadownload_mesage);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Synchronise");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        String version = "Version  " + ConstantField.APP_VERSION;
        txtVersion.setText(version);

        Cursor cursor = dbHelper.getUser(PreferenceCommon.getInstance().getUserGUID());
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Common.loggedUser = new User();
            Common.loggedUser.populateFromCursor(cursor);
        }
        cursor.close();

    }

    private void clickEvent() {
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.checkInternetConnectivity(Synchronise.this)) {
                    errorCount = 0;
                    progressDialog.show();

                    Common.enableButton(btnDownload, false);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (Common.loggedUser.getRoleId() == ConstantField.ROLE_ID_SHIKSHA_MITRA) {
                                    if (errorCount < maxErrorsAllowed) {
                                        uploadSessionData();
                                        Log.d(TAG, "uploadSessionData() data upload completed");
                                    }
                                }
                                if (Common.loggedUser.getRoleId() == ConstantField.ROLE_ID_FIELD_TEAM) {
                                    if (errorCount < maxErrorsAllowed) {
                                        downloadSchoolList();
                                        Log.d(TAG, "run: downloadSchool");
                                    }
                                    if (errorCount < maxErrorsAllowed) {
                                        uploadFinalSessionData();
                                        Log.d(TAG, "uploadSessionData() data upload completed");
                                    }
                                    if (errorCount < maxErrorsAllowed) {
                                        uploadRetailData();
                                        Log.d(TAG, "run: uploadRetailData()");
                                    }
                                }
                                Synchronise.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        fetchMessages();
                                        btnBack.setVisibility(View.VISIBLE);
                                        Common.enableButton(btnBack, true);
                                        if (errorCount == 0) {
                                            pendingMessage += "\n\nGreat!\nAll data downloaded successfully.";
                                            txtPendingMessage.setText(pendingMessage);
                                            txtPendingMessage.setTextColor(getColor(R.color.Green));
                                        } else {
                                            if (loginMode) {
                                                pendingMessage = "";
                                                pendingMessage = "\n\nError occurred!\nPlease try again. Ensure that you have an internet connection.";
                                                txtPendingMessage.setVisibility(View.VISIBLE);
                                                txtPendingMessage.setText(pendingMessage);
                                                txtPendingMessage.setTextColor(getColor(R.color.Red));
                                                //showErrorAlert(alertMsg);
                                            } else {
                                                //alertMsg = "Error downloading data! Please try again.";
                                                pendingMessage += "\n\nOpps!\nError occurred.Please try again later.";
                                                txtPendingMessage.setVisibility(View.VISIBLE);
                                                txtPendingMessage.setText(pendingMessage);
                                                txtPendingMessage.setTextColor(getColor(R.color.Red));
                                                //showErrorAlert(alertMsg);
                                            }
                                        }
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                                Synchronise.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        pendingMessage += "\n\nSomething went wrong.Try again later.";
                                        txtPendingMessage.setVisibility(View.VISIBLE);
                                        txtPendingMessage.setText(pendingMessage);
                                        btnBack.setVisibility(View.VISIBLE);
                                        Common.enableButton(btnBack, true);
                                        txtPendingMessage.setTextColor(getColor(R.color.Red));
                                    }
                                });
                            } finally {
                                //dbHelper.flashDataBase();
                            }
                        }
                    }).start();

                }

            }

        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.enableButton(btnDownload, true);
                Common.enableButton(btnBack, true);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    //upload session data - SM
    private void uploadSessionData() {
        Cursor cursor = dbHelper.unProcessedCommSendMessage(Command.TEACHER_SESSION, PreferenceCommon.getInstance().getUserGUID());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                CommunicationSend communicationSend = new CommunicationSend();
                communicationSend.populateFromCursor(cursor);

                if (!communicationSend.getCommandDetails().isEmpty()) {
                    String attendanceResponse = "";
                    boolean isSuccess = false;
                    Session sessionData = Session.fromJson(communicationSend.getCommandDetails());

                    //Extra code
                    String image1 = "";
                    String image2 = "";
                    String image3 = "";
                    String image4 = "";
                    String sGuid = sessionData.getSessionGuid();
                    Cursor cursor1 = dbHelper.getSessionDetails(sGuid);
                    cursor1.moveToFirst();
                    if (cursor1.getCount() > 0) {

                        image1 = cursor1.getString(cursor1.getColumnIndex("Img1"));
                        image2 = cursor1.getString(cursor1.getColumnIndex("Img2"));
                        image3 = cursor1.getString(cursor1.getColumnIndex("Img3"));
                        image4 = cursor1.getString(cursor1.getColumnIndex("Img4"));

                    }
                    cursor1.close();

                    List<Image> imageList = new ArrayList<>();

                    //Image 1
                    /*String imagePath1 = attendancedata.getImg1();
                    if (!imagePath1.isEmpty()) {
                        Image imgVenue = new Image();
                        String imageBase64 = Common.convertBase64(imagePath1, this);
                        imgVenue.setImageDefinitionId(ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_1);
                        imgVenue.setImageName(imageBase64);
                        imgVenue.setImageFileExt(ConstantField.IMAGE_FORMAT);

                        imageList.add(imgVenue);
                    }*/

                    addImageToList(imageList, image1, ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_1, this);
                    addImageToList(imageList, image2, ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_2, this);
                    addImageToList(imageList, image3, ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_3, this);
                    addImageToList(imageList, image4, ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_4, this);

                    if (!imageList.isEmpty())
                        sessionData.setImages(imageList);


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Command.COMMAND, Command.TEACHER_SESSION);
                        jsonObject.put(Command.VERSION, ConstantField.SERVER_APP_VERSION);
                        jsonObject.put(Command.DATA, sessionData.getJson());
                        jsonObject.put(Command.COMMAND_GUID, communicationSend.getCommunicationGUID());
                        jsonObject.put(Command.PROCESS_COUNT, communicationSend.getProcessCount());

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                        Response response = NetworkUtils.excuteNetworkRequest(NETWORK_URL
                                + ACTION_URL, body, true);

                        if (response != null) {
                            ResponseBody responseBody = response.body();
                            if (response.isSuccessful()) {
                                isSuccess = true;
                                attendanceResponse = responseBody.string();
                            } else {
                                //just increase the error count
                                attendanceResponse = responseBody.string();
                                errorCount++;
                            }
                            Log.d(TAG, "onResponse: uploadAttendanceData" + attendanceResponse);
                        }
                    } catch (Exception e) {
                        errorCount++;
                        e.printStackTrace();
                    } finally {
                        if (isSuccess) {
                            // update the message as processed
                            dbHelper.updateCommunicationSendStatus(communicationSend.getID(),
                                    ConstantField.COMM_STATUS_PROCESSED, "success", false);
                        } else {
                            // mark the message as failed
                            dbHelper.updateCommunicationSendStatus(communicationSend.getID(),
                                    ConstantField.COMM_STATUS_ERROR, "error", true);
                        }
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();

        } else {
            Log.d(TAG, "uploadSessionData: ");
        }
    }

    //Download school list - Field Team
    private void downloadSchoolList() {
        String schoolListResponse = "";
        // create your json here
        String ids = dbHelper.getExistingID("SchoolId", "sp_school");

        JSONObject jsonObject = new JSONObject();
        JSONObject objData = new JSONObject();
        try {
            //get last communication data
            String lastDate = fetchLastCommDate(Command.SCHOOL_LIST);

            objData.put("lastDate", lastDate);
            objData.put("existingIds", ids);

            jsonObject.put(Command.COMMAND, Command.SCHOOL_LIST);
            jsonObject.put(Command.VERSION, ConstantField.SERVER_APP_VERSION);
            jsonObject.put(Command.DATA, objData.toString());
            jsonObject.put(Command.COMMAND_GUID, UUID.randomUUID().toString());
            jsonObject.put(Command.PROCESS_COUNT, 1);

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Response response = NetworkUtils.excuteNetworkRequest(NETWORK_URL + ACTION_URL,
                    body, true);
            if (response != null) {
                ResponseBody responseBody = response.body();
                if (response.isSuccessful()) {
                    schoolListResponse = responseBody.string();
                    ProcessResponse processResponse = new ProcessResponse(this);
                    processResponse.processSchoolList(schoolListResponse);
                    Log.d(TAG, "onResponse: SchoolList" + schoolListResponse);
                    //AFTER PROCESSING -save last date
                    dbHelper.savedLastCommDate(Command.SCHOOL_LIST);
                } else {
                    //just increase the error count
                    errorCount++;
                    Log.e(TAG, "storeList: ");
                }
            }
        } catch (Exception e) {
            errorCount++;
            e.printStackTrace();
            Log.e(TAG, "storeList: ", e);
        }
    }

    //Upload final session - Field Team
    private void uploadFinalSessionData() {
        Cursor cursor = dbHelper.unProcessedCommSendMessage(Command.ADD_FINAL_SESSION, PreferenceCommon.getInstance().getUserGUID());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                CommunicationSend communicationSend = new CommunicationSend();
                communicationSend.populateFromCursor(cursor);

                if (!communicationSend.getCommandDetails().isEmpty()) {
                    String attendanceResponse = "";
                    boolean isSuccess = false;
                    Session sessionData = Session.fromJson(communicationSend.getCommandDetails());

                    //Extra code
                    String image1 = "";
                    String image2 = "";
                    String image3 = "";
                    String image4 = "";
                    String image5 = "";
                    String image6 = "";
                    String image7 = "";
                    String image8 = "";
                    int teacherProdId = 0;
                    int isTeacherDistributed = 0;

                    int studentProdId1 = 0;
                    int isStudentDistributed1 = 0;

                    int studentProdId2 = 0;
                    int isStudentDistributed2 = 0;
                    String sGuid = sessionData.getSessionGuid();
                    Cursor cursor1 = dbHelper.getSessionDetails(sGuid);
                    cursor1.moveToFirst();
                    if (cursor1.getCount() > 0) {

                        image1 = cursor1.getString(cursor1.getColumnIndex("Img1"));
                        image2 = cursor1.getString(cursor1.getColumnIndex("Img2"));
                        image3 = cursor1.getString(cursor1.getColumnIndex("Img3"));
                        image4 = cursor1.getString(cursor1.getColumnIndex("Img4"));
                        image5 = cursor1.getString(cursor1.getColumnIndex("Img5"));
                        image6 = cursor1.getString(cursor1.getColumnIndex("Img6"));
                        image7 = cursor1.getString(cursor1.getColumnIndex("Img7"));
                        image8 = cursor1.getString(cursor1.getColumnIndex("Img8"));
                        teacherProdId = cursor1.getInt(cursor1.getColumnIndex("TeacherProductId1"));
                        isTeacherDistributed = cursor1.getInt(cursor1.getColumnIndex("IsTeacherProductDistributed1"));
                        studentProdId1 = cursor1.getInt(cursor1.getColumnIndex("StudentProductId1"));
                        isStudentDistributed1 = cursor1.getInt(cursor1.getColumnIndex("IsStudentProductDistributed1"));
                        studentProdId2 = cursor1.getInt(cursor1.getColumnIndex("StudentProductId1"));
                        isStudentDistributed2 = cursor1.getInt(cursor1.getColumnIndex("IsStudentProductDistributed2"));

                    }
                    cursor1.close();

                    List<Image> imageList = new ArrayList<>();

                    addImageToList(imageList, image1, ConstantField.FINAL_SESSION_IMAGE_EXTERIOR, this);
                    addImageToList(imageList, image2, ConstantField.FINAL_SESSION_QUIZ_PROGRESS_1, this);
                    addImageToList(imageList, image3, ConstantField.FINAL_SESSION_QUIZ_PROGRESS_2, this);
                    addImageToList(imageList, image4, ConstantField.FINAL_SESSION_REWARD_1, this);
                    addImageToList(imageList, image5, ConstantField.FINAL_SESSION_REWARD_2, this);
                    addImageToList(imageList, image6, ConstantField.FINAL_SESSION_REWARD_3, this);
                    addImageToList(imageList, image7, ConstantField.FINAL_SESSION_REWARD_4, this);
                    addImageToList(imageList, image8, ConstantField.FINAL_SESSION_HW_SAMPLE, this);

                    if (!imageList.isEmpty())
                        sessionData.setImages(imageList);

                    List<SendProductModel> productList = new ArrayList<>();

                    SendProductModel teacherProduct = new SendProductModel(teacherProdId, isTeacherDistributed);
                    productList.add(teacherProduct);

                    SendProductModel studentProduct1 = new SendProductModel(studentProdId1, isStudentDistributed1);
                    productList.add(studentProduct1);

                    SendProductModel studentProduct2 = new SendProductModel(studentProdId2, isStudentDistributed2);
                    productList.add(studentProduct2);

                    if (!productList.isEmpty())
                        sessionData.setProducts(productList);


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Command.COMMAND, Command.ADD_FINAL_SESSION);
                        jsonObject.put(Command.VERSION, ConstantField.SERVER_APP_VERSION);
                        jsonObject.put(Command.DATA, sessionData.getJson());
                        jsonObject.put(Command.COMMAND_GUID, communicationSend.getCommunicationGUID());
                        jsonObject.put(Command.PROCESS_COUNT, communicationSend.getProcessCount());

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                        Response response = NetworkUtils.excuteNetworkRequest(NETWORK_URL
                                + ACTION_URL, body, true);

                        if (response != null) {
                            ResponseBody responseBody = response.body();
                            if (response.isSuccessful()) {
                                isSuccess = true;
                                attendanceResponse = responseBody.string();
                            } else {
                                //just increase the error count
                                attendanceResponse = responseBody.string();
                                errorCount++;
                            }
                            Log.d(TAG, "onResponse: uploadAttendanceData" + attendanceResponse);
                        }
                    } catch (Exception e) {
                        errorCount++;
                        e.printStackTrace();
                    } finally {
                        if (isSuccess) {
                            // update the message as processed
                            dbHelper.updateCommunicationSendStatus(communicationSend.getID(),
                                    ConstantField.COMM_STATUS_PROCESSED, "success", false);
                        } else {
                            // mark the message as failed
                            dbHelper.updateCommunicationSendStatus(communicationSend.getID(),
                                    ConstantField.COMM_STATUS_ERROR, "error", true);
                        }
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();

        } else {
            Log.d(TAG, "uploadSessionData: ");
        }
    }

    //Upload retail out reach detail - Field Team
    private void uploadRetailData() {
        Cursor cursor = dbHelper.unProcessedCommSendMessage(Command.ADD_RETAIL,
                PreferenceCommon.getInstance().getUserGUID());
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {

                CommunicationSend communicationSend = new CommunicationSend();
                communicationSend.populateFromCursor(cursor);

                if (!communicationSend.getCommandDetails().isEmpty()) {
                    String attendanceResponse = "";
                    boolean isSuccess = false;
                    RetailOutReachModel retailData = RetailOutReachModel.fromJson(communicationSend.getCommandDetails());

                    //Extra code
                    String image1 = "";
                    String sGuid = retailData.getRetailOutreachGuid();
                    Cursor cursor1 = dbHelper.getRetailsDetails(sGuid);
                    cursor1.moveToFirst();
                    if (cursor1.getCount() > 0) {
                        image1 = cursor1.getString(cursor1.getColumnIndex("Image1"));
                    }
                    cursor1.close();

                    List<Image> imageList = new ArrayList<>();

                    addImageToList(imageList, image1, ConstantField.RETAIL_IMAGE_SHOP_IMAGE, this);

                    if (!imageList.isEmpty())
                        retailData.setImages(imageList);


                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Command.COMMAND, Command.ADD_RETAIL);
                        jsonObject.put(Command.VERSION, ConstantField.SERVER_APP_VERSION);
                        jsonObject.put(Command.DATA, retailData.getJson());
                        jsonObject.put(Command.COMMAND_GUID, communicationSend.getCommunicationGUID());
                        jsonObject.put(Command.PROCESS_COUNT, communicationSend.getProcessCount());

                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                        Response response = NetworkUtils.excuteNetworkRequest(NETWORK_URL
                                + ACTION_URL, body, true);

                        if (response != null) {
                            ResponseBody responseBody = response.body();
                            if (response.isSuccessful()) {
                                isSuccess = true;
                                attendanceResponse = responseBody.string();
                            } else {
                                //just increase the error count
                                attendanceResponse = responseBody.string();
                                errorCount++;
                            }
                            Log.d(TAG, "onResponse: uploadAttendanceData" + attendanceResponse);
                        }
                    } catch (Exception e) {
                        errorCount++;
                        e.printStackTrace();
                    } finally {
                        if (isSuccess) {
                            // update the message as processed
                            dbHelper.updateCommunicationSendStatus(communicationSend.getID(),
                                    ConstantField.COMM_STATUS_PROCESSED, "success", false);
                        } else {
                            // mark the message as failed
                            dbHelper.updateCommunicationSendStatus(communicationSend.getID(),
                                    ConstantField.COMM_STATUS_ERROR, "error", true);
                        }
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();

        } else {
            Log.d(TAG, "uploadSessionData: ");
        }
    }

    @Override
    public void onBackPressed() {
    }

    //fetch unProcess pending message count
    private void fetchMessages() {
        pendingMessage = "";
        Cursor messageCursor = dbHelper.getUnprocessedCommSendMessageCount();

        int pendingCount = 0;
        // setup display
        if (messageCursor.getCount() > 0) {
            messageCursor.moveToFirst();
            pendingCount = messageCursor.getInt(0);
        }

        if (pendingCount > 0)
            pendingMessage += Integer.toString(pendingCount) + " Pending Message(s).";
        else
            pendingMessage += "No Pending Messages.";

        txtPendingMessage.setVisibility(View.VISIBLE);
        txtPendingMessage.setText(pendingMessage);
        messageCursor.close();
    }

    private void addImageToList(List<Image> imageList, String imagePath, int imageDefId, Context context) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Image imgVenue = new Image();
            String imageBase64 = Common.convertBase64(imagePath, context);
            imgVenue.setImageDefinitionId(imageDefId);
            imgVenue.setImageName(imageBase64);
            imgVenue.setImageFileExt(ConstantField.IMAGE_FORMAT);

            imageList.add(imgVenue);
        }
    }

    private String fetchLastCommDate(String action) {
        CommunicationOn communicationOn = new CommunicationOn();
        String lastDate = ConstantField.DEFAULT_DATE;
        String last;
        Cursor cursor = dbHelper.getLastCommDate(action);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            communicationOn.populateFromCursor(cursor);
            last = communicationOn.getLastDate();
            try {
                if (!last.isEmpty() && Common.stringToDate(last) != null)
                    lastDate = last;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        cursor.close();

        return lastDate;
    }
}