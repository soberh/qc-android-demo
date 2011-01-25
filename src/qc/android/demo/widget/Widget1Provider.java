package qc.android.demo.widget;

import qc.android.demo.R;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class Widget1Provider extends AppWidgetProvider {
	private static final String tag = Widget1Provider.class.getSimpleName();
	private static int count = 0;

	public void onReceive(Context context, Intent intent) {
		Log.d(tag, "onReceive" + (count++));
		super.onReceive(context, intent);
	}

	// android:updatePeriodMillis控制的，每循环一次就运行一次
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
		int[] appWidgetIds) {
		count++;
		Log.d(tag, "onUpdate:" + count + "," + appWidgetIds.length);
		Widget1Service.updateAppWidgetIds(appWidgetIds);
		if (appWidgetIds.length > 0)
			context.startService(new Intent(context, Widget1Service.class));
	}

	public static RemoteViews updateAppWidget(Context context, String msg) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
			R.layout.widget1);
		views.setTextViewText(R.id.widget_txtLable, msg);
		// Intent detailIntent = new Intent(context, NewsSiteList.class);
		// PendingIntent pending = PendingIntent.getActivity(context, 0,
		// detailIntent, 0);
		// views.setOnClickPendingIntent(R.id.widget_txtLable, pending);
		return views;
	}

	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(tag, "onDeleted");
		super.onDeleted(context, appWidgetIds);
	}

	public void onEnabled(Context context) {
		Log.d(tag, "onEnabled");
		super.onEnabled(context);
	}

	public void onDisabled(Context context) {
		Log.d(tag, "onDisabled");
		super.onDisabled(context);
	}
}
