package sce.itc.sikshamitra.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityVenueDataBinding;
import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.NetworkUtils;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.CommunicationSend;
import sce.itc.sikshamitra.model.Venue;

public class VenueData extends AppCompatActivity {
    private static final String TAG = "VenueDataActivity";
    private ActivityVenueDataBinding binding;
    private Toolbar toolbar;

    DatabaseHelper dbHelper;
    GPSTracker gps;
    private double lastLatitude = -1;
    private double lastLongitude = -1;

    //context
    private final Context context = this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_venue_data);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SM Training Venue Details");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateData();

        clickEvent();
    }

    private void populateData() {
        dbHelper = DatabaseHelper.getInstance(context);
        gps = new GPSTracker(context);

        if (gps.canGetLocation()) {
            lastLatitude = gps.getLatitude();
            lastLongitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("saving your data..");
        progressDialog.setTitle("Please Wait..");
        mainHandler = new Handler(Looper.getMainLooper());

    }

    private void clickEvent() {
        binding.btnRegister.setOnClickListener(v -> {
            if (!checkGps()) {
                permission();
            } else {
                gps = new GPSTracker(VenueData.this);
                // check if GPS enabled
                if (gps.canGetLocation()) {
                    lastLatitude = gps.getLatitude();
                    lastLongitude = gps.getLongitude();
                }
            }

            if (Common.checkLatLong(ConstantField.TEST_LATITUDE, ConstantField.TEST_LONGITUDE)) {
                //if (Common.checkLatLong(lastLatitude, lastLongitude)) {
                if (checkValidation()) {
                    try {
                        progressDialog.show();
                        binding.btnRegister.setEnabled(false);
                        timerHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: save attendance");
                                saveVenueDetails();
                            }
                        }, 200);

                    } catch (Exception e) {
                        Log.e(TAG, "onClick: ", e);
                    } finally {
                        binding.btnRegister.setEnabled(true);
                    }
                }
            } else {
                locationErrorMessage(getResources().getString(R.string.incorrect_location_message)
                        + getResources().getString(R.string.latitude) + String.valueOf(lastLatitude)
                        + getResources().getString(R.string.longitude) + String.valueOf(lastLongitude));
            }
        });
    }

    private void saveVenueDetails(){
        String scheduledDate = Common.iso8601Format.format(new Date());
        Venue data =
                new Venue(
                        binding.editVenueName.getText().toString().trim(),
                        scheduledDate,
                        Common.getString(binding.editAddressLine1.getText().toString().trim()),
                        Common.getString(binding.editAddressLine2.getText().toString().trim()),
                        Common.getString(binding.editCity.getText().toString().trim()),
                        Common.getString(binding.editDistrict.getText().toString().trim()),
                        Common.getString(binding.editState.getText().toString().trim()),
                        Common.getString(binding.editPinCode.getText().toString().trim()),
                        "",
                        Common.createGuid(),
                        lastLatitude,
                        lastLongitude,
                        Common.createGuid()
                );

        if ((Common.checkInternetConnectivity(context)
                || Common.checkInternetConnectivitySIMOnly(context))
                && PreferenceCommon.getInstance().getAutoSyncing() != ConstantField.AUTO_DOWNLOAD
        ) {
            if (dbHelper.saveVenueData(data)){
                callNetworkApi(data);
                String message = "";
                if (Common.DEBUGGING) {
                    message = getResources().getString(R.string.latitude)
                            + getResources().getString(R.string.colon) + String.valueOf(lastLatitude)
                            + getResources().getString(R.string.longitude)
                            + getResources().getString(R.string.colon) + String.valueOf(lastLongitude);
                    showSuccessAlert(getResources().getString(R.string.data_saved_uploaded) + message, false);
                }

            } else {
                progressDialog.dismiss();
                Common.showAlert(context, getResources().getString(R.string.data_not_saved_message));
            }
        }
    }

    /*
    * Call network api to upload data
    * */
    private void callNetworkApi(Venue attendanceDetail) {
        //just now saved unprocessed message
        Cursor cursorCount = dbHelper.currentUnprocessedCommSendMessage(Command.ADD_VENUE,
                PreferenceCommon.getInstance().getUserGUID(), attendanceDetail.getCommunicationGUID());
        if (cursorCount.getCount() > 0) {
            cursorCount.moveToFirst();
            CommunicationSend communicationSend = new CommunicationSend();
            communicationSend.populateFromCursor(cursorCount);
            if (!communicationSend.getCommandDetails().isEmpty()) {
                Venue attendance = Venue.fromJson(communicationSend.getCommandDetails());
                /*String imagePath = attendance.getImageFile();
                if (imagePath.isEmpty() == false) {
                    String image = Common.convertBase64(imagePath, this);
                    attendance.setImageFile(image);
                }*/
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Command.COMMAND, Command.ADD_VENUE);
                    jsonObject.put(Command.VERSION, ConstantField.APP_VERSION);
                    jsonObject.put(Command.DATA, attendance.getJson());
                    jsonObject.put(Command.COMMAND_GUID, communicationSend.getCommunicationGUID());
                    jsonObject.put(Command.PROCESS_COUNT, communicationSend.getProcessCount());
                    //jsonObject.put(Command.BACKGROUND, Common.INSTANT_DOWNLOAD);
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                    final OkHttpClient client = new OkHttpClient();
                    //.newBuilder().connectTimeout(10, TimeUnit.SECONDS)
                    //.retryOnConnectionFailure(false)
                    //.build();
                    client.newCall(NetworkUtils.enqueNetworkRequest(ConstantField.NETWORK_URL + ConstantField.ACTION_URL, body, true))
                            .enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    showSuccessAlert(getResources().getString(R.string.data_saved_not_uploaded), false);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String successMsg = response.body().string();
                                    if (response.isSuccessful()) {
                                        //update communication
                                        dbHelper.updateCommunicationSendStatus(communicationSend.getID(),
                                                ConstantField.COMM_STATUS_PROCESSED, "success", false);
                                        showSuccessAlert(getResources().getString(R.string.data_saved_uploaded), true);
                                    } else {
                                        showSuccessAlert(getResources().getString(R.string.data_saved_not_uploaded), false);
                                    }
                                }
                            });
                } catch (Exception e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
            cursorCount.close();
        }

    }

    private boolean checkValidation() {
        boolean isValid = true;
        if (binding.editVenueName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter venue name", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editAddressLine1.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter venue address", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,"Please enter city",Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editDistrict.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter district", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,"Please enter state",Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }

        return isValid;

    }

    public void permission() {
        if (!checkGps()) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(VenueData.this);
            dialog.setMessage("Location Settings is turned off!! Please turn it on.");
            dialog.setCancelable(false);
            dialog.setPositiveButton("GO To Setup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.show();
        }
    }
    //for checking GPS
    public boolean checkGps() {
        LocationManager lm = (LocationManager) VenueData.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            return false;
        } else {
            return true;
        }
    }

    private void locationErrorMessage(String s) {
        new MaterialAlertDialogBuilder(VenueData.this, R.style.RoundShapeTheme)
                .setTitle("Error").setMessage(s).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        dialogInterface.dismiss();

                    }
                }).setCancelable(false)
                .show();


    }

    private void showSuccessAlert(String s, boolean isUpload) {
        mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            progressDialog.dismiss();
            new MaterialAlertDialogBuilder(VenueData.this, R.style.RoundShapeTheme)
                    .setTitle("Great").setMessage(s).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            dialogInterface.dismiss();

                        }
                    }).setCancelable(false)
                    .show();
        });

    }
}
