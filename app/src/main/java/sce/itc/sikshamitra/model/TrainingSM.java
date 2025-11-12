package sce.itc.sikshamitra.model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrainingSM {
    @Expose(serialize = false)
    private int _id;
    @Expose(serialize = false)
    private String VenueName;
    @Expose(serialize = false)
    private String ScheduledDateTime;
    @Expose
    @SerializedName("latitude")
    private Double latitude;
    @Expose
    @SerializedName("longitude")
    private Double longitude;
    @Expose(serialize = false)
    private Integer SMCount;
    @Expose(serialize = false)
    private String Image1;
    @Expose(serialize = false)
    private String Image2;
    @Expose(serialize = false)
    private String Image3;
    @Expose(serialize = false)
    private String Image4;
    @Expose
    @SerializedName("remarks")
    private String remarks;
    @Expose
    @SerializedName("EndedOn")
    private String endedOn;
    @Expose
    @SerializedName("StartedOn")
    private String startedOn;
    @Expose
    @SerializedName("NoOfTeachers")
    private int noOfTeachers;
    @Expose
    @SerializedName("OrganizationId")
    private int organizationId;
    @Expose
    @SerializedName("VenueGuid")
    private String venueGuid;
    @Expose
    private String trainingGuid;
    @Expose
    private String userGuid;
    @Expose
    @SerializedName("inActive")
    private boolean inActive;
    private List<Image> images;

    @Expose(serialize = false)
    private int imageDefinitionId;
    @Expose(serialize = false)
    private String imageFile;
    @Expose(serialize = false)
    private String imageExt;

    /*
    * Default constructor
    * */
    public TrainingSM() {}

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
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getEndedOn() {
        return endedOn;
    }

    public void setEndedOn(String endedOn) {
        this.endedOn = endedOn;
    }

    public String getStartedOn() {
        return startedOn;
    }

    public void setStartedOn(String startedOn) {
        this.startedOn = startedOn;
    }

    public int getNoOfTeachers() {
        return noOfTeachers;
    }

    public void setNoOfTeachers(int noOfTeachers) {
        this.noOfTeachers = noOfTeachers;
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

    public String getTrainingGuid() {
        return trainingGuid;
    }

    public void setTrainingGuid(String trainingGuid) {
        this.trainingGuid = trainingGuid;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public boolean isInActive() {
        return inActive;
    }

    public void setInActive(boolean inActive) {
        this.inActive = inActive;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public int getImageDefinitionId() {
        return imageDefinitionId;
    }

    public void setImageDefinitionId(int imageDefinitionId) {
        this.imageDefinitionId = imageDefinitionId;
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
    /*
    * Constructor with parameters
    * */


    /*
    * Populate data from cursor
    * */

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
