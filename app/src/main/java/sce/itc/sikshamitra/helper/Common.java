package sce.itc.sikshamitra.helper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.UUID;

import sce.itc.sikshamitra.AlertCallBack;
import sce.itc.sikshamitra.R;
import sce.itc.sikshamitra.activity.VenueData;

public class Common {
    public static final boolean DEBUGGING = true;

    public static final String[] PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
    };

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

    public static String getGuid() {
        return UUID.randomUUID().toString();
    }



}
