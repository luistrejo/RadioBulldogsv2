package luistrejo.com.materialdesign;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    ImageButton buttonStart, subir, bajar;
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
    public static boolean corriendo;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private AudioManager myAudioManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_radio, container, false);
        buttonStart = (ImageButton) rootView.findViewById(R.id.startPlaying);
        buttonStart.setOnClickListener(this);
        subir = (ImageButton) rootView.findViewById(R.id.Subir);
        subir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });
        bajar = (ImageButton) rootView.findViewById(R.id.Bajar);
        bajar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        });
        myAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        caratula = (ImageView) rootView.findViewById(R.id.caratula);
        uid = (TextView) rootView.findViewById(R.id.titulocancion);
        name1 = (TextView) rootView.findViewById(R.id.nextcancion);
        uid.setSelected(true);
        name1.setSelected(true);
        settings = getActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        editor = settings.edit();
        Boolean presionado = settings.getBoolean("play", true);
        if (presionado == true) {
            Log.d(TAG, "Presionado");
            Drawable play1 = getResources().getDrawable(R.drawable.ic_play);
            buttonStart.setImageDrawable(play1);
        } else {
            Log.d(TAG, "No presionado");
            Drawable stop = getResources().getDrawable(R.drawable.ic_stop);
            buttonStart.setImageDrawable(stop);
        }
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


                if (corriendo) {
                    Log.d(TAG, "onClick: Deteniendo servicio");
                    getActivity().stopService(new Intent(getActivity(), Servicio.class));
                    Drawable play = getResources().getDrawable(R.drawable.ic_play);
                    buttonStart.setImageDrawable(play);
                    editor.putBoolean("play", true);
                    editor.commit();
                } else {
                    Log.d(TAG, "onClick: Starting service");
                    getActivity().startService(new Intent(getActivity(), Servicio.class));
                    Drawable stop = getResources().getDrawable(R.drawable.ic_stop);
                    buttonStart.setImageDrawable(stop);
                    editor.putBoolean("play", false);
                    editor.commit();
                }
                break;
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
                buttonStart.setEnabled(true);
                if (name == null) {
                    name = "No disponible";
                }
            } catch (NullPointerException e) {
                Log.d(TAG, "Fuerda de linea");
                id = "Fuera de linea";
                name = "Fuera de linea";
                buttonStart.setEnabled(false);
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
            options.inSampleSize = 8;
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
                caratula.setImageResource(R.drawable.sincaratula);
                try {
                    rootView.setBackgroundResource(R.drawable.fondochat);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Not atachet to activity");
                }
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
                    theIntrinsic.setRadius(10);
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