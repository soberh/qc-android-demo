package qc.android.activity;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
	// private static final String tag = "BaseActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initBean();
	}

	protected void initBean() {
	}
}