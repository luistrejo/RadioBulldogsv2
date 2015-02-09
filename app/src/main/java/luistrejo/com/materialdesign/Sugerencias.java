package luistrejo.com.materialdesign;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gc.materialdesign.views.CustomView;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Sugerencias extends Fragment {
    EditText sugerencia;
    CustomView enviar;

    public Sugerencias() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sugerencias, container, false);

        sugerencia = (EditText) rootView.findViewById(R.id.etsugerencia);
        enviar = (CustomView) rootView.findViewById(R.id.enviarsugerencia);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sugerencia.getText().toString().trim().equalsIgnoreCase(""))
                    new Enviar(Sugerencias.this.getActivity()).execute();
                else
                    Toast.makeText(Sugerencias.this.getActivity(), "Ups! Tal parece que no has escrito nada.", Toast.LENGTH_LONG).show();
            }
        });
        return rootView;
    }

    private class Enviar extends AsyncTask<String, String, String> {
        private Activity context;

        Enviar(Activity context) {
            this.context = context;
        }

        protected String doInBackground(String... params) {
            if (enviar())
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Sugerencia enviada, gracias por tu valiosa opinion.", Toast.LENGTH_SHORT).show();
                        sugerencia.setText("");
                        //Actualizamos la lista de comentarios cada vez que se envie un mensaje

                    }

                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Sugerencia no enviada, intenta de nuevo en unos segundos.", Toast.LENGTH_LONG).show();
                    }
                });
            return null;

        }

    }

    private boolean enviar() {
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient = new DefaultHttpClient();
        httppost = new HttpPost("http://192.168.0.109/RadioB/insertarsugerencia.php");

        //Consultamos valor usuario del shared preferences
        SharedPreferences settings = getActivity().getSharedPreferences("usuario", Context.MODE_PRIVATE);
        String usuario = settings.getString("usuario", "?");

        //Añadimos los datos que vamos a enviar
        nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario.toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("sugerencia", sugerencia.getText().toString().trim()));

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


}
