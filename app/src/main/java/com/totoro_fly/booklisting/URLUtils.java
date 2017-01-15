package com.totoro_fly.booklisting;

import android.os.AsyncTask;
import android.util.Log;

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
 * Created by totoro-fly on 2017/1/15.
 */

public class URLUtils {
    private String mUrl;
    private static final String TAG = "URLUtils";
    private ArrayList<Book> mBookArrayList;
//    ProgressDialog progressDialog;


    public ArrayList<Book> createBookArrayList(String mUrl) {
        this.mUrl = mUrl;
        BookAsyncTask bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute();
        return mBookArrayList;
    }

    private class BookAsyncTask extends AsyncTask<URL, Integer, ArrayList<Book>> {
        @Override
        protected void onPreExecute() {
//            progressDialog.show();
//            progressDialog.setMessage("刷新...");
        }

        @Override
        protected ArrayList doInBackground(URL... urls) {
            URL url = createUrl();
            String stringJson = "";
            stringJson = makeHTTPRequest(url);
            ArrayList bookList = extractFromJson(stringJson);
            return bookList;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> bookList) {
            if (bookList == null) {
                return;
            }
            mBookArrayList = bookList;
//            progressDialog.dismiss();
        }
    }

    private URL createUrl() {
        URL url = null;
        try {
            url = new URL(mUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "createUrl ", e);
            e.printStackTrace();
        }
        return url;
    }

    private String makeHTTPRequest(URL url) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String jsonResponse = "";
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(1000);
            httpURLConnection.setConnectTimeout(1500);
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {
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

    private String readFromStream(InputStream inputStream) {
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

    private ArrayList extractFromJson(String jsonResponse) {
        ArrayList<Book> bookList = new ArrayList<Book>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray items = baseJsonResponse.getJSONArray("items");
            if (items.length() > 0) {
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                    String title = volumeInfo.getString("title");
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    String author = " ";
                    for (int j = 0; j < authors.length(); j++) {
                        author = author + authors.getString(j);
                    }
                    String infoLink = volumeInfo.getString("infoLink");
                    JSONObject saleInfo = item.getJSONObject("saleInfo");
                    String amount = "";
                    if (saleInfo.has("listPrice")) {
                        JSONObject listPrice = saleInfo.getJSONObject("listPrice");
                        amount = listPrice.getString("amount");
                    } else {
                        amount = "无";
                    }
                    bookList.add(new Book(title, author, amount, infoLink));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "extractFromJsonn ", e);
            e.printStackTrace();
        }
        return bookList;
    }
}
