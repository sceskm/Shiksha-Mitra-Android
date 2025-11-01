package sce.itc.sikshamitra.databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.model.ComboProduct;
import sce.itc.sikshamitra.model.PreRegistration;
import sce.itc.sikshamitra.model.Product;
import sce.itc.sikshamitra.model.SchoolData;
import sce.itc.sikshamitra.model.Session;
import sce.itc.sikshamitra.model.Settings;
import sce.itc.sikshamitra.model.State;
import sce.itc.sikshamitra.model.TrainingSM;
import sce.itc.sikshamitra.model.User;
import sce.itc.sikshamitra.model.Venue;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getName();
    private static DatabaseHelper _dbHelper;
    private static String DB_PATH = ConstantField.DATABASE_PATH;
    private static String DB_NAME = ConstantField.DATABASE_NAME;
    private static int DB_VERSION = 1;
    private SQLiteDatabase myDataBase = null;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        // TODO Auto-generated constructor stub
    }

    public static DatabaseHelper getInstance(Context context) {
        if (_dbHelper == null) {
            _dbHelper = new DatabaseHelper(context);
        }
        return _dbHelper;
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            // do nothing - database already exist
            // upgrade to version 4 - not the correct method

        } else {

            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are going to overwrite that
            // database with our database.
            this.getReadableDatabase();
            this.close();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {

            String myPath = DB_PATH + DB_NAME;

            File file = new File(myPath);
            if (file.exists() && !file.isDirectory())
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            // database does't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public SQLiteDatabase openDataBase() throws SQLException {
        // Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath,
                null, SQLiteDatabase.OPEN_READWRITE);
        return myDataBase;
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.disableWriteAheadLogging();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    // public helper methods to access and get content from the database

    // return a cursor for a query
    public Cursor QueryDatabase(String query) {
        try {
            return myDataBase.rawQuery(query, null);
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public Cursor QueryDatabase(String query, String[] queryArgs) {

        try {
            return myDataBase.rawQuery(query, queryArgs);
        } catch (SQLException ex) {
            throw ex;
        }

    }

    // run a insert / update statement
    public void ExecuteScalar(String sql) throws SQLException {
        try {
            myDataBase.execSQL(sql);
        } catch (SQLException ex) {
            throw ex;
        }
    }


    public void updateDB() {
        int version = myDataBase.getVersion();
        boolean update = false;
        boolean isCheck;
        try {

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "updateDB: ", e);
        } finally {
            if (update) {
                myDataBase.endTransaction();
            }
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        db.disableWriteAheadLogging();
        super.onOpen(db);
    }

    // check if a column exists in table
    private boolean checkColumn(String table, String column) {
        boolean found = false;
        Cursor cur = QueryDatabase("PRAGMA table_info(" + table + ")");

        if (cur.getCount() > 0) {
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                if (cur.getString(1).equalsIgnoreCase(column)) {
                    found = true;
                    break;
                }
                cur.moveToNext();
            }
        }
        cur.close();

        return found;
    }

    public Cursor GetUser() {

        String sql = "SELECT * FROM User ";

        return QueryDatabase(sql);
    }

    public void deleteUser() {
        try {
            // delete existing rows
            myDataBase.execSQL("DELETE FROM user");

        } catch (SQLException ex) {

            throw ex;
        }
    }

    /*
     * Save venue data
     * */
    public boolean saveVenueData(Venue venueDetails) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();
            newEntry.put("VenueName", venueDetails.getVenueName());
            newEntry.put("ScheduledDateTime", venueDetails.getScheduledDateTime());
            newEntry.put("Address1", venueDetails.getAddress1());
            newEntry.put("Address2", venueDetails.getAddress2());
            newEntry.put("City", venueDetails.getCity());
            newEntry.put("District", venueDetails.getDistrict());
            newEntry.put("State", venueDetails.getState());
            newEntry.put("Pin", venueDetails.getPin());
            newEntry.put("VenueImage", venueDetails.getVenueImage());
            newEntry.put("VenueGUID", venueDetails.getVenueGUID());
            newEntry.put("Latitude", venueDetails.getLatitude());
            newEntry.put("Longitude", venueDetails.getLongitude());

            // Insert into VenueData table
            long retVal = myDataBase.insertOrThrow("sp_venue", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "saveVenueData: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }

    /*
     * Save pre-registration data
     * */
    public boolean savePreRegistration(PreRegistration preRegDetails) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("FirstName", preRegDetails.getFirstName());
            newEntry.put("LastName", preRegDetails.getLastName());
            newEntry.put("Mobile", preRegDetails.getMobile());
            newEntry.put("Username", preRegDetails.getUsername());
            newEntry.put("Password", preRegDetails.getPassword());
            newEntry.put("ShikshaMitraGUID", preRegDetails.getPassword());
            newEntry.put("VenueGUID", preRegDetails.getVenueGUID());
            newEntry.put("UserGUID", preRegDetails.getUserGUID());
            newEntry.put("CreatedOn", preRegDetails.getCreatedOn());
            newEntry.put("Latitude", preRegDetails.getLatitude());
            newEntry.put("Longitude", preRegDetails.getLongitude());


            // Insert into sp_preregistration table
            long retVal = myDataBase.insertOrThrow("sp_preregistration", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "savePreRegistration: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }

    public boolean saveTrainingData(TrainingSM trainingDetails) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("VenueName", trainingDetails.getVenueName());
            newEntry.put("ScheduledDateTime", trainingDetails.getScheduledDateTime());
            newEntry.put("Latitude", trainingDetails.getLatitude());
            newEntry.put("Longitude", trainingDetails.getLongitude());
            newEntry.put("SMCount", trainingDetails.getSMCount());
            newEntry.put("Image1", trainingDetails.getImage1());
            newEntry.put("Image2", trainingDetails.getImage2());
            newEntry.put("Image3", trainingDetails.getImage3());
            newEntry.put("Image4", trainingDetails.getImage4());
            newEntry.put("Remarks", trainingDetails.getRemarks());

            // Insert into sp_training table
            long retVal = myDataBase.insertOrThrow("sp_training", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "saveTraining: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }

    /*
     * Insert session data
     * */
    public boolean saveSession(Session sessionDetails) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("SessionGUID", sessionDetails.getSessionGUID());
            newEntry.put("SessionNo", sessionDetails.getSessionNo());
            newEntry.put("Img1", sessionDetails.getImg1());
            newEntry.put("Img2", sessionDetails.getImg2());
            newEntry.put("Img3", sessionDetails.getImg3());
            newEntry.put("Img4", sessionDetails.getImg4());
            newEntry.put("Img5", sessionDetails.getImg5());
            newEntry.put("Img6", sessionDetails.getImg6());
            newEntry.put("Img7", sessionDetails.getImg7());
            newEntry.put("Img8", sessionDetails.getImg8());
            newEntry.put("Img9", sessionDetails.getImg9());
            newEntry.put("Img10", sessionDetails.getImg10());
            newEntry.put("Img11", sessionDetails.getImg11());
            newEntry.put("Img12", sessionDetails.getImg12());
            newEntry.put("SessionStartedOn", sessionDetails.getSessionStartedOn());
            newEntry.put("SessionEndedOn", sessionDetails.getSessionEndedOn());
            newEntry.put("CommunicationStatus", sessionDetails.getCommunicationStatus());
            newEntry.put("CommunicationAttempt", sessionDetails.getCommunicationAttempt());
            newEntry.put("SessionStatus", sessionDetails.getSessionStatus());
            newEntry.put("SchoolGUID", sessionDetails.getSchoolGUID());
            newEntry.put("CommunicationGUID", sessionDetails.getCommunicationGUID());
            newEntry.put("Latitude", sessionDetails.getLatitude());
            newEntry.put("Longitude", sessionDetails.getLongitude());
            newEntry.put("CameraIssue", sessionDetails.getCameraIssue());
            newEntry.put("VenueGUID", sessionDetails.getVenueGUID());
            newEntry.put("UserGUID", sessionDetails.getUserGUID());
            newEntry.put("ShikshaMitraGUID", sessionDetails.getShikshaMitraGUID());

            long retVal = myDataBase.insertOrThrow("sp_session", null, newEntry);

            if (retVal > 0)
                dataSaved = true;


        } catch (SQLException ex) {
            Log.e(TAG, "saveSession: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }

    /*
     * School data entry
     * */
    public boolean saveSchool(SchoolData schoolDetails) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("FirstName", schoolDetails.getFirstName());
            newEntry.put("LastName", schoolDetails.getLastName());
            newEntry.put("Mobile", schoolDetails.getMobile());
            newEntry.put("Username", schoolDetails.getUsername());
            newEntry.put("UserGUID", schoolDetails.getUserGUID());
            newEntry.put("VenueGUID", schoolDetails.getVenueGUID());
            newEntry.put("AssosiateSchoolName", schoolDetails.getSchoolName());
            newEntry.put("SchoolGUID", schoolDetails.getSchoolGUID());
            newEntry.put("SchoolAddress1", schoolDetails.getSchoolAddress1());
            newEntry.put("SchoolAddress2", schoolDetails.getSchoolAddress2());
            newEntry.put("City", schoolDetails.getCity());
            newEntry.put("Locality", schoolDetails.getLocality());
            newEntry.put("District", schoolDetails.getDistrict());
            newEntry.put("State", schoolDetails.getState());
            newEntry.put("Pin", schoolDetails.getPin());
            newEntry.put("Email", schoolDetails.getEmail());
            newEntry.put("ContactName", schoolDetails.getContactName());
            newEntry.put("ContactNumber", schoolDetails.getContactNumber());
            newEntry.put("CreatedOn", schoolDetails.getCreatedOn());

            long retVal = myDataBase.insertOrThrow("sp_school", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

            if (dataSaved) {
                // Optional post-insert action
                dataSaved = false;
            }

        } catch (SQLException ex) {
            Log.e(TAG, "saveSchool: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }

    /*
     * Update school data entry
     * */
    public boolean updateSchool(SchoolData schoolDetails) {
        boolean dataUpdated = false;
        try {
            ContentValues updateValues = new ContentValues();

            updateValues.put("FirstName", schoolDetails.getFirstName());
            updateValues.put("LastName", schoolDetails.getLastName());
            updateValues.put("Mobile", schoolDetails.getMobile());
            updateValues.put("Username", schoolDetails.getUsername());
            updateValues.put("VenueGUID", schoolDetails.getVenueGUID());
            updateValues.put("SchoolName", schoolDetails.getSchoolName());
            updateValues.put("SchoolGUID", schoolDetails.getSchoolGUID());
            updateValues.put("SchoolAddress1", schoolDetails.getSchoolAddress1());
            updateValues.put("SchoolAddress2", schoolDetails.getSchoolAddress2());
            updateValues.put("City", schoolDetails.getCity());
            updateValues.put("Locality", schoolDetails.getLocality());
            updateValues.put("District", schoolDetails.getDistrict());
            updateValues.put("State", schoolDetails.getState());
            updateValues.put("Pin", schoolDetails.getPin());
            updateValues.put("Email", schoolDetails.getEmail());
            updateValues.put("ContactName", schoolDetails.getContactName());
            updateValues.put("ContactNumber", schoolDetails.getContactNumber());

            String whereClause = "UserGUID = ? AND CreatedOn = ?";
            String[] whereArgs = {schoolDetails.getUserGUID(), schoolDetails.getCreatedOn()};

            int rows = myDataBase.update("sp_school", updateValues, whereClause, whereArgs);

            if (rows > 0)
                dataUpdated = true;

        } catch (SQLException ex) {
            Log.e(TAG, "updateSchool: EXCEPTION", ex);
            throw ex;
        }

        return dataUpdated;
    }

    /*
     * Save user data
     * */
    public boolean saveUser(User userDetails) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("UserId", userDetails.getUserId());
            newEntry.put("UserName", userDetails.getUserName());
            newEntry.put("Password", userDetails.getPassword());
            newEntry.put("FirstName", userDetails.getFirstName());
            newEntry.put("LastName", userDetails.getLastName());
            newEntry.put("MobileNumber", userDetails.getMobileNumber());
            newEntry.put("Email", userDetails.getEmail());
            newEntry.put("LoggedIn", userDetails.getLoggedIn());
            newEntry.put("UserGUID", userDetails.getUserGUID());
            newEntry.put("SchoolGUID", userDetails.getSchoolGUID());
            newEntry.put("RoleId", userDetails.getRoleId());
            newEntry.put("LastLoggedIn", userDetails.getLastLoggedIn());
            newEntry.put("UserRole", userDetails.getUserRoleName());
            newEntry.put("AgencyId", userDetails.getAgencyId());

            long retVal = myDataBase.insertOrThrow("sp_user", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "saveUser: EXCEPTION", ex);
            throw ex;
        }
        return dataSaved;
    }

    public boolean updateUser(User userDetails) {
        boolean dataUpdated = false;
        try {
            ContentValues updateValues = new ContentValues();

            updateValues.put("UserName", userDetails.getUserName());
            updateValues.put("Password", userDetails.getPassword());
            updateValues.put("FirstName", userDetails.getFirstName());
            updateValues.put("LastName", userDetails.getLastName());
            updateValues.put("MobileNumber", userDetails.getMobileNumber());
            updateValues.put("Email", userDetails.getEmail());
            updateValues.put("LoggedIn", userDetails.getLoggedIn());
            updateValues.put("SchoolGUID", userDetails.getSchoolGUID());
            updateValues.put("RoleId", userDetails.getRoleId());
            updateValues.put("LastLoggedIn", userDetails.getLastLoggedIn());
            updateValues.put("UserRole", userDetails.getUserRoleName());
            updateValues.put("AgencyId", userDetails.getAgencyId());

            int rows = myDataBase.update(
                    "sp_user",
                    updateValues,
                    "UserGUID = ?",
                    new String[]{userDetails.getUserGUID()}
            );

            if (rows > 0)
                dataUpdated = true;

        } catch (SQLException ex) {
            Log.e(TAG, "updateUser: EXCEPTION", ex);
            throw ex;
        }

        return dataUpdated;
    }

    /*
    * Save settings data
    * */
    public boolean saveSettings(Settings settings) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("Parameter", settings.getParameter());
            newEntry.put("Value", settings.getValue());

            long retVal = myDataBase.insertOrThrow("sp_setting", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "saveUser: EXCEPTION", ex);
            throw ex;
        }
        return dataSaved;
    }

    /*
    * Save product data
    * */
    public boolean saveProduct(Product product){
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("ProductId", product.getProductId());
            newEntry.put("Product", product.getProduct());
            newEntry.put("ProductTypeId", product.getProductTypeId());

            long retVal = myDataBase.insertOrThrow("sp_product", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "saveProduct: EXCEPTION", ex);
            throw ex;
        }
        return dataSaved;
    }

    /*
    * Save combo - product data
    * */
    public boolean saveComboProduct(ComboProduct product){
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("ComboProductId", product.getProductId());
            newEntry.put("ComboProduct", product.getProduct());
            newEntry.put("ComboProductTypeId", product.getProductTypeId());

            long retVal = myDataBase.insertOrThrow("sp_comboproduct", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "saveComboProduct: EXCEPTION", ex);
            throw ex;
        }
        return dataSaved;
    }

    /*
    * Save state data
    * */
    public boolean saveState(State state){
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("StateId", state.getStateId());
            newEntry.put("StateName", state.getStateName());

            long retVal = myDataBase.insertOrThrow("sp_state", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "saveState: EXCEPTION", ex);
            throw ex;
        }
        return dataSaved;
    }

    /*
     * Get user details - original o
     * */
    public Cursor getUser(String userGuid) {
        String sql = "SELECT * FROM sp_user WHERE UserGUID = '" + userGuid + "'";
        return QueryDatabase(sql);
    }
    /*
    * Just for testing
    * */
    public Cursor getUserOld(String userGuid) {
        String sql = "SELECT * FROM sp_user";
        return QueryDatabase(sql);
    }

    public void updateCommunicationSendStatus(int messageID, int status, String details, boolean updateCount) {
        Date dateNow = new Date();

        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String sql = "UPDATE isp_communicationsend ";
        sql += "SET ProcessedOn = '" + dateformatYYYYMMDD.format(dateNow) + "', ";
        sql += "CommunicationStatusID = " + Integer.toString(status) + ", ";
        sql += "ProcessDetails = '" + details.replace("'", "''") + "', ";

        if (updateCount == true)
            sql += "ProcessCount = ProcessCount + 1 ";
        else
            sql = sql.substring(0, sql.length() - 2);

        sql += " WHERE _id = " + Integer.toString(messageID);

        ExecuteScalar(sql);
    }
    public Cursor getUnprocessedCommSendMessageCount() {
        String sql = "SELECT COUNT(*) AS UnSent FROM isp_communicationsend "
                + "WHERE  (CommunicationStatusID = 1 OR (CommunicationStatusID = 3 AND ProcessCount <= 50)) "
                + "AND InActive = 0";

        return QueryDatabase(sql);
    }

    //get only single communicationSend row for not uploaded message
    public Cursor currentUnprocessedCommSendMessage(String command, String userGUID, String CommSendGuid) {
        String sql = "SELECT * FROM isp_communicationsend "
                + " WHERE  Command = '" + command + "' "
                + " AND  UserGUID = '" + userGUID + "' "
                + " AND  CommunicationGUID = '" + CommSendGuid + "' "
                + " AND (CommunicationStatusID = 1 OR (CommunicationStatusID = 3 "
                + " AND ProcessCount <= 75)) "
                + " AND InActive = 0 ";
        return QueryDatabase(sql);
    }

    /*
    * Delete 'sp_user' table data
    * */
    public boolean deleteUserData() {
        try {
            String sql = "DELETE FROM sp_user";
            myDataBase.execSQL(sql);
        } catch (Exception ex) {
            Log.e(TAG, "Deleted:" + ex);
        }
        return true;
    }
    /*
    * Reset database
    * */
    public boolean resetDatabase(String userGUID) {

        try {
            //user
            String sql = "DELETE FROM sp_user WHERE UserGUID!=" + "'" + userGUID + "'";
            myDataBase.execSQL(sql);

            //store
            sql = "DELETE FROM sp_state";
            myDataBase.execSQL(sql);

            //attendance
            sql = "DELETE FROM sp_comboproduct ";
            myDataBase.execSQL(sql);

            //product
            sql = "DELETE FROM sp_product ";
            myDataBase.execSQL(sql);

            //communicationOn
            sql = "DELETE FROM sp_settings ";
            myDataBase.execSQL(sql);

        } catch (Exception ex) {
            Log.e("TAG", "Delete: " + ex);
        }
        return true;
    }


}
