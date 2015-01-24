package luistrejo.com.materialdesign;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
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



import java.io.InputStream;


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
    Handler mHandler2 = new Handler();
    ImageView caratula;
    public static String id = "", name = "";
    View rootView;
    Bitmap caratulaguardada, fondoguardada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_radio, container, false);

        initControls();
        volumeSeekbar = (SeekBar) rootView.findViewById(R.id.seekBar1);
        audioManager = (AudioManager) this.getActivity().getSystemService(Context.AUDIO_SERVICE);
        buttonStart = (Button) rootView.findViewById(R.id.startPlaying);
        buttonStop = (Button) rootView.findViewById(R.id.buttonStopPlay);
        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        caratula = (ImageView) rootView.findViewById(R.id.caratula);
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
                        Thread.sleep(8000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new Canciones().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new Canciones().execute();
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new DownloadImageTask((ImageView) rootView.findViewById(R.id.caratula))
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.0.109:8000/playingart?sid=1");
            new descargafondo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new DownloadImageTask((ImageView) rootView.findViewById(R.id.caratula))
                    .execute("http://192.168.0.109:8000/playingart?sid=1");
            new descargafondo().execute();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(8000);
                        mHandler2.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new DownloadImageTask((ImageView) rootView.findViewById(R.id.caratula))
                                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "http://192.168.0.109:8000/playingart?sid=1");
                                    new descargafondo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new DownloadImageTask((ImageView) rootView.findViewById(R.id.caratula))
                                            .execute("http://192.168.0.109:8000/playingart?sid=1");
                                    new descargafondo().execute();

                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

        caratulaguardada = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);
        fondoguardada = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);

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

    public class Canciones extends AsyncTask<String, String, JSONObject> {

        @Override
        public JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        public void onPostExecute(JSONObject json) {

            try {
                // Getting JSON Array
                user = json.getJSONArray(TAG_USER);
                JSONObject c = user.getJSONObject(0);
                // Storing  JSON item in a Variable
                id = c.getString(TAG_ID);
                name = c.getString(TAG_NAME);

                //Set JSON Data in TextView
                uid.setText(id);
                name1.setText(name);

            } catch (NullPointerException e) {
                Log.d(TAG, "Fuera de linea");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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

            if (result == null) {
                bmImage.setImageResource(R.drawable.bull);
                Log.d(TAG, "Caratula vacia");

            } else {
                if (result.sameAs(caratulaguardada) == false) {
                    bmImage.setImageBitmap(result);
                    caratulaguardada = Bitmap.createBitmap(result);

                } else {
                    Log.d(TAG, "Misma caratula");
                }
            }
        }
    }

    public class descargafondo extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = "http://192.168.0.109:8000/playingart?sid=1";
            Bitmap fondo = null;
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                fondo = BitmapFactory.decodeStream(in, null, options);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return fondo;
        }

        protected void onPostExecute(Bitmap result) {

            if (result == null) {
                rootView.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
                Log.d(TAG, "Fondo vacio");

            } else {
                if (result.sameAs(fondoguardada) == false) {
                    fondoguardada = Bitmap.createBitmap(result);

                    Bitmap outputBitmap = Bitmap.createBitmap(result.getHeight(),
                            result.getWidth(), Bitmap.Config.ARGB_8888);

                    RenderScript rs = RenderScript.create(Radio.this.getActivity());
                    ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur
                            .create(rs, Element.U8_4(rs));
                    Allocation tmpIn = Allocation.createFromBitmap(rs, result);
                    Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
                    theIntrinsic.setRadius(20);
                    theIntrinsic.setInput(tmpIn);
                    theIntrinsic.forEach(tmpOut);
                    tmpOut.copyTo(outputBitmap);


                    BitmapDrawable fondo = new BitmapDrawable(getResources(), outputBitmap);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        rootView.setBackgroundDrawable(fondo);
                    } else {

                        rootView.setBackground(fondo);
                    }
                    Log.d(TAG, "Fondo actualizado");
                } else {
                    Log.d(TAG, "Mismo fondo");
                }


            }
        }

    }


}
