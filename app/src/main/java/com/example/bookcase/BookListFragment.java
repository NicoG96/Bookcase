package com.example.bookcase;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import static com.example.bookcase.MainActivity.library;

public class BookListFragment extends Fragment {
    ArrayAdapter<Book> adapter;
    ListView booklist;

    public BookListFragment() {}
    onBookSelectedListener callback;

    public interface onBookSelectedListener {
        void onBookSelected(int index);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (onBookSelectedListener) context;
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);

        //find the listview and create its corresponding object
        booklist = v.findViewById(R.id.book_list);

        //create the adapter that will display the books
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, library);

        //start background task to populate list view
        new getBookListTask(booklist, adapter).execute("https://kamorris.com/lab/audlib/booksearch.php");

        //create listener for each book
        booklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.onBookSelected(position);
            }
        });
        return v;
    }

    public void getBooks(final String site) {
        //thread to parse JSON data from website
        Thread t = new Thread(){
            @Override
            public void run(){
                URL resource;

                try {
                    resource = new URL(site);
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

                    //getBooks() called for booklist update, so we have to clear old library
                    if(!library.isEmpty()) {
                        library.clear();
                    }

                    //now transfer the members of this array into an array of book objects
                    for(int i = 0; i < arr.length(); i++) {
                        //make new book
                        final Book book = new Book();

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
                    Log.e("JSONException", "Error: " + e.toString());
                }
            }
        };
        t.start();
    }

    private static class getBookListTask extends AsyncTask<String, Void, Void> {
        ListView booklist;
        ArrayAdapter<Book> ad;

        public getBookListTask(ListView booklist, ArrayAdapter<Book> ad) {
            this.booklist = booklist;
            this.ad = ad;
        }

        @Override
        protected void onPreExecute() {
            //clear any old data in the library and adapter
            if(!library.isEmpty()) {
                library.clear();
            }
            ad.clear();
        }

        @Override
        protected Void doInBackground(String... url) {
            URL resource;

            try {
                resource = new URL(url[0]);
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
                    final Book book = new Book();

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
                Log.e("JSONException", "Error: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            //add the new library to the array
            ad.addAll(library);

            //display that new library in the list
            booklist.setAdapter(ad);
        }
    }
}