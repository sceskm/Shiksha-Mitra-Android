package sce.itc.sikshamitra.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import sce.itc.sikshamitra.AppController;
import sce.itc.sikshamitra.databasehelper.DatabaseHelper;


public class PreferenceCommon {

    private static PreferenceCommon instance = null;
    private SharedPreferences prefs;
    private Context context;

    /**
     * Use PreferenceHelper.getInstance()
     */
    public PreferenceCommon() {

    }

    public static PreferenceCommon getInstance() {
        if (instance == null) {
            instance = new PreferenceCommon();
            instance.prefs = PreferenceManager.getDefaultSharedPreferences(AppController.getInstance().getApplicationContext());
        }

        return instance;
    }

    public String getSchoolGUID() {
        return prefs.getString("SchoolGUID", "");
    }

    public void setSchoolGUID(String key) {
        prefs.edit().putString("SchoolGUID", key).apply();
    }

    public String getActivityGUID() {
        return prefs.getString("ActivityGUID", "");
    }

    public void setActivityGUID(String key) {
        prefs.edit().putString("ActivityGUID", key).apply();
    }

    public String getSessionGUID() {
        return prefs.getString("SessionGUID", "");
    }

    public void setSessionGUID(String key) {
        prefs.edit().putString("SessionGUID", key).apply();
    }

    public String getUserGUID() {
        return prefs.getString("UserGUID", "");
    }

    public void setUserGUID(String key) {
        prefs.edit().putString("UserGUID", key).apply();
    }

    public String getCity() {
        return prefs.getString("City", "");
    }

    public void setCity(String key) {
        prefs.edit().putString("City", key).apply();
    }

    public int getCityId() {
        return prefs.getInt("CityId", 0);
    }

    public void setCityId(int key) {
        prefs.edit().putInt("CityId", key).apply();
    }

    public String getDistrict() {
        return prefs.getString("District", "");
    }

    public void setDistrict(String key) {
        prefs.edit().putString("District", key).apply();
    }

    public String getState() {
        return prefs.getString("State", "");
    }

    public void setState(String key) {
        prefs.edit().putString("State", key).apply();
    }

    public String getSessionNO() {
        return prefs.getString("SessionNO", "");
    }

    public void setSessionNO(String key) {
        prefs.edit().putString("SessionNO", key).apply();
    }

    public String getLastDate() {
        return prefs.getString("LastDate", "2001-01-01");
    }

    public void setLastdate(String key) {
        prefs.edit().putString("LastDate", key).apply();
    }

    /*public void resetPreferences() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strToday = sdf.format(today);

        if (!PreferenceCommon.getInstance().getLastDate().equals(strToday)) {
            String lstDate = PreferenceCommon.getInstance().getLastDate();
            final DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
            if ((dbHelper.SetClosePreviousActivity(strToday))) {
            }
            //yesterday data is saved in prefernce - reset
            PreferenceCommon.getInstance().setLastdate(strToday);
            this.setSchoolGUID("");
            this.setActivityGUID("");
            this.setSessionGUID("");


        }
    }*/

    public int getSyncTime() {
        return prefs.getInt("SyncTime", 0);
    }

    public void setSyncTime(int key) {
        prefs.edit().putInt("SyncTime", key).apply();
    }

    public int getMinimumLoggingInterval() {
        return 20 * 60;         // minutes
    }

    public int getVersion() {
        return prefs.getInt("Version", 0);
    }

    public void setVersion(int key) {
        prefs.edit().putInt("Version", key).apply();
    }

    public String getSchoolvisited() {
        return prefs.getString("Schoolvisited", "");
    }

    public void setSchoolvisited(String key) {
        prefs.edit().putString("Schoolvisited", key).apply();
    }

    public String getFirstAidBoxCondition() {
        return prefs.getString("FirstAidBoxCondition", "");
    }

    public void setFirstAidBoxCondition(String key) {
        prefs.edit().putString("FirstAidBoxCondition", key).apply();
    }

    public String getHopscotchPermission() {
        return prefs.getString("HopscotchPermission", "");
    }

    public void setHopscotchPermission(String key) {
        prefs.edit().putString("HopscotchPermission", key).apply();
    }

    public String getPhnIMEI() {
        return prefs.getString("IMEI", "");
    }

    public void setPhnIMEI(String key) {
        prefs.edit().putString("IMEI", key).apply();
    }

    public String getUserValidatedTime() {
        return prefs.getString("DateTime", "2000-01-01 00:00:00");
    }

    public void setUserValidatedTime(String key) {
        prefs.edit().putString("DateTime", key).apply();
    }

    public int getVideoClickEvent() {
        return prefs.getInt("ClickNo", 0);
    }

    public void setVideoClickEvent(int key) {
        prefs.edit().putInt("ClickNo", key).apply();
    }

    public String getCityDistrictList() {
        return prefs.getString("city_district", "");
    }

    public void setCityDistrictList(String key) {
        prefs.edit().putString("city_district", key).apply();
    }

    //city,district,state - list size
    public int getMultipleList() {
        return prefs.getInt("data_list", 0);
    }

    public void setMultipleList(int key) {
        prefs.edit().putInt("data_list", key).apply();
    }

    public String getProductType() {
        return prefs.getString("p_type", "");
    }

    public void setProductType(String key) {
        prefs.edit().putString("p_type", key).apply();
    }
}
