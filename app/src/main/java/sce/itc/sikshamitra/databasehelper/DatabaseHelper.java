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

import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.CommunicationOn;
import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.helper.PreferenceCommon;
import sce.itc.sikshamitra.model.ComboProduct;
import sce.itc.sikshamitra.model.CommunicationSend;
import sce.itc.sikshamitra.model.MySchoolData;
import sce.itc.sikshamitra.model.PreRegistration;
import sce.itc.sikshamitra.model.Product;
import sce.itc.sikshamitra.model.RetailOutReachModel;
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

    /*
     * Delete  table 'sp_user' data
     * */
    public void deleteUser() {
        try {
            // delete existing rows
            myDataBase.execSQL("DELETE FROM sp_user");

        } catch (SQLException ex) {

            throw ex;
        }
    }

    /*
     * Delete  table 'sp_settings' data
     * */
    public void deleteSettings() {
        try {
            // delete existing rows
            myDataBase.execSQL("DELETE FROM sp_settings");

        } catch (SQLException ex) {

            throw ex;
        }
    }

    public void deleteProduct() {
        try {
            // delete existing rows
            myDataBase.execSQL("DELETE FROM sp_product");

        } catch (SQLException ex) {

            throw ex;
        }
    }

    public void deleteComboProduct() {
        try {
            // delete existing rows
            myDataBase.execSQL("DELETE FROM sp_comboproduct");

        } catch (SQLException ex) {

            throw ex;
        }
    }

    public void deleteState() {
        try {
            // delete existing rows
            myDataBase.execSQL("DELETE FROM sp_state");

        } catch (SQLException ex) {

            throw ex;
        }
    }

    public void deleteDownloadedSchool() {
        try {
            // delete existing rows
            myDataBase.execSQL("DELETE FROM sp_school");

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
            newEntry.put("StateId", venueDetails.getStateId());
            newEntry.put("Pin", venueDetails.getPinCode());
            newEntry.put("OrganisationId", venueDetails.getOrganizationId());
            newEntry.put("VenueGUID", venueDetails.getVenueGuid());
            newEntry.put("Latitude", venueDetails.getLatitude());
            newEntry.put("Longitude", venueDetails.getLongitude());
            newEntry.put("CommunicationGUID", venueDetails.getCommunicationGuid());
            newEntry.put("ImageFile", venueDetails.getImageFile());
            newEntry.put("ImageDefinitionId", venueDetails.getImageDefinitionId());
            newEntry.put("ImageExt", venueDetails.getImageExt());

            // Insert into VenueData table
            long retVal = myDataBase.insertOrThrow("sp_venue", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

            if (dataSaved) {
                // create and save a communication message
                dataSaved = false;

                CommunicationSend commSend = venueDetails.createCommSend();

                if (saveCommunicationSend(commSend) > 0)
                    dataSaved = true;
            }

        } catch (SQLException ex) {
            Log.e(TAG, "saveVenueData: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }

    public long saveCommunicationSend(CommunicationSend commSend) {
        long rowID = -1;
        try {
            ContentValues newEntry = new ContentValues();
            newEntry.put("ProcessedOn", commSend.getProcessedOn());
            newEntry.put("ProcessDetails", commSend.getProcessDetails());
            newEntry.put("ProcessCount", commSend.getProcessCount());
            newEntry.put("CommandDate", commSend.getCommandDate());
            newEntry.put("Command", commSend.getCommand());
            newEntry.put("CommandDetails", commSend.getCommandDetails());
            newEntry.put("CommunicationStatusID", commSend.getCommunicationStatusID());
            newEntry.put("CommunicationGUID", commSend.getCommunicationGUID());
            newEntry.put("CreatedByID", commSend.getCreatedByID());
            newEntry.put("UserGUID", PreferenceCommon.getInstance().getUserGUID());
            //TODO Inactive
            //inActiveComminicationSendSeries(commSend.getCommunicationGUID());
            // save the new entry
            rowID = myDataBase.insertOrThrow("sp_communicationsend", null, newEntry);

        } catch (SQLException ex) {
            Log.e(TAG, "saveCommunicationSend: ", ex);
            throw ex;
        } finally {
            Log.d(TAG, "saveCommunicationSend: ");
        }

        return rowID;
    }

    /*
     * Insert session Teacher(SM) end
     * */
    public boolean saveSession(Session sessionDetails) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("SessionGUID", sessionDetails.getSessionGuid());
            newEntry.put("UserGUID", sessionDetails.getUserGuid());
            newEntry.put("SessionNo", sessionDetails.getSessionNo());
            newEntry.put("SchoolGUID", sessionDetails.getSchoolGuid());
            newEntry.put("NoOfStudents", sessionDetails.getNoOfStudent());
            newEntry.put("Img1", sessionDetails.getImg1());
            newEntry.put("Img2", sessionDetails.getImg2());
            newEntry.put("Img3", sessionDetails.getImg3());
            newEntry.put("Img4", sessionDetails.getImg4());
            newEntry.put("SessionStartedOn", sessionDetails.getSessionStart());
            newEntry.put("SessionEndedOn", sessionDetails.getSessionEnd());
            newEntry.put("Remarks", sessionDetails.getRemarks());
            newEntry.put("SessionStatus", sessionDetails.getSessionStatus());
            newEntry.put("CommunicationStatus", sessionDetails.getCommunicationStatus());
            newEntry.put("CommunicationAttempt", sessionDetails.getCommunicationAttempt());
            newEntry.put("CommunicationGUID", sessionDetails.getCommunicationGuid());
            newEntry.put("Latitude", sessionDetails.getLatitude());
            newEntry.put("Longitude", sessionDetails.getLongitude());
            newEntry.put("ImgDefinitionId1", sessionDetails.getImgDefinitionId1());
            newEntry.put("ImgDefinitionId2", sessionDetails.getImgDefinitionId2());
            newEntry.put("ImgDefinitionId3", sessionDetails.getImgDefinitionId3());
            newEntry.put("ImgDefinitionId4", sessionDetails.getImgDefinitionId4());
            newEntry.put("ImgExt1", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt2", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt3", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt4", ConstantField.IMAGE_FORMAT);

            long retVal = myDataBase.insertOrThrow("sp_session", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

            if (dataSaved) {
                // create and save a communication message
                dataSaved = false;

                CommunicationSend commSend = sessionDetails.createCommSend();

                if (saveCommunicationSend(commSend) > 0)
                    dataSaved = true;
            }


        } catch (SQLException ex) {
            Log.e(TAG, "saveSession: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }

    /*
    * nsert session from Agency end
    * */
    public boolean saveFinalSession(Session sessionDetails) {
        boolean dataSaved = false;
        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("SessionGUID", sessionDetails.getSessionGuid());
            newEntry.put("UserGUID", sessionDetails.getUserGuid());
            newEntry.put("SessionNo", sessionDetails.getSessionNo());
            newEntry.put("SchoolGUID", sessionDetails.getSchoolGuid());
            newEntry.put("NoOfStudents", sessionDetails.getNoOfStudent());
            newEntry.put("Img1", sessionDetails.getImg1());
            newEntry.put("Img2", sessionDetails.getImg2());
            newEntry.put("Img3", sessionDetails.getImg3());
            newEntry.put("Img4", sessionDetails.getImg4());
            newEntry.put("Img5", sessionDetails.getImg1());
            newEntry.put("Img6", sessionDetails.getImg2());
            newEntry.put("Img7", sessionDetails.getImg3());
            newEntry.put("Img8", sessionDetails.getImg4());
            newEntry.put("SessionStartedOn", sessionDetails.getSessionStart());
            newEntry.put("SessionEndedOn", sessionDetails.getSessionEnd());
            newEntry.put("Remarks", sessionDetails.getRemarks());
            newEntry.put("SessionStatus", sessionDetails.getSessionStatus());
            newEntry.put("CommunicationStatus", sessionDetails.getCommunicationStatus());
            newEntry.put("CommunicationAttempt", sessionDetails.getCommunicationAttempt());
            newEntry.put("CommunicationGUID", sessionDetails.getCommunicationGuid());
            newEntry.put("Latitude", sessionDetails.getLatitude());
            newEntry.put("Longitude", sessionDetails.getLongitude());
            newEntry.put("ImgDefinitionId1", sessionDetails.getImgDefinitionId1());
            newEntry.put("ImgDefinitionId2", sessionDetails.getImgDefinitionId2());
            newEntry.put("ImgDefinitionId3", sessionDetails.getImgDefinitionId3());
            newEntry.put("ImgDefinitionId4", sessionDetails.getImgDefinitionId4());
            newEntry.put("ImgDefinitionId5", sessionDetails.getImgDefinitionId1());
            newEntry.put("ImgDefinitionId6", sessionDetails.getImgDefinitionId2());
            newEntry.put("ImgDefinitionId7", sessionDetails.getImgDefinitionId3());
            newEntry.put("ImgDefinitionId8", sessionDetails.getImgDefinitionId4());
            newEntry.put("ImgExt1", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt2", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt3", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt4", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt5", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt6", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt7", ConstantField.IMAGE_FORMAT);
            newEntry.put("ImgExt8", ConstantField.IMAGE_FORMAT);
            newEntry.put("TeacherProductId1",sessionDetails.getTeacherProductId1());
            newEntry.put("IsTeacherProductDistributed1",sessionDetails.getTeacherIsDistributed1());
            newEntry.put("StudentProductId1",sessionDetails.getProductId1());
            newEntry.put("IsStudentProductDistributed1",sessionDetails.getIsDistributed1());
            newEntry.put("StudentProductId2",sessionDetails.getProductId2());
            newEntry.put("IsStudentProductDistributed2",sessionDetails.getIsDistributed2());

            long retVal = myDataBase.insertOrThrow("sp_session", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

            if (dataSaved) {
                // create and save a communication message
                dataSaved = false;

                CommunicationSend commSend = sessionDetails.createFinalCommSend();

                if (saveCommunicationSend(commSend) > 0)
                    dataSaved = true;
            }


        } catch (SQLException ex) {
            Log.e(TAG, "saveSession: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }


    /*
     * Save downloaded school
     * */
    public boolean saveSchool(MySchoolData schoolDetails) {
        boolean dataSaved = false;

        try {
            //deleteDownloadedSchool();
            ContentValues newEntry = new ContentValues();
            newEntry.put("SchoolId", schoolDetails.getSchoolId());
            newEntry.put("AssociateSchool", schoolDetails.getSchoolName());
            newEntry.put("SchoolGUID", schoolDetails.getSchoolGuid());
            newEntry.put("SchoolAddress1", schoolDetails.getAddress1());
            newEntry.put("SchoolAddress2", schoolDetails.getAddress2()); // Not provided in JSON
            newEntry.put("City", schoolDetails.getCity());
            newEntry.put("Locality", ""); // using block name as locality
            newEntry.put("District", schoolDetails.getDistrict());
            newEntry.put("DistrictCode", schoolDetails.getDistrictCode());
            newEntry.put("Block", schoolDetails.getBlockName());
            newEntry.put("BlockCode", schoolDetails.getBlockCode());
            newEntry.put("State", schoolDetails.getState());
            newEntry.put("StateId", schoolDetails.getStateId());
            newEntry.put("PinCode", schoolDetails.getPinCode());
            newEntry.put("Email", schoolDetails.getEmail());
            newEntry.put("ContactName", ""); // not in JSON
            newEntry.put("ContactNumber", schoolDetails.getPhone());
            newEntry.put("Mobile", schoolDetails.getPhone());
            newEntry.put("Username", ""); // optional if user not mapped
            newEntry.put("UserGUID", ""); // optional if not available
            newEntry.put("VenueGUID", ""); // optional
            newEntry.put("FirstName", ""); // optional
            newEntry.put("LastName", "");  // optional
            newEntry.put("UDISECode", schoolDetails.getUdiseCode());  // optional


            long retVal = myDataBase.insertOrThrow("sp_school", null, newEntry);

            dataSaved = retVal > 0;

        } catch (SQLException ex) {
            Log.e(TAG, "saveSchool: EXCEPTION", ex);
            throw ex;
        }

        return dataSaved;
    }

    /*
     * Update school data entry
     * *//*
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
    }*/

    /*
     * Save user data
     * */
    public boolean saveUser(User userDetails) {
        boolean dataSaved = false;
        try {
            deleteUser();
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
            newEntry.put("RoleId", userDetails.getRoleId());
            newEntry.put("LastLoggedIn", userDetails.getLastLoggedIn());
            newEntry.put("UserRole", userDetails.getUserRoleName());
            //newEntry.put("InActive", userDetails.getInActive());

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
            updateValues.put("RoleId", userDetails.getRoleId());
            updateValues.put("LastLoggedIn", userDetails.getLastLoggedIn());
            updateValues.put("UserRole", userDetails.getUserRoleName());

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

            long retVal = myDataBase.insertOrThrow("sp_settings", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            Log.e(TAG, "saveSettings: EXCEPTION", ex);
            throw ex;
        }
        return dataSaved;
    }

    /*
     * Save product data
     * */
    public boolean saveProduct(Product product) {
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
    public boolean saveComboProduct(ComboProduct product) {
        boolean dataSaved = false;
        try {

            ContentValues newEntry = new ContentValues();

            newEntry.put("ComboProductId", product.getProductId());
            newEntry.put("ComboProduct", product.getProduct());
            newEntry.put("ComboProductTypeId", product.getProductTypeId());
            newEntry.put("ComboId", product.getComboId());
            newEntry.put("ComboName", product.getComboName());

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
    public boolean saveState(State state) {
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

        String sql = "UPDATE sp_communicationsend ";
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
        String sql = "SELECT COUNT(*) AS UnSent FROM sp_communicationsend "
                + "WHERE  (CommunicationStatusID = 1 OR (CommunicationStatusID = 3 AND ProcessCount <= 50)) "
                + "AND InActive = 0";

        return QueryDatabase(sql);
    }

    //get all communicationSend rows for not uploaded messages
    public Cursor unProcessedCommSendMessage(String command, String userGUID) {

        String sql = "SELECT * FROM sp_communicationsend " + "WHERE  Command = '" + command
                + "' AND  UserGUID = '" + userGUID + "' AND (CommunicationStatusID = 1 OR (CommunicationStatusID = 3 AND ProcessCount <= 75)) AND InActive = 0 ";

        return QueryDatabase(sql);
    }

    //get only single communicationSend row for not uploaded message
    public Cursor currentUnprocessedCommSendMessage(String command, String userGUID, String CommSendGuid) {
        String sql = "SELECT * FROM sp_communicationsend "
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

    public boolean deleteEnteredVenueData(Venue venue) {
        try {
            String sql = "DELETE FROM sp_venue WHERE VenueGUID=" + "'" + venue.getVenueGuid() + "'";
            myDataBase.execSQL(sql);
        } catch (Exception ex) {
            Log.e(TAG, "Deleted:" + ex);
        }
        return true;
    }

    /*
     * Fetch downloaded school data
     * */
    public Cursor getMySchoolData() {
        String sql = "SELECT * FROM sp_school WHERE SchoolGUID IS NOT NULL ORDER BY AssociateSchool ASC";
        return QueryDatabase(sql);
    }

    public Cursor getSessionDetails(String sessionGuid) {
        String sql = "SELECT * FROM sp_session WHERE SessionGUID = '" + sessionGuid + "'";
        return QueryDatabase(sql);
    }

    public Cursor getAllStates() {
        String sql = "SELECT * FROM sp_state ORDER BY StateName ASC";
        return QueryDatabase(sql);
    }


    // Save Store
    public boolean saveDownloadedSchool(MySchoolData schoolDetails) {
        boolean dataSaved = false;
        Cursor cursorTest = null;
        try {
            ContentValues newEntry = new ContentValues();
            newEntry.put("SchoolId", schoolDetails.getSchoolId());
            newEntry.put("AssociateSchool", schoolDetails.getSchoolName());
            newEntry.put("SchoolGUID", schoolDetails.getSchoolGuid());
            newEntry.put("UDISECode", schoolDetails.getUdiseCode());
            newEntry.put("District", schoolDetails.getDistrict());
            newEntry.put("DistrictCode", schoolDetails.getDistrictCode());
            newEntry.put("Block", schoolDetails.getBlockName());
            newEntry.put("BlockCode", schoolDetails.getBlockCode());

            cursorTest = checkSchoolByGuid(schoolDetails.getSchoolGuid());
            long retVal = -1;
            cursorTest.moveToFirst();
            if (cursorTest.getCount() > 0) {
                // update the row
                String[] whereArgs = {String.valueOf(schoolDetails.getSchoolGuid())};
                retVal = myDataBase.update("sp_school", newEntry, "SchoolGUID = ?", whereArgs);
            } else {
                // insert
                retVal = myDataBase.insertOrThrow("sp_school", null, newEntry);
            }
            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (cursorTest != null)
                cursorTest.close();
        }

        return dataSaved;
    }

    // Check for Existing Store
    public Cursor checkSchoolByGuid(String guid) {
        String sql = "SELECT * " + " FROM sp_school " + " WHERE SchoolGUID = '" + guid + "'";
        return QueryDatabase(sql);
    }

    //delete ids which are inactive
    public boolean deleteIDs(String ids, String tableName, String coloumnName) {
        try {
            String sql = "DELETE FROM " + tableName + " WHERE " + coloumnName + " IN " + "(" + ids + ")";
            myDataBase.execSQL(sql);
        } catch (Exception ex) {
            Log.e("TAG", "Deleted: IDs " + ex);
        }
        return true;
    }

    //last communication
    public Cursor getLastCommDate(String action) {
        String sql = "SELECT * FROM sp_communicationon WHERE Action = '" + action + "' ";

        return QueryDatabase(sql);
    }

    public String getExistingID(String coloumnName, String tableName) {
        int id = 0;
        String ids = "";
        Cursor cursor = this.getIDs(coloumnName, tableName);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            id = cursor.getInt(0);
            ids += String.valueOf(id) + ",";
            cursor.moveToNext();
        }
        cursor.close();

        //remove last comma using String.substring() method
        if (!ids.isEmpty())
            ids = ids.substring(0, ids.length() - 1);


        return String.valueOf(ids);
    }

    // check for Existing ids
    public Cursor getIDs(String coloumnName, String tableName) {
        String sql = "SELECT " + coloumnName + " FROM " + tableName;

        return QueryDatabase(sql);
    }

    //get last communication date
    public boolean savedLastCommDate(String command) {
        boolean saved = false;
        CommunicationOn communicationOn = new CommunicationOn();
        communicationOn.setAction(command);
        communicationOn.setLastDate(Common.iso8601Format.format(new Date()));
        saved = this.saveLastCommDate(communicationOn);

        return saved;

    }

    public boolean saveLastCommDate(CommunicationOn communicationOn) {
        Cursor cursorTest = null;
        boolean dataSaved = false;
        try {
            // insert a row - we will always have only 1 row
            ContentValues newEntry = new ContentValues();
            newEntry.put("Action", communicationOn.getAction());
            newEntry.put("LastDate", communicationOn.getLastDate());

            cursorTest = getLastCommDate(communicationOn.getAction());

            long retVal = -1;
            cursorTest.moveToFirst();
            if (cursorTest.getCount() > 0) {
                // update the row
                String[] whereArgs = {String.valueOf(communicationOn.getAction())};

                retVal = myDataBase.update("sp_communicationon", newEntry, "Action = ?", whereArgs);
            } else {
                // insert
                retVal = myDataBase.insertOrThrow("sp_communicationon", null, newEntry);
            }
            if (retVal > 0)
                dataSaved = true;

        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (cursorTest != null)
                cursorTest.close();
        }
        return dataSaved;
    }


    /*
     * Retails details
     * */
    public boolean saveRetailDetailsOld(RetailOutReachModel retailsDetails) {
        boolean dataSaved = false;

        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("ShopName", retailsDetails.getShopName());
            newEntry.put("RetailOutReachGUID", retailsDetails.getRetailOutreachGuid());
            newEntry.put("UserGUID", retailsDetails.getUserGuid());
            newEntry.put("OrganizationId", retailsDetails.getOrganizationId());
            newEntry.put("NearbySchool", retailsDetails.getNearbySchool()); // If you are treating this as nearby school
            newEntry.put("SchoolGUID", retailsDetails.getSchoolGuid());
            newEntry.put("ContactName", retailsDetails.getContactName()); // not in JSON
            newEntry.put("ContactPhone", retailsDetails.getContactPhone());
            newEntry.put("Address1", retailsDetails.getAddress1());
            newEntry.put("Address2", retailsDetails.getAddress2()); // optional if not in JSON
            newEntry.put("Division", retailsDetails.getDivision()); // If division = district or separate
            newEntry.put("Block", retailsDetails.getBlock());
            newEntry.put("City", retailsDetails.getCity());
            newEntry.put("District", retailsDetails.getDistrict());
            newEntry.put("StateId", retailsDetails.getStateId());
            newEntry.put("PinCode", retailsDetails.getPinCode());
            //true/false fields
//            newEntry.put("IsKeepITCProducts", retailsDetails.getIsKeepITCProducts());
//            newEntry.put("ITCProductNames", retailsDetails.getItcProductNames());
//            newEntry.put("HandwashPouchesSold", retailsDetails.getHandWashPouchesSold());

            // BELOW FIELDS DO NOT EXIST IN YOUR JSON – set default 0 or empty
//            newEntry.put("StockItcProducts", true);
//            newEntry.put("BrandingInterested", true);
//            newEntry.put("ShopPainting", true);
//            newEntry.put("DealerBoard", true);
//            newEntry.put("Poster", true);
//            newEntry.put("Bunting", true);
//            newEntry.put("IsKeepCompetitorProduct", 1);
//            newEntry.put("ItcProductNames", "");

//            newEntry.put("SavlonSoapSold", retailsDetails.getSavlonSoapSold());
//            newEntry.put("FmcgPurchaseFrom", retailsDetails.getFmcgpurchaseFrom());
//            newEntry.put("DistributorDetails", retailsDetails.getDistributorDetails());
//            newEntry.put("MarketDetails", retailsDetails.getMarketDetails());

            newEntry.put("Latitude", retailsDetails.getLatitude());
            newEntry.put("Longitude", retailsDetails.getLongitude());

            newEntry.put("Image1", retailsDetails.getImage1());
            newEntry.put("ImgExt1", retailsDetails.getImgExt1());
            newEntry.put("ImgDefinitionId1", retailsDetails.getImgDefinitionId1());

            long retVal = myDataBase.insertOrThrow("sp_retailersdetails", null, newEntry);

            if (retVal > 0)
                dataSaved = true;

            if (dataSaved) {
                dataSaved = false;

                CommunicationSend commSend = retailsDetails.createCommSend();

                if (saveCommunicationSend(commSend) > 0)
                    dataSaved = true;
            }

        } catch (SQLException ex) {
            Log.e(TAG, "saveRetails: EXCEPTION", ex);
            throw ex;
        }


        return dataSaved;
    }

    public boolean saveRetailDetails(RetailOutReachModel retailsDetails) {
        boolean dataSaved = false;

        try {
            ContentValues newEntry = new ContentValues();

            newEntry.put("ShopName", retailsDetails.getShopName());
            newEntry.put("RetailOutReachGUID", retailsDetails.getRetailOutreachGuid());
            newEntry.put("UserGUID", retailsDetails.getUserGuid());
            newEntry.put("OrganizationId", retailsDetails.getOrganizationId());
            newEntry.put("NearbySchool", retailsDetails.getNearbySchool());
            newEntry.put("SchoolGUID", retailsDetails.getSchoolGuid());
            newEntry.put("ContactName", retailsDetails.getContactName());
            newEntry.put("ContactPhone", retailsDetails.getContactPhone());
            newEntry.put("Address1", retailsDetails.getAddress1());
            newEntry.put("Address2", retailsDetails.getAddress2());
            newEntry.put("Division", retailsDetails.getDivision());
            newEntry.put("Block", retailsDetails.getBlock());
            newEntry.put("City", retailsDetails.getCity());
            newEntry.put("District", retailsDetails.getDistrict());
            newEntry.put("StateId", retailsDetails.getStateId());
            newEntry.put("PinCode", retailsDetails.getPinCode());
            newEntry.put("Latitude", retailsDetails.getLatitude());
            newEntry.put("Longitude", retailsDetails.getLongitude());
            newEntry.put("Image1", retailsDetails.getImage1());
            newEntry.put("ImgExt1", retailsDetails.getImgExt1());
            newEntry.put("ImgDefinitionId1", retailsDetails.getImgDefinitionId1());

            newEntry.put("IsKeepITCProducts", retailsDetails.getIsKeepITCProducts());
            newEntry.put("ITCProductNames", retailsDetails.getItcProductNames());
            newEntry.put("HandwashPouchesSold", retailsDetails.getHandWashPouchesSold());

            // BELOW FIELDS DO NOT EXIST IN YOUR JSON – set default 0 or empty
            newEntry.put("BrandingInterested", retailsDetails.getBrandingInterested());
            newEntry.put("ShopPainting", retailsDetails.getShopPainting());
            newEntry.put("DealerBoard", retailsDetails.getDealerBoard());
            newEntry.put("Poster", retailsDetails.getPoster());
            newEntry.put("Bunting",retailsDetails.getBunting());
            newEntry.put("ItcProductNames", retailsDetails.getItcProductNames());

            newEntry.put("SavlonSoapSold", retailsDetails.getSavlonSoapSold());
            newEntry.put("FmcgPurchaseFrom", retailsDetails.getFmcgpurchaseFrom());
            newEntry.put("DistributorDetails", retailsDetails.getDistributorDetails());
            newEntry.put("MarketDetails", retailsDetails.getMarketDetails());
            newEntry.put("CommunicationGUID",retailsDetails.getCommunicationGuid());
            newEntry.put("CommunicationAttempt",retailsDetails.getCommunicationAttempt());
            newEntry.put("CommunicationStatus",retailsDetails.getCommunicationStatus());


            long retVal = myDataBase.insertOrThrow("sp_retailersdetails", null, newEntry);

            if (retVal > 0) {
                CommunicationSend commSend = retailsDetails.createCommSend();

                if (saveCommunicationSend(commSend) > 0) {
                    dataSaved = true;
                }
            }

        } catch (SQLException ex) {

            Log.e(TAG, "SAVE ERROR in sp_retailersdetails → " + ex.getMessage());
            ex.printStackTrace();  // full details in Logcat

            // Optional — show toast or alert in UI for testing
            // Toast.makeText(context, "DB Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();

            return false;
        } catch (Exception e) {

            Log.e(TAG, "Unknown Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return dataSaved;
    }


    public Cursor getTodayVenueDetails() {
        String  today = Common.yyyymmddFormat.format(new Date());
        String sql = "SELECT * FROM sp_venue"
                + " WHERE DATE(ScheduledDateTime) BETWEEN DATE('" + today + " 00:00:00') AND DATE('" + today + " 23:59:59')";
        return QueryDatabase(sql);
    }

    public Cursor getComboProduct() {
        String  today = Common.yyyymmddFormat.format(new Date());
        String sql = "SELECT * FROM sp_comboproduct";
        return QueryDatabase(sql);
    }

    public Cursor getRetailsDetails(String rGuid) {
        String sql = "SELECT * FROM sp_retailersdetails WHERE RetailOutReachGUID = '" + rGuid + "'";
        return QueryDatabase(sql);
    }

    public Cursor getSchoolDetails() {
        String sql = "SELECT * FROM sp_school";
        return QueryDatabase(sql);
    }

    public Cursor getSchoolData() {
        String sql = "SELECT * FROM sp_school";
        return QueryDatabase(sql);
    }


}
