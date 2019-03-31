package com.example.bookcase;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create array of books
        final ArrayList<Book> library = new ArrayList<>();

        //thread to parse JSON data from website
        Thread t = new Thread(){
            @Override
            public void run(){
                URL resource;

                try {
                    resource = new URL("https://kamorris.com/lab/audlib/booksearch.php");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()));

                    //variables to hold the website content
                    StringBuilder response = new StringBuilder();
                    String tmpResponse;

                    tmpResponse = reader.readLine();

                    //keep reading until end of page
                    while (tmpResponse != null) {
                        response.append(tmpResponse);
                        tmpResponse = reader.readLine();
                    }

                    //create a JSON array object from this data
                    JSONArray arr = new JSONArray(response.toString());

                    //now transfer the members of this array into an array of book objects
                    for(int i = 0; i < arr.length(); i++) {
                        //make new book
                        Book book = new Book();

                        //get the book from the JSON array
                        JSONObject jBook = arr.getJSONObject(i);

                        //get the values from that book and load them into the Book object
                        book.setId(jBook.getInt("book_id"));
                        book.setAuthor(jBook.getString("author"));
                        book.setPublished(jBook.getInt("published"));
                        book.setTitle(jBook.getString("title"));
                        book.setCoverURL(jBook.getString("cover_url"));

                        //then finally add that book to the library array
                        library.add(book);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        t.start();

        //make a search button object
        Button button = findViewById(R.id.search_button);

        //make a search text object
        final EditText query = findViewById(R.id.search_bar);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(query.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter a search query",Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    //do sum
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
            BookDetailsFragment bdf = (BookDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_book_details);
            assert bdf != null;

            //get the objects for the book from the xml
            TextView booktitle = findViewById(R.id.book_title_tv);
            TextView author = findViewById(R.id.author_tv);
            TextView published = findViewById(R.id.published_tv);
            //ImageView cover = rootView.findViewById(R.id.cover_iv);

            /*
            //now set the fields
            booktitle.setText(bdf.displayBookInfo(position));
            author.setText(bdf.displayBookInfo(position));
            published.setText(bdf.displayBookInfo(position));
            */
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
