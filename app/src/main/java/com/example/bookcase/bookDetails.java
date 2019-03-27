package com.example.bookcase;

import android.content.Intent;
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

        //instantiate a new page adapter (uses the fragment class to create objects for vPager)
        pAdapter = new FragmentCollectionAdapter(getSupportFragmentManager());

        //assign the intent to a class variable in the adapter
        pAdapter.bookSelected = intent.getIntExtra("book", 0);

        //set the pager to use this adapter containing the pages
        vPager.setAdapter(pAdapter);
    }
}