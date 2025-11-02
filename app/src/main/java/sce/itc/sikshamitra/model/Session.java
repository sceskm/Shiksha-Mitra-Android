package sce.itc.sikshamitra.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.Date;
import java.util.List;

import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.PreferenceCommon;

public class Session {
    @Expose(serialize = false)
    private int sessionId;
    @Expose
    private String sessionGuid;
    @Expose
    private String userGuid;
    @Expose
    private int sessionNo;
    @Expose
    private String schoolGuid;
    @Expose
    private int noOfStudent;

    @Expose(serialize = false)
    private String img1;
    @Expose(serialize = false)
    private String img2;
    @Expose(serialize = false)
    private String img3;
    @Expose(serialize = false)
    private String img4;

    @Expose
    private String sessionStart;
    @Expose
    private String sessionEnd;
    @Expose
    private String remarks;

    @Expose(serialize = false)
    private int sessionStatus;
    @Expose(serialize = false)
    private int communicationStatus;
    @Expose(serialize = false)
    private int communicationAttempt;

    @Expose(serialize = false)
    private String communicationGuid;
    @Expose
    private Double latitude;
    @Expose
    private Double longitude;
    @Expose(serialize = false)
    private int imgDefinitionId1;
    @Expose(serialize = false)
    private int imgDefinitionId2;
    @Expose(serialize = false)
    private int imgDefinitionId3;
    @Expose(serialize = false)
    private int imgDefinitionId4;

    @Expose(serialize = false)
    private String imgExt1;
    @Expose(serialize = false)
    private String imgExt2;
    @Expose(serialize = false)
    private String imgExt3;
    @Expose(serialize = false)
    private String imgExt4;

    @Expose
    private List<Image> images;

    public Session() {
    }

    public CommunicationSend createCommSend() {
        CommunicationSend commSend = new CommunicationSend();
        commSend.setProcessedOn(Common.iso8601Format.format(new Date()));
        commSend.setprocessDetails("");
        commSend.setProcessCount(0);
        commSend.setCommand(Command.TEACHER_SESSION);
        commSend.setCommandDate(Common.iso8601Format.format(new Date()));
        commSend.setCommunicationGUID(this.communicationGuid);
        commSend.setCommunicationStatusID(1);
        commSend.setCreatedByID(PreferenceCommon.getInstance().getUserId());

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
    public static Session fromJson(String json) {

        Gson gson = new Gson();
        Session attendance = gson.fromJson(json, Session.class);

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

    /*
    * Setter Getter
    * */

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionGuid() {
        return sessionGuid;
    }

    public void setSessionGuid(String sessionGuid) {
        this.sessionGuid = sessionGuid;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public int getSessionNo() {
        return sessionNo;
    }

    public void setSessionNo(int sessionNo) {
        this.sessionNo = sessionNo;
    }

    public String getSchoolGuid() {
        return schoolGuid;
    }

    public void setSchoolGuid(String schoolGuid) {
        this.schoolGuid = schoolGuid;
    }

    public int getNoOfStudent() {
        return noOfStudent;
    }

    public void setNoOfStudent(int noOfStudent) {
        this.noOfStudent = noOfStudent;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(String sessionStart) {
        this.sessionStart = sessionStart;
    }

    public String getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(String sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(int sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public int getCommunicationStatus() {
        return communicationStatus;
    }

    public void setCommunicationStatus(int communicationStatus) {
        this.communicationStatus = communicationStatus;
    }

    public int getCommunicationAttempt() {
        return communicationAttempt;
    }

    public void setCommunicationAttempt(int communicationAttempt) {
        this.communicationAttempt = communicationAttempt;
    }

    public String getCommunicationGuid() {
        return communicationGuid;
    }

    public void setCommunicationGuid(String communicationGuid) {
        this.communicationGuid = communicationGuid;
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

    public int getImgDefinitionId1() {
        return imgDefinitionId1;
    }

    public void setImgDefinitionId1(int imgDefinitionId1) {
        this.imgDefinitionId1 = imgDefinitionId1;
    }

    public int getImgDefinitionId2() {
        return imgDefinitionId2;
    }

    public void setImgDefinitionId2(int imgDefinitionId2) {
        this.imgDefinitionId2 = imgDefinitionId2;
    }

    public int getImgDefinitionId3() {
        return imgDefinitionId3;
    }

    public void setImgDefinitionId3(int imgDefinitionId3) {
        this.imgDefinitionId3 = imgDefinitionId3;
    }

    public int getImgDefinitionId4() {
        return imgDefinitionId4;
    }

    public void setImgDefinitionId4(int imgDefinitionId4) {
        this.imgDefinitionId4 = imgDefinitionId4;
    }

    public String getImgExt1() {
        return imgExt1;
    }

    public void setImgExt1(String imgExt1) {
        this.imgExt1 = imgExt1;
    }

    public String getImgExt2() {
        return imgExt2;
    }

    public void setImgExt2(String imgExt2) {
        this.imgExt2 = imgExt2;
    }

    public String getImgExt3() {
        return imgExt3;
    }

    public void setImgExt3(String imgExt3) {
        this.imgExt3 = imgExt3;
    }

    public String getImgExt4() {
        return imgExt4;
    }

    public void setImgExt4(String imgExt4) {
        this.imgExt4 = imgExt4;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
