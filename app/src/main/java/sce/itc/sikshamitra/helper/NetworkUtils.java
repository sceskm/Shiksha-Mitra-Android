package sce.itc.sikshamitra.helper;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getName();
    public static OkHttpClient client = new OkHttpClient();
    String url = "https://reqres.in/api/users";

    public static String getRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String postRequest(String url, RequestBody body) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static Response postResponse(String url, RequestBody body) {
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    //asynchronise
    public static Request enqueNetworkRequest(String url, RequestBody body, boolean sendToken) {
        Request request = null;
        if (sendToken) {
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader(ConstantField.AUTHORIZATION, ConstantField.BEARER + " " + PreferenceCommon.getInstance().getAccessToken())
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

        }

        return request;
    }


    //synchronous
    public static Response excuteNetworkRequest(String url, RequestBody requestBody, boolean sendToken) {
        Response response = null;
        final OkHttpClient client = new OkHttpClient();
        Request request = null;
        Call call = null;

        if (sendToken == false) {
            request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader(ConstantField.AUTHORIZATION, ConstantField.BEARER + " " + PreferenceCommon.getInstance().getAccessToken())
                    .build();
        }
        if (request != null) {
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "networkRequest: AUTHORIZATION value " + PreferenceCommon.getInstance().getAccessToken());


        return response;

    }

    //file upload network request
    public static Response addFileNetworkRequest(String url, RequestBody requestBody) {
        Response response = null;
        final OkHttpClient client = new OkHttpClient();
        Request request = null;
        Call call = null;

        request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        if (request != null) {
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "networkRequest: AUTHORIZATION value " + PreferenceCommon.getInstance().getAccessToken());
        return response;

    }

    //file upload network request
    public static Request enqueFileRequest(String url, RequestBody requestBody) {
        Response response = null;
        final OkHttpClient client = new OkHttpClient();
        Request request = null;

        request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        return request;

    }


}
