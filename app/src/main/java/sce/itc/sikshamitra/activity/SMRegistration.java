package sce.itc.sikshamitra.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
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
import sce.itc.sikshamitra.databinding.ActivitySmregistrationBinding;
import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.NetworkUtils;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.CommunicationSend;
import sce.itc.sikshamitra.model.PreRegistration;
import sce.itc.sikshamitra.model.Venue;

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
            getSupportActionBar().setTitle("SM Registration");
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
        String venueGuid = "70020418-a9ab-11f0-9bbe-8cec4b04410f";

        PreRegistration preRegistration = new PreRegistration();
        preRegistration.setFirstName(firstName);
        preRegistration.setLastName(lastName);
        preRegistration.setMobile(mobileNumber);
        preRegistration.setPassword(password);
        preRegistration.setVenueGUID(venueGuid);
        preRegistration.setUserGUID(PreferenceCommon.getInstance().getUserGUID());
        preRegistration.setSMGUID(Common.createGuid());
        preRegistration.setCreatedOn(Common.iso8601Format.format(new Date()));
        preRegistration.setLatitude(lastLatitude);
        preRegistration.setLongitude(lastLongitude);

        if (dbHelper.savePreRegistration(preRegistration))
            showSuccessAlert("Your data saved successfully.", false);
        else
            showSuccessAlert("Something went wrong, please try again.", false);
    }

    /*private void callNetworkApi(Venue attendanceDetail) {
        //just now saved unprocessed message
        Cursor cursorCount = dbHelper.currentUnprocessedCommSendMessage(Command.ADD_REGISTRATION,
                PreferenceCommon.getInstance().getUserGUID(), attendanceDetail.getCommunicationGuid());
        if (cursorCount.getCount() > 0) {
            cursorCount.moveToFirst();
            CommunicationSend communicationSend = new CommunicationSend();
            communicationSend.populateFromCursor(cursorCount);
            if (!communicationSend.getCommandDetails().isEmpty()) {
                Venue attendance = Venue.fromJson(communicationSend.getCommandDetails());
                *//*String imagePath = attendance.getImageFile();
                if (imagePath.isEmpty() == false) {
                    String image = Common.convertBase64(imagePath, this);
                    attendance.setImageFile(image);
                }*//*
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Command.COMMAND, Command.ADD_REGISTRATION);
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

    }*/

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