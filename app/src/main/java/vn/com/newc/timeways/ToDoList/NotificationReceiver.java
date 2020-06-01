package vn.com.newc.timeways.ToDoList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("NotificationReceiver", "Thong bao den han cong viec");
        Intent intentMusic=new Intent(context, NotificationDue.class);
        context.startService(intentMusic);
    }
}
