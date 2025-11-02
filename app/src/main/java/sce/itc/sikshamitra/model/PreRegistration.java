package sce.itc.sikshamitra.model;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;

public class PreRegistration {
    private int _id;

    private String FirstName;
    private String LastName;
    private String Mobile;
    private String Username;
    private String Password;
    private String SMGUID;
    private String VenueGUID;
    private String UserGUID;
    private String CreatedOn;
    private double Latitude;
    private double Longitude;
    private String CommunicationGUID;

    // --- Empty Constructor ---
    public PreRegistration() {
    }

    // --- Parameterized Constructor ---
    public PreRegistration(String firstName, String lastName, String mobile,
                           String username, String password, String SMGUID,
                           String venueGUID, String userGUID,
                           String createdOn, double latitude, double longitude,String communicationGUID) {
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Mobile = mobile;
        this.Username = username;
        this.Password = password;
        this.SMGUID = SMGUID;
        this.VenueGUID = venueGUID;
        this.UserGUID = userGUID;
        this.CreatedOn = createdOn;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.CommunicationGUID = communicationGUID;
    }

    // --- Getters and Setters ---
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getVenueGUID() {
        return VenueGUID;
    }

    public void setVenueGUID(String venueGUID) {
        VenueGUID = venueGUID;
    }

    public String getUserGUID() {
        return UserGUID;
    }

    public void setUserGUID(String userGUID) {
        UserGUID = userGUID;
    }

    public String getSMGUID() {
        return SMGUID;
    }

    public void setSMGUID(String SMGUID) {
        this.SMGUID = SMGUID;
    }
    public String getCreatedOn() {
        return CreatedOn;
    }
    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }
    public double getLatitude() {
        return Latitude;
    }
    public void setLatitude(double latitude) {
        Latitude = latitude;
    }
    public double getLongitude() {
        return Longitude;
    }
    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getCommunicationGUID() {
        return CommunicationGUID;
    }
    public void setCommunicationGUID(String communicationGUID) {
        CommunicationGUID = communicationGUID;
    }

    // --- populateFromCursor method ---
    public void populateFromCursor(Cursor cursorPreReg) {
        int idCol = cursorPreReg.getColumnIndex("_id");
        int firstNameCol = cursorPreReg.getColumnIndex("FirstName");
        int lastNameCol = cursorPreReg.getColumnIndex("LastName");
        int mobileCol = cursorPreReg.getColumnIndex("Mobile");
        int usernameCol = cursorPreReg.getColumnIndex("Username");
        int passwordCol = cursorPreReg.getColumnIndex("Password");
        int shikshaMitraGUIDCol = cursorPreReg.getColumnIndex("ShikshaMitraGUID");
        int venueGUIDCol = cursorPreReg.getColumnIndex("VenueGUID");
        int userGUIDCol = cursorPreReg.getColumnIndex("UserGUID");
        int createdOnCol = cursorPreReg.getColumnIndex("CreatedOn");
        int latitudeCol = cursorPreReg.getColumnIndex("Latitude");
        int longitudeCol = cursorPreReg.getColumnIndex("Longitude");
        int communicationGUIDCol = cursorPreReg.getColumnIndex("CommunicationGUID");

        this.set_id(cursorPreReg.getInt(idCol));
        this.setFirstName(cursorPreReg.getString(firstNameCol));
        this.setLastName(cursorPreReg.getString(lastNameCol));
        this.setMobile(cursorPreReg.getString(mobileCol));
        this.setUsername(cursorPreReg.getString(usernameCol));
        this.setPassword(cursorPreReg.getString(passwordCol));
        this.setSMGUID(cursorPreReg.getString(shikshaMitraGUIDCol));
        this.setVenueGUID(cursorPreReg.getString(venueGUIDCol));
        this.setUserGUID(cursorPreReg.getString(userGUIDCol));
        this.setCreatedOn(cursorPreReg.getString(createdOnCol));
        this.setLatitude(cursorPreReg.getDouble(latitudeCol));
        this.setLongitude(cursorPreReg.getDouble(longitudeCol));
        this.setCommunicationGUID(cursorPreReg.getString(communicationGUIDCol));
    }

    public CommunicationSend createCommSend() {
        CommunicationSend commSend = new CommunicationSend();
        commSend.setProcessedOn(Common.iso8601Format.format(new Date()));
        commSend.setprocessDetails("");
        commSend.setProcessCount(0);
        commSend.setCommand(Command.ADD_TEACHER);
        commSend.setCommandDate(Common.iso8601Format.format(new Date()));
        commSend.setCommunicationGUID(this.CommunicationGUID);
        commSend.setCommunicationStatusID(1);
        commSend.setCreatedByID(4);

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

        //Convert models to json object
        String attendanceJson = gson.toJson(this);

        commSend.setCommandDetails(attendanceJson);
        return commSend;
    }

    //create attendance from json
    public static Venue fromJson(String json) {

        Gson gson = new Gson();
        Venue attendance = gson.fromJson(json, Venue.class);

        return attendance;
    }

    public String getJson() {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

        //Convert models to json object
        String venueJson = gson.toJson(this);

        return venueJson;
    }
}
