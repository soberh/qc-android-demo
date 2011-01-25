package qc.android.demo.widget;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class Widget1Service extends Service implements Runnable {
	private static final String tag = Widget1Service.class.getSimpleName();
	private static Queue<Integer> sAppWidgetIds = new LinkedList<Integer>();
	private static boolean sThreadRunning = false;
	private static Object sLock = new Object();
	public static final String ACTION_UPDATE_ALL = "org.qc.test.UPDATE_ALL";

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(tag, "onBind");
		return null;
	}

	public static void updateAppWidgetIds(int[] appWidgetIds) {
		synchronized (sLock) {
			for (int appWidgetId : appWidgetIds) {
				sAppWidgetIds.add(appWidgetId);
			}
		}
	}

	public static int getNextWidgetId() {
		synchronized (sLock) {
			if (sAppWidgetIds.peek() == null) {
				return AppWidgetManager.INVALID_APPWIDGET_ID;
			} else {
				return sAppWidgetIds.poll();

			}
		}
	}

	private static boolean hasMoreUpdates() {
		synchronized (sLock) {
			boolean hasMore = !sAppWidgetIds.isEmpty();
			if (!hasMore) {
				sThreadRunning = false;
			}
			return hasMore;
		}
	}

	@Override
	public void onCreate() {
		Log.d(tag, "onCreate");
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(tag, "onStart");
		super.onStart(intent, startId);
		if (null != intent) {
			if (ACTION_UPDATE_ALL.equals(intent.getAction())) {
				AppWidgetManager widget = AppWidgetManager.getInstance(this);
				updateAppWidgetIds(widget.getAppWidgetIds(new ComponentName(
					this, Widget1Provider.class)));
			}
		}
		synchronized (sLock) {
			if (!sThreadRunning) {
				sThreadRunning = true;
				new Thread(this).start();
			}
		}
	}

	public void run() {
//		SharedPreferences setting = getSharedPreferences(
//			"com.xxxx.news_preferences", 0);
//		String updateTime = setting.getString("list_time", "1800000");
		String updateTime = "1000";//间隔

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		RemoteViews updateViews = null;

		Calendar calendar = Calendar.getInstance();
		//String msg = calendar.getTime().toGMTString();
		String msg = formatNum(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
		+ formatNum(calendar.get(Calendar.MINUTE)) + ":"
		+ formatNum(calendar.get(Calendar.SECOND));
		while (hasMoreUpdates()) {
			int appWidgetId = getNextWidgetId();
			updateViews = Widget1Provider.updateAppWidget(this, msg);
			if (updateViews != null) {
				appWidgetManager.updateAppWidget(appWidgetId, updateViews);
			}
		}

		Intent updateIntent = new Intent(ACTION_UPDATE_ALL);
		updateIntent.setClass(this, Widget1Service.class);
		PendingIntent pending = PendingIntent.getService(this, 0, updateIntent,
			0);

		Time time = new Time();
		long nowMillis = System.currentTimeMillis();
		time.set(nowMillis + Long.parseLong(updateTime));
		long updateTimes = time.toMillis(true);
		Log.d(tag, "request next update at " + updateTimes);

		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, updateTimes, pending);
		stopSelf();
	}

	private String formatNum(int i) {
		String s = "" + i;
		if(s.length() == 1)
			s = "0" + i;
		return s;
	}
}
