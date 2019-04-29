package com.example.bookcase;

import android.content.Context;
import android.content.SharedPreferences;
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
    Book viewingBook;
    Book playingBook;
    int bookIndex;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);

        //get the book index from the intent
        bookIndex = getIntent().getIntExtra("book", 0);
        viewingBook = library.get(bookIndex);

        //Instantiate a ViewPager from the xml item
        vPager = findViewById(R.id.pager);

        //set scroll memory limit
        vPager.setOffscreenPageLimit(library.size());

        //instantiate a new page adapter (uses the fragment class to create objects for vPager)
        pAdapter = new FragmentCollectionAdapter(getSupportFragmentManager());

        //tell the adapter what book was selected
        pAdapter.setBookSelected(bookIndex);

        //set the pager to use this adapter now containing the book the user selected
        vPager.setAdapter(pAdapter);
    }

    @Override
    public void playBook(File file, Book book){
        if(playingBook == null) {
            playingBook = viewingBook;
        }

        //save position of current audiobook if there's one playing
        if(audiobook != null) {
            saveInfo(playingBook.getTitle(), 100);
        }

        //check to see if this audiobook has been played before
        int pos = getInfo(book.getTitle());

        //start playing book
        audiobook.seekTo(pos);
        audiobook.play(file);

        //keep track of which book we're playing
        this.playingBook = book;
    }

    @Override
    public void streamBook(Book book){
        if(playingBook == null) {
            playingBook = viewingBook;
        }

        //save position of current audiobook if there's one playing
        if(audiobook != null) {
            saveInfo(playingBook.getTitle(), 100);
        }

        //check to see if this audiobook has been played before
        int pos = getInfo(book.getTitle());

        audiobook.seekTo(pos);
        audiobook.play(book.getId());

        //keep track of which book we're playing
        this.playingBook = book;
    }

    @Override
    public void pauseBook(){
        if(audiobook != null) {
            saveInfo(playingBook.getTitle(), 100);
        }
        audiobook.pause();
    }

    @Override
    public void stopBook(){
        saveInfo(playingBook.getTitle(), 0);
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

    public void saveInfo(String book_name, int position) {
        SharedPreferences sharedPref = getSharedPreferences("audioInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(book_name, position);
        editor.apply();
    }

    public int getInfo(String book_name) {
        SharedPreferences sharedPref = getSharedPreferences("audioInfo", Context.MODE_PRIVATE);
        return sharedPref.getInt(book_name, 0);
    }
}