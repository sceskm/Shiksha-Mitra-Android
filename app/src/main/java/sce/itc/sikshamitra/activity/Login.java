package sce.itc.sikshamitra.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityLoginBinding;
import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.NetworkUtils;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.ComboProduct;
import sce.itc.sikshamitra.model.LoginData;
import sce.itc.sikshamitra.model.MySchoolData;
import sce.itc.sikshamitra.model.Product;
import sce.itc.sikshamitra.model.Settings;
import sce.itc.sikshamitra.model.State;

public class Login extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    private Toolbar toolbar;

    private DatabaseHelper dbHelper;
    private final Login context = Login.this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change title programmatically
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.White));
        toolbar.setTitle("Home");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        clickEvent();
        populateData();
    }

    private void populateData() {
        dbHelper = DatabaseHelper.getInstance(this);

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Authenticating your data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        handler = new Handler();
    }

    private void clickEvent() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidInputs()) {
                    Common.enableButton(binding.btnLogin, false);
                    progressDialog.show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 200ms
                            callNetworkApi();

                        }
                    }, 200);
                }
            }
        });
    }

    private void callNetworkApi() {
        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            handler = new Handler(Looper.getMainLooper());

            jsonObject.put(Command.USER_NAME, binding.editUsername.getText().toString().trim());
            //jsonObject.put(Command.USER_NAME, ConstantField.USER_NAME);
            jsonObject.put(Command.PASSWORD, binding.editPwd.getText().toString().trim());
            //jsonObject.put(Command.PASSWORD, ConstantField.PASSWORD);
            jsonObject.put(Command.VERSION, ConstantField.APP_VERSION); //newly added to restrict others version

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            final OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).build();
            client.newCall(NetworkUtils.enqueNetworkRequest(ConstantField.NETWORK_URL + ConstantField.LOGIN_URL, body, false)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.post(() -> {
                        progressDialog.dismiss();
                        Common.enableButton(binding.btnLogin, true);
                        Common.showAlert(Login.this, "Internet connection failure.");
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String uResponse = response.body().string();
                    if (response.isSuccessful()) {
                        getResponse(uResponse);
                    } else {
                        handler.post(() -> {
                            progressDialog.dismiss();
                            Common.enableButton(binding.btnLogin, true);
                            Common.showAlert(Login.this, uResponse);
                        });
                    }
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
            triggerTestCrash(e.toString());
        }
    }

    private void getResponse(String response) {
        LoginData loginData = LoginData.downloadLoginUser(Common.getJsonObject(response));

        if (loginData != null && loginData.getUser() != null) {
            dbHelper.saveUser(loginData.getUser());

            for (Settings setting : loginData.getSettings()) {
                dbHelper.saveSettings(setting);
            }

            for (Product product : loginData.getProducts()) {
                dbHelper.saveProduct(product);
            }

            for (ComboProduct comboProduct : loginData.getComboProducts()) {
                dbHelper.saveComboProduct(comboProduct);
            }

            for (State state : loginData.getStates()) {
                dbHelper.saveState(state);
            }
            if (loginData.getSchoolData() != null) {
                for (MySchoolData school : loginData.getSchoolData()) {
                    dbHelper.saveSchool(school);
                }
            }
            savePreferences(loginData);
            navigateNextPage(loginData);
        } else {
            handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                new MaterialAlertDialogBuilder(context, R.style.RoundShapeTheme)
                        .setTitle("Oops!")
                        .setMessage("Invalid server response")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            });
        }

    }

    private void savePreferences(LoginData loginData) {
        try {
            PreferenceCommon.getInstance().setUserId(loginData.getUser().getUserId());
            PreferenceCommon.getInstance().setUserGUID(loginData.getUser().getUserGUID());
            PreferenceCommon.getInstance().setUserRoleId(loginData.getUser().getRoleId());
            PreferenceCommon.getInstance().setUsername(loginData.getUser().getUserName());
            PreferenceCommon.getInstance().setPassword(binding.editPwd.getText().toString().trim());
            PreferenceCommon.getInstance().setAccessToken(loginData.getUser().getAccessToken());

            if (loginData.getLastSession() != null) {
                if (loginData.getLastSession().getSessionNo() > 0) {
                    PreferenceCommon.getInstance().setLastSessionCount(loginData.getLastSession().getSessionNo());
                }
            }

            PreferenceCommon.getInstance().setLastLoggedInDateTime(Common.iso8601Format.format(new Date()));

        } catch (Exception e) {
            Log.e(TAG, "savePreferences: ", e);
        }
    }

    private void navigateNextPage(LoginData loginData) {
        try {
            Intent intent = null;
            if (loginData.getUser().getRoleId() == ConstantField.ROLE_ID_FIELD_TEAM) {
                intent = new Intent(Login.this, AgencyHome.class);
                intent.putExtra("userRoleId", loginData.getUser().getRoleId());
            } else {
                intent = new Intent(Login.this, Home.class);
                intent.putExtra("userRoleId", loginData.getUser().getRoleId());
            }
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "saveUser: ", e);
        }


    }

    private boolean checkValidInputs() {
        boolean isValid = true;
        if (binding.editUsername.getText().toString().trim().isEmpty()) {
            binding.editUsername.setError("Please enter username");
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editPwd.getText().toString().trim().isEmpty()) {
            binding.editPwd.setError("Please enter password");
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }

        return isValid;

    }

    private void triggerTestCrash(String reports) {
        FirebaseCrashlytics.getInstance().log("Testing Firebase Crashlytics crash report");

        // Optional: add custom key-value info for debugging
        FirebaseCrashlytics.getInstance().setCustomKey("UserID", PreferenceCommon.getInstance().getUserId());
        FirebaseCrashlytics.getInstance().setCustomKey("Screen", "LoginActivity");

        throw new RuntimeException(reports);
    }
}