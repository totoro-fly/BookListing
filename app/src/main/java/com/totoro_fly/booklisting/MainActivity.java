package com.totoro_fly.booklisting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
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
    @Bind(R.id.empty_view)
    TextView emptyView;
    private String bookListURL1 = "https://www.googleapis.com/books/v1/volumes?q=";
    private String bookListURL2 = "&country=us";
    private String bookListURL = "";
    private ProgressDialog progressDialog;
    BookAsyncTask bookAsyncTask;
    ArrayList numberEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        booklistingListview.setEmptyView(emptyView);
        EventBus.getDefault().register(this);
        numberEvent = new ArrayList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event event) {
        numberEvent.add(event.getmNumber());
    }

    public void toastInfo() {
        switch ((int) numberEvent.get(0)) {
            case 0:
                emptyView.setText("连接超时");
                Toast.makeText(this, "连接超时", Toast.LENGTH_LONG).show();
                break;
            case 1:
                emptyView.setText("无相关内容,请重新输入");
                Toast.makeText(this, "无相关内容,请重新输入", Toast.LENGTH_LONG).show();
                break;
        }
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
            if (!numberEvent.isEmpty()) {
                toastInfo();
                numberEvent.clear();
            }
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
        String title = null;
        //修正用户在首尾误输入的空格
        title = String.valueOf(titleEdittext.getText()).trim();
        if (title.isEmpty()) {
            Toast.makeText(this, R.string.input_info, Toast.LENGTH_LONG).show();
            changeFocusHideKeyboard();
            return;
        }
        Log.d(TAG, title);
        try {
            title = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        changeFocusHideKeyboard();
        Log.d(TAG, title);
        bookListURL = bookListURL1 + title + bookListURL2;
        bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
