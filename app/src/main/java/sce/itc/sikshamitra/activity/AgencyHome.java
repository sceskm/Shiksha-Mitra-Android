package sce.itc.sikshamitra.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.helper.Common;

public class AgencyHome extends AppCompatActivity {
    private static final int LOCATION_CAMERA_PERMISSION_CODE = 300;
    private Toolbar toolbar;
    private static final String TAG = "AgencyHome";
    private sce.itc.sikshamitra.databinding.ActivityAgencyHomeBinding binding;
    private DatabaseHelper dbHelper;
    private final AgencyHome context = AgencyHome.this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Change title programmatically
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.White));
        toolbar.setTitle("Agency Home");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(Common.PERMS, LOCATION_CAMERA_PERMISSION_CODE);
            } else {
                requestPermission();
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

        populateData();

        clickEvents();
    }
    private void populateData() {
        dbHelper = DatabaseHelper.getInstance(this);

        mainHandler = new Handler(Looper.getMainLooper());
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

    }

    private void clickEvents() {
        /*
        * Venue details button click event
        *
        * */
        binding.btnVenueDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.enableButton(binding.btnVenueDetails,false);
                if (checkPermission()) {
                    Intent intent = new Intent(AgencyHome.this, VenueActivity.class);
                    startActivity(intent);

                } else {
                    requestPermission();
                }
                Common.enableButton(binding.btnVenueDetails,true);
            }
        });
        /*
        * SM Registration button click event
        * */
        binding.btnRegistrationSm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.enableButton(binding.btnRegistrationSm,false);
                if (checkPermission()) {
                    Intent intent = new Intent(AgencyHome.this, PreRegistrationActivity.class);
                    startActivity(intent);
                } else {
                    requestPermission();
                }
                Common.enableButton(binding.btnRegistrationSm,true);
            }
        });
       /*
       * Retail Outreach button click event
       * */
        binding.btnRetailOutreach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.enableButton(binding.btnRetailOutreach,false);
                if (checkPermission()) {
                    Intent intent = new Intent(AgencyHome.this, RetailOutReach.class);
                    startActivity(intent);
                } else {
                    requestPermission();
                }
                Common.enableButton(binding.btnRetailOutreach,true);
            }
        });
        /*
        * SM Training Session button click event
        * */
        binding.btnSmTrainingSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.enableButton(binding.btnSmTrainingSession,false);
                if (checkPermission()) {
                    Intent intent = new Intent(AgencyHome.this, TrainingToSM.class);
                    startActivity(intent);
                } else {
                    requestPermission();
                }
                Common.enableButton(binding.btnSmTrainingSession,true);
            }
        });
        /*
        * Module 7 by agency click event
        * */
        binding.btnFinalSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.enableButton(binding.btnFinalSession,false);
                if (checkPermission()) {
                    Intent intent = new Intent(AgencyHome.this, FinalSessionActivity.class);
                    startActivity(intent);
                } else {
                    requestPermission();
                }
                Common.enableButton(binding.btnFinalSession,true);
            }
        });
    }

    public boolean checkPermission() {
        int location = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (location == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AgencyHome.this, Common.PERMS, LOCATION_CAMERA_PERMISSION_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_CAMERA_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "You need to grant location permission", Toast.LENGTH_SHORT).show();

            } else {
                return;
            }

        }
    }
}