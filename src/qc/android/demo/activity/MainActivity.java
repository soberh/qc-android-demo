package qc.android.demo.activity;

import qc.android.activity.BaseActivity;
import qc.android.demo.R;
import qc.android.manage.LoginManage;
import qc.android.util.AppUtils;
import qc.android.util.BeanUtils;
import qc.android.util.Callback;
import qc.android.util.Constants;
import qc.android.util.DialogUtils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends BaseActivity {
	private static final String tag = "MainTestActivity";
	private LoginManage loginManage;
	private int present = 0;
	private Dialog waitingDialog;

	protected void initBean() {
		super.initBean();
		loginManage = BeanUtils.getBean(LoginManage.class);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 允许在标题中显示进度条
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setProgressBarVisibility(true);

		setContentView(R.layout.main);

		final Activity _this = MainActivity.this;
		// 让窗口标题显示进度条
		((Button) findViewById(R.id.testBtnProgressTitle))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					new Thread(new Runnable() {
						public void run() {
							try {
								for (int i = 0; i < 100; i++) {
									present = i;
									Thread.sleep(50);
									Message m = new Message();
									if (present == 99)
										m.what = 1;
									else
										m.what = 0;
									progressTitleHandler.sendMessage(m);
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

		// 弹出等待对话框
		((Button) findViewById(R.id.testBtnProgressWait))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					waitingDialog = DialogUtils.waiting(_this,
						"Wait 5 Seconds...", new Runnable() {
							public void run() {
								DialogUtils.toast(_this, "finished waiting!");
							}
						});

					new Thread(new Runnable() {
						public void run() {
							try {
								// 动画显示两秒
								Thread.sleep(5000);
								progressWaitHandler.sendMessage(new Message());
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			});

		// 创建桌面快捷方式
		((Button) findViewById(R.id.testBtnCreateShortcut))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					AppUtils.createShortcut(_this, SplashActivity.class,
						getString(R.string.app_name));
				}
			});

		// Alert对话框
		((Button) findViewById(R.id.testBtnCreateAlert))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					DialogUtils.alert(_this, R.string.test_dlg_title,
						R.string.test_dlg_msg, null);
				}
			});

		// Confirm对话框
		((Button) findViewById(R.id.testBtnCreateConfirm))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					DialogUtils.confirm(_this, R.string.test_dlg_title,
						R.string.test_dlg_msg, new Runnable() {
							public void run() {
								DialogUtils.toast(_this,
									R.string.test_confirm_yes);
							}
						}, new Runnable() {
							public void run() {
								DialogUtils.toast(_this,
									R.string.test_confirm_no);
							}
						});
				}
			});

		// Prompt对话框
		final StringBuffer msg = new StringBuffer("Test Value");
		for (int i = 0; i < 15; i++)
			msg.append("\ni = " + i);
		((Button) findViewById(R.id.testBtnCreatePrompt))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					DialogUtils.prompt(_this, R.string.test_dlg_title,
						msg.toString(), new Callback<String>() {
							public void run(String value) {
								DialogUtils.toast(_this,
									getString(R.string.test_prompt_ok)
										+ ",value=" + value);
							}
						}, new Callback<String>() {
							public void run(String value) {
								DialogUtils.toast(_this,
									R.string.test_prompt_cancel);
							}
						});
				}
			});

		// info对话框
		final String info = "Test Message\naaa\nbbbbbbbb";
		((Button) findViewById(R.id.testBtnCreateInfo))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					DialogUtils
						.info(_this, R.string.test_dlg_title, info, null);
				}
			});

		// warn对话框
		((Button) findViewById(R.id.testBtnCreateWarn))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					DialogUtils
						.warn(_this, R.string.test_dlg_title, info, null);
				}
			});

		// error对话框
		((Button) findViewById(R.id.testBtnCreateError))
			.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					DialogUtils.error(_this, R.string.test_dlg_title, info,
						null);
				}
			});
	}

	private Handler progressTitleHandler = new Handler() {
		public void handleMessage(Message m) {
			if (m.what == 1) {
				// Thread.currentThread().interrupt();
				DialogUtils.toast(MainActivity.this, "Finished propress!");
			} else {
				setProgress(present * 100);
				// setSecondaryProgress(present * 100);
			}
		}
	};

	private Handler progressWaitHandler = new Handler() {
		public void handleMessage(Message m) {
			Log.d(tag, "handleMessage");
			if (waitingDialog != null && waitingDialog.isShowing()) {
				Log.d(tag, "cancel in handleMessage");
				waitingDialog.cancel();
			}
			waitingDialog = null;
		}
	};

	// 创建菜单
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 处理菜单的点击事件
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.about) { // 跳到关于界面
			Intent intent = new Intent();
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
			// this.finish();//如果也运行这个，则在关于界面按返回键会直接退出程序，否则返回本页面
		} else if (id == R.id.setting) {// 跳到设置界面
			Intent intent = new Intent();
			intent.setClass(this, SettingActivity.class);
			startActivity(intent);
		} else if (id == R.id.logout) {// 注销当前用户
			DialogUtils.confirm(this, R.string.menu_logout_confirmTile,
				R.string.menu_logout_confirmMsg, new Runnable() {
					public void run() {
						// 注销当前帐号
						loginManage.logout();
						SharedPreferences setting = getSharedPreferences(
							Constants.prefsFile, Activity.MODE_PRIVATE);
						setting
							.edit()
							.remove(getString(R.string.setting_historyPassword))
							.remove(getString(R.string.setting_historyAccount))
							.commit();

						// 返回到登录页面
						Intent intent = AppUtils
							.createIntent(MainActivity.this, LoginActivity.class);
						intent.putExtra(
							getString(R.string.login_key_isFromLogout), true);
						startActivity(intent);

						// 退出
						MainActivity.this.finish();
					}
				}, null);
		} else if (id == R.id.exit) {// 退出应用
			this.finish();
		}
		return true;
	}
}