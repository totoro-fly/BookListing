package com.totoro_fly.booklisting;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.enter_button)
    Button enterButton;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;
    @Bind(R.id.title_edittext)
    EditText titleEdittext;
    @Bind(R.id.booklisting_listview)
    ListView booklistingListview;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String bookListURL1 = "https://www.googleapis.com/books/v1/volumes?q=";
    private String bookListURL2 = "&country=us";
    private String bookListURL = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
    }

    private void changeFocusHideKeyboard() {
        titleEdittext.clearFocus();
        enterButton.setFocusable(true);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(titleEdittext.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //旋转屏幕时点击按钮
        enterButton.performClick();
    }

    private class BookAsyncTask extends AsyncTask<String, Integer, ArrayList<Book>> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
            progressDialog.setMessage("刷新...");
        }

        @Override
        protected ArrayList doInBackground(String... urls) {
            URL url = UrlUtils.createUrl(bookListURL);
            String stringJson = "";
            stringJson = UrlUtils.makeHTTPRequest(url);
            ArrayList bookList = UrlUtils.extractFromJson(stringJson);
            return bookList;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> bookList) {
            if (bookList == null) {
                return;
            }
            updateUI(bookList);
            progressDialog.dismiss();
        }

    }

    private void updateUI(ArrayList<Book> bookList) {
        BookAdapter bookAdapter = new BookAdapter(this, bookList);
        booklistingListview.setAdapter(bookAdapter);

    }

    @OnClick(R.id.enter_button)
    public void onClick() {
        String title = String.valueOf(titleEdittext.getText());
        if (title.isEmpty()) {
            return;
        }
        changeFocusHideKeyboard();
        bookListURL = bookListURL1 + title + bookListURL2;
        BookAsyncTask bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute();
    }
}
