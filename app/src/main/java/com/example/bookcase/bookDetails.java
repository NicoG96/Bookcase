package com.example.bookcase;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class bookDetails extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_book_details);

        //load the book array
        Resources res = getResources();
        String books[] = res.getStringArray(R.array.books);

        //get the intent
        Intent intent = getIntent();

        //get the color index from the intent
        int index = intent.getIntExtra("book", 0);

        //change title based on this intent
        this.setTitle(books[index]);
    }
}
