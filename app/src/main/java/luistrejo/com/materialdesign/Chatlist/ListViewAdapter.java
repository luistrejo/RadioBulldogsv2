package luistrejo.com.materialdesign.Chatlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import luistrejo.com.materialdesign.Chat;
import luistrejo.com.materialdesign.R;


/**
 * Created by Luis Trejo on 01/01/2015.
 */
public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<HashMap<String, String>> data;
    HashMap<String, String> resultp = new HashMap<String, String>();

    public ListViewAdapter(Context context,
                           ArrayList<HashMap<String, String>> arraylist) {
        this.context = context;
        data = arraylist;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        final TextView id;
        final TextView usuario;
        TextView comentario;
        TextView fecha;


        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View itemView = inflater.inflate(R.layout.chat_item, parent, false);
        // Get the position
        resultp = data.get(position);

        // Locate the TextViews in listview_item.xml
        id = (TextView) itemView.findViewById(R.id.id);
        usuario = (TextView) itemView.findViewById(R.id.usuario);
        comentario = (TextView) itemView.findViewById(R.id.comentario);
        fecha = (TextView) itemView.findViewById(R.id.fecha);


        // Capture position and set results to the TextViews
        id.setText(resultp.get(Chat.id));
        usuario.setText(resultp.get(Chat.usuario));
        comentario.setText(resultp.get(Chat.comentario));
        fecha.setText(resultp.get(Chat.fecha));


        final String user = usuario.getText().toString();
        ImageButton reporte = (ImageButton) itemView.findViewById(R.id.reportar);
        reporte.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                final String reportid = id.getText().toString();

                AlertDialog.Builder builder1 = new AlertDialog.Builder(ListViewAdapter.this.context);
                builder1.setMessage("¿Deseas denunciar este comentario del usuario " + user + " ?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Denunciar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Toast.makeText(ListViewAdapter.this.context, reportid, Toast.LENGTH_SHORT).show();
                                new reportar().execute(reportid);

                            }
                        });
                builder1.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog salir = builder1.create();
                salir.show();

            }
        });


        return itemView;
    }

    //envio del reporte al servidor
    public class reportar extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpClient httpclient;
            List<NameValuePair> nameValuePairs;
            HttpPost httppost;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost("http://192.168.0.109/radiobulldogE/reporte.php");
            String id = Arrays.toString(params).replaceAll(".*\\[|\\].*", "");
            //Añadimos los datos
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("id", id.trim()));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpclient.execute(httppost);
                Log.d("Reporte/ListViewAdapter", "Enviando reporte: " + id);
                return true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean == true) {

                Toast.makeText(ListViewAdapter.this.context, "Tu denuncia se ha enviado correctamente, muy pronto el administrador la revisara, gracias!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(ListViewAdapter.this.context, "Ocurrio un error al procesar tu solicitud, por favor intentalo de nuevo.", Toast.LENGTH_LONG).show();
            }

        }


    }
}

