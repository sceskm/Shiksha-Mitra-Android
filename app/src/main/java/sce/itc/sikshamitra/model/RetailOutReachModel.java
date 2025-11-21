package sce.itc.sikshamitra.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;
import sce.itc.sikshamitra.helper.PreferenceCommon;

public class RetailOutReachModel {
    @Expose
    private String shopName;
    @Expose
    private String retailOutreachGuid;
    @Expose
    private String userGuid;
    @Expose
    private int organizationId;
    @Expose
    private String nearbySchool;
    @Expose
    private String schoolGuid;
    @Expose
    private String contactName;
    @Expose
    private String contactPhone;
    @Expose
    private String address1;
    @Expose
    private String address2;
    @Expose
    private String division;
    private String location;
    @Expose
    private String block;
    @Expose
    private String city;
    @Expose
    private String district;
    @Expose(serialize = false)
    private String state;
    @Expose
    private int stateId;
    @Expose
    @SerializedName("pincode")
    private String pinCode;
    @Expose
    @SerializedName("stockItcproducts")
    private int isKeepITCProducts;
    @Expose
    @SerializedName("itcproductNames")
    private String itcProductNames;
    @Expose
    @SerializedName("handwashPouchesSold")
    private int handWashPouchesSold;

    @Expose
    @SerializedName("brandingInterested")
    private int brandingInterested;
    @Expose
    private int shopPainting;
    @Expose
    private int dealerBoard;
    @Expose
    private int poster;
    @Expose
    private int bunting;
    private int isKeepCompetitorProduct;


    @Expose
    private int savlonSoapSold;
    @Expose
    private String fmcgpurchaseFrom;
    @Expose
    private String distributorDetails;
    @Expose
    private String marketDetails;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose(serialize = false)
    private String communicationGuid;
    @Expose(serialize = false)
    private String createdOn;
    @Expose
    private String visitedOn;

    @Expose
    private List<Image> images;

    @Expose(serialize = false)
    private String image1;
    @Expose(serialize = false)
    private int imgDefinitionId1;
    @Expose(serialize = false)
    private String imgExt1;

    public RetailOutReachModel() {
    }

    // Getter and Setter methods for all fields

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getRetailOutreachGuid() {
        return retailOutreachGuid;
    }

    public void setRetailOutreachGuid(String retailOutreachGuid) {
        this.retailOutreachGuid = retailOutreachGuid;
    }

    public String getUserGuid() {
        return userGuid;
    }

    public void setUserGuid(String userGuid) {
        this.userGuid = userGuid;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public String getNearbySchool() {
        return nearbySchool;
    }

    public void setNearbySchool(String nearbySchool) {
        this.nearbySchool = nearbySchool;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
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

    public int getIsKeepITCProducts() {
        return isKeepITCProducts;
    }

    public void setIsKeepITCProducts(int isKeepITCProducts) {
        this.isKeepITCProducts = isKeepITCProducts;
    }

    public int getBrandingInterested() {
        return brandingInterested;
    }

    public void setBrandingInterested(int brandingInterested) {
        this.brandingInterested = brandingInterested;
    }

    public int getShopPainting() {
        return shopPainting;
    }

    public void setShopPainting(int shopPainting) {
        this.shopPainting = shopPainting;
    }

    public int getDealerBoard() {
        return dealerBoard;
    }

    public void setDealerBoard(int dealerBoard) {
        this.dealerBoard = dealerBoard;
    }

    public int getPoster() {
        return poster;
    }

    public void setPoster(int poster) {
        this.poster = poster;
    }

    public int getBunting() {
        return bunting;
    }

    public void setBunting(int bunting) {
        this.bunting = bunting;
    }

    public int getIsKeepCompetitorProduct() {
        return isKeepCompetitorProduct;
    }

    public void setIsKeepCompetitorProduct(int isKeepCompetitorProduct) {
        this.isKeepCompetitorProduct = isKeepCompetitorProduct;
    }

    public String getItcProductNames() {
        return itcProductNames;
    }

    public void setItcProductNames(String itcProductNames) {
        this.itcProductNames = itcProductNames;
    }

    public int getHandWashPouchesSold() {
        return handWashPouchesSold;
    }

    public void setHandWashPouchesSold(int handWashPouchesSold) {
        this.handWashPouchesSold = handWashPouchesSold;
    }

    public int getSavlonSoapSold() {
        return savlonSoapSold;
    }

    public void setSavlonSoapSold(int savlonSoapSold) {
        this.savlonSoapSold = savlonSoapSold;
    }

    public String getFmcgpurchaseFrom() {
        return fmcgpurchaseFrom;
    }

    public void setFmcgpurchaseFrom(String fmcgpurchaseFrom) {
        this.fmcgpurchaseFrom = fmcgpurchaseFrom;
    }

    public String getDistributorDetails() {
        return distributorDetails;
    }

    public void setDistributorDetails(String distributorDetails) {
        this.distributorDetails = distributorDetails;
    }

    public String getMarketDetails() {
        return marketDetails;
    }

    public void setMarketDetails(String marketDetails) {
        this.marketDetails = marketDetails;
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

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public int getImgDefinitionId1() {
        return imgDefinitionId1;
    }

    public void setImgDefinitionId1(int imgDefinitionId1) {
        this.imgDefinitionId1 = imgDefinitionId1;
    }

    public String getImgExt1() {
        return imgExt1;
    }

    public void setImgExt1(String imgExt1) {
        this.imgExt1 = imgExt1;
    }

    public String getCommunicationGuid() {
        return communicationGuid;
    }

    public void setCommunicationGuid(String communicationGuid) {
        this.communicationGuid = communicationGuid;
    }
    public String getCreatedOn() {
        return createdOn;
    }
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getVisitedOn() {
        return visitedOn;
    }
    public void setVisitedOn(String visitedOn) {
        this.visitedOn = visitedOn;
    }

    public String getSchoolGuid() {
        return schoolGuid;
    }
    public void setSchoolGuid(String schoolGuid) {
        this.schoolGuid = schoolGuid;
    }


    public CommunicationSend createCommSend() {
        CommunicationSend commSend = new CommunicationSend();
        commSend.setProcessedOn(Common.iso8601Format.format(new Date()));
        commSend.setprocessDetails("");
        commSend.setProcessCount(0);
        commSend.setCommand(Command.ADD_RETAIL);
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

}
