package com.totoro_fly.booklisting;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements BookListingFragment.OnFragmentInteractionListener, BlankFragment.OnFragmentInteractionListener {

    @Bind(R.id.enter_button)
    Button enterButton;
    @Bind(R.id.activity_main)
    LinearLayout activityMain;
    @Bind(R.id.title_edittext)
    EditText titleEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        fragmentTransaction.add(R.id.booklisting_fragment, new BlankFragment(), "BookListingFragment");
//        fragmentTransaction.commit();
    }

    @OnClick(R.id.enter_button)
    public void onClick() {
        String title = String.valueOf(titleEdittext.getText());
        if (title.isEmpty()) {
            Toast.makeText(this, "请输入书籍相关信息", Toast.LENGTH_SHORT).show();
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BookListingFragment bookListingFragment = BookListingFragment.newInstance(title, "");
        fragmentTransaction.replace(R.id.booklisting_fragment, bookListingFragment, "BookListingFragment");
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
