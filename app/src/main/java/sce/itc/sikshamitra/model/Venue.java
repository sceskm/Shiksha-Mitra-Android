package sce.itc.sikshamitra.model;

import android.database.Cursor;

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

    public Venue() {
    }

    public Venue(String venueName, String scheduledDateTime, String address1, String address2, String city, String district, String state, String pin, String venueImage, String venueGUID, Double latitude, Double longitude) {
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
}
