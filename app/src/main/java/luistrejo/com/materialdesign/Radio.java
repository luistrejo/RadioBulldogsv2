package luistrejo.com.materialdesign;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
    ImageView caratula;
    public static String id, name;
    View rootView;
    String idguardado;
    public static Bitmap caratulaimg, fondo;
    Context context;
    BitmapDrawable fondofinal;
    JSONObject json;
    JSONObject c;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
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
            new informacioncanciones().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new informacioncanciones().execute();
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
                                    new informacioncanciones().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new informacioncanciones().execute();

                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();

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

    public class informacioncanciones extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                JSONParser jParser = new JSONParser();
                json = jParser.getJSONFromUrl(url);
            } catch (NullPointerException e) {
                Log.d(TAG, "fuera");
            }
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                user = json.getJSONArray(TAG_USER);
                c = null;
                c = user.getJSONObject(0);
                name = c.getString(TAG_NAME);
                Servicio cancion = new Servicio();
                cancion.idnoti = id;

                if (name == null) {
                    name = "No disponible";
                }
            } catch (NullPointerException e) {
                Log.d(TAG, "Fuerda de linea");
                id = "Fuera de linea";
                name = "Fuera de linea";
            } catch (JSONException e) {
                name = "No disponible";
                Log.d(TAG, "No disponible");
            }
            try {
                id = c.getString(TAG_ID);
            } catch (NullPointerException e) {
                id = "Fuera de linea";
            } catch (JSONException e) {
                Log.d(TAG, id);
            }


            if (id.equals(idguardado) == false) {
                idguardado = id;


                uid.setText(id);
                name1.setText(name);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new caratulayfondo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new caratulayfondo().execute();
                }
            }


        }


    }

    private class caratulayfondo extends AsyncTask<Void, Void, Void> {


        protected Void doInBackground(Void... urls) {
            String urlcaratula = "http://192.168.0.109:8000/playingart?sid=1";
            caratulaimg = null;
            fondo = null;

            try {
                InputStream in = new java.net.URL(urlcaratula).openStream();
                caratulaimg = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 10;
            try {
                InputStream in = new java.net.URL(urlcaratula).openStream();
                fondo = BitmapFactory.decodeStream(in, null, options);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (fondo == null) {
                caratula.setImageResource(R.drawable.bull);
                rootView.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
                Log.d(TAG, "Sin caratula y fondo que mostrar");
            } else {
                caratula.setImageBitmap(caratulaimg);
                try {
                    Bitmap outputBitmap = Bitmap.createBitmap(fondo.getHeight(),
                            fondo.getWidth(), Bitmap.Config.ARGB_8888);

                    RenderScript rs = RenderScript.create(context);
                    ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur
                            .create(rs, Element.U8_4(rs));
                    Allocation tmpIn = Allocation.createFromBitmap(rs, fondo);
                    Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
                    theIntrinsic.setRadius(20);
                    theIntrinsic.setInput(tmpIn);
                    theIntrinsic.forEach(tmpOut);
                    tmpOut.copyTo(outputBitmap);

                    fondofinal = new BitmapDrawable(getResources(), outputBitmap);
                } catch (IllegalStateException e) {
                    Log.d(TAG, "Error cargando Renderscript");
                }


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    rootView.setBackgroundDrawable(fondofinal);
                } else {
                    rootView.setBackground(fondofinal);
                }
                Log.d(TAG, "Caratula y fondo actualizados.");

            }
        }
    }
}