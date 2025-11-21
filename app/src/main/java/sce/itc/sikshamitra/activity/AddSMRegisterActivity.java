package sce.itc.sikshamitra.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import sce.itc.sikshamitra.helper.NetworkUtils;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.ComboProduct;
import sce.itc.sikshamitra.model.PreRegistration;
import sce.itc.sikshamitra.model.SendComboProduct;

public class AddSMRegisterActivity extends AppCompatActivity {
    private static final String TAG = "SMRegistrationActivity";
    private ActivitySmregistrationBinding binding;
    private Toolbar toolbar;
    private DatabaseHelper dbHelper;
    private final AddSMRegisterActivity context = AddSMRegisterActivity.this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;

    private String[] arrVenue;
    private String[] arrVenueGuid;
    private String selectedVenueGuid = "";

    List<ComboProduct> comboProductList;
    List<SendComboProduct> sendComboProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        populateVenueDetails();

        populateComboProduct();

        clickEvents();
    }

    private void populateDetails() {
        dbHelper = DatabaseHelper.getInstance(context);
        progressDialog = new ProgressDialog(this);
    }

    private void populateVenueDetails() {
        Cursor cursorState = dbHelper.getTodayVenueDetails();

        if (cursorState != null) {
            if (cursorState.getCount() > 0) {

                arrVenue = new String[cursorState.getCount()];
                arrVenueGuid = new String[cursorState.getCount()];

                int i = 0;
                if (cursorState.moveToFirst()) {
                    do {
                        arrVenue[i] = cursorState.getString(cursorState.getColumnIndexOrThrow("VenueName"));
                        arrVenueGuid[i] = cursorState.getString(cursorState.getColumnIndexOrThrow("VenueGUID"));
                        i++;
                    } while (cursorState.moveToNext());
                }

            } else {
                arrVenue = new String[]{"No venue found"};
                arrVenueGuid = new String[]{""};
            }

            cursorState.close(); // ✔ safe place
        }

        ArrayAdapter<String> adapterSubType =
                new ArrayAdapter<>(this, R.layout.dropdown_item, arrVenue);

        binding.editVenueName.setAdapter(adapterSubType);

        binding.editVenueName.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedVenueGuid = (i >= 0) ? arrVenueGuid[i] : "";
        });
    }


    private void populateComboProduct() {
        Cursor cursorProduct = dbHelper.getComboProduct();
        cursorProduct.moveToFirst();
        comboProductList = new ArrayList<>();
        if (cursorProduct.getCount() > 0) {
            do {
                ComboProduct comboProduct = new ComboProduct();
                comboProduct.setComboId(cursorProduct.getInt(cursorProduct.getColumnIndexOrThrow("ComboId")));
                comboProduct.setComboName(cursorProduct.getString(cursorProduct.getColumnIndexOrThrow("ComboName")));
                comboProduct.setProductId(cursorProduct.getInt(cursorProduct.getColumnIndexOrThrow("ComboProductId")));
                comboProduct.setProduct(cursorProduct.getString(cursorProduct.getColumnIndexOrThrow("ComboProduct")));
                comboProduct.setProductTypeId(cursorProduct.getInt(cursorProduct.getColumnIndexOrThrow("ComboProductTypeId")));
                comboProductList.add(comboProduct);
            } while (cursorProduct.moveToNext());
        }
        cursorProduct.close();

        if (comboProductList.size() > 0) {
            String product1 = comboProductList.get(0).getProduct();
            String product2 = comboProductList.get(1).getProduct();
            String product3 = comboProductList.get(2).getProduct();
            String product4 = comboProductList.get(3).getProduct();
            String product5 = comboProductList.get(4).getProduct();
            String product6 = comboProductList.get(5).getProduct();

            binding.checkboxItem1.setText(product1);
            binding.checkboxItem2.setText(product2);
            binding.checkboxItem3.setText(product3);
            binding.checkboxItem4.setText(product4);
            binding.checkboxItem5.setText(product5);
            binding.checkboxItem6.setText(product6);

        }


    }

    private void addIfChecked(CheckBox checkBox, int index,
                              List<ComboProduct> comboProductList,
                              List<SendComboProduct> sendComboProductList) {

        if (index < comboProductList.size()) {

            ComboProduct cp = comboProductList.get(index);

            SendComboProduct send = new SendComboProduct();
            send.setComboId(cp.getComboId());
            send.setProductId(cp.getProductId());
            send.setReceiveStatus(checkBox.isChecked()); // true or false

            sendComboProductList.add(send);
        }
    }


    private void clickEvents() {

        binding.btnRegister.setOnClickListener(v -> {
            if (checkValidations()) {

                // disable button
                binding.btnRegister.setEnabled(false);

                // use static-safe handler to avoid memory leak
                handler.postDelayed(() -> {
                    try {
                        Log.d(TAG, "run: save attendance");

                        // 1. Create model from UI form fields
                        PreRegistration model = collectFormDataToModel();

                        // 2. Call confirmation API
                        callConfirmationApi(model);

                    } catch (Exception e) {
                        Log.e(TAG, "onClick error: ", e);
                        binding.btnRegister.setEnabled(true); // recover
                    }

                }, 200); // 200ms delay
            }
        });

    }

    private PreRegistration collectFormDataToModel() {
        PreRegistration preRegistration = new PreRegistration();

        String firstName = binding.editFirstName.getText().toString().trim();
        String lastName = binding.editLastName.getText().toString().trim();
        String mobileNumber = binding.editMobileNumber.getText().toString().trim();
        String password = binding.editPassword.getText().toString().trim();
        String venueGuid = selectedVenueGuid;
        String udiseCode = binding.editUdiseCode.getText().toString().trim();
        String email = "";
        String village = "";

        if (!binding.editSmEmail.getText().toString().trim().isEmpty())
            email = binding.editSmEmail.getText().toString().trim();

        if (!binding.editSmVillage.getText().toString().trim().isEmpty())
            village = binding.editSmVillage.getText().toString().trim();

        preRegistration.setFirstName(firstName);
        preRegistration.setLastName(lastName);
        preRegistration.setPhone(mobileNumber);
        preRegistration.setPassword(password);
        preRegistration.setVenueGuid(venueGuid);
        preRegistration.setUserGuid(Common.createGuid());
        preRegistration.setUdiseCode(udiseCode);
        preRegistration.setOrganizationId(ConstantField.ORGANIZATION_ID);
        preRegistration.setEmailAddress(email);
        preRegistration.setVillage(village);

        sendComboProductList = new ArrayList<>();

        addIfChecked(binding.checkboxItem1, 0, comboProductList, sendComboProductList);
        addIfChecked(binding.checkboxItem2, 1, comboProductList, sendComboProductList);
        addIfChecked(binding.checkboxItem3, 2, comboProductList, sendComboProductList);
        addIfChecked(binding.checkboxItem4, 3, comboProductList, sendComboProductList);


        preRegistration.setComboProduct(sendComboProductList);

        return preRegistration;
    }

    private void callConfirmationApi(PreRegistration model) {
        progressDialog.setMessage("Checking details...");
        progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        JSONObject objData = new JSONObject();
        try {
            objData.put("udiseCode", model.getUdiseCode());
            jsonObject.put(Command.COMMAND, Command.UDISE_DETAILS);
            jsonObject.put(Command.DATA, objData.toString());
            jsonObject.put(Command.COMMAND_GUID, Common.createGuid());
            jsonObject.put(Command.PROCESS_COUNT, 0);
            jsonObject.put(Command.VERSION, ConstantField.APP_VERSION);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            final OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            client.newCall(NetworkUtils.enqueNetworkRequest(ConstantField.NETWORK_URL + ConstantField.ACTION_URL, body, true))
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> {
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                                binding.btnRegister.setEnabled(true);
                                Toast.makeText(AddSMRegisterActivity.this, "Network error. Try again.", Toast.LENGTH_SHORT).show();
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseBody = response.body() != null ? response.body().string() : "";
                            runOnUiThread(() -> {
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                                if (response.isSuccessful()) {
// Show confirmation dialog with Confirm and Cancel
                                    showConfirmationDialog(model, responseBody);
                                } else {
                                    binding.btnRegister.setEnabled(true);
                                    Toast.makeText(AddSMRegisterActivity.this, "Server returned an error. - " + responseBody, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            binding.btnRegister.setEnabled(true);
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error.", Toast.LENGTH_SHORT).show();
        }

    }

    private void callNetworkApi(PreRegistration attendanceDetail) {
        progressDialog.setMessage("Submitting registration...");
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Command.COMMAND, Command.ADD_TEACHER);
            jsonObject.put(Command.DATA, attendanceDetail.getJson());
            jsonObject.put(Command.COMMAND_GUID, Common.createGuid());
            jsonObject.put(Command.PROCESS_COUNT, 0);
            jsonObject.put(Command.VERSION, ConstantField.APP_VERSION);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            final OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            client.newCall(NetworkUtils.enqueNetworkRequest(ConstantField.NETWORK_URL + ConstantField.ACTION_URL, body, true))
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> {
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                                binding.btnRegister.setEnabled(true);
                                Toast.makeText(AddSMRegisterActivity.this, "Submission failed. Try again.", Toast.LENGTH_LONG).show();
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseBody = response.body() != null ? response.body().string() : "";
                            runOnUiThread(() -> {
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                                binding.btnRegister.setEnabled(true);
                                if (response.isSuccessful()) {
                                    showSuccessAlert("Registered Done", "SM registration submitted successfully.");
                                } else {
                                    Toast.makeText(AddSMRegisterActivity.this, "Server error during submission. - " + responseBody, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            binding.btnRegister.setEnabled(true);
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error.", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkValidations() {
        if (binding.editFirstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddSMRegisterActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editLastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddSMRegisterActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editMobileNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddSMRegisterActivity.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.editMobileNumber.getText().toString().trim().isEmpty()) {
            String mobilePattern = "^[6-9]\\d{9}$";
            String mobileNumber = binding.editMobileNumber.getText().toString().trim();
            if (!mobileNumber.matches(mobilePattern)) {
                Toast.makeText(AddSMRegisterActivity.this, "Invalid mobile number. It must be a valid 10-digit Indian number.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (binding.editPassword.getText().toString().isEmpty()) {
            Toast.makeText(AddSMRegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.editPassword.getText().toString().isEmpty() && binding.editPassword.getText().toString().trim().length() < 4) {
            Toast.makeText(AddSMRegisterActivity.this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editConfirmPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddSMRegisterActivity.this, "Please enter confirm password.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.editConfirmPassword.getText().toString().trim().isEmpty()
                && !binding.editPassword.getText().toString().isEmpty()) {
            if (!binding.editConfirmPassword.getText().toString().trim().equals(binding.editConfirmPassword.getText().toString().trim())) {
                Toast.makeText(AddSMRegisterActivity.this, "Password and confirm password must be same.", Toast.LENGTH_SHORT).show();

                return false;
            }
        }
        if (selectedVenueGuid.isEmpty()) {
            Toast.makeText(AddSMRegisterActivity.this, "Please select venue", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private void showSuccessAlert(String title, String message) {
        runOnUiThread(() -> {
            androidx.appcompat.app.AlertDialog.Builder builder =
                    new androidx.appcompat.app.AlertDialog.Builder(AddSMRegisterActivity.this);

            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Continue", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private void showConfirmationDialog(PreRegistration model, String serverMessage) {
        // Convert serverMessage (JSON) to nice formatted text
        String formattedDetails = parseServerMessage(serverMessage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Details")
                .setMessage("Please verify the following school details:\n\n" + formattedDetails)
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    dialog.dismiss();
                    callNetworkApi(model);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    binding.btnRegister.setEnabled(true);
                });

        AlertDialog dlg = builder.create();
        if (!isFinishing() && !isDestroyed()) dlg.show();
    }

    private String parseServerMessage(String serverMessage) {
        try {
            JsonObject root = JsonParser.parseString(serverMessage).getAsJsonObject();
            JsonObject data = root.getAsJsonObject("data");

            return
                    "UDISE Code    : " + data.get("udiseCode").getAsString() + "\n" +
                            "School Name   : " + data.get("schoolName").getAsString() + "\n" +
                            "Block Code    : " + data.get("blockCode").getAsString() + "\n" +
                            "Block Name    : " + data.get("blockName").getAsString().trim() + "\n" +
                            "District      : " + data.get("district").getAsString().trim() + "\n" +
                            "District Code : " + data.get("districtCode").getAsString() + "\n" +
                            "State         : " + data.get("state").getAsString() + "\n";
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid data format.";
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {


        } catch (Exception ex) {
            Log.e(TAG, "onSaveInstanceState: ", ex);
        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {

        } catch (Exception e) {
            Log.e(TAG, "onRestoreInstanceState: ", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
// remove any pending messages/callbacks to avoid leaks
        handler.removeCallbacksAndMessages(null);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    // Use a handler implemented as a static inner class with WeakReference to avoid leaks
    private final SafeHandler handler = new SafeHandler(this);


    private static class SafeHandler extends Handler {
        private final WeakReference<AddSMRegisterActivity> activityRef;


        SafeHandler(AddSMRegisterActivity activity) {
            super(Looper.getMainLooper());
            activityRef = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            AddSMRegisterActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
// handle messages if you use any; keep minimal to avoid coupling
            super.handleMessage(msg);
        }
    }


}