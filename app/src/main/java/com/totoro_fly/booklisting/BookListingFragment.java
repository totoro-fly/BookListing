package com.totoro_fly.booklisting;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookListingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListingFragment extends Fragment {
    private static final String TAG = BookListingFragment.class.getSimpleName();
    private ArrayList<Book> mBookArrayList;
    //    private String bookListURL = "https://www.googleapis.com/books/v1/volumes?q=placeholder&country=us";
    private String bookListURL = "https://www.googleapis.com/books/v1/volumes?q=android&country=us";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String title = "";
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BookListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookListingFragment newInstance(String param1, String param2) {
        BookListingFragment fragment = new BookListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        BookAsyncTask bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute();

    }

    private class BookAsyncTask extends AsyncTask<URL, Integer, ArrayList<Book>> {
        @Override
        protected void onPreExecute() {
//            progressDialog.show();
//            progressDialog.setMessage("刷新...");
        }

        @Override
        protected ArrayList doInBackground(URL... urls) {
            URL url = createUrl(bookListURL);
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

    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        bookListURL = bookListURL.replace("placeholder", title);
        ListView listView = (ListView) inflater.inflate(R.layout.fragment_book_listing, container, false);
//        ArrayList<Book> bookList = new ArrayList<Book>();
//        Book textBook = new Book(bookListURL, "2", "3", "4");
//        bookList.add(textBook);
//        BookAdapter bookAdapter = new BookAdapter(getContext(), bookList);
//        ArrayList<Book> arrayList = urlutlis.createBookArrayList();
        BookAdapter bookAdapter = new BookAdapter(getContext(), mBookArrayList);
        listView.setAdapter(bookAdapter);
        // Inflate the layout for this fragment
        return listView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
