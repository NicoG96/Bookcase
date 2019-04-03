package com.example.bookcase;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedListener {
    final static ArrayList<Book> library = new ArrayList<>();
    BookDetailsFragment bdf;
    BookListFragment blf;
    EditText search;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the book list fragment
        blf = (BookListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_book_list);
        assert blf != null;

        //populate the list from link below
        blf.getBooks("https://kamorris.com/lab/audlib/booksearch.php");

        //make a search button object
        button = findViewById(R.id.search_button);

        //make a search text object
        search = findViewById(R.id.search_bar);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(search.getText().toString().equals("")) {
                    blf.getBooks("https://kamorris.com/lab/audlib/booksearch.php");
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter a search query", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    String query = search.getText().toString();
                    blf.getBooks("https://kamorris.com/lab/audlib/booksearch.php?search=" + query);
                }
            }
        });
    }

    @Override
    public void onBookSelected(int position) {
        //get the orientation of the device before doing anything
        int orientation = getResources().getConfiguration().orientation;

        //if device is portrait and not a tablet, we need to implement a new activity with a fragment adapter
        if (orientation == Configuration.ORIENTATION_PORTRAIT && !isTablet(this)) {
            //create intent to open the book details window
            Intent intent = new Intent(this, bookDetails.class);

            //send chosen book along with intent
            intent.putExtra("book", position);

            startActivity(intent);

        //otherwise its a 2 panel screen and we can directly manipulate the fragment from this activity
        } else if((orientation == Configuration.ORIENTATION_LANDSCAPE) || isTablet(this)){
            bdf = (BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_book_details);
            assert bdf != null;

            //now set the fields
            bdf.displayBookInfo(position);
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
