package sce.itc.sikshamitra.model;

import android.database.Cursor;

public class SchoolData {
    private int _id;
    private String FirstName;
    private String LastName;
    private String Mobile;
    private String Username;
    private String UserGUID;
    private String VenueGUID;
    private String SchoolName;
    private String SchoolGUID;
    private String SchoolAddress1;
    private String SchoolAddress2;
    private String City;
    private String Locality;
    private String District;
    private String State;
    private String Pin;
    private String Email;
    private String ContactName;
    private String ContactNumber;
    private String CreatedOn;
    private double Latitude;
    private double Longitude;

    public SchoolData() {
    }

    // --- Parameterized Constructor ---
    public SchoolData(int _id, String firstName, String lastName, String mobile, String username, String userGUID, String venueGUID, String schoolName, String schoolGUID, String schoolAddress1, String schoolAddress2, String city, String locality, String district, String state, String pin, String email, String contactName, String contactNumber, String createdOn, double latitude, double longitude) {
        this._id = _id;
        FirstName = firstName;
        LastName = lastName;
        Mobile = mobile;
        Username = username;
        UserGUID = userGUID;
        VenueGUID = venueGUID;
        SchoolName = schoolName;
        SchoolGUID = schoolGUID;
        SchoolAddress1 = schoolAddress1;
        SchoolAddress2 = schoolAddress2;
        City = city;
        Locality = locality;
        District = district;
        State = state;
        Pin = pin;
        Email = email;
        ContactName = contactName;
        ContactNumber = contactNumber;
        CreatedOn = createdOn;
        Latitude = latitude;
        Longitude = longitude;
    }


    public void populateFromCursor(Cursor cursorSchool) {
        int idCol = cursorSchool.getColumnIndex("_id");
        int firstNameCol = cursorSchool.getColumnIndex("FirstName");
        int lastNameCol = cursorSchool.getColumnIndex("LastName");
        int mobileCol = cursorSchool.getColumnIndex("Mobile");
        int usernameCol = cursorSchool.getColumnIndex("Username");
        int userGUIDCol = cursorSchool.getColumnIndex("UserGUID");
        int venueGUIDCol = cursorSchool.getColumnIndex("VenueGUID");
        int assosiateSchoolNameCol = cursorSchool.getColumnIndex("SchoolName");
        int schoolGUIDCol = cursorSchool.getColumnIndex("SchoolGUID");
        int schoolAddress1Col = cursorSchool.getColumnIndex("SchoolAddress1");
        int schoolAddress2Col = cursorSchool.getColumnIndex("SchoolAddress2");
        int cityCol = cursorSchool.getColumnIndex("City");
        int localityCol = cursorSchool.getColumnIndex("Locality");
        int districtCol = cursorSchool.getColumnIndex("District");
        int stateCol = cursorSchool.getColumnIndex("State");
        int pinCol = cursorSchool.getColumnIndex("Pin");
        int emailCol = cursorSchool.getColumnIndex("Email");
        int contactNameCol = cursorSchool.getColumnIndex("ContactName");
        int contactNumberCol = cursorSchool.getColumnIndex("ContactNumber");
        int createdOnCol = cursorSchool.getColumnIndex("CreatedOn");

        this._id = cursorSchool.getInt(idCol);
        this.FirstName = cursorSchool.getString(firstNameCol);
        this.LastName = cursorSchool.getString(lastNameCol);
        this.Mobile = cursorSchool.getString(mobileCol);
        this.Username = cursorSchool.getString(usernameCol);
        this.UserGUID = cursorSchool.getString(userGUIDCol);
        this.VenueGUID = cursorSchool.getString(venueGUIDCol);
        this.SchoolName = cursorSchool.getString(assosiateSchoolNameCol);
        this.SchoolGUID = cursorSchool.getString(schoolGUIDCol);
        this.SchoolAddress1 = cursorSchool.getString(schoolAddress1Col);
        this.SchoolAddress2 = cursorSchool.getString(schoolAddress2Col);
        this.City = cursorSchool.getString(cityCol);
        this.Locality = cursorSchool.getString(localityCol);
        this.District = cursorSchool.getString(districtCol);
        this.State = cursorSchool.getString(stateCol);
        this.Pin = cursorSchool.getString(pinCol);
        this.Email = cursorSchool.getString(emailCol);
        this.ContactName = cursorSchool.getString(contactNameCol);
        this.ContactNumber = cursorSchool.getString(contactNumberCol);
        this.CreatedOn = cursorSchool.getString(createdOnCol);


    }

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

    public String getUserGUID() {
        return UserGUID;
    }

    public void setUserGUID(String userGUID) {
        UserGUID = userGUID;
    }

    public String getVenueGUID() {
        return VenueGUID;
    }

    public void setVenueGUID(String venueGUID) {
        VenueGUID = venueGUID;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getSchoolGUID() {
        return SchoolGUID;
    }

    public void setSchoolGUID(String schoolGUID) {
        SchoolGUID = schoolGUID;
    }

    public String getSchoolAddress1() {
        return SchoolAddress1;
    }

    public void setSchoolAddress1(String schoolAddress1) {
        SchoolAddress1 = schoolAddress1;
    }

    public String getSchoolAddress2() {
        return SchoolAddress2;
    }

    public void setSchoolAddress2(String schoolAddress2) {
        SchoolAddress2 = schoolAddress2;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getLocality() {
        return Locality;
    }

    public void setLocality(String locality) {
        Locality = locality;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPin() {
        return Pin;
    }

    public void setPin(String pin) {
        Pin = pin;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
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
}
