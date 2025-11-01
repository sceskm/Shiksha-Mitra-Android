package sce.itc.sikshamitra.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sce.itc.sikshamitra.helper.Common;

public class LoginData {
    private User user;
    private List<Settings> settings;
    private List<Product> products;
    private List<ComboProduct> comboProducts;
    private List<State> states;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Settings> getSettings() {
        return settings;
    }

    public void setSettings(List<Settings> settings) {
        this.settings = settings;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<ComboProduct> getComboProducts() {
        return comboProducts;
    }

    public void setComboProducts(List<ComboProduct> comboProducts) {
        this.comboProducts = comboProducts;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }


    public static LoginData downloadLoginUser(JSONObject jsonObject) {
        LoginData loginData = new LoginData();

        try {
            if (jsonObject.has("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");

                // -------------------- USER --------------------
                User user = null;
                if (dataObject.has("user")) {
                    JSONObject userObject = dataObject.getJSONObject("user");
                    user = new User();
                    user.setUserId(Common.getInt(userObject.getString("userId")));
                    user.setUserGUID(Common.getString(userObject.getString("userGUID")));
                    user.setUserName(Common.getString(userObject.getString("userName")));
                    user.setFirstName(Common.getString(userObject.getString("firstName")));
                    user.setLastName(Common.getString(userObject.getString("lastName")));
                    user.setEmail(Common.getString(userObject.optString("email")));
                    user.setMobileNumber(Common.getString(userObject.optString("mobilePhone")));
                    user.setRoleId(Common.getInt(userObject.getString("roleId")));
                    user.setUserRoleName(Common.getString(userObject.optString("roleName")));
                    user.setInActive(Common.getBooleanToInt(userObject.optString("inActive")));
                }

                // -------------------- TOKEN --------------------
                if (dataObject.has("token") && user != null) {
                    JSONObject tokenObject = dataObject.getJSONObject("token");
                    user.setAccessToken(tokenObject.getString("accessToken"));
                    user.setRefreshToken(tokenObject.getString("refreshToken"));
                    user.setExpiresIn(tokenObject.getLong("expiresIn"));
                    user.setRefreshExpiresIn(tokenObject.getLong("refreshExpiresIn"));
                    user.setIssuedOn(tokenObject.getLong("issuedOn"));
                }

                loginData.setUser(user);

                // -------------------- SETTINGS --------------------
                List<Settings> settingsList = new ArrayList<>();
                if (dataObject.has("setting")) {
                    JSONArray settingsArray = dataObject.getJSONArray("setting");
                    for (int i = 0; i < settingsArray.length(); i++) {
                        JSONObject settingObj = settingsArray.getJSONObject(i);
                        Settings setting = new Settings();
                        setting.setParameter(Common.getString(settingObj.getString("parameter")));
                        setting.setValue(Common.getString(settingObj.getString("value")));
                        settingsList.add(setting);
                    }
                }
                loginData.setSettings(settingsList);

                // -------------------- PRODUCTS --------------------
                List<Product> productList = new ArrayList<>();
                if (dataObject.has("productList")) {
                    JSONArray productArray = dataObject.getJSONArray("productList");
                    for (int i = 0; i < productArray.length(); i++) {
                        JSONObject productObj = productArray.getJSONObject(i);
                        Product product = new Product();
                        product.setProductId(productObj.getInt("productId"));
                        product.setProduct(productObj.getString("product"));
                        product.setProductTypeId(productObj.getInt("productTypeId"));
                        productList.add(product);
                    }
                }
                loginData.setProducts(productList);

                // -------------------- COMBO PRODUCTS --------------------
                List<ComboProduct> comboList = new ArrayList<>();
                if (dataObject.has("comboProducts")) {
                    JSONObject comboObject = dataObject.getJSONObject("comboProducts");
                    int comboId = comboObject.getInt("comboId");
                    String comboName = Common.getString(comboObject.getString("comboName"));
                    JSONArray comboArray = comboObject.getJSONArray("comboProducts");

                    for (int i = 0; i < comboArray.length(); i++) {
                        JSONObject comboProdObj = comboArray.getJSONObject(i);
                        ComboProduct comboProduct = new ComboProduct();
                        comboProduct.setComboId(comboId);
                        comboProduct.setComboName(comboName);
                        comboProduct.setProductId(comboProdObj.getInt("productId"));
                        comboProduct.setProduct(Common.getString(comboProdObj.getString("product")));
                        comboProduct.setProductTypeId(comboProdObj.getInt("productTypeId"));
                        comboList.add(comboProduct);
                    }
                }
                loginData.setComboProducts(comboList);

                // -------------------- STATES --------------------
                List<State> stateList = new ArrayList<>();
                if (dataObject.has("state")) {
                    JSONArray stateArray = dataObject.getJSONArray("state");
                    for (int i = 0; i < stateArray.length(); i++) {
                        JSONObject stateObj = stateArray.getJSONObject(i);
                        State state = new State();
                        state.setStateId(stateObj.getInt("stateId"));
                        state.setStateName(Common.getString(stateObj.getString("state")));
                        stateList.add(state);
                    }
                }
                loginData.setStates(stateList);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return loginData;
    }

}
