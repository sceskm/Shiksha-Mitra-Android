package sce.itc.sikshamitra.model;

import android.database.Cursor;

public class Session {
    private int _id;

    private String SessionGUID;
    private int SessionNo;
    private String Img1;
    private String Img2;
    private String Img3;
    private String Img4;
    private String Img5;
    private String Img6;
    private String Img7;
    private String Img8;
    private String Img9;
    private String Img10;
    private String Img11;
    private String Img12;
    private String SessionStartedOn;
    private String SessionEndedOn;
    private int CommunicationStatus;
    private int CommunicationAttempt;
    private int SessionStatus;
    private String SchoolGUID;
    private String CommunicationGUID;
    private String Latitude;
    private String Longitude;
    private String CameraIssue;
    private String VenueGUID;
    private String UserGUID;
    private String ShikshaMitraGUID;

    public Session() {
    }

    public Session(int _id, String sessionGUID, int sessionNo, String img1, String img2, String img3, String img4, String img5, String img6, String img7, String img8, String img9, String img10, String img11, String img12, String sessionStartedOn, String sessionEndedOn, int communicationStatus, int communicationAttempt, int sessionStatus, String schoolGUID, String communicationGUID, String latitude, String longitude, String cameraIssue, String venueGUID, String userGUID, String shikshaMitraGUID) {
        this._id = _id;
        SessionGUID = sessionGUID;
        SessionNo = sessionNo;
        Img1 = img1;
        Img2 = img2;
        Img3 = img3;
        Img4 = img4;
        Img5 = img5;
        Img6 = img6;
        Img7 = img7;
        Img8 = img8;
        Img9 = img9;
        Img10 = img10;
        Img11 = img11;
        Img12 = img12;
        SessionStartedOn = sessionStartedOn;
        SessionEndedOn = sessionEndedOn;
        CommunicationStatus = communicationStatus;
        CommunicationAttempt = communicationAttempt;
        SessionStatus = sessionStatus;
        SchoolGUID = schoolGUID;
        CommunicationGUID = communicationGUID;
        Latitude = latitude;
        Longitude = longitude;
        CameraIssue = cameraIssue;
        VenueGUID = venueGUID;
        UserGUID = userGUID;
        ShikshaMitraGUID = shikshaMitraGUID;
    }

    public int getSessionId() {
        return _id;
    }

    public void setSessionId(int sessionId) {
        _id = sessionId;
    }

    public String getSessionGUID() {
        return SessionGUID;
    }

    public void setSessionGUID(String sessionGUID) {
        SessionGUID = sessionGUID;
    }

    public int getSessionNo() {
        return SessionNo;
    }

    public void setSessionNo(int sessionNo) {
        SessionNo = sessionNo;
    }

    public String getImg1() {
        return Img1;
    }

    public void setImg1(String img1) {
        Img1 = img1;
    }

    public String getImg2() {
        return Img2;
    }

    public void setImg2(String img2) {
        Img2 = img2;
    }

    public String getImg3() {
        return Img3;
    }

    public void setImg3(String img3) {
        Img3 = img3;
    }

    public String getImg4() {
        return Img4;
    }

    public void setImg4(String img4) {
        Img4 = img4;
    }

    public String getImg5() {
        return Img5;
    }

    public void setImg5(String img5) {
        Img5 = img5;
    }

    public String getImg6() {
        return Img6;
    }

    public void setImg6(String img6) {
        Img6 = img6;
    }

    public String getImg7() {
        return Img7;
    }

    public void setImg7(String img7) {
        Img7 = img7;
    }

    public String getImg8() {
        return Img8;
    }

    public void setImg8(String img8) {
        Img8 = img8;
    }

    public String getImg9() {
        return Img9;
    }

    public void setImg9(String img9) {
        Img9 = img9;
    }

    public String getImg10() {
        return Img10;
    }

    public void setImg10(String img10) {
        Img10 = img10;
    }

    public String getImg11() {
        return Img11;
    }

    public void setImg11(String img11) {
        Img11 = img11;
    }

    public String getImg12() {
        return Img12;
    }

    public void setImg12(String img12) {
        Img12 = img12;
    }

    public String getSessionStartedOn() {
        return SessionStartedOn;
    }

    public void setSessionStartedOn(String sessionStartedOn) {
        SessionStartedOn = sessionStartedOn;
    }

    public String getSessionEndedOn() {
        return SessionEndedOn;
    }

    public void setSessionEndedOn(String sessionEndedOn) {
        SessionEndedOn = sessionEndedOn;
    }

    public int getCommunicationStatus() {
        return CommunicationStatus;
    }

    public void setCommunicationStatus(int communicationStatus) {
        CommunicationStatus = communicationStatus;
    }

    public int getCommunicationAttempt() {
        return CommunicationAttempt;
    }

    public void setCommunicationAttempt(int communicationAttempt) {
        CommunicationAttempt = communicationAttempt;
    }

    public int getSessionStatus() {
        return SessionStatus;
    }

    public void setSessionStatus(int sessionStatus) {
        SessionStatus = sessionStatus;
    }

    public String getSchoolGUID() {
        return SchoolGUID;
    }

