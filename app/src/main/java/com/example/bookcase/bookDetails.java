package com.example.bookcase;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class bookDetails extends FragmentActivity {
    private ViewPager vPager;
    private FragmentCollectionAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        //get the book index from the intent
        int index = getIntent().getIntExtra("book", 0);

        //Instantiate a ViewPager from the xml item
        vPager = findViewById(R.id.pager);

        //set scroll memory limit
        vPager.setOffscreenPageLimit(10);

        //instantiate a new page adapter (uses the fragment class to create objects for vPager)
        pAdapter = new FragmentCollectionAdapter(getSupportFragmentManager());

        //tell the adapter what book was selected
        pAdapter.setBookSelected(index);

        //set the pager to use this adapter now containing the book the user selected
        vPager.setAdapter(pAdapter);
    }
}