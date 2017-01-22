package com.totoro_fly.booklisting;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by totoro-fly on 2017/1/14.
 */

public class BookAdapter extends BaseAdapter {
    ArrayList mArrayList;
    Context mContext;

    public BookAdapter(Context context, ArrayList arrayList) {
        mContext = context;
        mArrayList = arrayList;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return mArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private static class ViewHolder {
        private TextView mName;
        private TextView mAuthor;
        private TextView mPrice;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        ViewHolder viewHolder;
        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mName = (TextView) itemView.findViewById(R.id.name_textview);
            viewHolder.mAuthor = (TextView) itemView.findViewById(R.id.author_textview);
            viewHolder.mPrice = (TextView) itemView.findViewById(R.id.price_textview);
            itemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) itemView.getTag();
        }
        Book book = (Book) getItem(position);
        viewHolder.mName.setText(book.getmName());
        viewHolder.mAuthor.setText(book.getmAuthor());
        String amount = book.getmPrice();
        if (amount.equals(mContext.getString(R.string.noting)))
            viewHolder.mPrice.setText(amount);
        else
            viewHolder.mPrice.setText(NumberFormat.getCurrencyInstance(Locale.US).format(Double.parseDouble(amount)));
        return itemView;
    }
}
