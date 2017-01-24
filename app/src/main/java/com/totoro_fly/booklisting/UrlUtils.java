package com.totoro_fly.booklisting;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by totoro-fly on 2017/1/18.
 */

public class UrlUtils {
    private static final String TAG = "UriUtils";
//    public static Handler mHandler;

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "createUrl ", e);
            e.printStackTrace();
        }
        return url;
    }

    public static String makeHTTPRequest(URL url) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String jsonResponse = "";
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(8 * 1000);
            httpURLConnection.setConnectTimeout(10 * 1000);
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                return null;
            }
        } catch (IOException e) {
            EventBus.getDefault().post(new Event(0));
            Log.e(TAG, "makeHTTPUrl ", e);
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "makeHTTPUrl closeInputStream", e);
                    e.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            try {
                line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                Log.e(TAG, "readFromStream", e);
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public static ArrayList extractFromJson(String jsonResponse) {
        ArrayList<Book> bookList = new ArrayList<Book>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray items = baseJsonResponse.getJSONArray("items");
            if (items.length() > 0) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                    String title = volumeInfo.getString("title");
                    String author = " ";
                    if (volumeInfo.has("authors")) {
                        JSONArray authors = volumeInfo.getJSONArray("authors");
                        for (int j = 0; j < authors.length(); j++) {
                            author = author + authors.getString(j);
                        }
                    } else {
                        author = MyApplication.getContext().getString(R.string.noting);
                    }
                    String infoLink = volumeInfo.getString("infoLink");
                    JSONObject saleInfo = item.getJSONObject("saleInfo");
                    String amount = "";
                    if (saleInfo.has("listPrice")) {
                        JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                        amount = listPrice.getString("amount");
                    } else {
                        amount = MyApplication.getContext().getString(R.string.noting);
                    }
                    bookList.add(new Book(title, author, amount, infoLink));
                }
            }
        } catch (JSONException e) {
            EventBus.getDefault().post(new Event(1));
            Log.e(TAG, "extractFromJsonn ", e);
            e.printStackTrace();
        }
        return bookList;
    }
}