    public void setSchoolGUID(String schoolGUID) {
        SchoolGUID = schoolGUID;
    }

    public String getCommunicationGUID() {
        return CommunicationGUID;
    }

    public void setCommunicationGUID(String communicationGUID) {
        CommunicationGUID = communicationGUID;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getCameraIssue() {
        return CameraIssue;
    }

    public void setCameraIssue(String cameraIssue) {
        CameraIssue = cameraIssue;
    }

    public String getVenueGUID() {
        return VenueGUID;
    }

    public void setVenueGUID(String venueGUID) {
        VenueGUID = venueGUID;
    }

    public String getUserGUID() {
        return UserGUID;
    }

    public void setUserGUID(String userGUID) {
        UserGUID = userGUID;
    }

    public String getShikshaMitraGUID() {
        return ShikshaMitraGUID;
    }

    public void setShikshaMitraGUID(String shikshaMitraGUID) {
        ShikshaMitraGUID = shikshaMitraGUID;
    }


    public void populateFromCursor(Cursor cursorSession) {
        int sessionIdCol = cursorSession.getColumnIndex("_id");
        int sessionGUIDCol = cursorSession.getColumnIndex("SessionGUID");
        int sessionNoCol = cursorSession.getColumnIndex("SessionNo");
        int img1Col = cursorSession.getColumnIndex("Img1");
        int img2Col = cursorSession.getColumnIndex("Img2");
        int img3Col = cursorSession.getColumnIndex("Img3");
        int img4Col = cursorSession.getColumnIndex("Img4");
        int img5Col = cursorSession.getColumnIndex("Img5");
        int img6Col = cursorSession.getColumnIndex("Img6");
        int img7Col = cursorSession.getColumnIndex("Img7");
        int img8Col = cursorSession.getColumnIndex("Img8");
        int img9Col = cursorSession.getColumnIndex("Img9");
        int img10Col = cursorSession.getColumnIndex("Img10");
        int img11Col = cursorSession.getColumnIndex("Img11");
        int img12Col = cursorSession.getColumnIndex("Img12");
        int sessionStartedOnCol = cursorSession.getColumnIndex("SessionStartedOn");
        int sessionEndedOnCol = cursorSession.getColumnIndex("SessionEndedOn");
        int communicationStatusCol = cursorSession.getColumnIndex("CommunicationStatus");
        int communicationAttemptCol = cursorSession.getColumnIndex("CommunicationAttempt");
        int sessionStatusCol = cursorSession.getColumnIndex("SessionStatus");
        int schoolGUIDCol = cursorSession.getColumnIndex("SchoolGUID");
        int communicationGUIDCol = cursorSession.getColumnIndex("CommunicationGUID");
        int latitudeCol = cursorSession.getColumnIndex("Latitude");
        int longitudeCol = cursorSession.getColumnIndex("Longitude");
        int cameraIssueCol = cursorSession.getColumnIndex("CameraIssue");
        int venueGUIDCol = cursorSession.getColumnIndex("VenueGUID");
        int userGUIDCol = cursorSession.getColumnIndex("UserGUID");
        int shikshaMitraGUIDCol = cursorSession.getColumnIndex("ShikshaMitraGUID");

        this._id = cursorSession.getInt(sessionIdCol);
        this.SessionGUID = cursorSession.getString(sessionGUIDCol);
        this.SessionNo = cursorSession.getInt(sessionNoCol);
        this.Img1 = cursorSession.getString(img1Col);
        this.Img2 = cursorSession.getString(img2Col);
        this.Img3 = cursorSession.getString(img3Col);
        this.Img4 = cursorSession.getString(img4Col);
        this.Img5 = cursorSession.getString(img5Col);
        this.Img6 = cursorSession.getString(img6Col);
        this.Img7 = cursorSession.getString(img7Col);
        this.Img8 = cursorSession.getString(img8Col);
        this.Img9 = cursorSession.getString(img9Col);
        this.Img10 = cursorSession.getString(img10Col);
        this.Img11 = cursorSession.getString(img11Col);
        this.Img12 = cursorSession.getString(img12Col);
        this.SessionStartedOn = cursorSession.getString(sessionStartedOnCol);
        this.SessionEndedOn = cursorSession.getString(sessionEndedOnCol);
        this.CommunicationStatus = cursorSession.getInt(communicationStatusCol);
        this.CommunicationAttempt = cursorSession.getInt(communicationAttemptCol);
        this.SessionStatus = cursorSession.getInt(sessionStatusCol);
        this.SchoolGUID = cursorSession.getString(schoolGUIDCol);
        this.CommunicationGUID = cursorSession.getString(communicationGUIDCol);
        this.Latitude = cursorSession.getString(latitudeCol);
        this.Longitude = cursorSession.getString(longitudeCol);
        this.CameraIssue = cursorSession.getString(cameraIssueCol);
        this.VenueGUID = cursorSession.getString(venueGUIDCol);
        this.UserGUID = cursorSession.getString(userGUIDCol);
        this.ShikshaMitraGUID = cursorSession.getString(shikshaMitraGUIDCol);
    }

}
