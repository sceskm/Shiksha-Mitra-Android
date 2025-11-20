package sce.itc.sikshamitra.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrainingSM {

    @Expose
    private String trainingGuid;
    @Expose
    private String userGuid;
    @Expose (serialize = false)
    private String venueName;
    @Expose
    @SerializedName("VenueGuid")
    private String venueGuid;
    @Expose
    @SerializedName("OrganizationId")
    private int organizationId;
    @Expose
    @SerializedName("NoOfTeachers")
    private int noOfTeachers;
    @Expose
    @SerializedName("EndedOn")
    private String endedOn;
    @Expose
    @SerializedName("StartedOn")
    private String startedOn;

    @Expose
    @SerializedName("latitude")
    private Double latitude;
    @Expose
    @SerializedName("longitude")
    private Double longitude;

    @Expose
    @SerializedName("remarks")
    private String remarks;

    @Expose
    @SerializedName("inActive")
    private boolean inActive;

    @Expose
    @SerializedName("images")
    private List<Image> images;

    @Expose(serialize = false)
    private int imageDefinitionId1;
    @Expose(serialize = false)
    private String imageFile1;
    @Expose(serialize = false)
    private String imageExt1;

    @Expose(serialize = false)
    private int imageDefinitionId2;
    @Expose(serialize = false)
    private String imageFile2;
    @Expose(serialize = false)
    private String imageExt2;

    @Expose(serialize = false)
    private int imageDefinitionId3;
    @Expose(serialize = false)
    private String imageFile3;
    @Expose(serialize = false)
    private String imageExt3;

    @Expose(serialize = false)
    private int imageDefinitionId4;
    @Expose(serialize = false)
    private String imageFile4;
    @Expose(serialize = false)
    private String imageExt4;


    public TrainingSM() {
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

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueGuid() {
        return venueGuid;
    }

    public void setVenueGuid(String venueGuid) {
        this.venueGuid = venueGuid;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public int getNoOfTeachers() {
        return noOfTeachers;
    }

    public void setNoOfTeachers(int noOfTeachers) {
        this.noOfTeachers = noOfTeachers;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public int getImageDefinitionId1() {
        return imageDefinitionId1;
    }

    public void setImageDefinitionId1(int imageDefinitionId1) {
        this.imageDefinitionId1 = imageDefinitionId1;
    }

    public String getImageFile1() {
        return imageFile1;
    }

    public void setImageFile1(String imageFile1) {
        this.imageFile1 = imageFile1;
    }

    public String getImageExt1() {
        return imageExt1;
    }

    public void setImageExt1(String imageExt1) {
        this.imageExt1 = imageExt1;
    }

    public int getImageDefinitionId2() {
        return imageDefinitionId2;
    }

    public void setImageDefinitionId2(int imageDefinitionId2) {
        this.imageDefinitionId2 = imageDefinitionId2;
    }

    public String getImageFile2() {
        return imageFile2;
    }

    public void setImageFile2(String imageFile2) {
        this.imageFile2 = imageFile2;
    }

    public String getImageExt2() {
        return imageExt2;
    }

    public void setImageExt2(String imageExt2) {
        this.imageExt2 = imageExt2;
    }

    public int getImageDefinitionId3() {
        return imageDefinitionId3;
    }

    public void setImageDefinitionId3(int imageDefinitionId3) {
        this.imageDefinitionId3 = imageDefinitionId3;
    }

    public String getImageFile3() {
        return imageFile3;
    }

    public void setImageFile3(String imageFile3) {
        this.imageFile3 = imageFile3;
    }

    public String getImageExt3() {
        return imageExt3;
    }

    public void setImageExt3(String imageExt3) {
        this.imageExt3 = imageExt3;
    }

    public int getImageDefinitionId4() {
        return imageDefinitionId4;
    }

    public void setImageDefinitionId4(int imageDefinitionId4) {
        this.imageDefinitionId4 = imageDefinitionId4;
    }

    public String getImageFile4() {
        return imageFile4;
    }

    public void setImageFile4(String imageFile4) {
        this.imageFile4 = imageFile4;
    }

    public String getImageExt4() {
        return imageExt4;
    }

    public void setImageExt4(String imageExt4) {
        this.imageExt4 = imageExt4;
    }

    public String getJson() {

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

        //Convert models to json object
        String trainingJson = gson.toJson(this);

        return trainingJson;
    }
}
