package luistrejo.com.materialdesign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import luistrejo.com.materialdesign.Loginaux.Httppostaux;


public class Login extends Activity {
    EditText user;
    EditText pass;
    Button blogin;
    Httppostaux post;
    com.gc.materialdesign.widgets.ProgressDialog dialog;
    String URL_connect = "http://192.168.0.109/radiobulldogE/public/login";//ruta en donde estan nuestros archivos
    String usuario;
    boolean result_back;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        post = new Httppostaux();
        user = (EditText) findViewById(R.id.etemail);
        pass = (EditText) findViewById(R.id.etpass);
        blogin = (Button) findViewById(R.id.btlogin);
        settings = getSharedPreferences("usuario", MODE_PRIVATE);
        editor = settings.edit();
        TextView newcuenta = (TextView) findViewById(R.id.newcuenta);
        estatus();

        blogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                //Extreamos datos de los EditText
                usuario = user.getText().toString();
                String passw = pass.getText().toString();

                //verificamos si estan en blanco
                if (checklogindata(usuario, passw) == true) {

                    //si pasamos esa validacion ejecutamos el asynctask pasando el usuario y clave como parametros
                    new asynclogin().execute(usuario, passw);

                    //Guardamos el valor del usuario en un shared preferences
                    settings = getSharedPreferences("usuario", MODE_PRIVATE);
                    editor = settings.edit();
                    editor.clear();
                    editor.putString("usuario", usuario);
                    editor.commit();


                } else {
                    //si detecto un error en la primera validacion vibrar y mostrar un Toast con un mensaje de error.
                    err_login();
                }

            }
        });

        newcuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, Registro.class);
                startActivity(i);
            }
        });


    }

    //evaluamos el estatus del login
    //si ya esta logueado es true y lo redireccionamos a la activity principal

    public void estatus() {
        boolean getStatus = settings.getBoolean("login", false);
        if (getStatus == true) {
            Intent Main = new Intent(this, MainActivity.class);
            startActivity(Main);
        } else {
            return;
        }

    }

    //vibra y muestra un Toast
    public void err_login() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast toast1 = Toast.makeText(this, R.string.errorlogin, Toast.LENGTH_SHORT);
        toast1.show();
    }


    /*Valida el estado del logueo solamente necesita como parametros el usuario y passw*/
    public boolean loginstatus(String username, String password) {
        int logstatus = -1;
        id = "-1";
        /*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
         * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/
        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();
        postparameters2send.add(new BasicNameValuePair("usuario", username));
        postparameters2send.add(new BasicNameValuePair("password", password));

        //realizamos una peticion y como respuesta obtenes un array JSON
        JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);
        SystemClock.sleep(700);

        //si lo que obtuvimos no es null
        if (jdata != null && jdata.length() > 0) {

            JSONObject json_data; //creamos un objeto JSON
            try {
                json_data = jdata.getJSONObject(0); //leemos el primer segmento en nuestro caso el unico
                logstatus = json_data.getInt("logstatus");//accedemos al valor
                id = json_data.getString("id");

                Log.e("loginstatus", "logstatus= " + logstatus);//muestro por log que obtuvimos
                Log.e("id", "id= " + id);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //validamos el valor obtenido
            if (logstatus == 0) {// [{"logstatus":"0"}]
                Log.e("loginstatus ", "invalido");
                return false;
            } else {// [{"logstatus":"1"}]
                Log.e("loginstatus ", "valido");
                return true;
            }

        } else {    //json obtenido invalido verificar parte WEB.
            Log.e("JSON  ", "ERROR");
            return false;
        }

    }


    //validamos si no hay ningun campo en blanco
    public boolean checklogindata(String username, String password) {

        if (username.equals("") || password.equals("")) {
            Log.e("Login ui", "checklogindata user or pass error");
            return false;

        } else {

            return true;
        }

    }

/*		CLASE ASYNCTASK
 *
 * usaremos esta para poder mostrar el dialogo de progreso mientras enviamos y obtenemos los datos
 * podria hacerse lo mismo sin usar esto pero si el tiempo de respuesta es demasiado lo que podria ocurrir
 * si la conexion es lenta o el servidor tarda en responder la aplicacion sera inestable.
 * ademas observariamos el mensaje de que la app no responde.
 */

    class asynclogin extends AsyncTask<String, String, String> {

        String user, pass;

        protected void onPreExecute() {
            dialog = new com.gc.materialdesign.widgets.ProgressDialog(Login.this, "Autentificando", R.color.azulfuerte);
            dialog.show();
        }

        protected String doInBackground(String... params) {
            //obtnemos usr y pass
            user = params[0];
            pass = params[1];

            //enviamos y recibimos y analizamos los datos en segundo plano.
            if (loginstatus(user, pass) == true) {
                return "ok"; //login valido
            } else {
                return "err"; //login invalido
            }

        }

        /*Una vez terminado doInBackground segun lo que halla ocurrido
        pasamos a la sig. activity
        o mostramos error*/
        protected void onPostExecute(String result) {

            dialog.dismiss();
            Log.e("onPostExecute=", "" + result);

            if (result.equals("ok")) {
                editor.putString("id", id);
                editor.putBoolean("login", true);
                editor.commit();
                //Si el login fue valido redireccionamos a la main
                Intent i = new Intent(Login.this, MainActivity.class);
                i.putExtra("user", user);
                startActivity(i);

            } else {
                err_login();
            }

        }


    }

    //evitar que vuelva a la actividad principal
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}