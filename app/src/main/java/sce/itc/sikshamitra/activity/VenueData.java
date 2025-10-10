package sce.itc.sikshamitra.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.Date;
import java.util.Timer;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityVenueDataBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
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
            getSupportActionBar().setTitle("Venue Setup");
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
        binding.btnSubmit.setOnClickListener(v -> {
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
                        binding.btnSubmit.setEnabled(false);
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
                        binding.btnSubmit.setEnabled(true);
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
                        Common.getGuid(),
                        lastLatitude,
                        lastLongitude
                );
        if (dbHelper.saveVenueData(data)){
            String message = "";
            if (Common.DEBUGGING) {
                message = getResources().getString(R.string.latitude)
                        + getResources().getString(R.string.colon) + String.valueOf(lastLatitude)
                        + getResources().getString(R.string.longitude)
                        + getResources().getString(R.string.colon) + String.valueOf(lastLongitude);
                showSuccessAlert(getResources().getString(R.string.data_saved_message) + message, false);
            }
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
