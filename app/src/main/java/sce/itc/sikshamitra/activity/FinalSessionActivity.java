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
import java.text.SimpleDateFormat;
import java.util.Date;

import sce.itc.sikshamitra.AlertCallBack;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.CompressedImage;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.GPSTracker;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.Session;

public class FinalSessionActivity extends AppCompatActivity {
    public static final String TAG = FinalSessionActivity.class.getSimpleName();
    private sce.itc.sikshamitra.databinding.ActivityFinalSessionBinding binding;
    private Toolbar toolbar;
    private DatabaseHelper dbHelper;

    private GPSTracker gps;
    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;
    private final FinalSessionActivity context = FinalSessionActivity.this;

    //progress dialog for data upload
    private ProgressDialog progressDialog;
    private Handler mainHandler;
    private String startDate = "";

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

    /*
     * Image 5
     * */
    private Uri uriImage5;
    private String capturedImgStoragePathImage5 = "";
    private Uri uriCompressedImage5;
    private String imgImage5 = "";

    /*
     * Image 6
     * */
    private Uri uriImage6;
    private String capturedImgStoragePathImage6 = "";
    private Uri uriCompressedImage6;
    private String imgImage6 = "";

    /*
     * Image 7
     * */
    private Uri uriImage7;
    private String capturedImgStoragePathImage7 = "";
    private Uri uriCompressedImage7;
    private String imgImage7 = "";

    /*
     * Image 8
     * */
    private Uri uriImage8;
    private String capturedImgStoragePathImage8 = "";
    private Uri uriCompressedImage8;
    private String imgImage8 = "";

