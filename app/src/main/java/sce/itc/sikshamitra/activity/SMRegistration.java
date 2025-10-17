package sce.itc.sikshamitra.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


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
import sce.itc.sikshamitra.databinding.ActivitySmregistrationBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.model.PreRegistration;

public class SMRegistration extends AppCompatActivity {
    private static final String TAG = "SMRegistrationActivity";
    private ActivitySmregistrationBinding binding;
    private Toolbar toolbar;
    private DatabaseHelper dbHelper;
    private GPSTracker gps;
    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;
    private final SMRegistration context = SMRegistration.this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private Handler timerHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_smregistration);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SM Registration By Agency");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        populateDetails();

        clickEvents();
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

    private void clickEvents() {
        binding.btnRegister.setOnClickListener(v -> {
            if (checkValidations()) {
                try {
                    progressDialog.show();
                    binding.btnRegister.setEnabled(false);
                    timerHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: save attendance");
                            registerSM();
                        }
                    }, 200);

                } catch (Exception e) {
                    Log.e(TAG, "onClick: ", e);
                } finally {
                    binding.btnRegister.setEnabled(true);
                }


            }
        });
    }

    private void registerSM() {
        String firstName = binding.editFirstName.getText().toString().trim();
        String lastName = binding.editLastName.getText().toString().trim();
        String mobileNumber = binding.editMobileNumber.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();

        PreRegistration preRegistration = new PreRegistration();
        preRegistration.setFirstName(firstName);
        preRegistration.setLastName(lastName);
        preRegistration.setMobile(mobileNumber);
        preRegistration.setPassword(password);
        preRegistration.setVenueGUID("");
        preRegistration.setUserGUID("");
        preRegistration.setShikshaMitraGUID(Common.getGuid());
        preRegistration.setCreatedOn(Common.iso8601Format.format(new Date()));
        preRegistration.setLatitude(lastLatitude);
        preRegistration.setLongitude(lastLongitude);

        if (dbHelper.savePreRegistration(preRegistration))
            showSuccessAlert("Your data saved successfully.", false);
        else
            showSuccessAlert("Something went wrong, please try again.", false);
    }

    private boolean checkValidations() {
        if (binding.editFirstName.getText().toString().trim().isEmpty()) {
            binding.editFirstName.setError("Please enter first name");
            binding.editFirstName.requestFocus();
            return false;
        } else if (binding.editLastName.getText().toString().trim().isEmpty()) {
            binding.editLastName.setError("Please enter last name");
            binding.editLastName.requestFocus();
            return false;
        } else if (binding.editMobileNumber.getText().toString().trim().isEmpty()) {
            binding.editMobileNumber.setError("Please enter mobile number");
            binding.editMobileNumber.requestFocus();
            return false;
        } else if (!binding.editMobileNumber.getText().toString().trim().isEmpty()) {
            String mobilePattern = "^[6-9]\\d{9}$";
            String mobileNumber = binding.editMobileNumber.getText().toString().trim();
            if (!mobileNumber.matches(mobilePattern)) {
                System.out.println("Invalid mobile number. It must be a valid 10-digit Indian number.");
                return false;
            }
        } else if (!binding.editPassword.getText().toString().isEmpty() && binding.editPassword.getText().toString().trim().length() < 6) {
            binding.editPassword.setError("Password must be at least 6 characters");
            binding.editPassword.requestFocus();
            return false;
        } else if (binding.editConfirmPassword.getText().toString().trim().isEmpty()) {
            binding.editConfirmPassword.setError("Please enter confirm password");
            binding.editConfirmPassword.requestFocus();
            return false;
        } else if (!binding.editPassword.getText().toString().trim().equals(binding.editConfirmPassword.getText().toString().trim())) {
            binding.editConfirmPassword.setError("Password and confirm password must be same");
            binding.editConfirmPassword.requestFocus();
            return false;
        } else if (!binding.editPassword.getText().toString().trim().isEmpty()) {
            String passwordPattern = "^(?=.*[!@#$%^&*()_+\\-={}:;\"'<>,.?/]).{8,15}$";
            String password = binding.editPassword.getText().toString().trim();
            if (!password.matches(passwordPattern)) {
                binding.editPassword.setError("Password must be 8–15 characters and include at least one special character.");
                binding.editPassword.requestFocus();
                return false;
            }
        }

        return true;
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
}