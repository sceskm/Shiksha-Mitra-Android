package sce.itc.sikshamitra.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.List;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivitySchoolDetailsEntryBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.MySchoolData;
import sce.itc.sikshamitra.model.SchoolData;
import sce.itc.sikshamitra.model.User;

public class MySchoolEntry extends AppCompatActivity {
    private static final String TAG = "MySchoolEntry";
    private ActivitySchoolDetailsEntryBinding binding;
    private DatabaseHelper dbHelper;
    private final MySchoolEntry context = MySchoolEntry.this;
    private Toolbar toolbar;

    private GPSTracker gps;
    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    //private Handler timerHandler = new Handler();

    private List<MySchoolData> schoolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_school_details_entry);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change title programmatically
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My School Details");
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

    /*
     * Click events
     * */
    private void clickEvent() {
        /*binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    try {
                        progressDialog.show();
                        binding.buttonSubmit.setEnabled(false);
                        timerHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: save attendance");
                                saveData();
                            }
                        }, 200);

                    } catch (Exception e) {
                        Log.e(TAG, "onClick: ", e);
                    } finally {
                        binding.buttonSubmit.setEnabled(true);
                    }
                }
            }
        });*/
        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void populateData() {
        dbHelper = new DatabaseHelper(context);
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

        schoolList = new ArrayList<>();

        try {
            Cursor cursor = dbHelper.getMySchoolData();
            if (cursor.moveToFirst()) {
                do {
                    MySchoolData school = new MySchoolData();
                    school.populateFromCursor(cursor);
                    schoolList.add(school);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "populateData: ", e);
        }

        populateSchoolData();
        populateUserData();

    }

    private void populateSchoolData() {
        if (schoolList != null && !schoolList.isEmpty()) {
            for (MySchoolData school : schoolList) {
                binding.editSchoolName.setText(school.getSchoolName());
                binding.editUdiceCode.setText(school.getUdiseCode());
                binding.editDistrictName.setText(school.getDistrict());
                binding.editDistrictCode.setText(school.getDistrictCode());
                binding.editBlockName.setText(school.getBlockName());
                binding.editBlockCode.setText(school.getBlockCode());
                binding.editNameSm.setText(PreferenceCommon.getInstance().getUsername());
                binding.editSmMobile.setText(school.getPhone());
                binding.editVillageName.setText(school.getCity());
            }
        }
    }

    private void populateUserData() {
        try {
            Cursor cursor = dbHelper.getUser(PreferenceCommon.getInstance().getUserGUID());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                User user = new User();
                user.populateFromCursor(cursor);
                String fullName = Common.getString(user.getFirstName()) + " " + Common.getString(user.getLastName());
                binding.editNameSm.setText(fullName);
                binding.editSmMobile.setText(Common.getString(user.getMobileNumber()));
                binding.editSmEmail.setText(Common.getString(user.getEmail()));
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "populateUserData: ", e);

        }

    }


    /*
     * Save data
     * */
    private void saveData() {
        if (checkValidation()) {
            SchoolData schoolData = new SchoolData();
            schoolData.setSchoolName(Common.getString(binding.editSchoolName.getText().toString().trim()));
        }
    }

    /*
     * Check Validation
     * */
    private boolean checkValidation() {
        boolean ret = true;

        return ret;
    }
}