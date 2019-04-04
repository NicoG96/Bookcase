package com.example.bookcase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import static com.example.bookcase.MainActivity.library;

public class BookDetailsFragment extends Fragment {
    TextView title;
    TextView author;
    TextView published;
    ImageView cover;

    public BookDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_book_details, container, false);

        Context ctx = getContext();
        assert ctx != null;

        //get the textview objects from the xml file
        title = rootView.findViewById(R.id.book_title_tv);
        author = rootView.findViewById(R.id.author_tv);
        published = rootView.findViewById(R.id.published_tv);
        cover = rootView.findViewById(R.id.cover_iv);

        //TRY to get a passed index if there is one
        try {
            int index = getArguments().getInt("index");

            //if there is, then display the fields of the book
            displayBookInfo(index);

        /* if there isn't, then this is a 2-panel device instantiating this fragment for first time,
        so we can just return the inflated view as is */
        } catch(NullPointerException e) {
            return rootView;
        }

        return rootView;
    }

    public void displayBookInfo(int position) {
        title.setText(library.get(position).getTitle());
        author.setText(library.get(position).getAuthor());
        published.setText(Integer.toString(library.get(position).getPublished()));
        new downloadImgTask(cover).execute(library.get(position).getCoverURL());
    }

    private static class downloadImgTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public downloadImgTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap image = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                image = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            bmImage.setImageBitmap(image);
        }
    }
}
