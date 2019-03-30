package com.example.bookcase;

import android.content.Context;
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

        Context ctx = getContext();
        assert ctx != null;

        //get the textview object from xml
        TextView bookName = rootView.findViewById(R.id.book_title);
        assert getArguments() != null;

        //TRY to get a passed index if there is one
        try {
            int index = getArguments().getInt("index");

            //if there is, then set the textview to the name of that book
            bookName.setText(displayBookInfo(index));

        //if there isn't, then this is a 2-panel device instantiating this fragment for first time,
        //so we can just return the inflated view as is
        } catch(Exception e) {
            return rootView;
        }


        //otherwise its landscape or tablet
        return rootView;
    }

    public String displayBookInfo(int position) {
        String[] books = getResources().getStringArray(R.array.books);
        return books[position];
    }
}
