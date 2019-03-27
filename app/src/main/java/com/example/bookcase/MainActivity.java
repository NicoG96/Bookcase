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

        //if portrait, we need to implement a new activity with a fragment adapter
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //create intent to open the book details window
            Intent intent = new Intent(this, bookDetails.class);

            //send chosen book along with intent
            intent.putExtra("book", position);

            startActivity(intent);

        //otherwise its a 2 panel screen and we can directly manipulate the fragment from there
        } else {
            BookDetailsFragment bdf = (BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_book_details);
            assert bdf != null;

            //get the textview for the booktitle from the xml and set the name according to what was clicked
            TextView booktitle = findViewById(R.id.book_title);
            booktitle.setText(bdf.displayBook(position));
        }
    }
}
