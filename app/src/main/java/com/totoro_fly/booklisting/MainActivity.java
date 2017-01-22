package com.totoro_fly.booklisting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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
    BookAsyncTask bookAsyncTask;

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

    private class BookAsyncTask extends AsyncTask<String, Integer, ArrayList<Book>> {
        @Override
        protected void onPreExecute() {
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.refresh));
            progressDialog.show();
        }

        @Override
        protected ArrayList doInBackground(String... urls) {
            //传递Handler对象，试子线程使用sendmessage
            UrlUtils.mHandler = mHandler;
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

    public void updateUI(ArrayList<Book> bookList) {
        final BookAdapter bookAdapter = new BookAdapter(this, bookList);
        booklistingListview.setAdapter(bookAdapter);
        booklistingListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Book book = (Book) bookAdapter.getItem(i);
                Uri url = Uri.parse(book.getmBuyLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, url);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @OnClick(R.id.enter_button)
    public void onClick() {
        String title = String.valueOf(titleEdittext.getText());
        if (title.isEmpty()) {
            Toast.makeText(this, R.string.input_info, Toast.LENGTH_LONG).show();
            changeFocusHideKeyboard();
            return;
        }
        changeFocusHideKeyboard();
        bookListURL = bookListURL1 + title + bookListURL2;
        bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute();
    }

    //子线程catch之后，发送消息，结束dialog与AsyncTask。
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
