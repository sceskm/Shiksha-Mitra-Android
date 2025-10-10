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

import sce.itc.sikshamitra.helper.ConstantField;
import sce.itc.sikshamitra.model.PreRegistration;
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




}
