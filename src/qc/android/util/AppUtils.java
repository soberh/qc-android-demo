package qc.android.util;

import qc.android.demo.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;

public class AppUtils {
	/**
	 * 创建指定活动的桌面快捷方式 permission: <uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
	 * 
	 * or error: Permission Denial: broadcasting Intent {
	 * act=com.android.launcher.action.INSTALL_SHORTCUT (has extras) } from
	 * org.inno.android.activity (pid=1001, uid=10024) requires
	 * com.android.launcher.permission.INSTALL_SHORTCUT due to receiver
	 * com.android.launcher/com.android.launcher.InstallShortcutReceiver
	 * 
	 * @param context
	 *            上下文
	 * @param name
	 *            快捷方式的名称
	 * @param activity
	 *            快捷方式所要启动的Activity
	 */
	@SuppressWarnings("unchecked")
	public static void createShortcut(Context context, Class<?> activity,
		String name) {
		// 声明快捷方式
		String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
		Intent shortcutIntent = new Intent(ACTION_INSTALL_SHORTCUT);
		// --快捷方式的名称
		if (name != null && name.length() > 0)
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		// shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_DUPLICATE,
		// false);
		// --快捷方式的图标
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
			Intent.ShortcutIconResource.fromContext(context, R.drawable.icon));

		// 设置快捷方式启动的组件
		if (activity == null && context instanceof Activity)
			activity = (Class<Activity>) context.getClass();
		Intent intent = new Intent(context, activity);
		// Intent intent = new Intent();
		// intent.setComponent(new ComponentName("qc.android.demo",
		// ".activity.SplashActivity"));
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 创建快捷方式
		context.sendBroadcast(shortcutIntent);
	}

	/**
	 * 创建仅包含指定活动的意图
	 * 
	 * @param clazz
	 * @return
	 */
	public static Intent createIntent(Class<? extends Activity> clazz) {
		return new Intent().setComponent(new ComponentName(clazz.getPackage()
			.getName(), clazz.getName()));
	}

	public static Intent createIntent(Context context,
		Class<? extends Activity> clazz) {
		return new Intent(context, clazz);
	}

	/**
	 * 设置窗口全屏显示
	 * 
	 * @param activity
	 */
	public static void fullScreen(Activity activity) {
		activity.getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
	}

	/**
	 * 隐藏窗口的标题行 *
	 * 
	 * @param activity
	 */
	public static void hideTitle(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题行(必须放在setContentView之前，否则fc)
	}
}
