package luistrejo.com.materialdesign;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Luis Trejo on 08/02/2015.
 */
public class NotiPush extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "QhILlmjfJb85SIPeTeAiDogPfUhkEDoTnZGYSgO6",
                "ZTp0aqU3HcjAMOQsL8VzUzThaxXQIDZJd6b12QaK");
        ParseInstallation.getCurrentInstallation()
                .saveInBackground();
    }
}
