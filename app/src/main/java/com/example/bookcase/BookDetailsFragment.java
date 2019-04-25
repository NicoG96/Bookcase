package com.example.bookcase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    //delete/download button
    Button dl_btn;
    SeekBar dl_progress;

    public BookDetailsFragment() {}

    onAudioActionListener callback;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos = msg.what;
            seeker.setProgress(pos);
        }
    };

    public interface onAudioActionListener {
        void playBook(File file);
        void streamBook(int book_id);
        void pauseBook();
        void stopBook();
        void setProgHand(Handler handler);
        void setProgress(int position, boolean fromUser);
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

        //get download/delete button
        dl_btn = rootView.findViewById(R.id.download_btn);
        dl_progress = rootView.findViewById(R.id.dl_progress);

        //TRY to get a passed index if there is one
        try {
            int index = getArguments().getInt("index");

            //if there is, then display the fields of the book
            displayBookInfo(index, ctx);

        /* if there isn't, then this is a 2-panel device instantiating this fragment for first time,
        so we can just return the inflated view as is */
        } catch(NullPointerException e) {}

        //create click listeners
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeker.setMax(book.getDuration());

                //check if the file exists on the device first
                File file = new File(ctx.getFilesDir().getPath() + "/" + book.getId() + ".mp3");
                if(file.exists()) {
                    Toast.makeText(ctx, "Playing from device", Toast.LENGTH_LONG).show();
                    callback.playBook(file);

                //otherwise, just stream it
                } else {
                    Toast.makeText(ctx, "Streaming to device", Toast.LENGTH_LONG).show();
                    callback.streamBook(book.getId());
                }

                //start updating the seekbar
                seeker.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setProgHand(handler);
                    }
                });
            }
        });

        dl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                // check if the file exists on the device already
                File file = new File(ctx.getFilesDir().getPath() + "/" + book.getId() + ".mp3");

                //if it doesn't...
                if(!file.exists()) {
                    //...then download the file
                    String query = "https://kamorris.com/lab/audlib/download.php?id=" + Integer.toString(book.getId());

                    //and do it in background
                    new downloadBook(dl_progress, dl_btn, ctx).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);

                //otherwise it's a delete button
                } else {
                    boolean deleted = file.delete();
                    if(!deleted) {
                        Toast.makeText(ctx, "Error deleting file", Toast.LENGTH_LONG).show();
                        return;
                    }
                    //update button text to communicate to the user that the audiobook has been deleted
                    Toast.makeText(ctx, "File deleted!", Toast.LENGTH_LONG).show();
                    dl_btn.setText(R.string.download_txt);
                }
            }
        });

        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.pauseBook();
                seeker.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setProgHand(handler);
                    }
                });
            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.stopBook();
                seeker.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.setProgHand(handler);
                    }
                });
            }
        });

        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                callback.setProgress(progress, fromUser);
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

    public void displayBookInfo(int position, Context ctx) {
        this.book = library.get(position);
        title.setText(library.get(position).getTitle());
        author.setText(library.get(position).getAuthor());
        published.setText(Integer.toString(library.get(position).getPublished()));
        new downloadImgTask(cover).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, library.get(position).getCoverURL());

        //display audio controls
        play_btn.setVisibility(View.VISIBLE);
        pause_btn.setVisibility(View.VISIBLE);
        stop_btn.setVisibility(View.VISIBLE);
        seeker.setVisibility(View.VISIBLE);

        dl_btn.setVisibility(View.VISIBLE);

        //check if the book has been downloaded before, change display text if so
        File file = new File(ctx.getFilesDir().getPath() + "/" + book.getId() + ".mp3");

        if(file.exists()) {
            dl_btn.setText(R.string.delete_txt);
        } else {
            dl_btn.setText(R.string.download_txt);
        }
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

    private static class downloadBook extends AsyncTask<String, Integer, String> {
        SeekBar dl_progress;
        Button dl_btn;
        Context ctx;
        char id;

        public downloadBook(SeekBar dl_progress, Button dl_btn, Context ctx) {
            this.dl_progress = dl_progress;
            this.dl_btn = dl_btn;
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //make download progress bar visible
            dl_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            id = sUrl[0].charAt(sUrl[0].length() - 1);
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(ctx.getFilesDir().getPath() + "/" + id + ".mp3");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if(isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if(fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch(Exception e) {
                return e.toString();
            } finally {
                try {
                    if(output != null)
                        output.close();
                    if(input != null)
                        input.close();
                } catch(IOException ignored) {
                }

                if(connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            dl_progress.setIndeterminate(false);
            dl_progress.setMax(100);
            dl_progress.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //hide the download bar since download is finished
            dl_progress.setVisibility(View.INVISIBLE);

            //check if file downloaded
            if(result != null) {
                Toast.makeText(ctx, "Download error: " + result, Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(ctx, "File downloaded", Toast.LENGTH_SHORT).show();
                //update button text to communicate to the user that the audiobook has downloaded
                dl_btn.setText(R.string.delete_txt);
            }
        }
    }
}