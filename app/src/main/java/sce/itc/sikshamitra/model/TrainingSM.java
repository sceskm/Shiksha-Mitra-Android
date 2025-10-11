package sce.itc.sikshamitra.model;

import android.database.Cursor;

public class TrainingSM {
    private int _id;
    private String VenueName;
    private String ScheduledDateTime;
    private Double Latitude;
    private Double Longitude;
    private Integer SMCount;
    private String Image1;
    private String Image2;
    private String Image3;
    private String Image4;
    private String Remarks;

    public TrainingSM() {
    }

    // --- Parameterized Constructor ---
    public TrainingSM(String venueName, String scheduledDateTime, Double latitude, Double longitude,
                    Integer smCount, String image1, String image2, String image3,
                    String image4, String remarks) {
        this.VenueName = venueName;
        this.ScheduledDateTime = scheduledDateTime;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.SMCount = smCount;
        this.Image1 = image1;
        this.Image2 = image2;
        this.Image3 = image3;
        this.Image4 = image4;
        this.Remarks = remarks;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public Integer getSMCount() {
        return SMCount;
    }

    public void setSMCount(Integer SMCount) {
        this.SMCount = SMCount;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getImage3() {
        return Image3;
    }

    public void setImage3(String image3) {
        Image3 = image3;
    }

    public String getImage4() {
        return Image4;
    }

    public void setImage4(String image4) {
        Image4 = image4;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public void populateFromCursor(Cursor cursorTraining) {
        int idCol = cursorTraining.getColumnIndex("_id");
        int venueNameCol = cursorTraining.getColumnIndex("VenueName");
        int scheduledDateTimeCol = cursorTraining.getColumnIndex("ScheduledDateTime");
        int latitudeCol = cursorTraining.getColumnIndex("Latitude");
        int longitudeCol = cursorTraining.getColumnIndex("Longitude");
        int smCountCol = cursorTraining.getColumnIndex("SMCount");
        int image1Col = cursorTraining.getColumnIndex("Image1");
        int image2Col = cursorTraining.getColumnIndex("Image2");
        int image3Col = cursorTraining.getColumnIndex("Image3");
        int image4Col = cursorTraining.getColumnIndex("Image4");
        int remarksCol = cursorTraining.getColumnIndex("Remarks");

        this.set_id(cursorTraining.getInt(idCol));
        this.setVenueName(cursorTraining.getString(venueNameCol));
        this.setScheduledDateTime(cursorTraining.getString(scheduledDateTimeCol));
        this.setLatitude(cursorTraining.getDouble(latitudeCol));
        this.setLongitude(cursorTraining.getDouble(longitudeCol));
        this.setSMCount(cursorTraining.getInt(smCountCol));
        this.setImage1(cursorTraining.getString(image1Col));
        this.setImage2(cursorTraining.getString(image2Col));
        this.setImage3(cursorTraining.getString(image3Col));
        this.setImage4(cursorTraining.getString(image4Col));
        this.setRemarks(cursorTraining.getString(remarksCol));
    }
}
