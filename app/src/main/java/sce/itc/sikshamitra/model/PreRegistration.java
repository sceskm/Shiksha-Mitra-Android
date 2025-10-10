package sce.itc.sikshamitra.model;

import android.database.Cursor;

public class PreRegistration {
    private int _id;

    private String FirstName;
    private String LastName;
    private String Mobile;
    private String Username;
    private String Password;
    private String ShikshaMitraGUID;
    private String VenueGUID;
    private String UserGUID;
    private String CreatedOn;
    private double Latitude;
    private double Longitude;

    // --- Empty Constructor ---
    public PreRegistration() {
    }

    // --- Parameterized Constructor ---
    public PreRegistration(String firstName, String lastName, String mobile,
                           String username, String password, String shikshaMitraGUID,
                           String venueGUID, String userGUID,
                           String createdOn, double latitude, double longitude) {
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Mobile = mobile;
        this.Username = username;
        this.Password = password;
        this.ShikshaMitraGUID = shikshaMitraGUID;
        this.VenueGUID = venueGUID;
        this.UserGUID = userGUID;
        this.CreatedOn = createdOn;
        this.Latitude = latitude;
        this.Longitude = longitude;
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

    public String getShikshaMitraGUID() {
        return ShikshaMitraGUID;
    }

    public void setShikshaMitraGUID(String shikshaMitraGUID) {
        ShikshaMitraGUID = shikshaMitraGUID;
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

        this.set_id(cursorPreReg.getInt(idCol));
        this.setFirstName(cursorPreReg.getString(firstNameCol));
        this.setLastName(cursorPreReg.getString(lastNameCol));
        this.setMobile(cursorPreReg.getString(mobileCol));
        this.setUsername(cursorPreReg.getString(usernameCol));
        this.setPassword(cursorPreReg.getString(passwordCol));
        this.setShikshaMitraGUID(cursorPreReg.getString(shikshaMitraGUIDCol));
        this.setVenueGUID(cursorPreReg.getString(venueGUIDCol));
        this.setUserGUID(cursorPreReg.getString(userGUIDCol));
        this.setCreatedOn(cursorPreReg.getString(createdOnCol));
        this.setLatitude(cursorPreReg.getDouble(latitudeCol));
        this.setLongitude(cursorPreReg.getDouble(longitudeCol));
    }
}
