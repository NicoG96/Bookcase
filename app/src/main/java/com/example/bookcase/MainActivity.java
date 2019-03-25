package com.example.bookcase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBookSelected(int position) {
        BookDetailsFragment book = (BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_book_details);
        assert book != null;
        book.displayBook(position);
    }
}
