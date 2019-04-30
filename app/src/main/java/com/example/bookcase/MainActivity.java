package com.example.bookcase;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedListener, BookDetailsFragment.onAudioActionListener {
    final static ArrayList<Book> library = new ArrayList<>();
    BookDetailsFragment bdf;
    BookListFragment blf;
    EditText search;
    Button search_button;

    Book viewingBook;
    Book playingBook;
    int playingPos;

    String query;

    boolean connected;
    static AudiobookService.MediaControlBinder audiobook;

    //create service connection
    ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audiobook = (AudiobookService.MediaControlBinder) service;
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent audioIntent = new Intent(this, AudiobookService.class);
        bindService(audioIntent, sc, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unbindService(sc);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        try {
            savedInstanceState.putInt("book", playingBook.getId());
        } catch(Exception e) {}
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blf = (BookListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_book_list);
        assert blf != null;

        //make a search search_button object
        search_button = findViewById(R.id.search_button);

        //make a search text object
        search = findViewById(R.id.search_bar);

        search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(search.getText().toString().equals("")) {
                    blf.getBooks("https://kamorris.com/lab/audlib/booksearch.php");
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter a search query", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    query = search.getText().toString();
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

            //now display the book info on the screen
            bdf.displayBookInfo(position, getApplicationContext());

            //and display the audio controls
            bdf.play_btn.setVisibility(View.VISIBLE);
            bdf.pause_btn.setVisibility(View.VISIBLE);
            bdf.stop_btn.setVisibility(View.VISIBLE);
            bdf.seeker.setVisibility(View.VISIBLE);
        }
        viewingBook = library.get(position);
    }

    @Override
    public void playBook(File file, Book book, int pos){
        playingPos = pos;

        if(playingBook == null) {
            playingBook = viewingBook;
        }

        //save position of current audiobook if there's one playing
        if(audiobook != null) {
            if(pos - 10 <= 0) {
                saveInfo(playingBook.getTitle(), 0);
            } else {
                saveInfo(playingBook.getTitle(), pos - 10);
            }
        }

        //check to see if this audiobook has been played before
        int last = getInfo(book.getTitle());

        //start playing book
        audiobook.play(file, last);

        //keep track of which book we're playing
        this.playingBook = book;
    }

    @Override
    public void streamBook(Book book, int pos){
        playingPos = pos;

        if(playingBook == null) {
            playingBook = viewingBook;
        }

        //save position of current audiobook if there's one playing
        if(audiobook != null) {
            if(pos - 10 <= 0) {
                saveInfo(playingBook.getTitle(), 0);
            } else {
                saveInfo(playingBook.getTitle(), pos - 10);
            }
        }

        //check to see if this audiobook has been played before
        int last = getInfo(book.getTitle());

        audiobook.play(book.getId(), last);

        //keep track of which book we're playing
        this.playingBook = book;
    }

    @Override
    public void pauseBook(int pos){
        if(audiobook != null) {
            saveInfo(playingBook.getTitle(), pos);
            audiobook.pause();
            playingPos = pos;
        }
    }

    @Override
    public void stopBook(){
        saveInfo(playingBook.getTitle(), 0);
        audiobook.stop();
        playingPos = 0;
    }

    @Override
    public void setProgress(int position, boolean fromUser) {
        if(fromUser && audiobook != null) {
            audiobook.seekTo(position);
        }
    }

    @Override
    public void setProgHand(Handler handler) {
        if(audiobook != null) {
            audiobook.setProgressHandler(handler);
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
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
