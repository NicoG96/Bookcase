package com.example.bookcase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    String currentQuery;
    SharedPreferences prefs;
    SharedPreferences.Editor ed;

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", currentQuery);
    }

    @Override
    public void onPause() {
        super.onPause();

        ed.putString("query", currentQuery);
        ed.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);

        //find the listview and create its corresponding object
        booklist = v.findViewById(R.id.book_list);

        //create the adapter that will display the books
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, library);

        //start background task to populate list view
        if(savedInstanceState != null) {
            getBooks(savedInstanceState.getString("query"));
        } else {
            getBooks("https://kamorris.com/lab/audlib/booksearch.php");
        }

        //create listener for each book
        booklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.onBookSelected(position);
            }
        });

        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        ed = prefs.edit();
        ed.apply();
        return v;
    }

    public void getBooks(String site) {
        currentQuery = site;
        new getBookListTask(booklist, adapter).execute(site);
    }

    protected static class getBookListTask extends AsyncTask<String, Void, Void> {
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
                    book.setDuration(jBook.getInt("duration"));

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
            //reset the listview adapter to reflect changes
            booklist.setAdapter(ad);
        }
    }
}