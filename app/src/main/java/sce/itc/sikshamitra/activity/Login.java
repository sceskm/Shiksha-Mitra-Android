package sce.itc.sikshamitra.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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

import java.util.Date;

import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityLoginBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.model.User;

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
        dbHelper = new DatabaseHelper(context);

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
        fName = "Test";
        lName = "Sauvik";
        mobileNo = ConstantField.USER_NAME;
        userGuid = "123e4567-e89b-12d3-a456-426614174000";
        schoolGuid = "123e4567-e89b-12d3-a456-426614174001";
        loggedIn = ConstantField.IN_ACTIVE;


    }

    private void clickEvent() {
        binding.btnLogin.setOnClickListener(v -> {
            if (checkValidInputs()) {
                username = binding.editUsername.getText().toString().trim();
                password = binding.editPwd.getText().toString().trim();

                User user = new User();


            }
        });

        binding.btnLogin.setOnClickListener(v -> {
            if (checkValidInputs()) {
                try {
                    progressDialog.show();
                    binding.btnLogin.setEnabled(false);
                    timerHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: save attendance");
                            saveUser();
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

    private void saveUser() {
        Intent intent;
        User user = new User();
        Cursor cursor = dbHelper.getUser(userGuid);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            //Check if schoolGuid exists in database
            user.populateFromCursor(cursor);
            user.setLastLoggedIn(Common.yyyymmddFormat.format(new Date()));
            user.setLoggedIn(ConstantField.ACTIVE);
            if (user.getRoleId() == ConstantField.ROLE_ID_AGENCY) {
                dbHelper.deleteUser();
                dbHelper.saveUser(user);
                intent = new Intent(context, AgencyHome.class);
                intent.putExtra("userRoleId", user.getRoleId());
                startActivity(intent);
                finish();
                progressDialog.dismiss();
                return;
            } else if (user.getRoleId() == ConstantField.ROLE_ID_SHIKSHA_MITRA) {
                dbHelper.updateUser(user);
                if (user.getSchoolGUID().isEmpty()) {
                    intent = new Intent(context, SchoolDetailsEntry.class);
                    startActivity(intent);
                    finish();
                    progressDialog.dismiss();
                    return;
                } else {
                    intent = new Intent(context, Home.class);
                    intent.putExtra("userRoleId", user.getRoleId());
                    startActivity(intent);
                    finish();
                    progressDialog.dismiss();
                    return;
                }
            }

        } else {
            Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;

        }
        cursor.close();
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