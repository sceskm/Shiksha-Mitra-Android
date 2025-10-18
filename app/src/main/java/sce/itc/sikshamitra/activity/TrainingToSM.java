package sce.itc.sikshamitra.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityTrainingShikshaMitraBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.model.TrainingSM;

public class TrainingToSM extends AppCompatActivity {
    private static final String TAG = "TrainingToSMActivity";
    private Toolbar toolbar;
    private ActivityTrainingShikshaMitraBinding binding;
    private DatabaseHelper dbHelper;

    private GPSTracker gps;
    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;
    private final TrainingToSM context = TrainingToSM.this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private Handler timerHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_shiksha_mitra);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SM Training");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateDetails();

        clickEvent();
    }

    private void clickEvent() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    try {
                        progressDialog.show();
                        binding.btnSubmit.setEnabled(false);
                        timerHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                saveTrainingData();
                                Log.d(TAG, "run: save btnSubmit");
                            }
                        }, 200);

                    } catch (Exception e) {
                        Log.e(TAG, "onClick: ", e);
                    } finally {
                        binding.btnSubmit.setEnabled(true);
                    }


                }

            }
        });

    }

    private void populateDetails() {
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

    private void saveTrainingData() {
        TrainingSM trainingSM = new TrainingSM();
        trainingSM.setSMCount(Integer.parseInt(binding.editSmCount.getText().toString().trim()));
        trainingSM.setLatitude(lastLatitude);
        trainingSM.setLongitude(lastLongitude);
        trainingSM.setRemarks(binding.editRemarks.getText().toString().trim());
        trainingSM.setScheduledDateTime(Common.iso8601Format.format(new Date()));

        if (dbHelper.saveTrainingData(trainingSM)) {
            showSuccessAlert("Training data saved successfully", true);
        } else {
            mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(() -> {
                progressDialog.dismiss();
                new MaterialAlertDialogBuilder(this, R.style.RoundShapeTheme)
                        .setTitle("Error").setMessage("Failed to save training data, please try again").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        }).setCancelable(false)
                        .show();
            });

        }


    }

    private void showSuccessAlert(String s, boolean isUpload) {
        mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            progressDialog.dismiss();
            new MaterialAlertDialogBuilder(this, R.style.RoundShapeTheme)
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

    private boolean checkValidation() {
        if (binding.editSmCount.getText().toString().trim().isEmpty()) {
            binding.editSmCount.setError("Please enter training date");
            return false;
        }

        return true;
    }
}