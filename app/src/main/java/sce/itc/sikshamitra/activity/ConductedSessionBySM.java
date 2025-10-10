package sce.itc.sikshamitra.activity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import sce.itc.sikshamitra.AlertCallBack;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databinding.ActivityReportingBinding;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.CompressedImage;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_reporting);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change title programmatically
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.White));
        toolbar.setTitle("Activity Reporting");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (!checkGps()) {
            permission();
            binding.btnSubmit.setEnabled(false);
            Common.enableButton(binding.btnSubmit, false);
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
    }

    private File photoFile;
    private Uri uriImage1;
    private String capturedImgStoragePathImage1 = "";
    private Uri uriCompressedImage1;
    private String imgImage1 = "";


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
                }/* else if (cameraRequest == Constant.REQUEST_RECCE_OUTSIDE_FACADE) {
                    uriOutSideFacade = photoURI;
                    capturedImgStoragePathOutSideFacade = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, Constant.REQUEST_RECCE_OUTSIDE_FACADE);
                } else if (cameraRequest == Constant.REQUEST_RECCE_ACTIVITY_AREA_1) {
                    uriActivityArea1 = photoURI;
                    capturedImgStoragePathActivityArea1 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, Constant.REQUEST_RECCE_ACTIVITY_AREA_1);
                } else if (cameraRequest == Constant.REQUEST_RECCE_NOTICE_BOARD) {
                    uriNoticeBoard = photoURI;
                    capturedImgStoragePathNoticeBoard = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, Constant.REQUEST_RECCE_NOTICE_BOARD);
                } else if (cameraRequest == Constant.REQUEST_RECCE_PROMOTER_SELFIE) {
                    uriPromoterSelfie = photoURI;
                    capturedImgStoragePathReccePromoterSelfie = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, Constant.REQUEST_RECCE_PROMOTER_SELFIE);
                }*/
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
                /*if (requestCode == Constant.REQUEST_RECCE_OUTSIDE_FACADE) {
                    imgViewOutSideFacade.setBackgroundResource(0);
                    onCaptureImageResult(data, 2);
                }
                if (requestCode == Constant.REQUEST_RECCE_ACTIVITY_AREA_1) {
                    imgViewActivityArea1.setBackgroundResource(0);
                    onCaptureImageResult(data, 3);
                }
                if (requestCode == Constant.REQUEST_RECCE_NOTICE_BOARD) {
                    imgViewNoticeBoard.setBackgroundResource(0);
                    onCaptureImageResult(data, 4);
                }
                if (requestCode == Constant.REQUEST_RECCE_PROMOTER_SELFIE) {
                    imgViewPromoterSelfie.setBackgroundResource(0);
                    onCaptureImageResult(data, 5);
                }*/
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
                    //get gps
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
//                        binding.btnSubmit.setEnabled(true);
//                        Common.enableButton(btnSubmit, true);
                        timer.cancel();
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 2000);
    }


}