package qc.android.manage.impl;

import qc.android.manage.LoginManage;

public class LoginManageImpl implements LoginManage {

	public boolean login(String account, String password) {
		return login(account, password, (StringBuffer) null);
	}

	public boolean login(String account, String password, StringBuffer msg) {
		if ("dragon".equals(account) && "password".equals(password)) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		} else if ("error".equals(account)) {
			msg.append("error:code=111,严重！");
		}

		return false;
	}

	public void logout() {
		// TODO Auto-generated method stub

	}
}
