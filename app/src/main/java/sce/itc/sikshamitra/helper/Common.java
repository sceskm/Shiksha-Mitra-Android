package sce.itc.sikshamitra.helper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.icu.text.DecimalFormat;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import sce.itc.sikshamitra.AlertCallBack;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.model.User;

public class Common {
    public static final boolean DEBUGGING = false;

    public static final String[] PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
    };
    public static User loggedUser;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    public static SimpleDateFormat dateDisplay = new SimpleDateFormat("dd-MMM");
    public static SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat yyyymmddFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat yyMMddHHmmssFormat = new SimpleDateFormat("yyMMddHHmmss");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static void enableButton(Button button, boolean enable) {
        button.setEnabled(enable);
        if (enable) {
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.button_delete));
        } else {
            button.setBackgroundColor(button.getContext().getResources().getColor(R.color.button_disable_background));
        }
    }

    public static void showDeleteImageAlert(Context context, Button button, ImageView imageView, AlertCallBack callBack) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Do you want to delete?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                imageView.setImageResource(0);
                Picasso.with(context).load(R.drawable.icon_images).into(imageView);
                button.setText(ConstantField.CAPTURE);
                callBack.onResult(true);

            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callBack.onResult(false);
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static boolean checkLatLong(double latitude, double longitude) {
        boolean isValid = false;
        if ((latitude > ConstantField.IND_LATITUDE_1 && latitude < ConstantField.IND_LATITUDE_2)
                && (longitude > ConstantField.IND_LONGITUDE_1 && longitude < ConstantField.IND_LONGITUDE_2)) {
            isValid = true;
        }
        return isValid;
    }

    //convert string to int & int null value checking
    public static int getInt(String value) {
        int retVal = 0;

        try {
            if (value != null && !value.equals("null") && !value.trim().isEmpty()) {
                retVal = Integer.parseInt(value.toString());
            }
        } catch (Exception ex) {
        }
        return retVal;
    }

    //convert string to double & null value checking
    public static double getDouble(String value) {
        double retVal = 0.00;

        try {
            if (value != null && !value.equals("null") && !value.trim().isEmpty()) {
                retVal = Double.parseDouble(value.toString());
            }
        } catch (Exception ex) {
            //don't do anything
        }
        return retVal;
    }

    //convert string to float & int null value checking
    public static float getFloat(String value) {
        float retVal = 0.00F;

        try {
            if (value != null && !value.equals("null") && !value.trim().isEmpty()) {
                retVal = Float.parseFloat(value.toString());
            }
        } catch (Exception ex) {
            //don't do anything
        }
        return retVal;
    }

    //string null checking
    public static String getString(String value) {
        String retVal = "";
        try {
            if (value != null && !value.equals("null") && !value.trim().isEmpty()) {
                retVal = value.toString();
            }
        } catch (Exception ex) {
            //don't do anything
        }
        return retVal;
    }

    public static String createGuid() {
        return UUID.randomUUID().toString();
    }

    public static void showAlert(Context context, String message) {
        new MaterialAlertDialogBuilder(context, R.style.RoundShapeTheme)
                .setTitle("Oops!")
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                }).show();
    }

    public static boolean checkInternetConnectivitySIMOnly(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            // try to specifically access syscon - commented out we can use
            // later if required
            // cm.requestRouteToHost(ConnectivityManager.TYPE_MOBILE, int
            // hostAddress)
            return true;
        }
        return false;
    }

    public static boolean checkInternetConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) return false;

        Network network = cm.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        if (capabilities == null) return false;

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }


    public static JSONObject getJsonObject(String response) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }



    //boolean value to int
    public static int getBooleanToInt(String value) {
        int retVal = 0;

        if (value != null && !value.isEmpty() && !value.equals("null")) {
            if (value.equals("false"))
                retVal = 0;
            else
                retVal = 1;
        }

        return retVal;
    }

    //convert string value to int value
    public static int StringToInt(String value) {
        int retValue = 0;

        try {
            if (!value.equals(null) && !value.trim().isEmpty()) {
                retValue = Integer.parseInt(value);
            }
        } catch (Exception ex) {
            //don't do anything
            retValue = 0;
        }

        return retValue;
    }

    //convert string value to date time format
    public static Date stringToDate(String value) {
        String fDate[] = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm:ss a", "yyyy-MM-dd", "yyyy/MM/dd hh:mm:ss",
                "yyyy/MM/dd hh:mm:ss a"};
        Date date = null;

        for (String format : fDate) {
            DateFormat iso8601Format = new SimpleDateFormat(format);
            try {
                date = iso8601Format.parse(value);
                break;
            } catch (ParseException e) {
                Log.e("Common", "StringToDate: ", e);
            }
        }

        return date;
    }

    //split date-time  & get only date part like yyyy-MM-dd
    public static String getImageName(String fullString) {
        String imagename = "";
        String[] separated = fullString.split("/");
        imagename = separated[10];
        return imagename;
    }
    //get file path of captured image
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
    public static String convertBase64(String imageUri, Context context) {
        String covertedImage = "";
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(imageUri));
            // initialize byte stream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // compress Bitmap
            //int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, ConstantField.IMAGE_QUALITY, stream);
            //get image information
            //getImageInfo(stream);
            // Initialize byte array
            byte[] bytes = stream.toByteArray();
            // get base64 encoded string
            int size = bytes.length;
            covertedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
            // set encoded text on textview
            //textView.setText(sImage);
            Log.d("TAG", "uploadCompetitionAdd: " + covertedImage);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return covertedImage;
    }

    //convert string value to date time format
    public static String displayDate(String date, String currentFormat) {
        String result = "";
        Date currectFormat = null;
        SimpleDateFormat sdfOld = new SimpleDateFormat(currentFormat, Locale.getDefault());
        SimpleDateFormat sdfNew = new SimpleDateFormat("EEE dd MMMM yyyy", Locale.getDefault());
        if (!TextUtils.isEmpty(date)) {
            try {
                currectFormat = sdfOld.parse(date);

                if (currectFormat != null)
                    result = sdfNew.format(currectFormat);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
    //compare datetime
    public static long compareDates(String date1, String date2) {
        long differenceDates = 0;

        Date d1 = new Date();
        Date d2 = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            d1 = sdf.parse(datePart(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            d2 = sdf.parse(datePart(date2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = d2.getTime() - d1.getTime();
        differenceDates = difference / (24 * 60 * 60 * 1000);

        return differenceDates;
    }
    //split date-time  & get only date part like yyyy-MM-dd
    public static String datePart(String fullString) {
        String firstPart = "";
        String[] separated = fullString.split(" ");
        firstPart = separated[0];
        return firstPart;
    }

    //split time part
    public static String timePart(String fullString) {
        String endPart = "";
        String[] separated = fullString.split(" ");
        endPart = separated[1];
        return endPart;
    }

    public static double fourDecimalRoundOff(double value) {
        double retvalue = 0.00;
        DecimalFormat newFormat = new DecimalFormat("#.####");
        Double d = new Double(value);
        try {
            if (d != null)
                retvalue = Double.valueOf(newFormat.format(d));
        } catch (Exception e) {

        }

        return retvalue;

    }





}
