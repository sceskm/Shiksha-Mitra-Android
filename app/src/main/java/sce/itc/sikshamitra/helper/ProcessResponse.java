package sce.itc.sikshamitra.helper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sce.itc.sikshamitra.databasehelper.DatabaseHelper;
import sce.itc.sikshamitra.model.MySchoolData;


public class ProcessResponse {

    public static final String TAG = ProcessResponse.class.getName();
    public Context context;
    private DatabaseHelper dbHelper;

    public ProcessResponse(Context context) {
        this.context = context;
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public void processSchoolList(String userListResponse) {
        JSONObject jsonObject = null;
        JSONObject dataObject = null;
        JSONObject userObject = null;
        JSONArray schoolArray = null;
        String deletedIDs = "";
        try {
            jsonObject = new JSONObject(userListResponse);
            //String status = jsonObject.getString(ConstantField.STATUS);
            dataObject = jsonObject.getJSONObject(ConstantField.DATA);
            schoolArray = dataObject.getJSONArray(ConstantField.SCHOOL);
            //deletedIDs = dataObject.getString(ConstantField.DELETED_IDS);
            if (schoolArray.length() > 0) {
                for (int i = 0; i < schoolArray.length(); i++) {
                    userObject = schoolArray.getJSONObject(i);
                    MySchoolData user = MySchoolData.downloadMySchool(userObject);
                    if (dbHelper.saveDownloadedSchool(user)) {
                        Log.d(TAG, "getUserList: success");
                    }
                }
            }
            /*if (!deletedIDs.isEmpty()) {
                if (dbHelper.deleteIDs(deletedIDs, "sp_school", "SchoolId"))
                    Log.d(TAG, "processSchoolList: " + "deleted");
            }*/

        } catch (JSONException e) {
            Log.e(TAG, "processUserList: ", e);
        }
    }



}