    private String[] arrSchool;
    private String[] arrSchoolGuid;
    private int[] arrSchoolId;
    private int selectedSchoolId = -1;
    private String selectedSchoolGuid = "";
    private String selectedSchool = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_final_session);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Final Session");
        }
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        populateData();

        populateSchools();

        clickEvent();
    }

    private void populateData() {
        // Currently no dynamic data to populate
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

        startDate = Common.iso8601Format.format(new Date());
    }

    private void populateSchools() {
        // Currently no implementation for populating schools
        Cursor cursorState = dbHelper.getMySchoolData();
        if (cursorState != null && cursorState.getCount() > 0) {

            arrSchool = new String[cursorState.getCount()];
            arrSchoolGuid = new String[cursorState.getCount()];
            arrSchoolId = new int[cursorState.getCount()];

            int i = 0;
            if (cursorState.moveToFirst()) {
                do {
                    arrSchool[i] = cursorState.getString(cursorState.getColumnIndexOrThrow("AssociateSchool"));
                    arrSchoolGuid[i] = cursorState.getString(cursorState.getColumnIndexOrThrow("SchoolGUID"));
                    arrSchoolId[i] = cursorState.getInt(cursorState.getColumnIndexOrThrow("SchoolId"));
                    i++;
                } while (cursorState.moveToNext());
            }
        }

        cursorState.close();

        ArrayAdapter<String> adapterSubType =
                new ArrayAdapter<>(this, R.layout.dropdown_item, arrSchool);

        binding.editSchoolName.setAdapter(adapterSubType);
        binding.editSchoolName.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i >= 0) {
                selectedSchoolId = arrSchoolId[i];
                selectedSchool = arrSchool[i];
                selectedSchoolGuid = arrSchoolGuid[i];
            } else {
                selectedSchoolId = 0;
                selectedSchool = "";
                selectedSchoolGuid = "";
            }
        });
    }

    private void clickEvent() {
        binding.btnSubmitFinalSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.checkInternetConnectivity(FinalSessionActivity.this)) {
                    if (!checkGps()) {
                        permission();
                    } else {
                        gps = new GPSTracker(FinalSessionActivity.this);
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
                                binding.btnSubmitFinalSession.setEnabled(false);
                                mainHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "run: save attendance");
                                        saveFinalSession();
                                        progressDialog.dismiss();
                                    }
                                }, 200);

                            } catch (Exception e) {
                                Log.e(TAG, "onClick: ", e);
                            } finally {
                                binding.btnSubmitFinalSession.setEnabled(true);
                            }
                        }
                    } else {
                        locationErrorMessage(getResources().getString(R.string.incorrect_location_message)
                                + getResources().getString(R.string.latitude) + String.valueOf(lastLatitude)
                                + getResources().getString(R.string.longitude) + String.valueOf(lastLongitude));
                    }


                } else
                    Common.showAlert(FinalSessionActivity.this, getResources().getString(R.string.no_internet_connection));
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
                            launchCamera(ConstantField.REQUEST_FINAL_SESSION_EXTERIOR);
                        } else {
                            ActivityCompat.requestPermissions(FinalSessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_FINAL_SESSION_EXTERIOR);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_FINAL_SESSION_EXTERIOR);
                    }

                } else if (binding.btnCapture1.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(FinalSessionActivity.this, binding.btnCapture1, binding.imgSchoolFacade, new AlertCallBack() {
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
        //Quiz progress image 1 capture
        binding.btnCapture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture2.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_1);
                        } else {
                            ActivityCompat.requestPermissions(FinalSessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_1);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_1);
                    }

                } else if (binding.btnCapture2.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(FinalSessionActivity.this, binding.btnCapture2, binding.imgQuiz1, new AlertCallBack() {
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
        //Quiz progress image 2 capture
        binding.btnCapture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture3.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_2);
                        } else {
                            ActivityCompat.requestPermissions(FinalSessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_2);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_2);
                    }

                } else if (binding.btnCapture3.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(FinalSessionActivity.this, binding.btnCapture3, binding.imgQuiz2, new AlertCallBack() {
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
        //Rewarded student image 1 capture
        binding.btnCapture4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture4.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_FINAL_SESSION_REWARD_1);
                        } else {
                            ActivityCompat.requestPermissions(FinalSessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_FINAL_SESSION_REWARD_1);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_FINAL_SESSION_REWARD_1);
                    }

                } else if (binding.btnCapture4.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(FinalSessionActivity.this, binding.btnCapture4, binding.imgRewarded1, new AlertCallBack() {
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
        //Rewarded student image 2 capture
        binding.btnCapture5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture5.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_FINAL_SESSION_REWARD_2);
                        } else {
                            ActivityCompat.requestPermissions(FinalSessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_FINAL_SESSION_REWARD_2);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_FINAL_SESSION_REWARD_2);
                    }

                } else if (binding.btnCapture5.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(FinalSessionActivity.this, binding.btnCapture5, binding.imgRewarded2, new AlertCallBack() {
                        @Override
                        public void onResult(boolean isDeleted) {
                            if (isDeleted) {
                                if (uriCompressedImage5 != null)
                                    uriCompressedImage5 = null;
                                if (!imgImage5.isEmpty())
                                    imgImage5 = "";
                            }

                        }
                    });
                }

            }
        });
        //Rewarded student image 3 capture
        binding.btnCapture6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture6.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_FINAL_SESSION_REWARD_3);
                        } else {
                            ActivityCompat.requestPermissions(FinalSessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_FINAL_SESSION_REWARD_3);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_FINAL_SESSION_REWARD_3);
                    }

                } else if (binding.btnCapture6.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(FinalSessionActivity.this, binding.btnCapture6, binding.imgRewarded3, new AlertCallBack() {
                        @Override
                        public void onResult(boolean isDeleted) {
                            if (isDeleted) {
                                if (uriCompressedImage6 != null)
                                    uriCompressedImage6 = null;
                                if (!imgImage6.isEmpty())
                                    imgImage6 = "";
                            }

                        }
                    });
                }

            }
        });
        //Rewarded student image 4 capture
        binding.btnCapture7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture7.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_FINAL_SESSION_REWARD_4);
                        } else {
                            ActivityCompat.requestPermissions(FinalSessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_FINAL_SESSION_REWARD_4);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_FINAL_SESSION_REWARD_4);
                    }

                } else if (binding.btnCapture7.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(FinalSessionActivity.this, binding.btnCapture7, binding.imgRewarded4, new AlertCallBack() {
                        @Override
                        public void onResult(boolean isDeleted) {
                            if (isDeleted) {
                                if (uriCompressedImage7 != null)
                                    uriCompressedImage7 = null;
                                if (!imgImage7.isEmpty())
                                    imgImage7 = "";
                            }

                        }
                    });
                }

            }
        });
        //HW sample image capture
        binding.btnCapture8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.btnCapture8.getText().toString().trim().equalsIgnoreCase(ConstantField.CAPTURE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            launchCamera(ConstantField.REQUEST_FINAL_SESSION_HW_SAMPLE);
                        } else {
                            ActivityCompat.requestPermissions(FinalSessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    ConstantField.REQUEST_FINAL_SESSION_HW_SAMPLE);
                        }
                    } else {
                        launchCamera(ConstantField.REQUEST_FINAL_SESSION_HW_SAMPLE);
                    }

                } else if (binding.btnCapture8.getText().toString().trim().equalsIgnoreCase(ConstantField.DELETE)) {
                    Common.showDeleteImageAlert(FinalSessionActivity.this, binding.btnCapture8, binding.imgSmHoldingHw, new AlertCallBack() {
                        @Override
                        public void onResult(boolean isDeleted) {
                            if (isDeleted) {
                                if (uriCompressedImage8 != null)
                                    uriCompressedImage8 = null;
                                if (!imgImage8.isEmpty())
                                    imgImage8 = "";
                            }

                        }
                    });
                }

            }
        });


    }

    //Saved final session data
    private void saveFinalSession() {
        // Currently no implementation for saving final session data
        Session session = new Session();

        String studentNo = Common.getString(binding.editNoOfStudents.getText().toString().trim());
        //int sessionNo = Common.getInt(Common.getString(binding.edit.getText().toString().trim()));
        session.setSessionNo(7);
        session.setNoOfStudent(Common.getInt(studentNo));
        session.setSchoolName("Test School Name");

        session.setImg1(uriCompressedImage1.toString());
        session.setImgDefinitionId1(ConstantField.FINAL_SESSION_IMAGE_EXTERIOR);

        session.setImg2(uriCompressedImage2.toString());
        session.setImgDefinitionId2(ConstantField.FINAL_SESSION_QUIZ_PROGRESS_1);

        session.setImg3(uriCompressedImage3.toString());
        session.setImgDefinitionId3(ConstantField.FINAL_SESSION_QUIZ_PROGRESS_2);

        session.setImg4(uriCompressedImage4.toString());
        session.setImgDefinitionId4(ConstantField.FINAL_SESSION_REWARD_1);

        session.setImg5(uriCompressedImage5.toString());
        session.setImgDefinitionId5(ConstantField.FINAL_SESSION_REWARD_2);

        session.setImg6(uriCompressedImage6.toString());
        session.setImgDefinitionId6(ConstantField.FINAL_SESSION_REWARD_3);

        session.setImg7(uriCompressedImage7.toString());
        session.setImgDefinitionId7(ConstantField.FINAL_SESSION_REWARD_4);

        session.setImg8(uriCompressedImage8.toString());
        session.setImgDefinitionId8(ConstantField.FINAL_SESSION_HW_SAMPLE);


        session.setSessionGuid(Common.createGuid());
        session.setSchoolGuid("schoolGuid");
        session.setUserGuid(PreferenceCommon.getInstance().getUserGUID());

        session.setLatitude(Common.fourDecimalRoundOff(lastLatitude));
        session.setLongitude(Common.fourDecimalRoundOff(lastLongitude));

        session.setSessionStart(startDate);
        session.setSessionEnd(Common.iso8601Format.format(new Date()));

        session.setRemarks(Common.getString(binding.editRemarks.getText().toString().trim()));

        //Communication
        session.setCommunicationAttempt(0);
        session.setCommunicationGuid(Common.createGuid());
        session.setCommunicationStatus(ConstantField.COMM_STATUS_NOT_PROCESSED);

        if (dbHelper.saveSession(session)) {
            PreferenceCommon.getInstance().setLastSessionCount(7);
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
            new MaterialAlertDialogBuilder(FinalSessionActivity.this, R.style.RoundShapeTheme)
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

                if (cameraRequest == ConstantField.REQUEST_FINAL_SESSION_EXTERIOR) {
                    uriImage1 = photoURI;
                    capturedImgStoragePathImage1 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_FINAL_SESSION_EXTERIOR);
                } else if (cameraRequest == ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_1) {
                    uriImage2 = photoURI;
                    capturedImgStoragePathImage2 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_1);
                } else if (cameraRequest == ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_2) {
                    uriImage3 = photoURI;
                    capturedImgStoragePathImage3 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_2);
                } else if (cameraRequest == ConstantField.REQUEST_FINAL_SESSION_REWARD_1) {
                    uriImage4 = photoURI;
                    capturedImgStoragePathImage4 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_FINAL_SESSION_REWARD_1);
                } else if (cameraRequest == ConstantField.REQUEST_FINAL_SESSION_REWARD_2) {
                    uriImage5 = photoURI;
                    capturedImgStoragePathImage5 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_FINAL_SESSION_REWARD_2);
                } else if (cameraRequest == ConstantField.REQUEST_FINAL_SESSION_REWARD_3) {
                    uriImage6 = photoURI;
                    capturedImgStoragePathImage6 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_FINAL_SESSION_REWARD_3);
                } else if (cameraRequest == ConstantField.REQUEST_FINAL_SESSION_REWARD_4) {
                    uriImage7 = photoURI;
                    capturedImgStoragePathImage7 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_FINAL_SESSION_REWARD_4);
                } else if (cameraRequest == ConstantField.REQUEST_FINAL_SESSION_HW_SAMPLE) {
                    uriImage8 = photoURI;
                    capturedImgStoragePathImage8 = photoFile.getAbsolutePath();
                    startActivityForResult(takePictureIntent, ConstantField.REQUEST_FINAL_SESSION_HW_SAMPLE);
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
                if (requestCode == ConstantField.REQUEST_FINAL_SESSION_EXTERIOR) {
                    binding.imgSchoolFacade.setBackgroundResource(0);
                    onCaptureImageResult(data, 1);
                }
                if (requestCode == ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_1) {
                    binding.imgQuiz1.setBackgroundResource(0);
                    onCaptureImageResult(data, 2);
                }
                if (requestCode == ConstantField.REQUEST_FINAL_SESSION_QUIZ_PROGRESS_2) {
                    binding.imgQuiz2.setBackgroundResource(0);
                    onCaptureImageResult(data, 3);
                }
                if (requestCode == ConstantField.REQUEST_FINAL_SESSION_REWARD_1) {
                    binding.imgRewarded1.setBackgroundResource(0);
                    onCaptureImageResult(data, 4);
                }
                if (requestCode == ConstantField.REQUEST_FINAL_SESSION_REWARD_2) {
                    binding.imgRewarded2.setBackgroundResource(0);
                    onCaptureImageResult(data, 5);
                }
                if (requestCode == ConstantField.REQUEST_FINAL_SESSION_REWARD_3) {
                    binding.imgRewarded3.setBackgroundResource(0);
                    onCaptureImageResult(data, 6);
                }
                if (requestCode == ConstantField.REQUEST_FINAL_SESSION_REWARD_4) {
                    binding.imgRewarded4.setBackgroundResource(0);
                    onCaptureImageResult(data, 7);
                }
                if (requestCode == ConstantField.REQUEST_FINAL_SESSION_HW_SAMPLE) {
                    binding.imgSmHoldingHw.setBackgroundResource(0);
                    onCaptureImageResult(data, 8);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(FinalSessionActivity.this, "Captured image exceeds the free space in memory. " +
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

                binding.imgSchoolFacade.setImageURI(uriCompressedImage1);
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

                binding.imgQuiz1.setImageURI(uriCompressedImage2);
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

                binding.imgQuiz2.setImageURI(uriCompressedImage3);
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

                binding.imgRewarded1.setImageURI(uriCompressedImage4);
                binding.btnCapture4.setText("DELETE");
            } else if (i == 5) {
                File f2 = getFile(capturedImgStoragePathImage5); //return original path
                //compress the original image
                //Common.getFileSize(f2);
                CompressedImage getCompress = new CompressedImage(this);
                String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePathImage5);
                //Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);
                //just testing-if we dont get compressed file then set original path
                //if image is not compressed we can use the original image
                if (!compressedImageStoragePath.isEmpty())
                    imgImage5 = compressedImageStoragePath;
                else
                    imgImage5 = capturedImgStoragePathImage5;

                if (!imgImage5.isEmpty())
                    uriCompressedImage5 = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + getImageName(imgImage5));

                Log.d(TAG, "onCaptureFImageResult: " + uriCompressedImage5);

                binding.imgRewarded2.setImageURI(uriCompressedImage5);
                binding.btnCapture5.setText("DELETE");
            } else if (i == 6) {
                File f2 = getFile(capturedImgStoragePathImage6); //return original path
                //compress the original image
                //Common.getFileSize(f2);
                CompressedImage getCompress = new CompressedImage(this);
                String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePathImage6);
                //Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);
                //just testing-if we dont get compressed file then set original path
                //if image is not compressed we can use the original image
                if (!compressedImageStoragePath.isEmpty())
                    imgImage6 = compressedImageStoragePath;
                else
                    imgImage6 = capturedImgStoragePathImage6;

                if (!imgImage6.isEmpty())
                    uriCompressedImage6 = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + getImageName(imgImage6));

                Log.d(TAG, "onCaptureFImageResult: " + uriCompressedImage6);

                binding.imgRewarded3.setImageURI(uriCompressedImage6);
                binding.btnCapture6.setText("DELETE");
            } else if (i == 7) {
                File f2 = getFile(capturedImgStoragePathImage7); //return original path
                //compress the original image
                //Common.getFileSize(f2);
                CompressedImage getCompress = new CompressedImage(this);
                String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePathImage7);
                //Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);
                //just testing-if we dont get compressed file then set original path
                //if image is not compressed we can use the original image
                if (!compressedImageStoragePath.isEmpty())
                    imgImage7 = compressedImageStoragePath;
                else
                    imgImage7 = capturedImgStoragePathImage7;

                if (!imgImage7.isEmpty())
                    uriCompressedImage7 = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + getImageName(imgImage7));

                Log.d(TAG, "onCaptureFImageResult: " + uriCompressedImage7);

                binding.imgRewarded4.setImageURI(uriCompressedImage7);
                binding.btnCapture7.setText("DELETE");
            } else if (i == 8) {
                File f2 = getFile(capturedImgStoragePathImage8); //return original path
                //compress the original image
                //Common.getFileSize(f2);
                CompressedImage getCompress = new CompressedImage(this);
                String compressedImageStoragePath = getCompress.compressImage(capturedImgStoragePathImage8);
                //Log.d(TAG, "onCaptureImageResult: " + compressedImageStoragePath);
                //just testing-if we dont get compressed file then set original path
                //if image is not compressed we can use the original image
                if (!compressedImageStoragePath.isEmpty())
                    imgImage8 = compressedImageStoragePath;
                else
                    imgImage8 = capturedImgStoragePathImage8;
                if (!imgImage8.isEmpty())
                    uriCompressedImage8 = Uri.parse(ConstantField.COMPRESS_PHOTO_URI + getImageName(imgImage8));
                Log.d(TAG, "onCaptureFImageResult: " + uriCompressedImage8);
                binding.imgSmHoldingHw.setImageURI(uriCompressedImage8);
                binding.btnCapture8.setText("DELETE");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locationErrorMessage(String s) {
        new MaterialAlertDialogBuilder(FinalSessionActivity.this, R.style.RoundShapeTheme)
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
        return true;
    }

    public void permission() {
        if (!checkGps()) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(FinalSessionActivity.this);
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
        LocationManager lm = (LocationManager) FinalSessionActivity.this.getSystemService(Context.LOCATION_SERVICE);
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
            savedInstanceState.putString("capturedImg5", capturedImgStoragePathImage5);
            savedInstanceState.putString("capturedImg6", capturedImgStoragePathImage6);
            savedInstanceState.putString("capturedImg7", capturedImgStoragePathImage7);
            savedInstanceState.putString("capturedImg8", capturedImgStoragePathImage8);


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
            if (uriCompressedImage5 != null) {
                savedInstanceState.putParcelable("uriImg5", uriCompressedImage5);
            }
            if (uriCompressedImage6 != null) {
                savedInstanceState.putParcelable("uriImg6", uriCompressedImage6);
            }
            if (uriCompressedImage7 != null) {
                savedInstanceState.putParcelable("uriImg7", uriCompressedImage7);
            }
            if (uriCompressedImage8 != null) {
                savedInstanceState.putParcelable("uriImg8", uriCompressedImage8);
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
            capturedImgStoragePathImage5 = savedInstanceState.getString("capturedImg5");
            capturedImgStoragePathImage6 = savedInstanceState.getString("capturedImg6");
            capturedImgStoragePathImage7 = savedInstanceState.getString("capturedImg7");
            capturedImgStoragePathImage8 = savedInstanceState.getString("capturedImg8");


            if (savedInstanceState.getParcelable("uriImg1") != null) {
                uriCompressedImage1 = savedInstanceState.getParcelable("uriImg1");
                binding.imgRewarded1.setImageURI(uriCompressedImage1);
                binding.btnCapture1.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg2") != null) {
                uriCompressedImage2 = savedInstanceState.getParcelable("uriImg2");
                binding.imgRewarded2.setImageURI(uriCompressedImage2);
                binding.btnCapture2.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg3") != null) {
                uriCompressedImage3 = savedInstanceState.getParcelable("uriImg3");
                binding.imgRewarded3.setImageURI(uriCompressedImage3);
                binding.btnCapture3.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg4") != null) {
                uriCompressedImage4 = savedInstanceState.getParcelable("uriImg4");
                binding.imgRewarded3.setImageURI(uriCompressedImage4);
                binding.btnCapture4.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg5") != null) {
                uriCompressedImage5 = savedInstanceState.getParcelable("uriImg5");
                binding.imgRewarded2.setImageURI(uriCompressedImage5);
                binding.btnCapture5.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg6") != null) {
                uriCompressedImage6 = savedInstanceState.getParcelable("uriImg6");
                binding.imgRewarded3.setImageURI(uriCompressedImage6);
                binding.btnCapture6.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg7") != null) {
                uriCompressedImage7 = savedInstanceState.getParcelable("uriImg7");
                binding.imgRewarded4.setImageURI(uriCompressedImage7);
                binding.btnCapture7.setText("DELETE");
            }
            if (savedInstanceState.getParcelable("uriImg8") != null) {
                uriCompressedImage8 = savedInstanceState.getParcelable("uriImg8");
                binding.imgSmHoldingHw.setImageURI(uriCompressedImage8);
                binding.btnCapture8.setText("DELETE");
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