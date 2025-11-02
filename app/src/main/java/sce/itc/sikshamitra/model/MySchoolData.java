package sce.itc.sikshamitra.model;

import android.database.Cursor;

public class MySchoolData {
    private int schoolId;
    private String schoolGuid;
    private String schoolName;
    private String udiseCode;
    private String phone;
    private String email;
    private String district;
    private String districtCode;
    private String blockCode;
    private String blockName;
    private String pinCode;
    private int stateId;
    private String address1;
    private String address2;
    private String city;
    private double latitude;
    private double longitude;
    private String state;



    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolGuid() {
        return schoolGuid;
    }

    public void setSchoolGuid(String schoolGuid) {
        this.schoolGuid = schoolGuid;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getUdiseCode() {
        return udiseCode;
    }

    public void setUdiseCode(String udiseCode) {
        this.udiseCode = udiseCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public void populateFromCursor(Cursor cursor) {
        int schoolIdCol = cursor.getColumnIndex("SchoolId");
        int firstNameCol = cursor.getColumnIndex("FirstName");
        int lastNameCol = cursor.getColumnIndex("LastName");
        int mobileCol = cursor.getColumnIndex("Mobile");
        int usernameCol = cursor.getColumnIndex("Username");
        int userGUIDCol = cursor.getColumnIndex("UserGUID");
        int venueGUIDCol = cursor.getColumnIndex("VenueGUID");
        int udisecodeCol = cursor.getColumnIndex("UDISECode");
        int associateSchoolCol = cursor.getColumnIndex("AssociateSchool");
        int schoolGUIDCol = cursor.getColumnIndex("SchoolGUID");
        int address1Col = cursor.getColumnIndex("SchoolAddress1");
        int address2Col = cursor.getColumnIndex("SchoolAddress2");
        int cityCol = cursor.getColumnIndex("City");
        int localityCol = cursor.getColumnIndex("Locality");
        int districtCol = cursor.getColumnIndex("District");
        int districtCodeCol = cursor.getColumnIndex("DistrictCode");
        int blockCol = cursor.getColumnIndex("Block");
        int blockCodeCol = cursor.getColumnIndex("BlockCode");
        int stateCol = cursor.getColumnIndex("State");
        int stateIdCol = cursor.getColumnIndex("StateId");
        int pinCodeCol = cursor.getColumnIndex("PinCode");
        int emailCol = cursor.getColumnIndex("Email");
        int contactNameCol = cursor.getColumnIndex("ContactName");
        int contactNumberCol = cursor.getColumnIndex("ContactNumber");

        this.setSchoolId(cursor.getInt(schoolIdCol));
//        this.setFirstName(cursor.getString(firstNameCol));
//        this.setLastName(cursor.getString(lastNameCol));
//        this.setMobile(cursor.getString(mobileCol));
//        this.setUsername(cursor.getString(usernameCol));
//        this.setUserGUID(cursor.getString(userGUIDCol));
//        this.setVenueGUID(cursor.getString(venueGUIDCol));
        this.setUdiseCode(cursor.getString(udisecodeCol));
        this.setSchoolName(cursor.getString(associateSchoolCol));
        this.setSchoolGuid(cursor.getString(schoolGUIDCol));
        this.setAddress1(cursor.getString(address1Col));
        this.setAddress2(cursor.getString(address2Col));
        this.setCity(cursor.getString(cityCol));
        //this.setLocality(cursor.getString(localityCol));
        this.setDistrict(cursor.getString(districtCol));
        this.setDistrictCode(cursor.getString(districtCodeCol));
        this.setBlockName(cursor.getString(blockCol));
        this.setBlockCode(cursor.getString(blockCodeCol));
        this.setState(cursor.getString(stateCol));
        this.setStateId(cursor.getInt(stateIdCol));
        this.setPinCode(cursor.getString(pinCodeCol));
        this.setEmail(cursor.getString(emailCol));
        //this.setContactName(cursor.getString(contactNameCol));
        //this.setContactNumber(cursor.getString(contactNumberCol));
    }


}
