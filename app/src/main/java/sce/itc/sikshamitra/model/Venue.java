package sce.itc.sikshamitra.model;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;

public class Venue {
    private String VenueName;
    private String ScheduledDateTime;
    private String Address1;
    private String Address2;
    private String City;
    private String District;
    private String State;
    private String Pin;
    private String VenueImage;
    private String VenueGUID;
    private Double Latitude;
    private Double Longitude;
    private String CommunicationGUID;

    public Venue() {
    }

    public Venue(String venueName, String scheduledDateTime, String address1, String address2, String city, String district, String state, String pin, String venueImage, String venueGUID, Double latitude, Double longitude,String communicationGUID) {
        VenueName = venueName;
        ScheduledDateTime = scheduledDateTime;
        Address1 = address1;
        Address2 = address2;
        City = city;
        District = district;
        State = state;
        Pin = pin;
        VenueImage = venueImage;
        VenueGUID = venueGUID;
        Latitude = latitude;
        Longitude = longitude;
        CommunicationGUID = communicationGUID;
    }

    public void populateFromCursor(Cursor cursorVenue) {
        int venueNameCol = cursorVenue.getColumnIndex("VenueName");
        int scheduledDateTimeCol = cursorVenue.getColumnIndex("ScheduledDateTime");
        int address1Col = cursorVenue.getColumnIndex("Address1");
        int address2Col = cursorVenue.getColumnIndex("Address2");
        int cityCol = cursorVenue.getColumnIndex("City");
        int districtCol = cursorVenue.getColumnIndex("District");
        int stateCol = cursorVenue.getColumnIndex("State");
        int pinCol = cursorVenue.getColumnIndex("Pin");
        int venueImageCol = cursorVenue.getColumnIndex("VenueImage");
        int venueGUIDCol = cursorVenue.getColumnIndex("VenueGUID");
        int latitudeCol = cursorVenue.getColumnIndex("Latitude");
        int longitudeCol = cursorVenue.getColumnIndex("Longitude");
        int communicationGUIDCol = cursorVenue.getColumnIndex("CommunicationGUID");

        this.setVenueName(cursorVenue.getString(venueNameCol));
        this.setScheduledDateTime(cursorVenue.getString(scheduledDateTimeCol));
        this.setAddress1(cursorVenue.getString(address1Col));
        this.setAddress2(cursorVenue.getString(address2Col));
        this.setCity(cursorVenue.getString(cityCol));
        this.setDistrict(cursorVenue.getString(districtCol));
        this.setState(cursorVenue.getString(stateCol));
        this.setPin(cursorVenue.getString(pinCol));
        this.setVenueImage(cursorVenue.getString(venueImageCol));
        this.setVenueGUID(cursorVenue.getString(venueGUIDCol));
        this.setLatitude(cursorVenue.getDouble(latitudeCol));
        this.setLongitude(cursorVenue.getDouble(longitudeCol));
        this.setCommunicationGUID(cursorVenue.getString(communicationGUIDCol));
    }

    public String getVenueName() {
        return VenueName;
    }

    public void setVenueName(String venueName) {
        VenueName = venueName;
    }

    public String getScheduledDateTime() {
        return ScheduledDateTime;
    }

    public void setScheduledDateTime(String scheduledDateTime) {
        ScheduledDateTime = scheduledDateTime;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
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

    public String getVenueImage() {
        return VenueImage;
    }

    public void setVenueImage(String venueImage) {
        VenueImage = venueImage;
    }

    public String getVenueGUID() {
        return VenueGUID;
    }

    public void setVenueGUID(String venueGUID) {
        VenueGUID = venueGUID;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public String getCommunicationGUID() {
        return CommunicationGUID;
    }

    public void setCommunicationGUID(String communicationGUID) {
        CommunicationGUID = communicationGUID;
    }

    public CommunicationSend createCommSend() {
        CommunicationSend commSend = new CommunicationSend();
        commSend.setProcessedOn(Common.iso8601Format.format(new Date()));
        commSend.setprocessDetails("");
        commSend.setProcessCount(0);
        commSend.setCommand(Command.ADD_VENUE);
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
