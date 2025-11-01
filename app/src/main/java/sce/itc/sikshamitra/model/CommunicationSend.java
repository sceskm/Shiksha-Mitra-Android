package sce.itc.sikshamitra.model;

import android.database.Cursor;

public class CommunicationSend {

    private int id;
    private int createdByID;
    private String processedOn;
    private String processDetails;
    private int processCount;
    private int communicationStatusID;
    private String commandDate;
    private String command;
    private String commandDetails;
    private String communicationGUID;
    private String receivedData;
    private int inActive;
    private String commandGuid;

    public int getID() {
        return id;
    }

    public void setID(int value) {
        id = value;
    }

    public int getCreatedByID() {
        return createdByID;
    }

    public void setCreatedByID(int value) {
        createdByID = value;
    }

    public String getProcessedOn() {
        return processedOn;
    }

    public void setProcessedOn(String value) {
        processedOn = value;
    }

    public int getProcessCount() {
        return processCount;
    }

    public void setProcessCount(int value) {
        processCount = value;
    }

    public void setProcessDetails(String processDetails) {
        this.processDetails = processDetails;
    }

    public int getCommunicationStatusID() {
        return communicationStatusID;
    }

    public void setCommunicationStatusID(int value) {
        communicationStatusID = value;
    }

    public String getProcessDetails() {
        return processDetails;
    }

    public void setprocessDetails(String value) {
        processDetails = value;
    }

    public String getCommandDate() {
        return commandDate;
    }

    public void setCommandDate(String value) {
        commandDate = value;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String value) {
        command = value;
    }

    public String getCommandDetails() {
        return commandDetails;
    }

    public void setCommandDetails(String value) {
        commandDetails = value;
    }

    public String getCommunicationGUID() {
        return communicationGUID;
    }

    public void setCommunicationGUID(String value) {
        communicationGUID = value;
    }

    public String getReceivedData() {
        return receivedData;
    }

    public void setReceivedData(String value) {
        receivedData = value;
    }

    public int getInActive() {
        return inActive;
    }

