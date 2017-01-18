package com.totoro_fly.booklisting;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListingFragment extends Fragment {
    private static final String TAG = BookListingFragment.class.getSimpleName();
    //    private String bookListURL = "https://www.googleapis.com/books/v1/volumes?q=placeholder&country=us";
    private String bookListURL = "https://www.googleapis.com/books/v1/volumes?q=android&country=us";
    private ListView mListview;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipe;

    

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
        progressDialog = new ProgressDialog(getContext());
        BookAsyncTask bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class BookAsyncTask extends AsyncTask<String, Integer, ArrayList<Book>> implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            progressDialog.setMessage("刷新...");
        }

        @Override
        protected ArrayList doInBackground(String... urls) {
            URL url = UrlUtils.createUrl(bookListURL);
            String stringJson = "";
            stringJson = UrlUtils.makeHTTPRequest(url, getContext());
            ArrayList bookList = UrlUtils.extractFromJson(stringJson);
            return bookList;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> bookList) {
            if (bookList == null) {
                return;
            }
            BookAdapter bookAdapter = new BookAdapter(getContext(), bookList);
            mListview.setAdapter(bookAdapter);
            swipe.setOnRefreshListener(this);
            progressDialog.dismiss();

        }

        @Override
        public void onRefresh() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipe.setRefreshing(false);
                }
            }, 1000);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ListView listView = (ListView) inflater.inflate(R.layout.fragment_book_listing, container, false);
        mListview = (ListView) listView.findViewById(R.id.booklisting_listview);
        swipe = (SwipeRefreshLayout) listView.findViewById(R.id.swipe_refresh);
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
