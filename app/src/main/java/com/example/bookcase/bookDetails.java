package com.example.bookcase;

import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.io.File;

import static com.example.bookcase.MainActivity.audiobook;
import static com.example.bookcase.MainActivity.library;

public class bookDetails extends FragmentActivity implements BookDetailsFragment.onAudioActionListener{
    private ViewPager vPager;
    private FragmentCollectionAdapter pAdapter;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        //get the book index from the intent
        int index = getIntent().getIntExtra("book", 0);

        //Instantiate a ViewPager from the xml item
        vPager = findViewById(R.id.pager);

        //set scroll memory limit
        vPager.setOffscreenPageLimit(library.size());

        //instantiate a new page adapter (uses the fragment class to create objects for vPager)
        pAdapter = new FragmentCollectionAdapter(getSupportFragmentManager());

        //tell the adapter what book was selected
        pAdapter.setBookSelected(index);

        //set the pager to use this adapter now containing the book the user selected
        vPager.setAdapter(pAdapter);
    }

    @Override
    public void playBook(File file){
        audiobook.play(file);
    }

    @Override
    public void streamBook(int book_id){
        audiobook.play(book_id);
    }

    @Override
    public void pauseBook(){
        audiobook.pause();
    }

    @Override
    public void stopBook(){
        audiobook.stop();
    }

    @Override
    public void setProgHand(Handler handler) {
        if(audiobook != null) {
            audiobook.setProgressHandler(handler);
        }
    }

    @Override
    public void setProgress(int position, boolean fromUser) {
        if(fromUser && audiobook != null) {
            audiobook.seekTo(position);
        }
    }
}