package sce.itc.sikshamitra.helper;

public class ConstantField {
    public static final int SPLASH_TIME_OUT = 3000;
    public static final String APP_VERSION = "1.0.0";

    public static final String IMAGE_FORMAT = "jpg";
    public static final String ORIGINAL_IMAGE_NAME = "siksha_";
    public static final String COMPRESS_IMAGE_DIRECTORY = "Compressed";
    public static final String COMPRESS_PHOTO_URI = "content://sce.itc.sikshamitra.fileprovider/my_images/Compressed/";
    public static final int IMAGE_QUALITY = 100;
    public static final int IMAGE_SCALE_UP = 1;
    public static final int IMAGE_SIZE_KB = 250;
    public static final String CAPTURE = "Capture";
    public static final String DELETE = "Delete";
    public static final String DATABASE_PATH= "/data/data/sce.itc.sikshamitra/databases/";
    public static final String DATABASE_NAME= "smdatabase.sqlite";
    public static final double TEST_LONGITUDE = 88.3639;
    public static final double TEST_LATITUDE = 22.5726;
    //south
    public static final double IND_LATITUDE_1 = 8;
    public static final double IND_LONGITUDE_1 = 69;
    //north
    public static final double IND_LATITUDE_2 = 38;
    public static final double IND_LONGITUDE_2 = 98;

    public static final int ACTIVE = 1;
    public static final int IN_ACTIVE = 0;

    /*
    *
    * */
    public static final String AUTO_SYNCING = "autosyncing";
    public static final int MANUAL_DOWNLOAD = 2;
    public static final int AUTO_DOWNLOAD = 1;
    public static final int INSTANT_DOWNLOAD = 3;

    public static final int DEFAULT = 0;
    public static final int SUCCESS = 1;
    public static final int FAILURE = 2;

    public static final int COMM_STATUS_NOT_PROCESSED = 1;
    public static final int COMM_STATUS_PROCESSED = 2;
    public static final int COMM_STATUS_ERROR = 3;


    /*
    * Url details
    * */
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";
    public static final String REFRESH_TOKEN = "refreshtoken";
    public static final String ACCESS_TOKEN = "accesstoken";
    public static final String LOGIN_URL = "/mobile/v1/appauth/login";
    //added https at dev server
    public static final String NETWORK_URL = "https://sm2025api.dhanushteam.com";
    public static final String DEFAULT_DATE = "2010-01-01 00:00:00";
    public static final String REFRESH_TOKEN_CREATED = "refreshtokencreated";
    public static final String ACTION_URL = "/mobile/v1/AppAuth/action";

    /*
    * User table data
    * */
    public static final String USER_NAME = "9878642233";
    public static final String USER_NAME_SM = "shiksha.mitra";
    public static final String USER_NAME_AGENCY = "agency";
    public static final String PASSWORD = "2233";

    public static final int ROLE_ID_FIELD_TEAM = 4; // for agency - field team
    public static final int ROLE_ID_SHIKSHA_MITRA = 5; //for shiksha mitra - teacher


    public static final int VENUE_CAMERA_REQUEST = 101;

    public static final int VENUE_IMAGE_DEFINITION_ID = 10001;

    public static final int SM_SESSION_IMAGE_DEFINITION_ID_1 = 20001;
    public static final int SM_SESSION_IMAGE_DEFINITION_ID_2 = 20002;
    public static final int SM_SESSION_IMAGE_DEFINITION_ID_3 = 20003;
    public static final int SM_SESSION_IMAGE_DEFINITION_ID_4 = 20004;

    //Teacher - Session Images
    public static final int REQUEST_IMAGE_1 = 110;
    public static final int REQUEST_IMAGE_2 = 111;
    public static final int REQUEST_IMAGE_3 = 112;
    public static final int REQUEST_IMAGE_4 = 113;



}
