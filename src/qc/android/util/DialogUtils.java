package qc.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class DialogUtils {
	private static final String tag = "DialogUtils";

	/**
	 * 显示警告的alert信息 (支持国际化)
	 * 
	 * @param context
	 * @param titleId
	 *            标题对应的资源id
	 * @param msgId
	 *            消息内容对应 的资源id
	 * @param callback
	 *            点击确认按钮的回调函数
	 * @return
	 */
	public static Dialog alert(Context context, int titleId, int msgId,
		final Runnable callback) {
		Dialog dialog = new AlertDialog.Builder(context)
			.setMessage(msgId)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(
				context.getResources().getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 销毁对话框
						dialog.dismiss();

						// 调用回调函数
						if (callback != null)
							callback.run();
					}
				}).create();
		if (titleId > 0)
			dialog.setTitle(titleId);
		dialog.show();
		return dialog;
	}

	public static Dialog alert(Context context, String title, String msg,
		final Runnable callback) {
		Dialog dialog = new AlertDialog.Builder(context)
			.setMessage(msg)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton(
				context.getResources().getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 销毁对话框
						dialog.dismiss();

						// 调用回调函数
						if (callback != null)
							callback.run();
					}
				}).create();
		if (title != null && title.length() > 0)
			dialog.setTitle(title);
		dialog.show();
		return dialog;
	}

	/**
	 * 显示确认对话框
	 * 
	 * @param context
	 * @param titleId
	 *            标题对应的资源id
	 * @param msgId
	 *            消息内容对应 的资源id
	 * @param onYes
	 *            点击是按钮的回调函数
	 * @param onNo
	 *            点击否按钮的回调函数
	 * @return
	 */
	public static Dialog confirm(Context context, int titleId, int msgId,
		final Runnable onYes, final Runnable onNo) {
		Dialog dialog = new AlertDialog.Builder(context)
			.setTitle(titleId)
			.setMessage(msgId)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton(
				context.getResources().getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 销毁对话框
						dialog.dismiss();

						// 调用回调函数
						if (onYes != null)
							onYes.run();
					}
				})
			.setNegativeButton(
				context.getResources().getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 销毁对话框
						dialog.dismiss();

						// 调用回调函数
						if (onNo != null)
							onNo.run();
					}
				}).create();
		dialog.show();
		return dialog;
	}

	/**
	 * 显示信息输入对话框
	 * 
	 * @param <T>
	 * 
	 * @param context
	 * @param titleId
	 *            标题对应的资源id
	 * @param value
	 *            预设值的内容
	 * @param onOk
	 *            点击是按钮的回调函数
	 * @param onCancel
	 *            点击否按钮的回调函数
	 * @return
	 */
	public static Dialog prompt(Activity context, int titleId, String value,
		final Callback<String> onOk, final Callback<String> onCancel) {
		// 使用code进行布局的方法
		ScrollView layout = new ScrollView(context);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
			LayoutParams.WRAP_CONTENT));
		layout.setPadding(4, 0, 4, 0);
		final EditText editText = new EditText(context);
		if (value != null)
			editText.setText(value);
		editText.setMinLines(4);
		editText.setGravity(Gravity.TOP);
		layout.addView(editText);

		// 使用xml进行布局的方法
		// LayoutInflater inflater = context.getLayoutInflater();
		// View layout = inflater.inflate(R.layout.prompt,
		// (ViewGroup) context.findViewById(R.id.prompt_layout_root));
		// final EditText editText = (EditText) layout
		// .findViewById(R.id.promptEditText);
		// editText.setText(value);

		Dialog dialog = new AlertDialog.Builder(context)
			.setTitle(titleId)
			.setView(layout)
			.setIcon(android.R.drawable.ic_dialog_info)
			.setPositiveButton(
				context.getResources().getString(android.R.string.yes),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String value = editText.getText().toString();
						// 销毁对话框
						dialog.dismiss();

						// 调用回调函数
						if (onOk != null)
							onOk.run(value);
					}
				})
			.setNegativeButton(
				context.getResources().getString(android.R.string.no),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 销毁对话框
						dialog.dismiss();

						// 调用回调函数
						if (onCancel != null)
							onCancel.run(null);
					}
				}).create();
		dialog.show();
		return dialog;
	}

	public static Dialog info(Context context, int titleId, String msg,
		final Runnable onClosed) {
		return show(context, titleId, msg, android.R.drawable.ic_dialog_info,
			onClosed);
	}

	public static Dialog warn(Context context, int titleId, String msg,
		final Runnable onClosed) {
		return show(context, titleId, msg, android.R.drawable.ic_dialog_alert,
			onClosed);
	}

	public static Dialog error(Context context, int titleId, String msg,
		final Runnable onClosed) {
		return show(context, titleId, msg, android.R.drawable.ic_delete,
			onClosed);
	}

	/**
	 * 显示信息对话框
	 * 
	 * @param context
	 * @param titleId
	 *            标题对应的资源id
	 * @param msg
	 *            消息内容
	 * @param iconId
	 *            标题中图标的资源id
	 * @param onClosed
	 *            关闭对话框后的回调函数
	 * @return
	 */
	public static Dialog show(Context context, int titleId, String msg,
		int iconId, final Runnable onClosed) {
		Dialog dialog = new AlertDialog.Builder(context)
			.setTitle(titleId)
			.setMessage(msg)
			.setIcon(iconId)
			.setPositiveButton(
				context.getResources().getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 销毁对话框
						dialog.dismiss();

						// 调用回调函数
						if (onClosed != null)
							onClosed.run();
					}
				}).create();
		dialog.show();
		return dialog;
	}

	/**
	 * 显示简单的tip提示信息
	 * 
	 * @param context
	 * @param msgId
	 *            提示信息对应的资源id
	 */
	public static void toast(Context context, int msgId) {
		Toast.makeText(context, msgId, Toast.LENGTH_LONG).show();
	}

	/**
	 * 显示简单的tip提示信息
	 * 
	 * @param context
	 * @param msg
	 *            提示信息的内容
	 */
	public static void toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	public static Dialog waiting(Context context, String msg,
		final Runnable callback) {
		ProgressDialog dlg = new ProgressDialog(context);
		dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dlg.setCancelable(true);
		dlg.setMessage(msg);
		dlg.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				Log.d(tag, "onCancel");
				// 销毁对话框
				dialog.dismiss();

				// 调用回调函数
				if (callback != null)
					callback.run();
			}
		});

		dlg.show();
		return dlg;
	}
}
