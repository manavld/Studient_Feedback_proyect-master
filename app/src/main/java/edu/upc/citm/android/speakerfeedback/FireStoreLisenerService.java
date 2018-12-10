package edu.upc.citm.android.speakerfeedback;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class FireStoreLisenerService extends Service {

    private boolean is_first_time = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("SpeakerFeedback","FireStoreLisenerService.onCreate");
        db.collection("rooms").document("testroom").collection("polls")
                .whereEqualTo("open", true)
                .addSnapshotListener(ListenerPoll);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("SpeakerFeedback","FireStoreLisenerService.onStartCommand");

        createForegroundNotification();
        if(!is_first_time)
            createForegroundNotification();

        return START_NOT_STICKY;
    }

    private void createForegroundNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle(String.format("Connectat a testroom"))
                .setSmallIcon(R.drawable.ic_message)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);

        is_first_time = true;
    }

    private EventListener<QuerySnapshot> ListenerPoll = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if (e != null) {
                Log.e("SpeakerFeedback", "Error al rebre els 'polls'", e);
                return;
            }
            for (DocumentSnapshot doc : documentSnapshots) {
                Poll poll = doc.toObject(Poll.class);
                if (poll.isOpen()) {
                    Log.i("SpeakerFeedback", poll.getQuestion());
                    Intent intent = new Intent(FireStoreLisenerService.this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(FireStoreLisenerService.this, 0, intent, 0);
                    Notification notification = new NotificationCompat.Builder(FireStoreLisenerService.this, App.CHANNEL_ID)
                            .setContentTitle(String.format(poll.getQuestion()))
                            .setSmallIcon(R.drawable.ic_message)
                            .setContentIntent(pendingIntent)
                            .build();
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                    notificationManager.notify(2 , notification);
                }
            }
        }
    };
    @Override
    public void onDestroy() {
        Log.i("SpeakerFeedback","FireStoreLisenerService.onDestroy");

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
