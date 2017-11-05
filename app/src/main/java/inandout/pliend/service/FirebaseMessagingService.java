package inandout.pliend.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import inandout.pliend.R;
import inandout.pliend.activity.MainActivity;
import inandout.pliend.app.AppConfig;
import inandout.pliend.helper.SQLiteHandler;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";
    private SQLiteHandler db;

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //추가한것
        // sendNotification(remoteMessage.getData().get("message"));
    }

    public void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sprout)
                .setContentTitle("FloMate")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendQuestToServer(NotificationCompat.Builder message) {
        // Add custom implementation, as needed.
        // HashMap<String, String> user = db.getUserDetails();
        // email = user.get("email");
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int am_pm = c.get(Calendar.AM_PM);

        String strYear = String.valueOf(year);
        String strMonth = String.valueOf(month);
        String strDay = String.valueOf(day);
        String strHour = String.valueOf(hour);
        String strMinute = String.valueOf(minute);
        String strAm_pm = String.valueOf(am_pm);

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String email = user.get("email");

        Log.d("email at messaging", email);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("email", email)
                .add("content", message.mContentText.toString())
                .add("year", strYear)
                .add("month", strMonth)
                .add("day", strDay)
                .add("hour", strHour)
                .add("minute", strMinute)
                .add("am_pm", strAm_pm)
                .build();

        //request
        Request request = new Request.Builder()
                .url(AppConfig.URL_REG_QUEST)
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

