package com.totoro_fly.booklisting;

/**
 * Created by totoro-fly on 2017/1/13.
 */

public class Book {
    private String mName;
    private String mAuthor;
    private String mPrice;
    private String mBuyLink;

    public Book(String mName, String mAuthor, String mPrice, String mBuyLink) {
        this.mName = mName;
        this.mAuthor = mAuthor;
        this.mPrice = mPrice;
        this.mBuyLink = mBuyLink;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getmBuyLink() {
        return mBuyLink;
    }

    public void setmBuyLink(String mBuyLink) {
        this.mBuyLink = mBuyLink;
    }
}
