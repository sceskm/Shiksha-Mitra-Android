package sce.itc.sikshamitra.model;

import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import sce.itc.sikshamitra.helper.Command;
import sce.itc.sikshamitra.helper.Common;

public class PreRegistration {
    @Expose
    @SerializedName("firstName")
    private String firstName;
    @Expose
    @SerializedName("lastName")
    private String lastName;
    @Expose
    @SerializedName("phone")
    private String phone;
    @Expose
    @SerializedName("password")
    private String password;
    @Expose
    @SerializedName("venueGuid")
    private String venueGuid;
    @Expose
    @SerializedName("userGuid")
    private String userGuid;
    @Expose
    @SerializedName("udiseCode")
    private String udiseCode;
    @Expose
    @SerializedName("organizationId")
    private int organizationId;
    @Expose
    @SerializedName("trainerKit")
    private boolean trainerKit;

    @Expose
    @SerializedName("productsReceived")
    private List<SendComboProduct> comboProduct;


    // --- Empty Constructor ---
    public PreRegistration() {
    }

    public List<SendComboProduct> getComboProduct() {
        return comboProduct;
    }

    public void setComboProduct(List<SendComboProduct> comboProduct) {
        this.comboProduct = comboProduct;
    }

    public String getUdiseCode() {
        return udiseCode;
    }
    public void setUdiseCode(String udiseCode) {
        this.udiseCode = udiseCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVenueGuid() {
        return venueGuid;
    }

    public void setVenueGuid(String venueGuid) {
        this.venueGuid = venueGuid;
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
    public boolean isTrainerKit() {
        return trainerKit;
    }
    public void setTrainerKit(boolean trainerKit) {
        this.trainerKit = trainerKit;
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
