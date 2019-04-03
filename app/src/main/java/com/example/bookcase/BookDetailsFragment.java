package com.example.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.bookcase.MainActivity.library;

public class BookDetailsFragment extends Fragment {
    TextView title;
    TextView author;
    TextView published;
    ImageView cover;

    public BookDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_book_details, container, false);

        Context ctx = getContext();
        assert ctx != null;

        //get the textview objects from the xml file
        title = rootView.findViewById(R.id.book_title_tv);
        author = rootView.findViewById(R.id.author_tv);
        published = rootView.findViewById(R.id.published_tv);
        cover = rootView.findViewById(R.id.cover_iv);

        //TRY to get a passed index if there is one
        try {
            int index = getArguments().getInt("index");

            //if there is, then display the fields of the book
            displayBookInfo(index);

        //if there isn't, then this is a 2-panel device instantiating this fragment for first time,
        //so we can just return the inflated view as is
        } catch(NullPointerException e) {
            return rootView;
        }

        return rootView;
    }

    public void displayBookInfo(int position) {
        title.setText(library.get(position).getTitle());
        author.setText(library.get(position).getAuthor());
        //published.setText(Integer.toString(library.get(position).getPublished()));
        //cover.setText(library.get(position).getCoverURL());
    }
}
