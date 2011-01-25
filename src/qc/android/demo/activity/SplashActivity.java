package qc.android.demo.activity;

import java.util.Map.Entry;

import qc.android.activity.BaseActivity;
import qc.android.demo.R;
import qc.android.manage.LoginManage;
import qc.android.util.AppUtils;
import qc.android.util.BeanUtils;
import qc.android.util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SplashActivity extends BaseActivity {
	private static final String tag = "SplashActivity";
	public static Class<? extends Activity> mainActivityClass = MainActivity.class;
	private LoginManage loginManage;
	private Thread loginThread;

	protected void initBean() {
		super.initBean();
		loginManage = BeanUtils.getBean(LoginManage.class);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(tag,"intent=" + Intent.ACTION_CREATE_SHORTCUT);

		// 快捷方式的创建(长按左面在弹出的菜单中选中快捷方式后的处理)
		if (Intent.ACTION_CREATE_SHORTCUT.equals(this.getIntent().getAction())) {
			Intent addShortcut = new Intent();
			// 快捷方式的名称
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
			// 快捷方式的图标
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this, R.drawable.icon));
			// 快捷方式启动的组件
			Intent intent = new Intent(this,SplashActivity.class);
//			intent.setComponent(new ComponentName(SplashActivity.class.getPackage().getName(),
//				SplashActivity.class.getName()));
			addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

			setResult(RESULT_OK, addShortcut);
			this.finish();
			return;
		}

		SharedPreferences setting = getSharedPreferences(Constants.prefsFile,
			Activity.MODE_PRIVATE);
		for (Entry<String, ?> e : setting.getAll().entrySet()) {
			Log.d(tag, e.getKey() + "=" + e.getValue());
		}

		// 全屏控制
		Boolean fullScreen = setting.getBoolean(
			getString(R.string.setting_fullScreen), false);
		if (fullScreen) {
			AppUtils.fullScreen(this);
		}

		// 检测是否允许自动登录、记住上次的登录密码
		Boolean enabledAutoLogin = setting.getBoolean(
			getString(R.string.setting_autoLogin), false);
		if (enabledAutoLogin) {
			final String acount = setting.getString(
				getString(R.string.setting_historyAccount), null);
			final String password = setting.getString(
				getString(R.string.setting_historyPassword), null);
			if (acount != null && password != null) {
				// 开启新线程远程登录
				if (loginThread != null) {
					loginThread.destroy();
				}
				loginThread = new Thread() {
					public void run() {
						try {
							boolean success = loginManage.login(acount,
								password);

							Message msg = loginHandler.obtainMessage();
							Bundle bundle = new Bundle();
							bundle.putBoolean("success", success);
							msg.setData(bundle);
							loginHandler.sendMessage(msg);
						} catch (Exception e) {
							Message msg = loginHandler.obtainMessage();
							Bundle bundle = new Bundle();
							bundle.putBoolean("e", true);
							bundle.putBoolean("success", false);
							bundle.putString("msg", e.getMessage());
							msg.setData(bundle);
							loginHandler.sendMessage(msg);
						}
					}
				};
				loginThread.start();
				return;
			}
		}

		// 跳转到登录界面
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);

		// setContentView(R.layout.main);
		this.finish();
	}

	// 登录线程的处理器
	private final Handler loginHandler = new Handler() {
		public void handleMessage(Message _msg) {
			Bundle bundle = _msg.getData();
			boolean success = bundle.getBoolean("success");
			String msg = bundle.getString("msg");
			Log.d(tag, "auto logined: e=" + bundle.getBoolean("e"));
			Log.d(tag, "auto logined: success=" + success);
			Log.d(tag, "auto logined: msg=" + msg);
			if (success) {
				// 跳转到主页
				startActivity(AppUtils.createIntent(mainActivityClass));
			} else {
				// 跳转到登录界面
				startActivity(AppUtils.createIntent(LoginActivity.class));
			}
			loginThread = null;
			SplashActivity.this.finish();
		}
	};
}