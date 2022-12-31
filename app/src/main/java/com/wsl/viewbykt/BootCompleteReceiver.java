package com.wsl.viewbykt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wsl.viewbykt.coordinator.CoordinatorActivity;

public class BootCompleteReceiver extends BroadcastReceiver {

    public BootCompleteReceiver()
    {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "======");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Log.e("TAG", "======android.intent");
            Intent intentMainActivity = new Intent(context, CoordinatorActivity.class);
//            intentMainActivity.setClassName(context.getPackageName(), "io.dcloud.PandoraEntryActivity");
            intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentMainActivity);
        }
    }
}