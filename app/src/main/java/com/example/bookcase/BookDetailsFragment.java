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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.InputStream;
import static com.example.bookcase.MainActivity.library;

public class BookDetailsFragment extends Fragment {
    Book book;

    //book details
    TextView title;
    TextView author;
    TextView published;
    ImageView cover;

    //audio controls
    ImageButton play_btn;
    ImageButton pause_btn;
    ImageButton stop_btn;
    SeekBar seeker;

    public BookDetailsFragment() {}

    onAudioActionListener callback;

    public interface onAudioActionListener {
        void playBook(int book_id);
        void pauseBook();
        void stopBook();
        void setprogress(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (onAudioActionListener) context;
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
        View rootView = inflater.inflate(R.layout.fragment_book_details, container, false);

        final Context ctx = getContext();
        assert ctx != null;

        //get the textview objects from the xml file
        title = rootView.findViewById(R.id.book_title_tv);
        author = rootView.findViewById(R.id.author_tv);
        published = rootView.findViewById(R.id.published_tv);
        cover = rootView.findViewById(R.id.cover_iv);

        //get the audio objects
        play_btn = rootView.findViewById(R.id.play_btn);
        pause_btn = rootView.findViewById(R.id.pause_btn);
        stop_btn = rootView.findViewById(R.id.stop_btn);
        seeker = rootView.findViewById(R.id.seeker);

        //TRY to get a passed index if there is one
        try {
            int index = getArguments().getInt("index");

            //if there is, then display the fields of the book
            displayBookInfo(index);

        /* if there isn't, then this is a 2-panel device instantiating this fragment for first time,
        so we can just return the inflated view as is */
        } catch(NullPointerException e) {}

        //create click listeners
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.playBook(book.getId());
            }
        });

        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.pauseBook();
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.stopBook();
            }
        });

        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                callback.setprogress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }

    public void displayBookInfo(int position) {
        this.book = library.get(position);
        title.setText(library.get(position).getTitle());
        author.setText(library.get(position).getAuthor());
        published.setText(Integer.toString(library.get(position).getPublished()));
        new downloadImgTask(cover).execute(library.get(position).getCoverURL());
        play_btn.setVisibility(View.VISIBLE);
        pause_btn.setVisibility(View.VISIBLE);
        stop_btn.setVisibility(View.VISIBLE);
        seeker.setVisibility(View.VISIBLE);
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
