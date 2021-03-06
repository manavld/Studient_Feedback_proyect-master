package edu.upc.citm.android.speakerfeedback;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public  static final  String CHANNEL_ID = "SpeakerFeedback";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChanels();
    }

    private void createNotificationChanels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "SpeakerFeedBack Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
