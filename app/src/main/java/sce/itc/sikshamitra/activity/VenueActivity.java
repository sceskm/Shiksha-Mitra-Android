package sce.itc.sikshamitra.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.annotation.Nullable;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import sce.itc.sikshamitra.databinding.ActivityVenueDataBinding;
import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.CompressedImage;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.NetworkUtils;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.CommunicationSend;
import sce.itc.sikshamitra.model.Image;
import sce.itc.sikshamitra.model.Venue;

public class VenueActivity extends AppCompatActivity {
    private static final String TAG = "VenueActivity";
    private ActivityVenueDataBinding binding;
    private Toolbar toolbar;

    DatabaseHelper dbHelper;
    GPSTracker gps;
    private double lastLatitude = -1;
    private double lastLongitude = -1;

    //context
    private final Context context = this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private Handler timerHandler = new Handler();

    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;

    private File photoFile;
    private Uri imgURI;
    private String capturedImgStoragePath;

    private String attendanceImage = "";

    private MaterialAutoCompleteTextView stateAutoComplete;
    private TextInputLayout stateInputLayout;

    private String[] arrState;
    private int[] arrStateId;
    int selectedStateId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_venue_data);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SM Training Venue Details");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateData();

        populateStates();

        clickEvent();
    }

    private void populateData() {
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

    private void populateStates() {
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
    }


    private void clickEvent() {
        //button capture image
        binding.btnCaptureVenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermission(CAMERA_PERMISSION, ConstantField.VENUE_CAMERA_REQUEST)) {
                        launchCamera(ConstantField.VENUE_CAMERA_REQUEST);
                    } else {
                        requestPermissions();
                    }
                }

            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            if (Common.checkInternetConnectivity(VenueActivity.this)) {
                if (!checkGps()) {
                    permission();
                } else {
                    gps = new GPSTracker(VenueActivity.this);
                    // check if GPS enabled
                    if (gps.canGetLocation()) {
                        lastLatitude = gps.getLatitude();
                        lastLongitude = gps.getLongitude();
                    }

                    if (Common.DEBUGGING) {
                        lastLatitude = ConstantField.TEST_LATITUDE;
                        lastLongitude = ConstantField.TEST_LONGITUDE;
                    }
                }

                //if (Common.checkLatLong(ConstantField.TEST_LATITUDE, ConstantField.TEST_LONGITUDE)) {
                if (Common.checkLatLong(lastLatitude, lastLongitude)) {
                    if (checkValidation()) {
                        try {
                            progressDialog.show();
                            binding.btnRegister.setEnabled(false);
                            timerHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "run: save attendance");
                                    saveVenueDetails();
                                }
                            }, 200);

                        } catch (Exception e) {
                            Log.e(TAG, "onClick: ", e);
                        } finally {
                            binding.btnRegister.setEnabled(true);
                        }
                    }
                } else {
                    locationErrorMessage(getResources().getString(R.string.incorrect_location_message)
                            + getResources().getString(R.string.latitude) + String.valueOf(lastLatitude)
                            + getResources().getString(R.string.longitude) + String.valueOf(lastLongitude));
                }


            } else
                Common.showAlert(VenueActivity.this, getResources().getString(R.string.no_internet_connection));
        });


    }

    //launch camera
    private void launchCamera(int attendanceCameraRequest) {
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

                if (attendanceCameraRequest == ConstantField.VENUE_CAMERA_REQUEST) {
                    imgURI = photoURI;
                    capturedImgStoragePath = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, 101);

                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveVenueDetails() {
        String scheduledDate = Common.iso8601Format.format(new Date());
        int userId = PreferenceCommon.getInstance().getUserId();

        String imagePath = imgURI.toString();
        String base64 = Common.convertBase64(imagePath, this);


        List<Image> imageList = new ArrayList<>();
        Image imgVenue = new Image();
        imgVenue.setImageDefinitionId(ConstantField.VENUE_IMAGE_DEFINITION_ID);
        imgVenue.setImageName(base64);
        imgVenue.setImageFileExt(ConstantField.IMAGE_FORMAT);

        imageList.add(imgVenue);


        // Venue data sent
        Venue data = new Venue(
                Common.getString(binding.editVenueName.getText().toString().trim()),
                scheduledDate,
                Common.getString(binding.editAddressLine1.getText().toString().trim()),
                Common.getString(binding.editAddressLine2.getText().toString().trim()),
                Common.getString(binding.editCity.getText().toString().trim()),
                Common.getString(binding.editDistrict.getText().toString().trim()),
                Common.getString(binding.editState.getText().toString().trim()),
                selectedStateId,
                Common.getString(binding.editPinCode.getText().toString().trim()),
                userId,
                ConstantField.ORGANIZATION_ID,
                Common.createGuid(),
                lastLatitude,
                lastLongitude,
                Common.createGuid(),
                imageList
        );
        data.setImageDefinitionId(ConstantField.VENUE_IMAGE_DEFINITION_ID);
        data.setImageFile(imagePath);
        data.setImageExt(ConstantField.IMAGE_FORMAT);

        if (dbHelper.saveVenueData(data)) {
            callNetworkApi(data);
        } else {
            progressDialog.dismiss();
            Common.showAlert(context, getResources().getString(R.string.data_not_saved_message));
        }
    }

    /*
     * Call network api to upload data
     * */
    private void callNetworkApi(Venue attendanceDetail) {
        //just now saved unprocessed message
        Cursor cursorCount = dbHelper.currentUnprocessedCommSendMessage(Command.ADD_VENUE,
                PreferenceCommon.getInstance().getUserGUID(), attendanceDetail.getCommunicationGuid());
        if (cursorCount.getCount() > 0) {
            cursorCount.moveToFirst();
            CommunicationSend communicationSend = new CommunicationSend();
            communicationSend.populateFromCursor(cursorCount);
            if (!communicationSend.getCommandDetails().isEmpty()) {
                Venue attendance = Venue.fromJson(communicationSend.getCommandDetails());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Command.COMMAND, Command.ADD_VENUE);
                    jsonObject.put(Command.DATA, attendance.getJson());
                    jsonObject.put(Command.COMMAND_GUID, communicationSend.getCommunicationGUID());
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
                                    //delete entered venue data
                                    dbHelper.deleteEnteredVenueData(attendance);
                                    showErrorAlert("Network connection failed","Something went wrong.Try again later.");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String successMsg = response.body().string();
                                    if (response.isSuccessful()) {
                                        //update communication
                                        dbHelper.updateCommunicationSendStatus(communicationSend.getID(),
                                                ConstantField.COMM_STATUS_PROCESSED, "success", false);
                                        showSuccessAlert("Registered Successfully", "Your venue details have been registered successfully.");
                                    } else {
                                        //delete entered venue data
                                        dbHelper.deleteEnteredVenueData(attendance);
                                        showErrorAlert("Error Occurred","Something went wrong.Try again later.");
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

    }

    private boolean checkValidation() {
        boolean isValid = true;
        if (binding.editVenueName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter venue name", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editAddressLine1.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter venue address", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter city", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editDistrict.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter district", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }
        if (binding.editState.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter state", Toast.LENGTH_SHORT).show();
            isValid = false;
            return isValid;
        }

        return isValid;

    }

    public void permission() {
        if (!checkGps()) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(VenueActivity.this);
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
        LocationManager lm = (LocationManager) VenueActivity.this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            return false;
        } else {
            return true;
        }
    }

    private void locationErrorMessage(String s) {
        new MaterialAlertDialogBuilder(VenueActivity.this, R.style.RoundShapeTheme)
                .setTitle("Error").setMessage(s).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        dialogInterface.dismiss();

                    }
                }).setCancelable(false)
                .show();


    }

    private void showSuccessAlert(String title, String message) {
        mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            progressDialog.dismiss();
            new MaterialAlertDialogBuilder(VenueActivity.this, R.style.RoundShapeTheme)
                    .setTitle(title).setMessage(message).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            finish();
                        }
                    }).setCancelable(false)
                    .show();
        });

    }

    private void showErrorAlert(String title, String message) {
        mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            progressDialog.dismiss();
            new MaterialAlertDialogBuilder(VenueActivity.this, R.style.RoundShapeTheme)
                    .setTitle(title).setMessage(message).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //deleteData();
                            dialogInterface.dismiss();

                        }
                    }).setCancelable(false)
                    .show();
        });

    }

    /*
     * Permission check for camera
     * */
    public boolean checkPermission(String permission, int requestCode) {
        boolean isGranted = false;
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            isGranted = true;
        }
        return isGranted;
    }

    // This function is called when the user accepts or decline the permission.
