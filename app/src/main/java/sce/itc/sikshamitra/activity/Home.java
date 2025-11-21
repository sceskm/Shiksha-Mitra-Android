package sce.itc.sikshamitra.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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


import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityHomeBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.User;

public class Home extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;
    private Toolbar toolbar;
    private static final int LOCATION_CAMERA_PERMISSION_CODE = 300;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change title programmatically
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.White));
        toolbar.setTitle("Home");

        //setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //hasAllPermissions();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(Common.PERMS, LOCATION_CAMERA_PERMISSION_CODE);
            } else {
                requestPermission();
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

        clickEvent();

        populateHome();
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
                if (user.getRoleId() == ConstantField.ROLE_ID_SHIKSHA_MITRA)
                    fullName += " (Shiksha Mitra)";
                binding.txtUserName.setText(fullName);

                String version = "Version " + ConstantField.APP_VERSION;
                binding.txtRoleName.setText(version);

            }
        } catch (Exception ex) {
            Log.e(TAG, "populateHome: ", ex);
        }
        String displayDate = Common.displayDate(Common.yyyymmddFormat.format(new Date()), "yyyy-MM-dd");
        binding.txtCurrentDate.setText(displayDate);


    }

    private void clickEvent() {
        /*
         * Session Conducted by SM
         * */
        binding.btnStudentSessionBySm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    int sessionCont = PreferenceCommon.getInstance().getLastSessionCount();
                    Log.d(TAG, "onClick: sessionCont : " + sessionCont);
                    if (sessionCont <= ConstantField.MAX_NO_SESSION_ALLOWED_SM) {
                        Intent intent = new Intent(Home.this, ConductedSessionBySM.class);
                        startActivity(intent);
                    } else {
                        new MaterialAlertDialogBuilder(Home.this, R.style.RoundShapeTheme)
                                .setTitle("Oops!")
                                .setMessage("You are allowed only 6 sessions. Please contact the agency.")
                                .setPositiveButton("Okay", (dialog, which) -> dialog.dismiss())
                                .setCancelable(false)
                                .show();
                    }
                } else {
                    requestPermission();
                }

            }
        });
        binding.btnSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent intent = new Intent(Home.this, Synchronise.class);
                    startActivity(intent);
                } else {
                    requestPermission();
                }
            }
        });
        /*
         * School data entry
         * */
        binding.btnSchoolEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent intent = new Intent(Home.this, MySchoolEntry.class);
                    startActivity(intent);
                } else {
                    requestPermission();
                }
            }
        });
    }


    private void navigateToActivityReporting() {

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
        ActivityCompat.requestPermissions(Home.this, Common.PERMS, LOCATION_CAMERA_PERMISSION_CODE);
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    finishAffinity(); // Close the App
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss(); // Close the dialog only
                })
                .show();
    }

}