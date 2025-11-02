package sce.itc.sikshamitra.model;

public class LastSession {
    private int sessionId;
    private String sessionGuid;
    private String sessionStart;
    private String sessionEnd;
    private int sessionNo;

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

    public int getSessionNo() {
        return sessionNo;
    }

    public void setSessionNo(int sessionNo) {
        this.sessionNo = sessionNo;
    }
}
