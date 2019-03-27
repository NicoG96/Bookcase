package com.example.bookcase;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class bookDetails extends FragmentActivity {
    //set up class vars
    private ViewPager vPager;
    private FragmentCollectionAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        //get the intent from the click
        Intent intent = getIntent();

        //Instantiate a ViewPager from the xml item
        vPager = findViewById(R.id.pager);

        vPager.setOffscreenPageLimit(2);

        //instantiate a new page adapter (uses the fragment class to create objects for vPager)
        pAdapter = new FragmentCollectionAdapter(getSupportFragmentManager());

        //get the intent
        int index = intent.getIntExtra("book", 0);

        //send the book array index to the adapter
        pAdapter.setBookSelected(index);

        //determine the number of views by subtracting the indexed item from array size
        pAdapter.setSIZE(10-index);

        //set the pager to use this adapter containing the pages
        vPager.setAdapter(pAdapter);
    }
}