package sce.itc.sikshamitra.model;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;

public class Venue {
    @Expose
    private String venueGuid;
    @Expose
    private String venueName;
    @Expose(serialize = false)
    private String scheduledDateTime;
    @Expose
    private String address1;
    @Expose
    private String address2;
    @Expose
    private String city;
    @Expose
    private String district;
    @Expose(serialize = false)
    private String state;
    @Expose
    private int stateId;
    @Expose
    private String pinCode;
    @Expose
    private int userId;
    @Expose
    private int organizationId;
    @Expose
    private Double latitude;
    @Expose
    private Double longitude;
    private String communicationGuid;
    @Expose
    private List<Image> images;

    @Expose(serialize = false)
    private int imageDefinitionId;
    @Expose(serialize = false)
    private String imageFile;
    @Expose(serialize = false)
    private String imageExt;

    public Venue() {
    }

    public Venue(String venueName, String scheduledDateTime, String address1, String address2,
                 String city, String district, String state, int stateId, String pinCode, int userId,
                 int organizationId, String venueGuid, Double latitude,
                 Double longitude, String communicationGuid, List<Image> images) {
        this.venueName = venueName;
        this.scheduledDateTime = scheduledDateTime;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.district = district;
        this.state = state;
        this.stateId = stateId;
        this.pinCode = pinCode;
        this.userId = userId;
        this.organizationId = organizationId;
        this.venueGuid = venueGuid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.communicationGuid = communicationGuid;
        this.images = images;

    }

    public void populateFromCursor(Cursor cursorVenue) {
        int venueNameCol = cursorVenue.getColumnIndex("VenueName");
        int scheduledDateTimeCol = cursorVenue.getColumnIndex("ScheduledDateTime");
        int address1Col = cursorVenue.getColumnIndex("Address1");
        int address2Col = cursorVenue.getColumnIndex("Address2");
        int cityCol = cursorVenue.getColumnIndex("City");
        int districtCol = cursorVenue.getColumnIndex("District");
        int stateCol = cursorVenue.getColumnIndex("State");
        int stateIdCol = cursorVenue.getColumnIndex("StateId");
        int pinCol = cursorVenue.getColumnIndex("Pin");
        int venueImageCol = cursorVenue.getColumnIndex("VenueImage");
        int venueGUIDCol = cursorVenue.getColumnIndex("VenueGUID");
        int latitudeCol = cursorVenue.getColumnIndex("Latitude");
        int longitudeCol = cursorVenue.getColumnIndex("Longitude");
        int communicationGUIDCol = cursorVenue.getColumnIndex("CommunicationGUID");
        int organizationIdCol = cursorVenue.getColumnIndex("organizationId");

        this.setVenueName(cursorVenue.getString(venueNameCol));
        this.setScheduledDateTime(cursorVenue.getString(scheduledDateTimeCol));
        this.setAddress1(cursorVenue.getString(address1Col));
        this.setAddress2(cursorVenue.getString(address2Col));
        this.setCity(cursorVenue.getString(cityCol));
        this.setDistrict(cursorVenue.getString(districtCol));
        this.setState(cursorVenue.getString(stateCol));
        this.setStateId(cursorVenue.getInt(stateIdCol));
        this.setPinCode(cursorVenue.getString(pinCol));
        this.setOrganizationId(cursorVenue.getInt(organizationIdCol));
        //this.setImageName(cursorVenue.getString(venueImageCol));
        this.setVenueGuid(cursorVenue.getString(venueGUIDCol));
        this.setLatitude(cursorVenue.getDouble(latitudeCol));
        this.setLongitude(cursorVenue.getDouble(longitudeCol));
        this.setCommunicationGuid(cursorVenue.getString(communicationGUIDCol));
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public String getImageExt() {
        return imageExt;
    }

    public void setImageExt(String imageExt) {
        this.imageExt = imageExt;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getScheduledDateTime() {
        return scheduledDateTime;
    }

    public void setScheduledDateTime(String scheduledDateTime) {
        this.scheduledDateTime = scheduledDateTime;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }



    public String getVenueGuid() {
        return venueGuid;
    }

    public void setVenueGuid(String venueGuid) {
        this.venueGuid = venueGuid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCommunicationGuid() {
        return communicationGuid;
    }

    public void setCommunicationGuid(String communicationGuid) {
        this.communicationGuid = communicationGuid;
    }

    public int getImageDefinitionId() {
        return imageDefinitionId;
    }

    public void setImageDefinitionId(int imageDefinitionId) {
        this.imageDefinitionId = imageDefinitionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public CommunicationSend createCommSend() {
        CommunicationSend commSend = new CommunicationSend();
        commSend.setProcessedOn(Common.iso8601Format.format(new Date()));
        commSend.setprocessDetails("");
        commSend.setProcessCount(0);
        commSend.setCommand(Command.ADD_VENUE);
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
