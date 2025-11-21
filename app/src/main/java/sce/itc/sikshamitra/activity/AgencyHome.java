package sce.itc.sikshamitra.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
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

import java.util.Date;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.User;

public class AgencyHome extends AppCompatActivity {
    private static final int LOCATION_CAMERA_PERMISSION_CODE = 300;
    private Toolbar toolbar;
    private static final String TAG = "AgencyHome";
    private sce.itc.sikshamitra.databinding.ActivityAgencyHomeBinding binding;
    private DatabaseHelper dbHelper;
    private final AgencyHome context = AgencyHome.this;

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

        populateHome();

        clickEvents();
    }

    private void populateHome() {
        dbHelper = DatabaseHelper.getInstance(this);

        try {
            Cursor cursor = dbHelper.getUser(PreferenceCommon.getInstance().getUserGUID());
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                User user = new User();
                user.populateFromCursor(cursor);
                String fullName = "Welcome " + Common.getString(user.getFirstName()) + " " + Common.getString(user.getLastName());

                if (user.getRoleId() == ConstantField.ROLE_ID_FIELD_TEAM)
                   fullName += " (Field Team)";

                binding.txtUserName.setText(fullName);
            }
            cursor.close();
        } catch (Exception ex) {
            Log.e(TAG, "populateHome: ", ex);
        }
        String displayDate = Common.displayDate(Common.yyyymmddFormat.format(new Date()), "yyyy-MM-dd");
        binding.txtCurrentDate.setText(displayDate);

        String appVersion = ConstantField.APP_VERSION;
        binding.txtAppVersion.setText("Version " + appVersion);


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
                    if (checkTodayVenue()) {
                        Intent intent = new Intent(AgencyHome.this, AddSMRegisterActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(AgencyHome.this,"Create today's venue first before starting the registration.",Toast.LENGTH_SHORT).show();
                    }
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
                    Intent intent = new Intent(AgencyHome.this, RetailOutReachActivity.class);
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
                    if (checkTodayVenue()) {
                        Intent intent = new Intent(AgencyHome.this, AddTrainingToSMActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(AgencyHome.this,"Create today's venue first before starting the training.",Toast.LENGTH_SHORT).show();
                    }
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

    private boolean checkTodayVenue(){
        boolean isValid = false;
        Cursor cursor = dbHelper.getTodayVenueDetails();
        cursor.moveToFirst();
        if (cursor.getCount()>0)
            isValid = true;

        cursor.close();
        return isValid;
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