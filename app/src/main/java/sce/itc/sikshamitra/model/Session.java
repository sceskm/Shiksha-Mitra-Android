package sce.itc.sikshamitra.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import sce.itc.sikshamitra.helper.ApiExclusionStrategy;
import sce.itc.sikshamitra.helper.ApiGroup;
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
    @Expose(serialize = false)
    private String schoolName;
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
    @Expose(serialize = false)
    private String img5;
    @Expose(serialize = false)
    private String img6;
    @Expose(serialize = false)
    private String img7;
    @Expose(serialize = false)
    private String img8;

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
    private int imgDefinitionId5;
    @Expose(serialize = false)
    private int imgDefinitionId6;
    @Expose(serialize = false)
    private int imgDefinitionId7;
    @Expose(serialize = false)
    private int imgDefinitionId8;

    @Expose(serialize = false)
    private String imgExt1;
    @Expose(serialize = false)
    private String imgExt2;
    @Expose(serialize = false)
    private String imgExt3;
    @Expose(serialize = false)
    private String imgExt4;
    @Expose(serialize = false)
    private String imgExt5;
    @Expose(serialize = false)
    private String imgExt6;
    @Expose(serialize = false)
    private String imgExt7;
    @Expose(serialize = false)
    private String imgExt8;

    @Expose
    private List<Image> images;

    @Expose
    @ApiGroup("final")
    @SerializedName("SessionProducts")
    private List<SendProductModel> products;

    @Expose(serialize = false)
    private int productId1;

    @Expose(serialize = false)
    private int isDistributed1;

    @Expose(serialize = false)
    private int productId2;

    @Expose(serialize = false)
    private int isDistributed2;

    @Expose(serialize = false)
    private int teacherProductId1;

    @Expose(serialize = false)
    private int teacherIsDistributed1;


    public Session() {
    }

    //For Teacher (SM)
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
                .addSerializationExclusionStrategy(new ApiExclusionStrategy("conducted"))
                .setPrettyPrinting()
                .create();

        //Convert models to json object
        String attendanceJson = gson.toJson(this);

        commSend.setCommandDetails(attendanceJson);
        return commSend;
    }

    /*
    * Final Session
    * */
    public CommunicationSend createFinalCommSend() {
        CommunicationSend commSend = new CommunicationSend();
        commSend.setProcessedOn(Common.iso8601Format.format(new Date()));
        commSend.setprocessDetails("");
        commSend.setProcessCount(0);
        commSend.setCommand(Command.ADD_FINAL_SESSION);
        commSend.setCommandDate(Common.iso8601Format.format(new Date()));
        commSend.setCommunicationGUID(this.communicationGuid);
        commSend.setCommunicationStatusID(1);
        commSend.setCreatedByID(PreferenceCommon.getInstance().getUserId());

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .addSerializationExclusionStrategy(new ApiExclusionStrategy("final"))
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

    public String getImg8() {
        return img8;
    }

    public void setImg8(String img8) {
        this.img8 = img8;
    }

    public int getImgDefinitionId8() {
        return imgDefinitionId8;
    }

    public void setImgDefinitionId8(int imgDefinitionId8) {
        this.imgDefinitionId8 = imgDefinitionId8;
    }

    public String getImgExt8() {
        return imgExt8;
    }

    public void setImgExt8(String imgExt8) {
        this.imgExt8 = imgExt8;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getImg5() {
        return img5;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
    }

    public String getImg6() {
        return img6;
    }

    public void setImg6(String img6) {
        this.img6 = img6;
    }

    public String getImg7() {
        return img7;
    }

    public void setImg7(String img7) {
        this.img7 = img7;
    }

    public int getImgDefinitionId5() {
        return imgDefinitionId5;
    }

    public void setImgDefinitionId5(int imgDefinitionId5) {
        this.imgDefinitionId5 = imgDefinitionId5;
    }

    public int getImgDefinitionId6() {
        return imgDefinitionId6;
    }

    public void setImgDefinitionId6(int imgDefinitionId6) {
        this.imgDefinitionId6 = imgDefinitionId6;
    }

    public int getImgDefinitionId7() {
        return imgDefinitionId7;
    }

    public void setImgDefinitionId7(int imgDefinitionId7) {
        this.imgDefinitionId7 = imgDefinitionId7;
    }

    public String getImgExt5() {
        return imgExt5;
    }

    public void setImgExt5(String imgExt5) {
        this.imgExt5 = imgExt5;
    }

    public String getImgExt6() {
        return imgExt6;
    }

    public void setImgExt6(String imgExt6) {
        this.imgExt6 = imgExt6;
    }

    public String getImgExt7() {
        return imgExt7;
    }

    public void setImgExt7(String imgExt7) {
        this.imgExt7 = imgExt7;
    }

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

    public List<SendProductModel> getProducts() {
        return products;
    }

    public void setProducts(List<SendProductModel> products) {
        this.products = products;
    }

    public int getProductId1() {
        return productId1;
    }

    public void setProductId1(int productId1) {
        this.productId1 = productId1;
    }

    public int getIsDistributed1() {
        return isDistributed1;
    }

    public void setIsDistributed1(int isDistributed1) {
        this.isDistributed1 = isDistributed1;
    }

    public int getProductId2() {
        return productId2;
    }

    public void setProductId2(int productId2) {
        this.productId2 = productId2;
    }

    public int getIsDistributed2() {
        return isDistributed2;
    }

    public void setIsDistributed2(int isDistributed2) {
        this.isDistributed2 = isDistributed2;
    }

    public int getTeacherProductId1() {
        return teacherProductId1;
    }

    public void setTeacherProductId1(int teacherProductId1) {
        this.teacherProductId1 = teacherProductId1;
    }

    public int getTeacherIsDistributed1() {
        return teacherIsDistributed1;
    }

    public void setTeacherIsDistributed1(int teacherIsDistributed1) {
        this.teacherIsDistributed1 = teacherIsDistributed1;
    }
}
