package sce.itc.sikshamitra.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import org.json.JSONObject;

import java.io.IOException;
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
    private Handler mainHandler;
    private Handler timerHandler = new Handler();

    String username = "";
    String password = "";
    String userGuid = "";
    String schoolGuid = "";
    int roleId = 0;
    String fName = "";
    String lName = "";
    String mobileNo = "";
    int loggedIn = 0;
    String email = "";
    String lastLoggedIn = "";
    private Response response;
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

        mainHandler = new Handler(Looper.getMainLooper());
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        if (Common.DEBUGGING) {
            populateStaticUser();

            binding.editUsername.setText(username);
            binding.editPwd.setText(password);

        }


    }

    private void populateStaticUser() {
        username = ConstantField.USER_NAME;
        password = ConstantField.PASSWORD;
        roleId = ConstantField.ROLE_ID_SHIKSHA_MITRA;
        fName = "Arun";
        lName = "Agarwal";
        mobileNo = ConstantField.USER_NAME;
        userGuid = "123e4567-e89b-12d3-a456-426614174000";
        schoolGuid = "123e4567-e89b-12d3-a456-426614174001";
        loggedIn = ConstantField.IN_ACTIVE;


    }

    private void clickEvent() {
        binding.btnLogin.setOnClickListener(v -> {
            if (checkValidInputs()) {
                try {
                    progressDialog.show();
                    binding.btnLogin.setEnabled(false);
                    timerHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: save attendance");
                            callNetworkApi();
                            //saveUser();
                            /*username = binding.editUsername.getText().toString().trim();
                            password = binding.editPwd.getText().toString().trim();

                            Intent intent = null;

                            if (username.equals(ConstantField.USER_NAME_SM)) {
                                intent = new Intent(context, Home.class);
                                intent.putExtra("userRoleId", ConstantField.ROLE_ID_AGENCY);
                            } else if (username.equals(ConstantField.USER_NAME_AGENCY)) {
                                intent = new Intent(context, AgencyHome.class);
                                intent.putExtra("userRoleId", ConstantField.ROLE_ID_SHIKSHA_MITRA);
                            } else {
                                Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show();
                            }

                            startActivity(intent);
                            finish();*/
                        }
                    }, 200);

                } catch (Exception e) {
                    Log.e(TAG, "onClick: ", e);
                } finally {
                    binding.btnLogin.setEnabled(true);
                }


            }
        });
    }

    private void callNetworkApi() {
        // create your json here
        JSONObject jsonObject = new JSONObject();
        try {
            handler = new Handler(Looper.getMainLooper());

            //jsonObject.put(Command.USER_NAME, binding.editUsername.getText().toString().trim());
            jsonObject.put(Command.USER_NAME, ConstantField.USER_NAME);
            //jsonObject.put(Command.PASSWORD, binding.editPwd.getText().toString().trim());
            jsonObject.put(Command.PASSWORD, ConstantField.PASSWORD);
            jsonObject.put(Command.VERSION, ConstantField.APP_VERSION); //newly added to restrict others version

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            final OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).build();
            client.newCall(NetworkUtils.enqueNetworkRequest(ConstantField.NETWORK_URL + ConstantField.LOGIN_URL, body, false)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.post(() -> {
                        progressDialog.dismiss();
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
                            Common.showAlert(Login.this, uResponse);
                        });
                    }
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
            e.printStackTrace();
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
            for (MySchoolData school : loginData.getSchoolData()) {
                dbHelper.saveSchool(school);
            }
            savePreferences(loginData);
            navigateNextPage(loginData);
        } else {
            runOnUiThread(() -> {
                progressDialog.dismiss();
                Common.showAlert(Login.this, "Invalid server response");
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
            if (loginData.getLastSession().getSessionNo() > 0)
                PreferenceCommon.getInstance().setLastSessionCount(loginData.getLastSession().getSessionNo());

        } catch (Exception e) {
            Log.e(TAG, "savePreferences: ", e);
        }
    }

    private void navigateNextPage(LoginData loginData) {
        Intent intent;
        try {
            if (loginData.getUser().getRoleId() == ConstantField.ROLE_ID_FIELD_TEAM) {
                intent = new Intent(context, AgencyHome.class);
                intent.putExtra("userRoleId", loginData.getUser().getRoleId());

            } else {
                intent = new Intent(context, Home.class);
                intent.putExtra("userRoleId", loginData.getUser().getRoleId());
            }
            runOnUiThread(() -> {
                progressDialog.dismiss();
                // Navigate to next screen or show success message
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();

            });
        } catch (Exception e) {
            Log.e(TAG, "saveUser: ", e);
            Toast.makeText(context, "An error occurred while logging in", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
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
}