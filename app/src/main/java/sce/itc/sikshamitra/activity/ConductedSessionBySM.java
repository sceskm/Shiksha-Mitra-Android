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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sce.itc.sikshamitra.AlertCallBack;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityReportingBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.CompressedImage;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.MySchoolData;
import sce.itc.sikshamitra.model.Session;

public class ConductedSessionBySM extends AppCompatActivity {
    private static final String TAG = ConductedSessionBySM.class.getName();
    private ActivityReportingBinding binding;
    private Toolbar toolbar;
    private double lastLatitude = -1;
    private double lastLongitude = -1;
    Timer timer;
    Handler h = new Handler();
    private Context context = this;
    private GPSTracker gps;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private Handler timerHandler = new Handler();

    private DatabaseHelper dbHelper;

    private List<MySchoolData> schoolList;
    private String schoolGuid = "";
    private String startDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reporting);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Training Session");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateData();

        populateSchoolData();

        if (!checkGps()) {
            permission();
            binding.btnSave.setEnabled(false);
            Common.enableButton(binding.btnSave, false);
        } else {
            gps = new GPSTracker(ConductedSessionBySM.this);
            // check if GPS enabled
            if (gps.canGetLocation()) {
                lastLatitude = gps.getLatitude();
                lastLongitude = gps.getLongitude();
            }
        }
        clickEvent();

    }

    private void populateData() {
        dbHelper = new DatabaseHelper(context);
        dbHelper = DatabaseHelper.getInstance(context);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("saving your data..");
        progressDialog.setTitle("Please Wait..");
        mainHandler = new Handler(Looper.getMainLooper());

        startDate = Common.iso8601Format.format(new Date());

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

        int sessionCont = PreferenceCommon.getInstance().getLastSessionCount() + 1;
        binding.editSessionCount.setText(Common.getString(String.valueOf(sessionCont)));
    }

    private void populateSchoolData() {
        if (schoolList != null && !schoolList.isEmpty()) {
            for (MySchoolData school : schoolList) {
                schoolGuid = school.getSchoolGuid();
            }
        }
    }

    /*
     * Image 1
     * */
    private File photoFile;
    private Uri uriImage1;
    private String capturedImgStoragePathImage1 = "";
    private Uri uriCompressedImage1;
    private String imgImage1 = "";

    /*
     * Image 2
     * */
    private File photoFil2;
    private Uri uriImage2;
    private String capturedImgStoragePathImage2 = "";
    private Uri uriCompressedImage2;
    private String imgImage2 = "";

    /*
     * Image 3
     * */
    private File photoFil3;
    private Uri uriImage3;
    private String capturedImgStoragePathImage3 = "";
    private Uri uriCompressedImage3;
    private String imgImage3 = "";

    /*
     * Image 2
     * */
    private File photoFil4;
    private Uri uriImage4;
    private String capturedImgStoragePathImage4 = "";
    private Uri uriCompressedImage4;
    private String imgImage4 = "";

    public void clickEvent() {
        binding.btnCapture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.btnCapture1.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_IMAGE_1);
                        } else {
                            ActivityCompat.requestPermissions(ConductedSessionBySM.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_IMAGE_1);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_IMAGE_1);
                    }

                } else if (binding.btnCapture1.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(ConductedSessionBySM.this, binding.btnCapture1, binding.imgCamera1, new AlertCallBack() {
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

        binding.btnCapture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture2.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_IMAGE_2);
                        } else {
                            ActivityCompat.requestPermissions(ConductedSessionBySM.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_IMAGE_2);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_IMAGE_2);
                    }

                } else if (binding.btnCapture2.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(ConductedSessionBySM.this, binding.btnCapture2, binding.imgCamera2, new AlertCallBack() {
                        @Override
                        public void onResult(boolean isDeleted) {
                            if (isDeleted) {
                                if (uriCompressedImage2 != null)
                                    uriCompressedImage2 = null;
                                if (!imgImage2.isEmpty())
                                    imgImage2 = "";
                            }

                        }
                    });
                }

            }
        });

        binding.btnCapture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture3.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_IMAGE_3);
                        } else {
                            ActivityCompat.requestPermissions(ConductedSessionBySM.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_IMAGE_3);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_IMAGE_3);
                    }

                } else if (binding.btnCapture3.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(ConductedSessionBySM.this, binding.btnCapture3, binding.imgCamera3, new AlertCallBack() {
                        @Override
                        public void onResult(boolean isDeleted) {
                            if (isDeleted) {
                                if (uriCompressedImage3 != null)
                                    uriCompressedImage3 = null;
                                if (!imgImage3.isEmpty())
                                    imgImage3 = "";
                            }

                        }
                    });
                }

            }
        });

        binding.btnCapture4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture4.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_IMAGE_4);
                        } else {
                            ActivityCompat.requestPermissions(ConductedSessionBySM.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_IMAGE_4);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_IMAGE_4);
                    }

                } else if (binding.btnCapture4.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(ConductedSessionBySM.this, binding.btnCapture4, binding.imgCamera4, new AlertCallBack() {
                        @Override
                        public void onResult(boolean isDeleted) {
                            if (isDeleted) {
                                if (uriCompressedImage4 != null)
                                    uriCompressedImage4 = null;
                                if (!imgImage4.isEmpty())
                                    imgImage4 = "";
                            }

                        }
                    });
                }

            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    try {
                        progressDialog.show();
                        binding.btnSave.setEnabled(false);
                        timerHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: save attendance");
                                saveSession();
                            }
                        }, 200);

                    } catch (Exception e) {
                        Log.e(TAG, "onClick: ", e);
                    } finally {
                        binding.btnSave.setEnabled(true);
                    }
                }
            }
        });
    }

    private void saveSession() {
        Session session = new Session();

        String studentNo = Common.getString(binding.editStdCount.getText().toString().trim());
        int sessionNo = Common.getInt(Common.getString(binding.editSessionCount.getText().toString().trim()));
        session.setSessionNo(sessionNo);
        session.setNoOfStudent(Common.getInt(studentNo));

        session.setImg1(uriCompressedImage1.toString());
        session.setImgDefinitionId1(ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_1);

        session.setImg2(uriCompressedImage2.toString());
        session.setImgDefinitionId2(ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_2);

        session.setImg3(uriCompressedImage3.toString());
        session.setImgDefinitionId3(ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_3);

        session.setImg4(uriCompressedImage4.toString());
        session.setImgDefinitionId4(ConstantField.SM_SESSION_IMAGE_DEFINITION_ID_4);

        session.setSessionGuid(Common.createGuid());
        session.setSchoolGuid(schoolGuid);
        session.setUserGuid(PreferenceCommon.getInstance().getUserGUID());

        session.setLatitude(lastLatitude);
        session.setLongitude(lastLongitude);

        session.setSessionStart(startDate);
        session.setSessionEnd(Common.iso8601Format.format(new Date()));

        session.setRemarks(Common.getString(binding.editRemarks.getText().toString().trim()));

        //Communication
        session.setCommunicationAttempt(0);
        session.setCommunicationGuid(Common.createGuid());
        session.setCommunicationStatus(ConstantField.COMM_STATUS_NOT_PROCESSED);

        if (dbHelper.saveSession(session)) {
            PreferenceCommon.getInstance().setLastSessionCount(sessionNo);
            showSuccessAlert("Data saved successfully. Please upload the data from the 'Synchronise' page.");
            Toast.makeText(this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
        } else {
            showSuccessAlert("Something went wrong.");
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessAlert(String s) {
        mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            progressDialog.dismiss();
            new MaterialAlertDialogBuilder(ConductedSessionBySM.this, R.style.RoundShapeTheme)
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
        if (binding.editStdCount.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter student count", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!binding.editStdCount.getText().toString().trim().isEmpty()) {
            int count = Common.getInt(Common.getString(binding.editStdCount.getText().toString().trim()));
            if (count == 0) {
                Toast.makeText(this, "Please enter correct number", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (uriCompressedImage1 == null || uriCompressedImage1.toString().isEmpty()) {
            Toast.makeText(this, "Capture image 1", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uriCompressedImage2 == null || uriCompressedImage2.toString().isEmpty()) {
            Toast.makeText(this, "Capture image 2", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uriCompressedImage3 == null || uriCompressedImage3.toString().isEmpty()) {
            Toast.makeText(this, "Capture image 3", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (uriCompressedImage4 == null || uriCompressedImage4.toString().isEmpty()) {
            Toast.makeText(this, "Capture image 4", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editRemarks.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Remarks blank", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

                if (cameraRequest == ConstantField.REQUEST_IMAGE_1) {
                    uriImage1 = photoURI;
                    capturedImgStoragePathImage1 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_IMAGE_1);
                } else if (cameraRequest == ConstantField.REQUEST_IMAGE_2) {
                    uriImage2 = photoURI;
                    capturedImgStoragePathImage2 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_IMAGE_2);
                } else if (cameraRequest == ConstantField.REQUEST_IMAGE_3) {
                    uriImage3 = photoURI;
                    capturedImgStoragePathImage3 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_IMAGE_3);
                } else if (cameraRequest == ConstantField.REQUEST_IMAGE_4) {
                    uriImage4 = photoURI;
                    capturedImgStoragePathImage4 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_IMAGE_4);
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
                if (requestCode == ConstantField.REQUEST_IMAGE_1) {
                    binding.imgCamera1.setBackgroundResource(0);
                    onCaptureImageResult(data, 1);
                }
                if (requestCode == ConstantField.REQUEST_IMAGE_2) {
                    binding.imgCamera2.setBackgroundResource(0);
                    onCaptureImageResult(data, 2);
                }
                if (requestCode == ConstantField.REQUEST_IMAGE_3) {
                    binding.imgCamera3.setBackgroundResource(0);
                    onCaptureImageResult(data, 3);
                }
                if (requestCode == ConstantField.REQUEST_IMAGE_4) {
                    binding.imgCamera4.setBackgroundResource(0);
                    onCaptureImageResult(data, 4);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ConductedSessionBySM.this, "Captured image exceeds the free space in memory. " +
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
            } else if (i == 2) {
                File f2 = getFile(capturedImgStoragePathImage2); //return original path
                //compress the original image
                //Common.getFileSize(f2);
                CompressedImage getCompress = new CompressedImage(this);
                String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePathImage2);
                //Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);
                //just testing-if we dont get compressed file then set original path
                //if image is not compressed we can use the original image
                if (!compressedImageStoragePath.isEmpty())
                    imgImage2 = compressedImageStoragePath;
                else
                    imgImage2 = capturedImgStoragePathImage2;

                if (!imgImage2.isEmpty())
                    uriCompressedImage2 = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + getImageName(imgImage2));

                Log.d(TAG, "onCaptureFImageResult: " + uriCompressedImage2);

                binding.imgCamera2.setImageURI(uriCompressedImage2);
                binding.btnCapture2.setText("DELETE");
            } else if (i == 3) {
                File f2 = getFile(capturedImgStoragePathImage3); //return original path
                //compress the original image
                //Common.getFileSize(f2);
                CompressedImage getCompress = new CompressedImage(this);
                String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePathImage3);
                //Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);
                //just testing-if we dont get compressed file then set original path
                //if image is not compressed we can use the original image
                if (!compressedImageStoragePath.isEmpty())
                    imgImage3 = compressedImageStoragePath;
                else
                    imgImage3 = capturedImgStoragePathImage3;

                if (!imgImage3.isEmpty())
                    uriCompressedImage3 = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + getImageName(imgImage3));

                Log.d(TAG, "onCaptureFImageResult: " + uriCompressedImage3);

                binding.imgCamera3.setImageURI(uriCompressedImage3);
                binding.btnCapture3.setText("DELETE");
            } else if (i == 4) {
                File f2 = getFile(capturedImgStoragePathImage4); //return original path
                //compress the original image
                //Common.getFileSize(f2);
                CompressedImage getCompress = new CompressedImage(this);
                String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePathImage4);
                //Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);
                //just testing-if we dont get compressed file then set original path
                //if image is not compressed we can use the original image
                if (!compressedImageStoragePath.isEmpty())
                    imgImage4 = compressedImageStoragePath;
                else
                    imgImage4 = capturedImgStoragePathImage4;

                if (!imgImage4.isEmpty())
                    uriCompressedImage4 = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + getImageName(imgImage4));

                Log.d(TAG, "onCaptureFImageResult: " + uriCompressedImage2);

                binding.imgCamera4.setImageURI(uriCompressedImage4);
                binding.btnCapture4.setText("DELETE");
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void requestPermission() {
        // Request camera permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 110);
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

    public boolean checkGps() {
        LocationManager lm = (LocationManager) ConductedSessionBySM.this.getSystemService(Context.LOCATION_SERVICE);
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

    public void permission() {
        if (!checkGps()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ConductedSessionBySM.this);
            dialog.setMessage("Location Settings is turned off!! Please turn it on.");
            dialog.setCancelable(false);
            dialog.setPositiveButton("GO To Setup", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });

            dialog.show();

        }

        //start a timer to disable the start button for 4 seconds
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                h.post(new Runnable() {
                    public void run() {
                        binding.btnSave.setEnabled(true);
                        Common.enableButton(binding.btnSave, true);
                        timer.cancel();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 2000);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {
            savedInstanceState.putString("Img1", imgImage1);
            savedInstanceState.putString("Img2", imgImage2);
            savedInstanceState.putString("Img3", imgImage3);
            savedInstanceState.putString("Img4", imgImage4);

            savedInstanceState.putString("capturedImg1", capturedImgStoragePathImage1);
            savedInstanceState.putString("capturedImg2", capturedImgStoragePathImage2);
            savedInstanceState.putString("capturedImg3", capturedImgStoragePathImage3);
            savedInstanceState.putString("capturedImg4", capturedImgStoragePathImage4);


            if (uriCompressedImage1 != null) {
                savedInstanceState.putParcelable("uriImg1", uriCompressedImage1);
            }
            if (uriCompressedImage2 != null) {
                savedInstanceState.putParcelable("uriImg2", uriCompressedImage2);
            }
            if (uriCompressedImage3 != null) {
                savedInstanceState.putParcelable("uriImg3", uriCompressedImage3);
            }
            if (uriCompressedImage4 != null) {
                savedInstanceState.putParcelable("uriImg4", uriCompressedImage4);
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
            imgImage2 = savedInstanceState.getString("Img2");
            imgImage3 = savedInstanceState.getString("Img3");
            imgImage4 = savedInstanceState.getString("Img4");

            capturedImgStoragePathImage1 = savedInstanceState.getString("capturedImg1");
            capturedImgStoragePathImage2 = savedInstanceState.getString("capturedImg2");
            capturedImgStoragePathImage3 = savedInstanceState.getString("capturedImg3");
            capturedImgStoragePathImage4 = savedInstanceState.getString("capturedImg4");


            if (savedInstanceState.getParcelable("uriImg1") != null) {
                uriCompressedImage1 = savedInstanceState.getParcelable("uriImg1");
                binding.imgCamera1.setImageURI(uriCompressedImage1);
                binding.btnCapture1.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg2") != null) {
                uriCompressedImage2 = savedInstanceState.getParcelable("uriImg2");
                binding.imgCamera2.setImageURI(uriCompressedImage2);
                binding.btnCapture2.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg3") != null) {
                uriCompressedImage3 = savedInstanceState.getParcelable("uriImg3");
                binding.imgCamera3.setImageURI(uriCompressedImage3);
                binding.btnCapture3.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg4") != null) {
                uriCompressedImage4 = savedInstanceState.getParcelable("uriImg4");
                binding.imgCamera4.setImageURI(uriCompressedImage4);
                binding.btnCapture4.setText("DELETE");
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
}