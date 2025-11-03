package sce.itc.sikshamitra.model;

import android.database.Cursor;

public class User {
    private int userId;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private int loggedIn;
    private String userGUID;

    private int roleId;
    private String lastLoggedIn;
    private String userRoleName;

    private int inActive;

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private long refreshExpiresIn;
    private long issuedOn;

    public User() {
    }
    // --- Parameterized Constructor ---
    public User(int userId, String userName, String password, String firstName, String lastName, String mobileNumber, String email, int loggedIn, String userGUID, String schoolGUID, int roleId, String lastLoggedIn, String userRoleName, int agencyId) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.loggedIn = loggedIn;
        this.userGUID = userGUID;
        this.roleId = roleId;
        this.lastLoggedIn = lastLoggedIn;
        this.userRoleName = userRoleName;
    }

    // --- Getter and Setter methods ---


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(int loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUserGUID() {
        return userGUID;
    }

    public void setUserGUID(String userGUID) {
        this.userGUID = userGUID;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(String lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public String getUserRoleName() {
        return userRoleName;
    }

    public void setUserRoleName(String userRoleName) {
        this.userRoleName = userRoleName;
    }

    public int getInActive() {
        return inActive;
    }

    public void setInActive(int inActive) {
        this.inActive = inActive;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public long getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    public void setRefreshExpiresIn(long refreshExpiresIn) {
        this.refreshExpiresIn = refreshExpiresIn;
    }

    public long getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(long issuedOn) {
        this.issuedOn = issuedOn;
    }

    public void populateFromCursor(Cursor cursor) {
        int userIdCol = cursor.getColumnIndex("UserId");
        int userNameCol = cursor.getColumnIndex("UserName");
        int passwordCol = cursor.getColumnIndex("Password");
        int firstNameCol = cursor.getColumnIndex("FirstName");
        int lastNameCol = cursor.getColumnIndex("LastName");
        int mobileNumberCol = cursor.getColumnIndex("MobileNumber");
        int emailCol = cursor.getColumnIndex("Email");
        int loggedInCol = cursor.getColumnIndex("LoggedIn");
        int userGUIDCol = cursor.getColumnIndex("UserGUID");
        int roleIdCol = cursor.getColumnIndex("RoleId");
        int lastLoggedInCol = cursor.getColumnIndex("LastLoggedIn");
        int userRoleCol = cursor.getColumnIndex("UserRole");
        //int inActiveCol = cursor.getColumnIndex("InActive");

        this.setUserId(cursor.getInt(userIdCol));
        this.setUserName(cursor.getString(userNameCol));
        this.setPassword(cursor.getString(passwordCol));
        this.setFirstName(cursor.getString(firstNameCol));
        this.setLastName(cursor.getString(lastNameCol));
        this.setMobileNumber(cursor.getString(mobileNumberCol));
        this.setEmail(cursor.getString(emailCol));
        this.setLoggedIn(cursor.getInt(loggedInCol));
        this.setUserGUID(cursor.getString(userGUIDCol));
        this.setRoleId(cursor.getInt(roleIdCol));
        this.setLastLoggedIn(cursor.getString(lastLoggedInCol));
        this.setUserRoleName(cursor.getString(userRoleCol));
        //this.setInActive(cursor.getInt(inActiveCol));
    }

}
