package luistrejo.com.materialdesign;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Luis Trejo on 30/12/2014.
 */
public class Servicio extends Service {
    private static final String TAG = "Servicio";
    public static MediaPlayer player;
    public static String idnoti = "", idnotiguardado = "";
    Handler mHandler = new Handler();
    NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Notification noti;
    public static Bitmap imgcaratula;
    int notifyID = 1;

    private void cambio() {
        if (idnoti.equals(idnotiguardado) == false) {
            idnotiguardado = idnoti;
            imgcaratula = null;
            Radio caratula = new Radio();


            //Cuando la imagen es null crash (arreglar)
            ///
            //
            if (caratula.caratulaimg != null) {
                imgcaratula = Bitmap.createScaledBitmap(caratula.caratulaimg, 125, 125, false);
                builder.setLargeIcon(imgcaratula);
                builder.setContentText(idnoti);
                mNotificationManager.notify(notifyID, builder.build());
            } else {
                builder.setLargeIcon(null);
                builder.setContentText(idnoti);
                mNotificationManager.notify(notifyID, builder.build());
            }

        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate() {
        Toast.makeText(this, "Servicio creado",
                Toast.LENGTH_SHORT).show();
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
            Log.d(TAG, "Fuera de servicio");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    try {
                        Thread.sleep(3000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                cambio();
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {

        Toast.makeText(this, "Servicio Iniciado", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        player.start();
        Radio.corriendo = true;
        Intent intent1 = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setTicker("Radio Bulldogs: La estacion de radio mas perra!")
                .setContentTitle("Radio Bulldogs")
                .setContentText(idnoti)
                .setLargeIcon(imgcaratula)
                .setContentIntent(pIntent);
        noti = builder.build();
        noti.flags = Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(notifyID, builder.build());

        startForeground(notifyID, noti);

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Servicio detenido", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        player.stop();
        Radio.corriendo = false;
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }
}