    public void setInActive(int value) {
        inActive = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommandGuid() {
        return commandGuid;
    }

    public void setCommandGuid(String commandGuid) {
        this.commandGuid = commandGuid;
    }

    public void populateFromCursor(Cursor cursorSend) {
        int idCol = cursorSend.getColumnIndex("_id");
        int procOnCol = cursorSend.getColumnIndex("ProcessedOn");
        int procDtlCol = cursorSend.getColumnIndex("ProcessDetails");
        int procCountCol = cursorSend.getColumnIndex("ProcessCount");
        int cmdDateCol = cursorSend.getColumnIndex("CommandDate");
        int cmdCol = cursorSend.getColumnIndex("Command");
        int cmdDtlCol = cursorSend.getColumnIndex("CommandDetails");
        int inActiveCol = cursorSend.getColumnIndex("InActive");
        int commGUIDCol = cursorSend.getColumnIndex("CommunicationGUID");
        int commStatusCol = cursorSend.getColumnIndex("CommunicationStatusID");
        int createdCol = cursorSend.getColumnIndex("CreatedByID");

        this.id = cursorSend.getInt(idCol);
        this.processedOn = cursorSend.getString(procOnCol);
        this.processDetails = cursorSend.getString(procDtlCol);
        this.processCount = cursorSend.getInt(procCountCol);
        this.commandDate = cursorSend.getString(cmdDateCol);
        this.command = cursorSend.getString(cmdCol);
        this.commandDetails = cursorSend.getString(cmdDtlCol);
        this.communicationGUID = cursorSend.getString(commGUIDCol);
        this.communicationStatusID = cursorSend.getInt(commStatusCol);
        this.createdByID = cursorSend.getInt(createdCol);
        this.inActive = cursorSend.getInt(inActiveCol);

    }

	/*public void createResetMessage(String TLCode) {

		setHeader(common.COMMAND_RESET);
		this.processCount = 100; // one time message only
		this.setCommandDetails("Reset=" + common.loggedUser.getAndoridPassword() + "~~" + "EMPCODE=" + TLCode + "~~"
				+ "CommunicationGUID=" + this.communicationGUID);
	}

	public void createEmployeeListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_USERLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createStoreListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_STORELIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createProductListMessage(Date lastDate, String productStoreLiString, int storeID) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_PRODUCTLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID + "~~" + "StoreID=" + storeID + "~~" + "Product_StoreIDs="
				+ productStoreLiString);
	}

	public void createPromoProductListMessage(Date lastDate, String productStoreLiString, int storeID) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_PROMOPRODUCTLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID + "~~" + "StoreID=" + storeID + "~~" + "Product_StoreIDs="
				+ productStoreLiString);
	}

	public void createAttendanceListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_ATTENDANCELIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createJourneyListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_JOURNEYPLANLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createAgencyPJpListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_AGENCYPJPLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createQuestionsListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_QUESTIONLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createStoreTargetListMessage(Date lastDate, String storeIDs) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_STORETARGETLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "StoreIDs=" + storeIDs + "~~"
				+ "CommunicationGUID=" + this.communicationGUID);
	}

	public void createPurposeListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_PURPOSELIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createLastStockListMessage(Date lastDate, int storeID) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
		setHeader(common.COMMAND_LASTSTOCKLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "StoreID="
				+ Integer.toString(storeID) + "~~" + "CommunicationGUID=" + this.communicationGUID);
	}

	public void createAlertListMessage(Date lastDate) {
		// Send Last date with time to avoid same data update
		SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		setHeader(common.COMMAND_ALERTLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + iso8601Format.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createCompetitionParamListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_COMPETITION_PARAMETER_LIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	// doyel june'2015
	public void createProductCategoryListMessage(Date lastDate) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_CATEGORYLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createQADataListMessage(Date lastDate) {
		// Send Last date with time to avoid same data update
		SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		setHeader(common.COMMAND_QADATALIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + iso8601Format.format(lastDate) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	// doyel july'2015
	public void createAttendanceUserListMessage(Date lastDate, int storeID) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
		setHeader(common.COMMAND_NONTABLETUSERLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "StoreID="
				+ Integer.toString(storeID) + "~~" + "CommunicationGUID=" + this.communicationGUID);
	}

	// doyel july'2015
	public void createCampaignListMessage(Date lastDate, int StoreID) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_CAMPAIGNLIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "StoreID="
				+ Integer.toString(StoreID) + "~~" + "CommunicationGUID=" + this.communicationGUID);
	}

	// doyel july'2015
	public void createCampaignStoreListMessage(Date lastDate, int StoreID, String CityID) {
		SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

		setHeader(common.COMMAND_CAMPAIGNSTORELIST);
		this.processCount = 100; // one time message only
		this.setCommandDetails("LastDate=" + dateformatYYYYMMDD.format(lastDate) + "~~" + "StoreID="
				+ Integer.toString(StoreID) + "~~" + "CityIDs=" + CityID + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	// =====================================================================================

	public void createUserMessage(int userID) {

		setHeader(common.COMMAND_USER);
		this.processCount = 100; // one time message only
		this.setCommandDetails("UserID=" + Integer.toString(userID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createStoreMessage(int storeID) {

		setHeader(common.COMMAND_STORE);
		this.processCount = 100; // one time message only
		this.setCommandDetails("StoreID=" + Integer.toString(storeID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createProductMessage(int productID) {

		setHeader(common.COMMAND_PRODUCT);
		this.processCount = 100; // one time message only
		this.setCommandDetails("ProductID=" + Integer.toString(productID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createPromoProductMessage(int productID) {

		setHeader(common.COMMAND_PROMOPRODUCT);
		this.processCount = 100; // one time message only
		this.setCommandDetails("ProductID=" + Integer.toString(productID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createAttendanceMessage(int attendanceID) {

		setHeader(common.COMMAND_ATTENDANCEDOWNLOAD);
		this.processCount = 100; // one time message only
		this.setCommandDetails("AttendanceID=" + Integer.toString(attendanceID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createJourneyMessage(int journeyPlanID) {

		setHeader(common.COMMAND_JOURNEYPLANDOWNLOAD);
		this.processCount = 100; // one time message only
		this.setCommandDetails("JourneyPlanID=" + Integer.toString(journeyPlanID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createQuestionsMessage(int questionID) {

		setHeader(common.COMMAND_QUESTIONDOWNLOAD);
		this.processCount = 100; // one time message only
		this.setCommandDetails("QuestionID=" + Integer.toString(questionID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createStoreTargetMessage(int storeID) {

		setHeader(common.COMMAND_STORETARGETDOWNLOAD);
		this.processCount = 100; // one time message only
		this.setCommandDetails("StoreID=" + Integer.toString(storeID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createPurposeMessage(int purposeID) {

		setHeader(common.COMMAND_PURPOSEDOWNLOAD);
		this.processCount = 100; // one time message only
		this.setCommandDetails("PurposeID=" + Integer.toString(purposeID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createLastStockMessage(int storeID) {
		setHeader(common.COMMAND_LASTSTOCK);
		this.processCount = 100; // one time message only
		this.setCommandDetails("StoreID=" + Integer.toString(storeID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createAlertDetailMessage(int alertID) {
		setHeader(common.COMMAND_ALERTS);
		this.processCount = 100; // one time message only
		this.setCommandDetails("AlertID=" + Integer.toString(alertID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createCompetitionParamMessage(String paramID_Type) {

		setHeader(common.COMMAND_COMPETITION_PARAMETER);
		this.processCount = 100; // one time message only
		this.setCommandDetails("ParamID_TypeID=" + paramID_Type + "~~" + "CommunicationGUID=" + this.communicationGUID);
	}

	public void createQADataDetailMessage(int qaDataID) {
		setHeader(common.COMMAND_QADATA);
		this.processCount = 100; // one time message only
		this.setCommandDetails("QAUserDataID=" + Integer.toString(qaDataID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	public void createAgencyJourneyMessage(int journeyPlanID) {

		setHeader(common.COMMAND_AGENCYPJPDOWNLOADDATA);
		this.processCount = 100; // one time message only
		this.setCommandDetails("JourneyPlanID=" + Integer.toString(journeyPlanID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	// doyel june'2015
	public void createProductCategoryMessage(int categoryID) {

		setHeader(common.COMMAND_CATEGORYDATA);
		this.processCount = 100; // one time message only
		this.setCommandDetails("ProductCategoryID=" + Integer.toString(categoryID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	// doyel july'2015
	public void createAttendanceUserMessage(int userID) {

		setHeader(common.COMMAND_NONTABLETUSERDATA);
		this.processCount = 100; // one time message only
		this.setCommandDetails("UserID=" + Integer.toString(userID) + "~~" + "CommunicationGUID="
				+ this.communicationGUID);
	}

	// doyel july'2015
	public void createCampaignMessage(int campaignLocationMapID) {

		setHeader(common.COMMAND_CAMPAIGNDATA);
		this.processCount = 100; // one time message only
		this.setCommandDetails("CampaignLocationMapID=" + Integer.toString(campaignLocationMapID) + "~~"
				+ "CommunicationGUID=" + this.communicationGUID);
	}

	// doyel july'2015
	public void createCampaignStoreMessage(int campaignStorePromotionID) {

		setHeader(common.COMMAND_CAMPAIGNSTOREDATA);
		this.processCount = 100; // one time message only
		this.setCommandDetails("CampaignStorePromotionID=" + Integer.toString(campaignStorePromotionID) + "~~"
				+ "CommunicationGUID=" + this.communicationGUID);
	}

	private void setHeader(String command) {
		Calendar cal = new GregorianCalendar(2000, 0, 1);
		Date processDate = cal.getTime();

		this.setProcessedOn(processDate);
		this.setprocessDetails("");
		this.setProcessCount(0);
		this.setCommand(command);
		this.setCommandDate(new Date());
		this.setCommunicationGUID("");
		this.setCommunicationStatusID(message.COMM_STATUS_NOT_PROCESSED);
		// If application stopped, SyncUser will take care
		this.setCreatedByID(common.loggedUser != null ? common.loggedUser.getUserID() : common.syncUser.getUserID());
		this.setCommunicationGUID(UUID.randomUUID().toString());
	}*/

}
