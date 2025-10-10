package sce.itc.sikshamitra;

import android.app.Application;
import android.database.SQLException;

import sce.itc.sikshamitra.databasehelper.DatabaseHelper;

public class AppController extends Application {
    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //open database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(mInstance);
        try {
            dbHelper.createDataBase();
        } catch (Exception ioe) {
            throw new Error("Unable to create database");
        }
        try {
            dbHelper.openDataBase();
            dbHelper.updateDB();
        } catch (SQLException sqle) {
            throw sqle;
        }

    }
}
