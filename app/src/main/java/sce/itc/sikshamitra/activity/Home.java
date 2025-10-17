package sce.itc.sikshamitra.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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


import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databinding.ActivityHomeBinding;
import sce.itc.sikshamitra.helper.Common;

public class Home extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;
    private Toolbar toolbar;

//    private boolean cameraPermissionGranted = false;
//    private boolean locationPermissionGranted = false;
//    private boolean storageWritePermissionGranted = false;

    private static final int LOCATION_CAMERA_PERMISSION_CODE = 300;

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
    }

    private void clickEvent() {
        /*
        * Session Conducted by SM
        * */
        binding.btnStudentSessionBySm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent intent = new Intent(Home.this, ConductedSessionBySM.class);
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
                    Intent intent = new Intent(Home.this, SchoolDetailsEntry.class);
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

    /*private void requestAllPermissions() {
        // Build permission list
        if (shouldShowPermissionRationale()) {
            showRationaleDialog();
            return;
        }

        String[] perms;
        if (Build.VERSION.SDK_INT <= 28) {
            perms = new String[] { Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };
        } else {
            perms = new String[] { Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION };
        }
        permissionLauncher.launch(perms);
    }
    private boolean shouldShowPermissionRationale() {
        boolean show = false;
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) show = true;
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) show = true;
        if (Build.VERSION.SDK_INT <= 28 && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) show = true;
        return show;
    }
    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions required")
                .setMessage("This app needs camera and location to capture photos and geo-tag them. Please grant the permissions.")
                .setPositiveButton("Ok", (dialog, which) -> requestAllPermissions())
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions permanently denied")
                .setMessage("You have permanently denied some permissions. Please open app settings to enable them.")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateStatus() {
        cameraPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        locationPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        storageWritePermissionGranted = Build.VERSION.SDK_INT <= 28 ? (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) : true;

        String status = String.format("Camera: %s\nLocation: %s\nStorage (write required <=28): %s",
                cameraPermissionGranted ? "GRANTED" : "DENIED",
                locationPermissionGranted ? "GRANTED" : "DENIED",
                storageWritePermissionGranted ? "GRANTED" : "N/A or GRANTED");
        tvStatus.setText(status);

        btnTakePhoto.setEnabled(cameraPermissionGranted && locationPermissionGranted);
    }
    private boolean hasAllPermissions() {
        boolean camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean storage = Build.VERSION.SDK_INT <= 28 ?
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED : true;
        return camera && location && storage;
    }*/

}