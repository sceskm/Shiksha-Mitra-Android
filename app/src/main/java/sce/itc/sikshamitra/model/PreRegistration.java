package sce.itc.sikshamitra.model;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;

public class PreRegistration {
    @Expose(serialize = false)
    private int _id;
    @Expose
    @SerializedName("firstName")
    private String firstName;
    @Expose
    @SerializedName("lastName")
    private String lastName;
    @Expose
    @SerializedName("phone")
    private String phone;
    @Expose (serialize = false)
    private String userName;
    @Expose
    @SerializedName("password")
    private String password;
    @Expose (serialize = false)
    private String smGuid;
    @Expose
    @SerializedName("venueGUID")
    private String venueGuid;
    @Expose
    @SerializedName("userGUID")
    private String userGuid;
    @Expose(serialize = false)
    private String createdOn;
    @Expose(serialize = false)
    private double latitude;
    @Expose(serialize = false)
    private double longitude;
    @Expose(serialize = false)
    private String communicationGuid;
    @Expose
    @SerializedName("email")
    private String email;
    @Expose
    @SerializedName("city")
    private String city;
    @Expose
    @SerializedName("udiseCoode")
    private String udiseCode;
    // --- Empty Constructor ---
    public PreRegistration() {
    }

    // --- Parameterized Constructor ---
    public PreRegistration(String firstName, String lastName, String phone,
                           String userName, String password, String smGuid,
                           String venueGuid, String userGuid,
                           String createdOn, double latitude, double longitude, String communicationGuid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.userName = userName;
        this.password = password;
        this.smGuid = smGuid;
        this.venueGuid = venueGuid;
        this.userGuid = userGuid;
        this.createdOn = createdOn;
        this.latitude = latitude;
        this.longitude = longitude;
        this.communicationGuid = communicationGuid;
    }

    // --- Getters and Setters ---
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVenueGuid() {
        return venueGuid;
    }

    public void setVenueGuid(String venueGuid) {
        this.venueGuid = venueGuid;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public String getSmGuid() {
        return smGuid;
    }

    public void setSmGuid(String smGuid) {
        this.smGuid = smGuid;
    }
    public String getCreatedOn() {
        return createdOn;
    }
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCommunicationGuid() {
        return communicationGuid;
    }
    public void setCommunicationGuid(String communicationGuid) {
        this.communicationGuid = communicationGuid;
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
        this.setPhone(cursorPreReg.getString(mobileCol));
        this.setUserName(cursorPreReg.getString(usernameCol));
        this.setPassword(cursorPreReg.getString(passwordCol));
        this.setSmGuid(cursorPreReg.getString(shikshaMitraGUIDCol));
        this.setVenueGuid(cursorPreReg.getString(venueGUIDCol));
        this.setUserGuid(cursorPreReg.getString(userGUIDCol));
        this.setCreatedOn(cursorPreReg.getString(createdOnCol));
        this.setLatitude(cursorPreReg.getDouble(latitudeCol));
        this.setLongitude(cursorPreReg.getDouble(longitudeCol));
        this.setCommunicationGuid(cursorPreReg.getString(communicationGUIDCol));
    }

    public CommunicationSend createCommSend() {
        CommunicationSend commSend = new CommunicationSend();
        commSend.setProcessedOn(Common.iso8601Format.format(new Date()));
        commSend.setprocessDetails("");
        commSend.setProcessCount(0);
        commSend.setCommand(Command.ADD_TEACHER);
        commSend.setCommandDate(Common.iso8601Format.format(new Date()));
        commSend.setCommunicationGUID(this.communicationGuid);
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
