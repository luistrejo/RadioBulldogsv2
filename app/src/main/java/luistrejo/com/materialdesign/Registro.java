package luistrejo.com.materialdesign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.List;

import luistrejo.com.materialdesign.Loginaux.Httppostaux;


public class Registro extends ActionBarActivity {
    String TAG = "Registro";
    private EditText nombre;
    private EditText paterno;
    private EditText materno;
    private EditText usuario;
    private EditText contrasena;
    private Spinner especialidad;
    private Button insertar;
    Httppostaux post;
    String URL_connect = "http://192.168.0.109/RadioB/userexistente/acces.php";//ruta en donde estan nuestros archivos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        post = new Httppostaux();

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        nombre = (EditText) findViewById(R.id.etnombre);
        paterno = (EditText) findViewById(R.id.etpaterno);
        materno = (EditText) findViewById(R.id.etmaterno);
        usuario = (EditText) findViewById(R.id.etusuario);
        contrasena = (EditText) findViewById(R.id.etcontraseña);
        especialidad = (Spinner) findViewById(R.id.spinEspecialidad);
        insertar = (Button) findViewById(R.id.btregistrar);
        Button cancelar = (Button) findViewById(R.id.cancelar);
        //Registro button action
        insertar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String usuario1 = usuario.getText().toString();

                if (!nombre.getText().toString().trim().equalsIgnoreCase("") ||
                        !paterno.getText().toString().trim().equalsIgnoreCase("") ||
                        !materno.getText().toString().trim().equalsIgnoreCase("") ||
                        !usuario.getText().toString().trim().equalsIgnoreCase("") ||
                        !contrasena.getText().toString().trim().equalsIgnoreCase("") ||
                        !especialidad.getSelectedItem().toString().trim().equalsIgnoreCase(""))

                    //si pasamos esa validacion ejecutamos el asynctask pasando el usuario y clave como parametros
                    new asynclogin().execute(usuario1);
                else {
                    vibrator.vibrate(200);
                    Toast.makeText(Registro.this, "Ups! Tal parece que no has escrito nada.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Composicion del array con las especialidades que estan en arrays.xml
        Spinner sp = (Spinner) findViewById(R.id.spinEspecialidad);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.especialidades, android.R.layout.simple_spinner_item);
        sp.setAdapter(adapter);

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(Registro.this, Login.class);
                startActivity(login);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //envio de datos al servidor
    private boolean insertar() {
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient = new DefaultHttpClient();
        httppost = new HttpPost("http://192.168.0.109/RadioB/nuevousuario.php");
        //Añadimos los datos
        nameValuePairs = new ArrayList<NameValuePair>(6);
        nameValuePairs.add(new BasicNameValuePair("nombre", nombre.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("paterno", paterno.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("materno", materno.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("usuario", usuario.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("contrasena", contrasena.getText().toString().trim()));
        nameValuePairs.add(new BasicNameValuePair("especialidad", especialidad.getSelectedItem().toString().trim()));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
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

    public class Insertar extends AsyncTask<String, String, String> {
        private Activity context;

        Insertar(Activity context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            if (insertar())
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Registro exitoso.", Toast.LENGTH_LONG).show();
                        nombre.setText("");
                        paterno.setText("");
                        materno.setText("");
                        usuario.setText("");
                        contrasena.setText("");
                        Intent i = new Intent(Registro.this, Login.class);
                        startActivity(i);
                    }
                });
            else
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Algo ha salido mal :(, intentalo de nuevo.", Toast.LENGTH_LONG).show();
                    }
                });
            return null;

        }
    }


    //Valida el estado del logueo solamente necesita como parametros el usuario y passw*/

    public boolean loginstatus(String username) {
        int logstatus = -1;

    	/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
         * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/
        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("usuario", username));

        //realizamos una peticion y como respuesta obtenes un array JSON
        JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);

      		/*como estamos trabajando de manera local el ida y vuelta sera casi inmediato
               * para darle un poco realismo decimos que el proceso se pare por unos segundos para poder
      		 * observar el progressdialog
      		 * la podemos eliminar si queremos
      		 */
        //SystemClock.sleep(700);

        //si lo que obtuvimos no es null
        if (jdata != null && jdata.length() > 0) {

            JSONObject json_data; //creamos un objeto JSON
            try {
                json_data = jdata.getJSONObject(0); //leemos el primer segmento en nuestro caso el unico
                logstatus = json_data.getInt("logstatus");//accedemos al valor
                Log.e("loginstatus", "logstatus= " + logstatus);//muestro por log que obtuvimos
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //validamos el valor obtenido
            if (logstatus == 0) {// [{"logstatus":"0"}]
                Log.e("usuariovalidacion ", "Valido");
                return false;
            } else {// [{"logstatus":"1"}]
                Log.e("usuariovalidacion ", "Este usuario ya existe.");
                return true;
            }

        } else {    //json obtenido invalido verificar parte WEB.
            Log.e("JSON  ", "ERROR");
            return false;
        }

    }

    class asynclogin extends AsyncTask<String, String, String> {

        String usuario;

        protected void onPreExecute() {
            //para el progress dialog

        }

        protected String doInBackground(String... params) {
            //obtnemos usr y pass
            usuario = params[0];

            //enviamos y recibimos y analizamos los datos en segundo plano.
            if (loginstatus(usuario) == true) {
                return "ok"; //login valido
            } else {
                return "err"; //login invalido
            }

        }

        //Una vez terminado doInBackground segun lo que halla ocurrido
        // pasamos a la sig. tarea que es registrar los datos en la bd
        protected void onPostExecute(String result) {

            //pDialog.dismiss();//ocultamos progess dialog.
            Log.e("onPostExecute=", "" + result);

            if (result.equals("ok")) {
                usuarioexistente();

            } else {
                Log.d(TAG, "Usuario valido, pasamos a registrarlos a la bd.");
                new Insertar(Registro.this).execute();
            }

        }

    }

    public void usuarioexistente() {
        Vibrator vibrator1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator1.vibrate(200);
        Toast.makeText(Registro.this, "Nombre de usuario ya en uso, por favor intenta con otro.", Toast.LENGTH_LONG).show();
    }

}
