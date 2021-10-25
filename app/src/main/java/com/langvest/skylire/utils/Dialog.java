package com.langvest.skylire.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.langvest.skylire.ChatActivity;
import com.langvest.skylire.R;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.Objects;

public class Dialog {

	private final Activity activity;
	private String title;
	private String text = "";
	private String okButtonTitle;
	private String cancelButtonTitle;
	private OnClickListener okButtonOnClickListener;
	private OnClickListener cancelButtonOnClickListener;
	private OnDismissListener onDismissListener;
	private OnTouchOutside onTouchOutside;
	private AlertDialog dialog;
	private boolean isOkButton = false;
	private boolean isCancelButton = false;
	private boolean isDismissListener = false;
	private boolean isTouchOutside = false;
	private String username = "";
	private String usernameColor = "19";
	private String usernameIcon = "";

	public Dialog(Activity activity) {
		this.activity = activity;
	}

	public Dialog setTitle(String title) {
		this.title = title;
		return this;
	}

	public Dialog setText(String text) {
		this.text = text;
		return this;
	}

	public Dialog setOkButton(String title, OnClickListener onClickListener) {
		okButtonTitle = title;
		okButtonOnClickListener = onClickListener;
		isOkButton = true;
		return this;
	}

	public Dialog setCancelButton(String title, OnClickListener onClickListener) {
		cancelButtonTitle = title;
		cancelButtonOnClickListener = onClickListener;
		isCancelButton = true;
		return this;
	}

	public Dialog setOnDismiss(OnDismissListener onDismissListener) {
		this.onDismissListener = onDismissListener;
		isDismissListener = true;
		return this;
	}

	public Dialog setOnTouchOutside(OnTouchOutside onTouchOutside) {
		this.onTouchOutside = onTouchOutside;
		isTouchOutside = true;
		return this;
	}

	public Dialog setUsername(String username) {
		this.username = username;
		return this;
	}

	public Dialog setUsernameColor(String usernameColor) {
		this.usernameColor = usernameColor;
		return this;
	}

	public Dialog setUsernameIcon(String usernameIcon) {
		this.usernameIcon = usernameIcon;
		return this;
	}

	public void show(boolean canHide, boolean autoDismiss) {
		final float scale = activity.getResources().getDisplayMetrics().density;
		int $20dp = (int) (20*scale+0.5f);

		dialog = new AlertDialog.Builder(activity).create();
		dialog.setCancelable(canHide);
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.dialog, null);
		dialog.setView(dialogView);

		LinearLayout buttons = dialogView.findViewById(R.id.dialog_buttons);
		TextView title = dialogView.findViewById(R.id.dialog_title);
		TextView text = dialogView.findViewById(R.id.dialog_text);
		TextView cancelButton = dialogView.findViewById(R.id.dialog_button_cancel);
		TextView okButton = dialogView.findViewById(R.id.dialog_button_ok);

		LinearLayout usernameContainer = dialogView.findViewById(R.id.dialog_username_container);

		if(!this.username.equals("")) {
			ImageView icon = dialogView.findViewById(R.id.dialog_username_icon);
			TextView username = dialogView.findViewById(R.id.dialog_username);
			ShimmerTextView goldenUsername = dialogView.findViewById(R.id.dialog_username_golden);
			ShimmerTextView epicUsername = dialogView.findViewById(R.id.dialog_username_epic);

			if(usernameColor.equals("g")) {
				goldenUsername.setVisibility(View.VISIBLE);
				goldenUsername.setText(this.username);
				new GoldenAdapter(activity).start(goldenUsername, false);
			} else if(usernameColor.equals("e")) {
				epicUsername.setVisibility(View.VISIBLE);
				epicUsername.setText(this.username);
				new EpicAdapter(activity).start(epicUsername, false);
			} else {
				username.setVisibility(View.VISIBLE);
				username.setText(this.username);
				username.setTextColor(activity.getResources().getColor(MessageAdapter.getColorConstant(Integer.parseInt(usernameColor))));
			}

			if(!usernameIcon.equals("")) {
				icon.setVisibility(View.VISIBLE);
				Glide.with(activity).load(MessageAdapter.getIconConstant(usernameIcon)).into(icon);
			}
		} else usernameContainer.setVisibility(View.GONE);

		title.setText(this.title);
		text.setText(this.text);

		if(isOkButton || isCancelButton) {
			if(isCancelButton) {
				cancelButton.setText(cancelButtonTitle);
				cancelButton.setOnClickListener(v -> {
					cancelButtonOnClickListener.execute(this);
					if(autoDismiss) this.dismiss();
				});
			} else {
				cancelButton.setVisibility(View.GONE);
				LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.setMarginStart(0);
				okButton.setLayoutParams(params);
			}
			okButton.setText(okButtonTitle);
			okButton.setOnClickListener(v -> {
				okButtonOnClickListener.execute(this);
				if(autoDismiss) this.dismiss();
			});
		} else buttons.setVisibility(View.GONE);

		if(isTouchOutside) dialog.setOnCancelListener(dialogInterface -> onTouchOutside.execute());
		if(isDismissListener) dialog.setOnDismissListener(dialogInterface -> onDismissListener.execute());



		android.graphics.drawable.GradientDrawable gradientDrawable = new android.graphics.drawable.GradientDrawable();
		gradientDrawable.setColor(ContextCompat.getColor(activity, R.color.background_container));
		gradientDrawable.setCornerRadius($20dp);
		Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(gradientDrawable);

		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	@FunctionalInterface
	public interface OnClickListener {
		void execute(Dialog dialog);
	}

	@FunctionalInterface
	public interface OnDismissListener {
		void execute();
	}

	@FunctionalInterface
	public interface OnTouchOutside {
		void execute();
	}
}