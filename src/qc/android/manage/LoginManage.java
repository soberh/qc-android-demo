package qc.android.manage;

public interface LoginManage {
	/**
	 * 登录系统
	 * @param account 帐号
	 * @param password 密码
	 * @return 登录成功返回true,否则返回false
	 */
	boolean login(String account, String password);

	/**
	 * 登录系统
	 * @param account 帐号
	 * @param password 密码
	 * @param msg 登录不成功的相关错误信息
	 * @return 登录成功返回true,否则返回false
	 */
	boolean login(String account, String password, StringBuffer msg);

	void logout();

}
