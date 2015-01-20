package luistrejo.com.materialdesign;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import luistrejo.com.materialdesign.Radiocanciones.JSONParser;

/**
 * A simple {@link Fragment} subclass.
 */
public class Radio extends Fragment implements View.OnClickListener {

    private static final String TAG = "Radio";
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    Button buttonStart, buttonStop;
    public String url = "http://192.168.0.109/RadioB/pag.php";
    private static final String TAG_USER = "streams";
    private static final String TAG_ID = "songtitle";
    private static final String TAG_NAME = "nexttitle";
    JSONArray user = null;
    TextView uid, name1;
    Handler mHandler = new Handler();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_radio, container, false);

        initControls();
        volumeSeekbar = (SeekBar) rootView.findViewById(R.id.seekBar1);
        audioManager = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        buttonStart = (Button) rootView.findViewById(R.id.startPlaying);
        buttonStop = (Button) rootView.findViewById(R.id.buttonStopPlay);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        uid = (TextView) rootView.findViewById(R.id.titulocancion);
        name1 = (TextView) rootView.findViewById(R.id.nextcancion);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new Canciones().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new Canciones().execute();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(5000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new Canciones().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new Canciones().execute();
                                }                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

        new DownloadImageTask((ImageView)rootView.findViewById(R.id.caratula))
                .execute("http://192.168.0.109:8000/playingart?sid=1");

        return rootView;
    }

    @Override

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startPlaying:
                Log.d(TAG, "onClick: Starting service");
                getActivity().startService(new Intent(getActivity(), Servicio.class));
                break;
            case R.id.buttonStopPlay:
                Log.d(TAG, "onClick: Deteniendo servicio");
                getActivity().stopService(new Intent(getActivity(), Servicio.class));
                break;
        }
        if (v == buttonStart) {
            buttonStop.setEnabled(true);
            buttonStart.setEnabled(false);
        } else if (v == buttonStop) {
            buttonStop.setEnabled(false);
            buttonStart.setEnabled(true);
        }


    }


    // control de volumen con el seckbar
    private void initControls() {
        try {
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Canciones extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                // Getting JSON Array
                user = json.getJSONArray(TAG_USER);
                JSONObject c = user.getJSONObject(0);
                // Storing  JSON item in a Variable
                String id = c.getString(TAG_ID);
                String name = c.getString(TAG_NAME);
                //Set JSON Data in TextView
                uid.setText(id);
                name1.setText(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
