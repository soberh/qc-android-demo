package qc.android.demo.activity;

import java.util.Map.Entry;

import qc.android.activity.BaseActivity;
import qc.android.demo.R;
import qc.android.manage.LoginManage;
import qc.android.util.BeanUtils;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * 登录界面
 * 
 * @author dragon
 * 
 */
public class LoginActivity extends BaseActivity {
	private static final String tag = "LoginActivity";
	private LoginManage loginManage;
	private EditText txtUserName;
	private EditText txtUserPassword;
	private CheckBox chkRemberPassword;
	private CheckBox chkAutoLogin;
	private Button btnSubmit;
	private Thread loginThread;
	private Dialog waitingDialog;

	protected void initBean() {
		super.initBean();
		loginManage = BeanUtils.getBean(LoginManage.class);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// AppUtils.hideTitle(this);//隐藏标题行
		setContentView(R.layout.login);

		final SharedPreferences setting = getSharedPreferences(
			Constants.prefsFile, Activity.MODE_PRIVATE);
		for (Entry<String, ?> e : setting.getAll().entrySet()) {
			Log.d(tag, e.getKey() + "=" + e.getValue());
		}

		txtUserName = ((EditText) findViewById(R.id.txtUserName));
		txtUserPassword = ((EditText) findViewById(R.id.txtUserPassword));
		chkRemberPassword = ((CheckBox) findViewById(R.id.chkRemberPassword));
		chkAutoLogin = ((CheckBox) findViewById(R.id.chkAutoLogin));

		// 检测是否记住了密码
		final Boolean rememberPassword = setting.getBoolean(
			getString(R.string.setting_rememberPassword), false);
		if (rememberPassword) {
			// 显示记住的帐号和密码信息
			String acount = setting.getString(
				getString(R.string.setting_historyAccount), null);
			String password = setting.getString(
				getString(R.string.setting_historyPassword), null);
			if (acount != null) {
				txtUserName.setText(acount);
			}
			Bundle bundle = this.getIntent().getExtras();
			boolean isFromLogout = bundle != null ? bundle
				.getBoolean(getString(R.string.login_key_isFromLogout)) : false;
			if (password != null && !isFromLogout) {
				txtUserPassword.setText(password);
			}
		}

		// 控制选项的选中状态
		chkRemberPassword.setChecked(rememberPassword);
		chkAutoLogin.setChecked(setting.getBoolean(
			getString(R.string.setting_autoLogin), false));

		chkRemberPassword
			.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton checkBox,
					boolean checked) {
					setting
						.edit()
						.putBoolean(
							getString(R.string.setting_rememberPassword),
							checked).commit();
				}
			});
		chkAutoLogin
			.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton checkBox,
					boolean checked) {
					setting
						.edit()
						.putBoolean(getString(R.string.setting_autoLogin),
							checked).commit();
				}
			});

		btnSubmit = (Button) this.findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				btnSubmit.setEnabled(false);
				// 验证帐号、密码
				final String account = txtUserName.getText().toString();
				final String password = txtUserPassword.getText().toString();
				if (account == null || account.length() == 0) {
					DialogUtils.alert(LoginActivity.this, 0,
						R.string.error_login_emptyAccount, null);
					btnSubmit.setEnabled(true);
					return;
				}
				if (password == null || password.length() == 0) {
					DialogUtils.alert(LoginActivity.this, 0,
						R.string.error_login_emptyPassword, null);
					btnSubmit.setEnabled(true);
					return;
				}

				// 保存帐号和密码信息
				if (rememberPassword) {
					setting
						.edit()
						.putString(getString(R.string.setting_historyAccount),
							account)
						.putString(getString(R.string.setting_historyPassword),
							password).commit();
				} else {
					setting.edit()
						.remove(getString(R.string.setting_historyAccount))
						.remove(getString(R.string.setting_historyPassword))
						.commit();
				}

				// 显示登录动画
				waitingDialog = DialogUtils.waiting(LoginActivity.this,
					getString(R.string.login_waitingMsg), null);

				// 开启新线程远程登录
				if (loginThread != null) {
					loginThread.destroy();
				}
				loginThread = new Thread() {
					public void run() {
						try {
							StringBuffer sb = new StringBuffer();
							boolean success = loginManage.login(account,
								password, sb);

							Message msg = loginHandler.obtainMessage();
							Bundle bundle = new Bundle();
							bundle.putBoolean("success", success);
							if (sb.length() > 0)
								bundle.putString("msg", sb.toString());
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
			}
		});
	}

	// 登录线程的处理器
	private final Handler loginHandler = new Handler() {
		public void handleMessage(Message _msg) {
			//关闭等待对话框
			if (waitingDialog != null && waitingDialog.isShowing())
				waitingDialog.cancel();
			waitingDialog = null;
			
			Bundle bundle = _msg.getData();
			boolean success = bundle.getBoolean("success");
			String msg = bundle.getString("msg");
			Log.i(tag, "logined: e=" + bundle.getBoolean("e"));
			Log.i(tag, "logined: success=" + success);
			Log.i(tag, "logined: msg=" + msg);
			if (success) {
				// 跳转到主页
				startActivity(new Intent(LoginActivity.this,
					SplashActivity.mainActivityClass));
				
				//显示登录成功信息
				DialogUtils.toast(LoginActivity.this, R.string.login_welcomeMsg);

				// 销毁当前活动
				LoginActivity.this.finish();
			} else {
				if (msg == null || msg.length() == 0) {
					msg = LoginActivity.this
						.getString(R.string.error_loginFailed);
				}
				DialogUtils.alert(LoginActivity.this, null, msg, null);
			}
			btnSubmit.setEnabled(true);
			loginThread = null;
		}
	};
}