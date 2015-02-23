package luistrejo.com.materialdesign;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.CustomView;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import luistrejo.com.materialdesign.Chatlist.JSONfunctions;
import luistrejo.com.materialdesign.Chatlist.ListViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    private EditText mensaje;
    private CustomView enviar;
    private com.gc.materialdesign.widgets.ProgressDialog pDialog;

    // Declaramos variables para listview
    JSONObject jsonobject;
    JSONArray jsonarray;
    ListView listview;
    ListViewAdapter adapter;
    ArrayList<HashMap<String, String>> arraylist;
    public static String usuario = "usuario";
    public static String comentario = "comentario";
    public static String fecha = "fecha";
    Handler mHandler = new Handler();
    String TAG = "Chat";
    ArrayList<HashMap<String, String>> valor, valornuevo;
    HashMap<String, String> map;
    int longitudguardada;
    TextView error;
    Boolean cambio, errorb;
    ProgressBarCircularIndeterminate circulo;
    ImageView errorimg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        //corremos metodo para que carge listview

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new DownloadJSON().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new DownloadJSON().execute();
        }
        mensaje = (EditText) rootView.findViewById(R.id.etMensaje);
        enviar = (CustomView) rootView.findViewById(R.id.Enviar);
        error = (TextView) rootView.findViewById(R.id.mensajeconexion);
        errorimg = (ImageView) rootView.findViewById(R.id.errorimg);
        circulo = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.cargandocomentarios);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mensaje.getText().toString().trim().equalsIgnoreCase(""))
                    new Enviar(Chat.this.getActivity()).execute();
                else
                    Toast.makeText(Chat.this.getActivity(), "Ups! Tal parece que no has escrito nada.", Toast.LENGTH_LONG).show();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(10000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new DownloadJSON().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new DownloadJSON().execute();
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

    //enviamos el mensaje escrito al servidor
    private boolean enviar() {
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient = new DefaultHttpClient();
        httppost = new HttpPost("http://192.168.1.64/radiobulldogE/public/comentar");

        //Consultamos valor usuario del shared preferences
        SharedPreferences settings = getActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String usuario = settings.getString("id", "0");
        //Añadimos los datos que vamos a enviar
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario.toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("comentario", mensaje.getText().toString().trim()));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);

            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private class Enviar extends AsyncTask<String, String, String> {
        private Activity context;

        Enviar(Activity context) {
            this.context = context;
        }

        protected void onPreExecute() {
            //para el progress dialog
            pDialog = new com.gc.materialdesign.widgets.ProgressDialog(Chat.this.getActivity(), "Enviando Comentario", R.color.azulfuerte);
            pDialog.show();
        }

        protected String doInBackground(String... params) {
            if (enviar())
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Mensaje enviado.", Toast.LENGTH_SHORT).show();
                        mensaje.setText("");
                        //Actualizamos la lista de comentarios cada vez que se envie un mensaje
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new DownloadJSON().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new DownloadJSON().execute();
                        }
                    }

                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Mensaje no enviado, intenta de nuevo en unos segundos.", Toast.LENGTH_LONG).show();
                    }
                });
            return null;

        }

        protected void onPostExecute(String result) {

            pDialog.dismiss();


        }

    }

    // Clase que carga el listview con los datos del servidor
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            cambio = false;
            errorb = false;
            //jsonobject = JSONfunctions.getJSONfromURL("http://192.168.0.109/RadioB/GetData.php");
            jsonobject = JSONfunctions.getJSONfromURL(null);
            try {
                jsonarray = jsonobject.getJSONArray("comentarios");
            } catch (NullPointerException e) {
                Log.d(TAG, "Fuera de linea");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonarray != null) {
                if (jsonarray.length() != longitudguardada) {
                    Log.d(TAG, "Diferente chat");
                    cambio = true;
                    longitudguardada = jsonarray.length();
                    arraylist = new ArrayList<HashMap<String, String>>();
                    valor = new ArrayList<HashMap<String, String>>();
                    valornuevo = new ArrayList<HashMap<String, String>>();

                    for (int i = 0; i < jsonarray.length(); i++) {
                        try {

                            map = new HashMap<String, String>();
                            jsonobject = jsonarray.getJSONObject(i);
                            // Retrive JSON Objects
                            map.put("usuario", jsonobject.getString("usuario"));
                            map.put("comentario", jsonobject.getString("comentario"));
                            map.put("fecha", jsonobject.getString("fecha"));
                            // Set the JSON Objects into the array
                            arraylist.add(map);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Log.d(TAG, "Mismo chat");
                }
            } else {
                Log.d(TAG, "Json array null");
                errorb = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            circulo.setVisibility(View.GONE);
            if (cambio == true) {
                try {
                    listview = (ListView) getActivity().findViewById(R.id.listview);
                    adapter = new ListViewAdapter(getActivity(), arraylist);
                    listview.setAdapter(adapter);
                } catch (Exception e) {
                    Log.d(TAG, "Error al cargar lista de comentarios");
                }
            } else {
            }

            if (errorb == true) {
                error.setVisibility(View.VISIBLE);
                errorimg.setVisibility(View.VISIBLE);
            } else {
                error.setVisibility(View.GONE);
                errorimg.setVisibility(View.GONE);
            }
        }


    }

}




