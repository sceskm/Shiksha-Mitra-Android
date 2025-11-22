package sce.itc.sikshamitra.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
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
import sce.itc.sikshamitra.AlertCallBack;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.databinding.ActivityTrainingShikshaMitraBinding;
import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.CompressedImage;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.NetworkUtils;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.Image;
import sce.itc.sikshamitra.model.TrainingSM;

public class AddTrainingToSMActivity extends AppCompatActivity {
    private static final String TAG = "AddTrainingToSMActivity";
    private Toolbar toolbar;
    private ActivityTrainingShikshaMitraBinding binding;
    private DatabaseHelper dbHelper;

    private GPSTracker gps;
    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;
    private final AddTrainingToSMActivity context = AddTrainingToSMActivity.this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;


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
     * Image 4
     * */
    private File photoFil4;
    private Uri uriImage4;
    private String capturedImgStoragePathImage4 = "";
    private Uri uriCompressedImage4;
    private String imgImage4 = "";

    private String[] arrVenue;
    private String[] arrVenueGuid;
    private String selectedVenueGuid = "";

    private String startedOn = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_training_shiksha_mitra);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SM Training");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateDetails();

        populateVenueDetails();

        clickEvent();
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

    private void clickEvent() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    binding.btnSubmit.setEnabled(false); // disable to avoid multiple click
                    // use static-safe handler to avoid memory leak
                    handler.postDelayed(() -> {
                        try {
                            Log.d(TAG, "run: save attendance");

                            // 1. Create model from UI form fields
                            TrainingSM model = modelTrainingData();

                            // 2. Call confirmation API
                            callNetworkApi(model);


                        } catch (Exception e) {
                            Log.e(TAG, "onClick error: ", e);
                            binding.btnSubmit.setEnabled(true); // recover
                        }

                    }, 200); // 200ms delay


                }

            }
        });

        binding.btnCapture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startedOn = Common.iso8601Format.format(new Date());
                if (binding.btnCapture1.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_TRAINING_1);
                        } else {
                            ActivityCompat.requestPermissions(AddTrainingToSMActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_TRAINING_1);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_TRAINING_1);
                    }

                } else if (binding.btnCapture1.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(AddTrainingToSMActivity.this, binding.btnCapture1, binding.imgTraining1, new AlertCallBack() {
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
                            launchCamera(ConstantField.REQUEST_TRAINING_2);
                        } else {
                            ActivityCompat.requestPermissions(AddTrainingToSMActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_TRAINING_2);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_TRAINING_2);
                    }

                } else if (binding.btnCapture2.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(AddTrainingToSMActivity.this, binding.btnCapture2, binding.imgTraining2, new AlertCallBack() {
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
                            launchCamera(ConstantField.REQUEST_TRAINING_3);
                        } else {
                            ActivityCompat.requestPermissions(AddTrainingToSMActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_TRAINING_3);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_TRAINING_3);
                    }

                } else if (binding.btnCapture3.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(AddTrainingToSMActivity.this, binding.btnCapture3, binding.imgTraining3, new AlertCallBack() {
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
                            launchCamera(ConstantField.REQUEST_TRAINING_4);
                        } else {
                            ActivityCompat.requestPermissions(AddTrainingToSMActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_TRAINING_4);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_TRAINING_4);
                    }

                } else if (binding.btnCapture4.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(AddTrainingToSMActivity.this, binding.btnCapture4, binding.imgTraining4, new AlertCallBack() {
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

    }

    private void callNetworkApi(TrainingSM trainingData) {
        progressDialog.setMessage("Submitting training data...");
        progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Command.COMMAND, Command.ADD_TRAINING);
            jsonObject.put(Command.DATA, trainingData.getJson());
            jsonObject.put(Command.COMMAND_GUID, Common.createGuid());
            jsonObject.put(Command.PROCESS_COUNT, 0);
            jsonObject.put(Command.VERSION, ConstantField.SERVER_APP_VERSION);
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
                                binding.btnSubmit.setEnabled(true);
                                Toast.makeText(AddTrainingToSMActivity.this, "Submission failed. Try again.", Toast.LENGTH_LONG).show();
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseBody = response.body() != null ? response.body().string() : "";
                            runOnUiThread(() -> {
                                if (progressDialog.isShowing()) progressDialog.dismiss();
                                binding.btnSubmit.setEnabled(true);
                                if (response.isSuccessful()) {
                                    showSuccessAlert("Submitted Successfully", "Training data submitted successfully.");
                                } else {
                                    Toast.makeText(AddTrainingToSMActivity.this, "Server error during submission. - " + responseBody, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            binding.btnSubmit.setEnabled(true);
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error.", Toast.LENGTH_SHORT).show();
        }

    }

    private void showSuccessAlert(String title, String message) {
        runOnUiThread(() -> {
            androidx.appcompat.app.AlertDialog.Builder builder =
                    new androidx.appcompat.app.AlertDialog.Builder(AddTrainingToSMActivity.this);

            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Okay", (dialog, which) -> {
                        dialog.dismiss();
                        finish();   // 🔥 Close the activity after continue
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private TrainingSM modelTrainingData() {
        TrainingSM trainingSM = new TrainingSM();
        trainingSM.setNoOfTeachers(Common.getInt(binding.editSmCount.getText().toString().trim()));
        trainingSM.setLatitude(Common.fourDecimalRoundOff(lastLatitude));
        trainingSM.setLongitude(Common.fourDecimalRoundOff(lastLongitude));
        trainingSM.setRemarks(binding.editRemarks.getText().toString().trim());
        trainingSM.setVenueGuid(selectedVenueGuid);
        trainingSM.setUserGuid(PreferenceCommon.getInstance().getUserGUID());
        trainingSM.setTrainingGuid(Common.createGuid());

        trainingSM.setOrganizationId(ConstantField.ORGANIZATION_ID); //hardcoded organization id

        trainingSM.setStartedOn(startedOn);
        trainingSM.setEndedOn(Common.iso8601Format.format(new Date()));
        trainingSM.setInActive(false);
        //set image1
        trainingSM.setImageFile1(uriCompressedImage1.toString());
        trainingSM.setImageDefinitionId1(ConstantField.SM_TRAINING_IMAGE_DEFINITION_ID_1);
        trainingSM.setImageExt1(ConstantField.IMAGE_FORMAT);
//set image2
        trainingSM.setImageFile2(uriCompressedImage2.toString());
        trainingSM.setImageDefinitionId2(ConstantField.SM_TRAINING_IMAGE_DEFINITION_ID_2);
        trainingSM.setImageExt2(ConstantField.IMAGE_FORMAT);
//set image3
        trainingSM.setImageFile3(uriCompressedImage3.toString());
        trainingSM.setImageDefinitionId3(ConstantField.SM_TRAINING_IMAGE_DEFINITION_ID_3);
        trainingSM.setImageExt3(ConstantField.IMAGE_FORMAT);
        //set image4
        trainingSM.setImageFile4(uriCompressedImage4.toString());
        trainingSM.setImageDefinitionId4(ConstantField.SM_TRAINING_IMAGE_DEFINITION_ID_4);
        trainingSM.setImageExt4(ConstantField.IMAGE_FORMAT);

        List<Image> imageList = new ArrayList<>();
        addImageToList(imageList, uriCompressedImage1.toString(), ConstantField.SM_TRAINING_IMAGE_DEFINITION_ID_1, this);
        addImageToList(imageList, uriCompressedImage2.toString(), ConstantField.SM_TRAINING_IMAGE_DEFINITION_ID_2, this);
        addImageToList(imageList, uriCompressedImage3.toString(), ConstantField.SM_TRAINING_IMAGE_DEFINITION_ID_3, this);
        addImageToList(imageList, uriCompressedImage4.toString(), ConstantField.SM_TRAINING_IMAGE_DEFINITION_ID_4, this);

        if (!imageList.isEmpty())
            trainingSM.setImages(imageList);

        return trainingSM;
    }

    private boolean checkValidation() {
        if (binding.editSmCount.getText().toString().trim().isEmpty()) {
            Toast.makeText(AddTrainingToSMActivity.this, "Please enter teacher count", Toast.LENGTH_SHORT).show();
            return false;
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

                if (cameraRequest == ConstantField.REQUEST_TRAINING_1) {
                    uriImage1 = photoURI;
                    capturedImgStoragePathImage1 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_TRAINING_1);
                } else if (cameraRequest == ConstantField.REQUEST_TRAINING_2) {
                    uriImage2 = photoURI;
                    capturedImgStoragePathImage2 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_TRAINING_2);
                } else if (cameraRequest == ConstantField.REQUEST_TRAINING_3) {
                    uriImage3 = photoURI;
                    capturedImgStoragePathImage3 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_TRAINING_3);
                } else if (cameraRequest == ConstantField.REQUEST_TRAINING_4) {
                    uriImage4 = photoURI;
                    capturedImgStoragePathImage4 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_TRAINING_4);
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
                if (requestCode == ConstantField.REQUEST_TRAINING_1) {
                    binding.imgTraining1.setBackgroundResource(0);
                    onCaptureImageResult(data, 1);
                }
                if (requestCode == ConstantField.REQUEST_TRAINING_2) {
                    binding.imgTraining2.setBackgroundResource(0);
                    onCaptureImageResult(data, 2);
                }
                if (requestCode == ConstantField.REQUEST_TRAINING_3) {
                    binding.imgTraining3.setBackgroundResource(0);
                    onCaptureImageResult(data, 3);
                }
                if (requestCode == ConstantField.REQUEST_TRAINING_4) {
                    binding.imgTraining4.setBackgroundResource(0);
                    onCaptureImageResult(data, 4);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AddTrainingToSMActivity.this, "Captured image exceeds the free space in memory. " +
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

                binding.imgTraining1.setImageURI(uriCompressedImage1);
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

                binding.imgTraining2.setImageURI(uriCompressedImage2);
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

                binding.imgTraining3.setImageURI(uriCompressedImage3);
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

                binding.imgTraining4.setImageURI(uriCompressedImage4);
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
                binding.imgTraining1.setImageURI(uriCompressedImage1);
                binding.btnCapture1.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg2") != null) {
                uriCompressedImage2 = savedInstanceState.getParcelable("uriImg2");
                binding.imgTraining2.setImageURI(uriCompressedImage2);
                binding.btnCapture2.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg3") != null) {
                uriCompressedImage3 = savedInstanceState.getParcelable("uriImg3");
                binding.imgTraining3.setImageURI(uriCompressedImage3);
                binding.btnCapture3.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg4") != null) {
                uriCompressedImage4 = savedInstanceState.getParcelable("uriImg4");
                binding.imgTraining4.setImageURI(uriCompressedImage4);
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

    private void addImageToList(List<Image> imageList, String imagePath, int imageDefId, Context context) {
        if (imagePath != null && !imagePath.isEmpty()) {
            Image imgVenue = new Image();
            String imageBase64 = Common.convertBase64(imagePath, context);
            imgVenue.setImageDefinitionId(imageDefId);
            imgVenue.setImageName(imageBase64);
            imgVenue.setImageFileExt(ConstantField.IMAGE_FORMAT);

            imageList.add(imgVenue);
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
    private final AddTrainingToSMActivity.SafeHandler handler = new AddTrainingToSMActivity.SafeHandler(this);


    private static class SafeHandler extends Handler {
        private final WeakReference<AddTrainingToSMActivity> activityRef;


        SafeHandler(AddTrainingToSMActivity activity) {
            super(Looper.getMainLooper());
            activityRef = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            AddTrainingToSMActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;
// handle messages if you use any; keep minimal to avoid coupling
            super.handleMessage(msg);
        }
    }

}