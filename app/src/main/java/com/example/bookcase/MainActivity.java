package com.example.bookcase;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBookSelected(int position) {
        //get the orientation of the device before doing anything
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //create intent to open the book details window
            Intent intent = new Intent(this, bookDetails.class);

            //send chosen book along with intent
            intent.putExtra("book", position);

            startActivity(intent);
        } else {
            BookDetailsFragment bdf = (BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_book_details);
            assert bdf != null;
            //TextView test = findViewById(R.id.book_title);
            //test.setText(bdf.displayBook(position));
        }
    }
}
