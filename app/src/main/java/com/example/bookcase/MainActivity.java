package com.example.bookcase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedListener {
    boolean notFirstRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBookSelected(int position) {
        //create intent to open the book details window
        Intent intent = new Intent(this, bookDetails.class);

        //send chosen book along with intent
        intent.putExtra("book", position);

        //start new window
        if(notFirstRun) {
            startActivity(intent);
        } else {
            notFirstRun = true;
        }
    }
}
