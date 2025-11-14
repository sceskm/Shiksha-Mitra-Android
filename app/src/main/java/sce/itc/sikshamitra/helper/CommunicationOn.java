package sce.itc.sikshamitra.helper;

import android.database.Cursor;

public class CommunicationOn {
    int id;
    String action;
    String lastDate;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    // Creating column of table
    public void populateFromCursor(Cursor cursorSetUp) {

        int idCol = cursorSetUp.getColumnIndex("_id");
        int actionCol = cursorSetUp.getColumnIndex("Action");
        int lastDateCol = cursorSetUp.getColumnIndex("LastDate");

        this.setId(cursorSetUp.getInt(idCol));
        this.setAction(cursorSetUp.getString(actionCol));
        this.setLastDate(cursorSetUp.getString(lastDateCol));
    }
}
