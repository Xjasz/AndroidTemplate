package xjasz.core.webservice;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import xjasz.core.Singleton;
import xjasz.core.parser.WebDataParser;
import xjasz.core.reflection.listeners.Listener;
import xjasz.core.reflection.messages.NetworkMessage;
import xjasz.core.util.DialogProgress;
import xjasz.core.util.contstant.Constants;


public class WebService {
    private final String TAG = WebService.class.getSimpleName();

    private final static String URL_APP_DATA = "http://www.delvedapps.com/app/FakeGpsController/php/AppData.php";
    private final static String URL_APP_ANALYTICS = "http://www.delvedapps.com/app/FakeGpsController/php/Analytics.php";
    private final static String URL_APP_COMMANDS = "http://www.delvedapps.com/app/FakeGpsController/php/Commands.php";

    public final static int POST = 1;
    public final static int GET = 2;
    public final static int GET_APP_DATA = 10;
    public final static int GET_APP_COMMANDS = 11;
    public final static int PUT_APP_ANALYTICS = 12;
    public final static int APP_MUST_UPDATE = 777;


    private int retryCount = 1;

    public void webEvent(final int[] type, final String[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int status;
                try {
                    final OkHttpClient client = new OkHttpClient();
                    client.setConnectTimeout(Constants.WEB_SERVICE_TIMEOUT, TimeUnit.MILLISECONDS);
                    client.setReadTimeout(Constants.WEB_SERVICE_TIMEOUT, TimeUnit.MILLISECONDS);

                    final Request.Builder builder = new Request.Builder();
                    builder.url(buildUrl(type[1], data));

                    if (type[0] == GET) {
                        builder.addHeader(Constants.CONTENT_TYPE, Constants.MIME_TYPE_JSON);
                    } else if (type[0] == POST) {
                        builder.post(buildRequestBody(type[1], data));
                    }

                    Response response = client.newCall(builder.build()).execute();
                    status = response.code();
                    String content = response.body().string();

                    Log.d(TAG, "Response Url : " + response.request().url());
                    Log.d(TAG, "Response Code : " + String.valueOf(status));
                    Log.d(TAG, "Response Content : " + content);

                    if (response.isSuccessful()) {
                        new WebDataParser().parseRawWebObject(content, type[1]);
                    }

                } catch (Exception e) {
                    status = ((e instanceof IOException) ? Constants.IO_ERROR_CODE : Constants.JSON_ERROR_CODE);
                    e.printStackTrace();
                }

                if (status == Constants.IO_ERROR_CODE && retryCount <= Constants.WEB_RETRY_MAX) {
                    DialogProgress.updateProgressDialog("Trying again " + String.valueOf(retryCount++) +
                            " of " + String.valueOf(Constants.WEB_RETRY_MAX) + ".");
                    webEvent(type, data);
                } else {
                    try {
                        retryCount = 1;
                        Listener.send(new NetworkMessage<>(type[1], getInternalResponse(status)));
                    } catch (Exception e) {
                        Log.d(TAG, "app was killed");
                    }
                }
            }
        }
        ).start();
    }

    private static String getInternalResponse(int status) {
        if (status >= 200 && status < 300)
            return Constants.VALID_WEB_EVENT;
        else if (status == Constants.IO_ERROR_CODE)
            return Constants.IO_ERROR;
        else if (status == Constants.JSON_ERROR_CODE)
            return Constants.APP_ERROR + String.valueOf(Constants.JSON_ERROR_CODE);
        else if (status >= 500 && status < 600)
            return Constants.SERVER_ERROR + String.valueOf(status);
        else if (status >= 400 && status < 500)
            return Constants.CLIENT_ERROR + String.valueOf(status);
        else
            return Constants.UNKNOWN_ERROR + String.valueOf(status);
    }

    private static RequestBody buildRequestBody(int type, String[] data) throws JSONException {
        final MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);

        switch (type) {
            case POST:
                break;
            default:
                //default
                builder.addFormDataPart("id", Singleton.DEVICE_ID);
                //encoder
                new FormEncodingBuilder().add("p1", data[0]).add("p2", data[1]).add("p3", data[2]).build();
                // json
                RequestBody.create(MediaType.parse(Constants.MIME_TYPE_JSON), new JSONObject().toString());
                //file
                builder.addFormDataPart("file", data[3],
                        RequestBody.create(MediaType.parse(MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(Singleton.getShortFileExtension(data[3]))), new File(data[3])));
                throw new RuntimeException("(Invalid Post Body) type: " + String.valueOf(type));
        }

        return builder.build();
    }


    private static String buildUrl(int type, String[] data) {
        switch (type) {
            case GET_APP_DATA:
                return URL_APP_DATA;
            case PUT_APP_ANALYTICS:
                return URL_APP_ANALYTICS;
            case GET_APP_COMMANDS:
                return URL_APP_COMMANDS;
            default:
                return null;
        }
    }

}