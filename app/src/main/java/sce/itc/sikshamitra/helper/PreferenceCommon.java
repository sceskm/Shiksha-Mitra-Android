package sce.itc.sikshamitra.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import sce.itc.sikshamitra.AppController;


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

    public String getLastLoggedInDateTime() {
        return prefs.getString("LastLoggedDate", "2001-01-01");
    }

    public String getAccessToken() {
        return prefs.getString(ConstantField.ACCESS_TOKEN, "");
    }

    public void setAccessToken(String key) {
        prefs.edit().putString(ConstantField.ACCESS_TOKEN, key).apply();
    }

    public String getRefreshToken() {
        return prefs.getString(ConstantField.REFRESH_TOKEN, "");
    }

    public void setRefreshToken(String key) {
        prefs.edit().putString(ConstantField.REFRESH_TOKEN, key).apply();
    }

    public String getRefreshTokenCreated() {
        return prefs.getString(ConstantField.REFRESH_TOKEN_CREATED, ConstantField.DEFAULT_DATE);
    }

    public void setRefreshTokenCreated(String key) {
        prefs.edit().putString(ConstantField.REFRESH_TOKEN_CREATED, key).apply();
    }

    public void setLastLoggedInDateTime(String key) {
        prefs.edit().putString("LastLoggedDate", key).apply();
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

    public int getAutoSyncing() {
        return prefs.getInt(ConstantField.AUTO_SYNCING, ConstantField.DEFAULT);
    }

    public void setAutoSyncing(int key) {
        prefs.edit().putInt(ConstantField.AUTO_SYNCING, key).apply();
    }

    /*
     * Role ID
     * */
    public int getUserRoleId() {
        return prefs.getInt("role_id", 0);
    }

    public void setUserRoleId(int key) {
        prefs.edit().putInt("role_id", key).apply();
    }
    /*
    * User Id
    * */
    public int getUserId() {
        return prefs.getInt("user_id", 0);
    }

    public void setUserId(int key) {
        prefs.edit().putInt("user_id", key).apply();
    }

    /*
    * Username
    * */
    public String getUsername() {
        return prefs.getString("user_name", "");
    }

    public void setUsername(String key) {
        prefs.edit().putString("user_name", key).apply();
    }

    public String getPassword() {
        return prefs.getString("password", "");
    }

    public void setPassword(String key) {
        prefs.edit().putString("password", key).apply();
    }

    public int getLastSessionCount() {
        return prefs.getInt("session_count", 0);
    }

    public void setLastSessionCount(int key) {
        prefs.edit().putInt("session_count", key).apply();
    }



}
