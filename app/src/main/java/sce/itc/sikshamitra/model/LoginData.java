package sce.itc.sikshamitra.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                    user.setUserId(userObject.getInt("userId"));
                    user.setUserGUID(userObject.getString("userGUID"));
                    user.setUserName(userObject.getString("userName"));
                    user.setFirstName(userObject.getString("firstName"));
                    user.setLastName(userObject.getString("lastName"));
                    user.setEmail(userObject.optString("email", ""));
                    user.setMobileNumber(userObject.optString("mobilePhone", ""));
                    user.setRoleId(userObject.getInt("roleId"));
                    user.setUserRoleName(userObject.optString("roleName", ""));
                    user.setInActive(userObject.optInt("inActive", 0));
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
                        setting.setParameter(settingObj.getString("parameter"));
                        setting.setValue(settingObj.getString("value"));
                        settingsList.add(setting);
                    }
                }
                loginData.setSettings(settingsList);

                // -------------------- PRODUCTS --------------------
                List<Product> productList = new ArrayList<>();
                if (dataObject.has("products")) {
                    JSONArray productArray = dataObject.getJSONArray("products");
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
                    String comboName = comboObject.getString("comboName");
                    JSONArray comboArray = comboObject.getJSONArray("comboProducts");

                    for (int i = 0; i < comboArray.length(); i++) {
                        JSONObject comboProdObj = comboArray.getJSONObject(i);
                        ComboProduct comboProduct = new ComboProduct();
                        comboProduct.setComboId(comboId);
                        comboProduct.setComboName(comboName);
                        comboProduct.setProductId(comboProdObj.getInt("productId"));
                        comboProduct.setProduct(comboProdObj.getString("product"));
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
                        state.setStateName(stateObj.getString("state"));
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
