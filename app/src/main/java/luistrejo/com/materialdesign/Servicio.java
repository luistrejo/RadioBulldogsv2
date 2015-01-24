package luistrejo.com.materialdesign;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Luis Trejo on 30/12/2014.
 */
public class Servicio extends Service {
    private static final String TAG = "Servicio";
    MediaPlayer player;
    String id = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Toast.makeText(this, "Servicio creado", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            player.setDataSource("http://192.168.0.109:8000");
            player.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Radio cancion = new Radio();
        id = cancion.id;

    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
     public int onStartCommand (Intent intent,int flags, int startid){

        Toast.makeText(this, "Servicio Iniciado", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        player.start();

        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Radio Bulldogs")
                .setContentText(id)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(pIntent)
                .setTicker("Radio Buldogs! La estacion de radio mas perra.")
                .build();


        // hide the notification after its selected
        noti.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(1337, noti);


        return START_NOT_STICKY;


    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Servicio detenido", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        player.stop();
        stopForeground(true);
    }

}
