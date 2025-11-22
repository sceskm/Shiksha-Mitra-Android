package sce.itc.sikshamitra.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import sce.itc.sikshamitra.AlertCallBack;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityRetailOutReachBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.CompressedImage;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.RetailOutReachModel;

public class RetailOutReachActivity extends AppCompatActivity {
    private static final String TAG = "RetailOutReach";
    private ActivityRetailOutReachBinding binding;
    private Toolbar toolbar;
    private DatabaseHelper dbHelper;

    private GPSTracker gps;
    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;
    private final RetailOutReachActivity context = RetailOutReachActivity.this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private String startDate = "";

    /*
     * Image 1
     * */
    private File photoFile;
    private Uri uriImage1;
    private String capturedImgStoragePathImage1 = "";
    private Uri uriCompressedImage1;
    private String imgImage1 = "";

    private String[] arrState;
    private int[] arrStateId;
    int selectedStateId = -1;

    //school
    private String[] arrSchool;
    private String[] arrSchoolGuid;
    String selectedSchoolGuid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_retail_out_reach);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Retail OutReach");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateData();

        clickEvents();

    }

    private void clickEvents() {
        /*
         * Submit action
         * */
        binding.btnSubmitRetailerData.setOnClickListener(v -> {

            // Internet check
            if (!Common.checkInternetConnectivity(RetailOutReachActivity.this)) {
                Common.showAlert(RetailOutReachActivity.this,
                        getResources().getString(R.string.no_internet_connection));
                return;
            }

            // GPS check
            if (!checkGps()) {
                permission();
                return;
            }

            gps = new GPSTracker(RetailOutReachActivity.this);
            if (gps.canGetLocation()) {
                lastLatitude = gps.getLatitude();
                lastLongitude = gps.getLongitude();
            }

            // Lat-long validation
            if (!Common.checkLatLong(lastLatitude, lastLongitude)) {
                locationErrorMessage(
                        getString(R.string.incorrect_location_message)
                                + getString(R.string.latitude) + lastLatitude
                                + getString(R.string.longitude) + lastLongitude
                );
                return;
            }

            // Disable button to prevent multiple taps
            binding.btnSubmitRetailerData.setEnabled(false);

            // Show loader
            progressDialog.show();

            try {
                if (checkValidation()) {
                    boolean result = saveRetailData();   // This should return success/failure

                    progressDialog.dismiss();

                    if (result) {
                        showAlert("Success", "Retailer details saved successfully!");
                    } else {
                        showAlert("Failed", "Something went wrong while saving data.");
                    }

                } else {
                    progressDialog.dismiss();
                    binding.btnSubmitRetailerData.setEnabled(true);
                }

            } catch (Exception e) {
                progressDialog.dismiss();
                binding.btnSubmitRetailerData.setEnabled(true);
                Log.e(TAG, "onClick error: ", e);
                showAlert("Error", "Unexpected error occurred.");
            }

        });


        //Outside exterior image capture
        binding.btnCapture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnCapture1.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.RETAIL_IMAGE_SHOP);
                        } else {
                            ActivityCompat.requestPermissions(RetailOutReachActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.RETAIL_IMAGE_SHOP);
                        }
                    } else {
                        launchCamera(ConstantField.RETAIL_IMAGE_SHOP);
                    }

                } else if (binding.btnCapture1.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(RetailOutReachActivity.this, binding.btnCapture1, binding.imgCamera1, new AlertCallBack() {
                        @Override
                        public void onResult(boolean isDeleted) {
                            if (isDeleted) {
                                if (uriCompressedImage1 != null)
                                    uriCompressedImage1 = null;
                                if (!imgImage1.isEmpty())
                                    imgImage1 = "";
                            }

                        }
                    });
                }

            }
        });
    }

    private boolean saveRetailData() {
        RetailOutReachModel retail = new RetailOutReachModel();
        retail.setLatitude(Common.fourDecimalRoundOff(lastLatitude));
        retail.setLongitude(Common.fourDecimalRoundOff(lastLongitude));
        retail.setCreatedOn(Common.iso8601Format.format(new Date()));
        retail.setVisitedOn(Common.yyyymmddFormat.format(new Date()));
        //get data from fields
        retail.setShopName(binding.editShopName.getText().toString().trim());
        retail.setRetailOutreachGuid(Common.createGuid());
        retail.setUserGuid(PreferenceCommon.getInstance().getUserGUID());
        retail.setOrganizationId(ConstantField.ORGANIZATION_ID);
        //retail.setNearbySchool();
        retail.setSchoolGuid(selectedSchoolGuid);
        retail.setAddress1(binding.editAddressLine1.getText().toString().trim());
        retail.setAddress2(binding.editAddressLine2.getText().toString().trim());
        retail.setCity(binding.editCity.getText().toString().trim());
        //retail.setState(binding.editState.getText().toString().trim());
        retail.setStateId(selectedStateId);
        retail.setPinCode(binding.editPinCode.getText().toString().trim());
        retail.setDistrict(binding.editDistrict.getText().toString().trim());
        retail.setDivision(binding.editDivision.getText().toString().toString());
        retail.setContactName(binding.editContactPersonName.getText().toString().trim());
        retail.setContactPhone(binding.editContactPersonPhoneNumber.getText().toString().trim());
        retail.setContactName(binding.editContactPersonName.getText().toString().trim());
        retail.setBlock(binding.editBlock.getText().toString().trim());

//      radio button isKeepITCProduct   1--> yes , 2 --> no , other --> 0
        if (binding.rdoItcYes.isChecked())
            retail.setIsKeepITCProducts(1);
        else if (binding.rdoItcNo.isChecked())
            retail.setIsKeepITCProducts(2);
        else
            retail.setIsKeepITCProducts(0);

//radio button isKeepITCProduct   1--> yes , 2 --> no , other --> 0
        if (binding.rdoBrandingYes.isChecked())
            retail.setBrandingInterested(1);
        else if (binding.rdoBrandingYes.isChecked())
            retail.setBrandingInterested(2);
        else
            retail.setBrandingInterested(0);

        if (binding.chkShopPainting.isChecked())
            retail.setShopPainting(1);
        else
            retail.setShopPainting(0);

        if (binding.chkDealerBoard.isChecked())
            retail.setDealerBoard(1);
        else
            retail.setDealerBoard(0);

        if (binding.chkPoster.isChecked())
            retail.setPoster(1);
        else
            retail.setPoster(0);

        if (binding.chkBunting.isChecked())
            retail.setBunting(1);
        else
            retail.setBunting(0);

        retail.setHandWashPouchesSold(Common.getInt(binding.editHandwashPouchesSold.getText().toString().trim()));
        retail.setSavlonSoapSold(Common.getInt(binding.editSavlonSoapSold.getText().toString().trim()));
        retail.setItcProductNames(binding.editItcProductsAvailable.getText().toString().trim());
        retail.setFmcgpurchaseFrom(binding.editFmcgProductSource.getText().toString().trim());
        retail.setDistributorDetails(binding.editDistributerDetails.getText().toString().trim());
        retail.setMarketDetails(binding.editNearbyWholeSaleMarket.getText().toString().trim());

        //Image 1
        retail.setImage1(uriCompressedImage1.toString());
        retail.setImgDefinitionId1(ConstantField.RETAIL_IMAGE_SHOP_IMAGE);
        retail.setImgExt1(ConstantField.IMAGE_FORMAT);

        retail.setCommunicationAttempt(0);
        retail.setCommunicationStatus(ConstantField.COMM_STATUS_NOT_PROCESSED);
        retail.setCommunicationGuid(Common.createGuid());

        if (dbHelper.saveRetailDetails(retail)) {
            return true;
        } else {
            return false;
        }
    }

    private void showAlert(String title, String message) {
        handler.post(() -> {
            progressDialog.dismiss();
            new MaterialAlertDialogBuilder(RetailOutReachActivity.this, R.style.RoundShapeTheme)
                    .setTitle(title).setMessage(message).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            dialogInterface.dismiss();

                        }
                    }).setCancelable(false)
                    .show();
        });
    }

    private void locationErrorMessage(String s) {
        new MaterialAlertDialogBuilder(RetailOutReachActivity.this, R.style.RoundShapeTheme)
                .setTitle("Error").setMessage(s).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        dialogInterface.dismiss();

                    }
                }).setCancelable(false)
                .show();


    }

    private boolean checkValidation() {
        if (binding.editShopName.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Please fill retailer outlet name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editContactPersonName.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Please fill contact person name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editContactPersonPhoneNumber.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Please fill contact number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.editContactPersonPhoneNumber.getText().toString().isEmpty()) {
            String mobilePattern = "^[6-9]\\d{9}$";
            String mobileNumber = binding.editContactPersonPhoneNumber.getText().toString().trim();
            if (!mobileNumber.matches(mobilePattern)) {
                Toast.makeText(RetailOutReachActivity.this, "Invalid mobile number. It must be a valid 10-digit Indian number.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (binding.editAddressLine1.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter address1", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editAddressLine2.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter address2", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editCity.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter retailer's city/ village", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editLocality.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter retailer's locality", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editBlock.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter retailer's block", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editDistrict.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter retailer's district", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editDivision.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter retailer's division", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedStateId <= 0) {
            Toast.makeText(RetailOutReachActivity.this, "Choose retailer's state ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editPinCode.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter pin code.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedSchoolGuid.isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Choose nearby school name. ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.rdoItcYes.isChecked() && !binding.rdoItcNo.isChecked()) {
            Toast.makeText(RetailOutReachActivity.this, "Please select whether the outlet keeps ITC products. ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.rdoBrandingYes.isChecked() && !binding.rdoBrandingYes.isChecked()) {
            Toast.makeText(RetailOutReachActivity.this, "Please select whether the interested ITC branding. ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.rdoYes.isChecked() && !binding.rdoNo.isChecked()) {
            Toast.makeText(RetailOutReachActivity.this, "Please select whether the selling hand wash or not . ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.editFmcgProductSource.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter FMCG product source.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editDistributerDetails.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter distributor's details", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editNearbyWholeSaleMarket.getText().toString().isEmpty()) {
            Toast.makeText(RetailOutReachActivity.this, "Enter nearby wholesale market.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editItcProductsAvailable.getText().toString().trim().isEmpty()){
            Toast.makeText(RetailOutReachActivity.this,"Fill available ITC products",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editHandwashPouchesSold.getText().toString().trim().isEmpty()){
            Toast.makeText(RetailOutReachActivity.this,"Fill hand wash pouches sold.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editSavlonSoapSold.getText().toString().trim().isEmpty()){
            Toast.makeText(RetailOutReachActivity.this,"Fill savlon soap sold.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uriCompressedImage1 == null || uriCompressedImage1.toString().isEmpty()) {
            Toast.makeText(this, "Capture shop image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void populateData() {
        dbHelper = DatabaseHelper.getInstance(this);
        gps = new GPSTracker(context);

        if (gps.canGetLocation()) {
            lastLatitude = gps.getLatitude();
            lastLongitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Saving data....");
        startDate = Common.iso8601Format.format(new Date());

        //Populate state
        Cursor cursorState = dbHelper.getAllStates();
        if (cursorState != null && cursorState.getCount() > 0) {

            arrState = new String[cursorState.getCount()];
            arrStateId = new int[cursorState.getCount()];

            int i = 0;
            if (cursorState.moveToFirst()) {
                do {
                    arrState[i] = cursorState.getString(cursorState.getColumnIndexOrThrow("StateName"));
                    arrStateId[i] = cursorState.getInt(cursorState.getColumnIndexOrThrow("StateId"));
                    i++;
                } while (cursorState.moveToNext());
            }
        }

        cursorState.close();

        ArrayAdapter<String> adapterSubType =
                new ArrayAdapter<>(this, R.layout.dropdown_item, arrState);

        binding.editState.setAdapter(adapterSubType);
        binding.editState.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedStateId = (i >= 0) ? arrStateId[i] : 0;
        });

        //Populate school
        Cursor cursorSchool = dbHelper.getSchoolDetails();
        if (cursorSchool != null && cursorSchool.getCount() > 0) {

            arrSchool = new String[cursorSchool.getCount()];
            arrSchoolGuid = new String[cursorSchool.getCount()];

            int i = 0;
            if (cursorSchool.moveToFirst()) {
                do {
                    arrSchool[i] = cursorSchool.getString(cursorSchool.getColumnIndexOrThrow("AssociateSchool"));
                    arrSchoolGuid[i] = cursorSchool.getString(cursorSchool.getColumnIndexOrThrow("SchoolGUID"));
                    i++;
                } while (cursorSchool.moveToNext());
            }
        }

        cursorSchool.close();

        ArrayAdapter<String> adapterSchool =
                new ArrayAdapter<>(this, R.layout.dropdown_item, arrSchool);

        binding.editSchoolName.setAdapter(adapterSchool);
        binding.editSchoolName.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedSchoolGuid = (i >= 0) ? arrSchoolGuid[i] : "0";
        });
    }

    public void permission() {
        if (!checkGps()) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(RetailOutReachActivity.this);
            dialog.setMessage("Location Settings is turned off!! Please turn it on.");
            dialog.setCancelable(false);
            dialog.setPositiveButton("GO To Setup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.show();
        }
    }

    //for checking GPS
    public boolean checkGps() {
        LocationManager lm = (LocationManager) RetailOutReachActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e(TAG, "checkGps: ", ex);
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Log.e(TAG, "checkGps: ", ex);
        }

        if (!gps_enabled && !network_enabled) {
            return false;
        } else {
            return true;
        }
    }

    public static File getFile(String fileName) {
        File file = null;
        try {
            file = new File("", fileName);
            if (file.exists()) {
                return file;
            } else {
                return null;
            }

        } catch (Exception ex) {
            file = null;
        }

        return file;
    }

    public static String getImageName(String fullString) {
        String imagename = "";
        String[] separated = fullString.split("/");
        imagename = separated[10];
        return imagename;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with image capture
                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied. You can handle this case, show an explanation, or disable functionality that requires the permission.
                Toast.makeText(this, "Permissions required to capture images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void launchCamera(int cameraRequest) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = createImageFile(ConstantField.ORIGINAL_IMAGE_NAME);
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "sce.itc.sikshamitra.fileprovider",
                        photoFile
                );
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                if (cameraRequest == ConstantField.RETAIL_IMAGE_SHOP) {
                    uriImage1 = photoURI;
                    capturedImgStoragePathImage1 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.RETAIL_IMAGE_SHOP);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (requestCode == ConstantField.RETAIL_IMAGE_SHOP) {
                    binding.imgCamera1.setBackgroundResource(0);
                    onCaptureImageResult(data, 1);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(RetailOutReachActivity.this, "Captured image exceeds the free space in memory. " +
                        "Kindly free your phone memory and try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile(String filename) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imgfilename = filename + timeStamp;

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imgfilename /* prefix */, ConstantField.IMAGE_FORMAT,/* suffix */storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        Log.d(TAG, "createImageFile: Image File" + image.getPath());
        Log.d(TAG, "createImageFile: Image Absolute path" + image.getAbsolutePath());

        return image;
    }

    private void onCaptureImageResult(Intent data, int i) {
        try {
            if (i == 1) {
                File f2 = getFile(capturedImgStoragePathImage1); //return original path
                //compress the original image
                //Common.getFileSize(f2);
                CompressedImage getCompress = new CompressedImage(this);
                String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePathImage1);
                //Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);
                //just testing-if we dont get compressed file then set original path
                //if image is not compressed we can use the original image
                if (!compressedImageStoragePath.isEmpty())
                    imgImage1 = compressedImageStoragePath;
                else
                    imgImage1 = capturedImgStoragePathImage1;

                if (!imgImage1.isEmpty())
                    uriCompressedImage1 = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + getImageName(imgImage1));

                Log.d(TAG, "onCaptureFImageResult: " + uriCompressedImage1);

                binding.imgCamera1.setImageURI(uriCompressedImage1);
                binding.btnCapture1.setText("DELETE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {
            savedInstanceState.putString("Img1", imgImage1);


            savedInstanceState.putString("capturedImg1", capturedImgStoragePathImage1);

            if (uriCompressedImage1 != null) {
                savedInstanceState.putParcelable("uriImg1", uriCompressedImage1);
            }

        } catch (Exception ex) {
            Log.e(TAG, "onSaveInstanceState: ", ex);
        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            imgImage1 = savedInstanceState.getString("Img1");


            capturedImgStoragePathImage1 = savedInstanceState.getString("capturedImg1");


            if (savedInstanceState.getParcelable("uriImg1") != null) {
                uriCompressedImage1 = savedInstanceState.getParcelable("uriImg1");
                binding.imgCamera1.setImageURI(uriCompressedImage1);
                binding.btnCapture1.setText("DELETE");
            }
        } catch (Exception e) {
            Log.e(TAG, "onRestoreInstanceState: ", e);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Do you want to exit? Your data will loss.")
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    finish(); // Close the activity
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss(); // Close the dialog only
                })
                .show();
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
    private final RetailOutReachActivity.SafeHandler handler = new RetailOutReachActivity.SafeHandler(this);


    private static class SafeHandler extends Handler {
        private final WeakReference<RetailOutReachActivity> activityRef;


        SafeHandler(RetailOutReachActivity activity) {
            super(Looper.getMainLooper());
            activityRef = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            RetailOutReachActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
// handle messages if you use any; keep minimal to avoid coupling
            super.handleMessage(msg);
        }
    }
}