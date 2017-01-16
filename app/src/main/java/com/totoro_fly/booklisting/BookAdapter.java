package com.totoro_fly.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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

//    public void refresh(ArrayList<Book> arrayList) {
//        mArrayList = arrayList;
//        notifyDataSetChanged();
//    }


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
        viewHolder.mPrice.setText(book.getmPrice());
        return itemView;
    }
}
