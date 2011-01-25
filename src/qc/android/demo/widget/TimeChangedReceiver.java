package qc.android.demo.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeChangedReceiver extends BroadcastReceiver {
	private static final String tag = "TimeChangedReceiver";
	private static int count = 0;

	@Override
	public void onReceive(Context paramContext, Intent paramIntent) {
		Log.d(tag, "onReceive：" + (count++) + "," + paramIntent.getAction());
	}
}
