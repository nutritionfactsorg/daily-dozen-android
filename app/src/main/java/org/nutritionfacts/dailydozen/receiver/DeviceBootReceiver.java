package org.nutritionfacts.dailydozen.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.nutritionfacts.dailydozen.util.NotificationUtil;

/**
 * The purpose of the DeviceBootReceiver is to set the update reminder on successful device startup
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil.init(context);
    }
}
