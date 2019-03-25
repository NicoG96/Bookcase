package com.example.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BookListFragment extends Fragment {

    public BookListFragment() {}
    onBookSelectedListener callback;

    public interface onBookSelectedListener {
        void onBookSelected(int index);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (onBookSelectedListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }


    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);

        //get the parent activity context
        Context ctx = getContext();
        assert ctx != null;

        //find the listview and create its corresponding object
        ListView booklist = v.findViewById(R.id.book_list);

        //create the adapter that will display the books
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.books));

        //load that adapter into the listview
        booklist.setAdapter(adapter);

        //create listener for each book
        booklist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //change the background color of the second fragment
                //callback.onBookSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        return v;
    }
}