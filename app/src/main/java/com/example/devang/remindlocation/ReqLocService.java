package com.example.devang.remindlocation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ReqLocService extends Service {
    public ReqLocService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        Log.d("ReqLocService","onStartCommand called");
        return super.onStartCommand(intent, flags, startId);

    }

    public void onCreate() {
        Log.d("ReqLocService","onCreate called");
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();

        while(true)
        {
            Log.d("ReqLocService","infinite loop");
        }
    }
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }
}
