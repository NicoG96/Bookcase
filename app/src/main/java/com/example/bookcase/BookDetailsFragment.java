package com.example.bookcase;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookDetailsFragment extends Fragment {

    public BookDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_book_details, container, false);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //get the textview object from xml
            TextView bookName = rootView.findViewById(R.id.book_title);
            assert getArguments() != null;

            //find the book referenced
            int index = getArguments().getInt("index");

            //set the textview to the name of that book
            bookName.setText(displayBook(index));
        }
        return rootView;
    }

    public String displayBook(int position) {
        String[] books = getResources().getStringArray(R.array.books);
        return books[position];
    }
}