// Request Code is used to check which permission called this function.
// This request code is provided when the user is prompt for permission.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ConstantField.VENUE_CAMERA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with image capture
                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied. You can handle this case, show an explanation, or disable functionality that requires the permission.
                Toast.makeText(this, "Permissions required to capture images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //create original image file
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (requestCode == ConstantField.VENUE_CAMERA_REQUEST) {
                    binding.imgVenue.setBackgroundResource(0);
                    onCaptureImageResult(data, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(VenueActivity.this, "Captured image exceeds the free space in memory. " +
                        "Kindly free your phone memory and try again.", Toast.LENGTH_LONG).show();
                //log exception in firebase
                //new FirebaseLog().setFirebaseException(TAG, "onActivityResult()", e);
            }

        }
    }

    //captured image,compress captured image,set button
    private void onCaptureImageResult(Intent data, int i) {
        //populate captured image to imageview section

        //try to read the file
        try {
            File f2 = Common.getFile(capturedImgStoragePath); //return original path

            //compress the original image
            CompressedImage getCompress = new CompressedImage(this);
            String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePath);

            Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);

            //just testing-if we dont get compressed file then set original path
            //if image is not compressed we can use the original image
            if (!compressedImageStoragePath.isEmpty()) {
                attendanceImage = compressedImageStoragePath;
            } else {
                attendanceImage = capturedImgStoragePath;
            }
            if (!attendanceImage.isEmpty())
                imgURI = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + Common.getImageName(attendanceImage));

            Log.d(TAG, "onCaptureFImageResult: " + imgURI);
            binding.imgVenue.setImageURI(imgURI);
            binding.btnCaptureVenue.setText("DELETE");

        } catch (Exception ex) {
            Log.e(TAG, "onCaptureImageResult: ", ex);
        }

    }

    private void requestPermissions() {
        // Request camera permission
        ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION}, ConstantField.VENUE_CAMERA_REQUEST);
    }
}
