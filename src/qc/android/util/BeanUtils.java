package qc.android.util;

import qc.android.manage.LoginManage;
import qc.android.manage.impl.LoginManageImpl;

public class BeanUtils {
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz) {
		if (clazz == LoginManage.class) {
			return (T) new LoginManageImpl();
		}
		return null;
	}
}
