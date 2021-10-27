package com.langvest.skylire;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuItemCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.langvest.skylire.utils.AppMode;
import com.langvest.skylire.utils.Auth;
import com.langvest.skylire.utils.Dialog;
import com.langvest.skylire.utils.EpicAdapter;
import com.langvest.skylire.utils.GoldenAdapter;
import com.langvest.skylire.utils.Message;
import com.langvest.skylire.utils.MessageAdapter;
import com.langvest.skylire.utils.Suggestion;
import com.plattysoft.leonids.ParticleSystem;
import com.romainpiel.shimmer.ShimmerTextView;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class ChatActivity extends AppCompatActivity {

	LinearLayout messageInputContainer;
	LinearLayout loadingContainer;
	LinearLayout errorContainer;
	TextView retryButton;
	View messageInputDivider;
	ListView chatContainer;
	ArrayAdapter<Message> adapter;
	EditText messageInputText;
	ImageView messageSendImage;
	ArrayList<Message> list = new ArrayList<>();
	CircleImageView profileImage;
	BottomSheetDialog bottomDialog;
	private FirebaseUser user;
	private DatabaseReference FBD_messages;
	private Query FBD_messagesWithLimit;
	private DatabaseReference FBD_muted;
	private DatabaseReference FBD_suggestion;
	private ChildEventListener messagesChildEventListenerWithLimit;
	private ValueEventListener mutedValueEventListener;
	private ValueEventListener suggestionValueEventListener;
	private boolean isMessagesChildEventListenerWithLimit = false;
	private boolean isMutedValueEventListener = false;
	private boolean isSuggestionValueEventListener = false;
	private final ArrayList<String> ignoredKeys = new ArrayList<>();
	private final int messagesRenderLimit = 100;
	private long minTimestamp = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.ChatActivity);
		setContentView(R.layout.activity_chat);
		setTitle("Skylire Live Chat");

		AppMode.getInstance(ChatActivity.this, false).startListenForModeCode();

		user = FirebaseAuth.getInstance().getCurrentUser();

		FBD_messages = FirebaseDatabase.getInstance().getReference().child("messages");
		FBD_messagesWithLimit = FirebaseDatabase.getInstance().getReference().child("messages").limitToLast(messagesRenderLimit);
		FBD_muted = FirebaseDatabase.getInstance().getReference().child("muted");
		FBD_suggestion = FirebaseDatabase.getInstance().getReference().child("suggestion");

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		loadingContainer = findViewById(R.id.loading_container);
		errorContainer = findViewById(R.id.error_container);
		retryButton = findViewById(R.id.retry_button);
		chatContainer = findViewById(R.id.chat_container);
		messageInputText = findViewById(R.id.message_input);
		messageSendImage = findViewById(R.id.message_send);
		messageInputContainer = findViewById(R.id.message_input_container);
		messageInputDivider = findViewById(R.id.message_input_divider);

		messageSendImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				messageSendImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				messageSendImage.setClickable(false);
			}
		});

		messageSendImage.setClickable(false);

		messageSendImage.setOnClickListener(v -> {
			int amount = (int) (messageInputText.getText().toString().length()*0.4);
			amount = Math.max(amount, 5);
			amount = Math.min(amount, 30);
			new ParticleSystem(ChatActivity.this, amount, R.drawable.particle, 500)
					.setSpeedRange(0.02f, 0.2f)
					.setScaleRange(0.3f, 1f)
					.setFadeOut(500)
					.setAcceleration(0.0008f, 270)
					.oneShot(v, amount);

			sendMessage(messageInputText.getText().toString());

			messageInputText.setText("");
			messageSendImage.setClickable(false);
			messageSendImage.setColorFilter(ContextCompat.getColor(ChatActivity.this, R.color.disabled_icon), android.graphics.PorterDuff.Mode.SRC_IN);
		});

		retryButton.setOnClickListener(v -> {
			errorContainer.setVisibility(View.GONE);
			loadingContainer.setVisibility(View.VISIBLE);
			renderChat();
		});

		messageInputText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				boolean canToSend = !messageInputText.getText().toString().trim().equals("");
				messageSendImage.setClickable(canToSend);
				messageSendImage.setColorFilter(ContextCompat.getColor(ChatActivity.this, canToSend ? R.color.primary : R.color.disabled_icon), android.graphics.PorterDuff.Mode.SRC_IN);
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});

		adapter = new MessageAdapter(ChatActivity.this, R.layout.message, R.id.username, list);
		chatContainer.setAdapter(adapter);

		initializeBottomDialog();
		renderChat();
	}

	private void sendMessage(String text) {
		Message message = new Message(text, user.getEmail(), user.getDisplayName(), getUserIcon(), getUserColor());
		String key = FBD_messages.push().getKey();
		if(key != null) {
			ignoredKeys.add(key);
			addMessageToList(message, key);
			FBD_messages.child(key).setValue(message);
		}
	}

	private String getUserColor() {
		SharedPreferences preferences = getSharedPreferences("common", MODE_PRIVATE);
		return preferences.getString("color", "19");
	}

	private String getUserIcon() {
		SharedPreferences preferences = getSharedPreferences("common", MODE_PRIVATE);
		return preferences.getString("icon", "");
	}

	private void addMessageToList(Message message, String key) {
		if(list.toArray().length+1 > messagesRenderLimit) list.remove(0);
		minTimestamp = message.getS();
		message._setKey(key);
		message._setAnimate(true);
		list.add(message);
		adapter.notifyDataSetChanged();
	}

	private void deleteMessageFromList(Message message) {
		list.remove(message);
		adapter.notifyDataSetChanged();
	}

	private void renderChat() {
		FBD_messages.limitToLast(messagesRenderLimit).get().addOnCompleteListener(task -> {
			if(task.isSuccessful()) {
				Iterable<DataSnapshot> messagesDataSnapshotList = task.getResult().getChildren();
				for(DataSnapshot snapshotItem : messagesDataSnapshotList) {
					Message message = snapshotItem.getValue(Message.class);
					if(message != null) {
						minTimestamp = message.getS();
						message._setKey(snapshotItem.getKey());
						message._setAnimate(true);
						list.add(message);
					}
				}

				SharedPreferences preferences = getSharedPreferences("security", MODE_PRIVATE);
				if(preferences.getString("muted_emails", "").contains(user.getEmail() + ";")) mute();
				else unmute();

				loadingContainer.setVisibility(View.GONE);
				chatContainer.setVisibility(View.VISIBLE);

				startIntroduction();

				addMessageToList(new Message(Message.WELCOME_TYPE, user.getDisplayName()), "welcome-message");

				messagesChildEventListenerWithLimit = new ChildEventListener() {
					@Override
					public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
						String key = dataSnapshot.getKey();
						Message newMessage = dataSnapshot.getValue(Message.class);
						if(newMessage != null && !ignoredKeys.contains(key) && minTimestamp < newMessage.getS()) addMessageToList(newMessage, key);
					}

					@Override
					public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

					@Override
					public void onChildRemoved(@NonNull DataSnapshot snapshot) {
						String key = snapshot.getKey();
						for(Message message : list) if(message._getKey().equals(key)) {
							deleteMessageFromList(message);
							break;
						}
					}

					@Override
					public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

					@Override
					public void onCancelled(@NonNull DatabaseError error) {}
				};

				FBD_messagesWithLimit.addChildEventListener(messagesChildEventListenerWithLimit);
				isMessagesChildEventListenerWithLimit = true;
			} else {
				loadingContainer.setVisibility(View.GONE);
				errorContainer.setVisibility(View.VISIBLE);
			}
		});
	}

	private void startListening() {
		if(messagesChildEventListenerWithLimit != null && !isMessagesChildEventListenerWithLimit) {
			FBD_messagesWithLimit.addChildEventListener(messagesChildEventListenerWithLimit);
			isMessagesChildEventListenerWithLimit = true;
		}
		if(mutedValueEventListener != null && !isMutedValueEventListener) {
			FBD_muted.addValueEventListener(mutedValueEventListener);
			isMutedValueEventListener = true;
		}
		if(suggestionValueEventListener != null && !isSuggestionValueEventListener) {
			FBD_suggestion.addValueEventListener(suggestionValueEventListener);
			isSuggestionValueEventListener = true;
		}
	}

	private void stopListening() {
		if(messagesChildEventListenerWithLimit != null && isMessagesChildEventListenerWithLimit) {
			FBD_messagesWithLimit.removeEventListener(messagesChildEventListenerWithLimit);
			isMessagesChildEventListenerWithLimit = false;
		}
		if(mutedValueEventListener != null && isMutedValueEventListener) {
			FBD_muted.removeEventListener(mutedValueEventListener);
			isMutedValueEventListener = false;
		}
		if(suggestionValueEventListener != null && isSuggestionValueEventListener) {
			FBD_suggestion.removeEventListener(suggestionValueEventListener);
			isSuggestionValueEventListener = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopListening();
	}

	@Override
	protected void onResume() {
		super.onResume();
		startListening();
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopListening();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.toolbar_chat, menu);

		MenuItem menuProfileItem = menu.findItem(R.id.menu_item_avatar);
		@SuppressWarnings("deprecation") View view = MenuItemCompat.getActionView(menuProfileItem);

		profileImage = view.findViewById(R.id.toolbar_menu_item_profile_image);
		FrameLayout profileImageContainer = view.findViewById(R.id.toolbar_menu_item_profile_container);

		Glide.with(ChatActivity.this).load(user.getPhotoUrl()).into(profileImage);

		profileImageContainer.setOnClickListener(v -> bottomDialog.show());

		return super.onCreateOptionsMenu(menu);
	}

	private void initializeBottomDialog() {
		bottomDialog = new BottomSheetDialog(ChatActivity.this, R.style.BottomSheetDialogTheme);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View bottomDialogContainer = inflater.inflate(R.layout.bottom_dialog, findViewById(R.id.bottom_dialog_container));

		CircleImageView avatar = bottomDialogContainer.findViewById(R.id.bottom_dialog_avatar);
		ImageView icon = bottomDialogContainer.findViewById(R.id.bottom_dialog_username_icon);
		TextView email = bottomDialogContainer.findViewById(R.id.bottom_dialog_email);
		TextView logoutButton = bottomDialogContainer.findViewById(R.id.bottom_dialog_logout_button);
		TextView username = bottomDialogContainer.findViewById(R.id.bottom_dialog_username);
		ShimmerTextView goldenUsername = bottomDialogContainer.findViewById(R.id.bottom_dialog_username_golden);
		ShimmerTextView epicUsername = bottomDialogContainer.findViewById(R.id.bottom_dialog_username_epic);

		String userColor = getUserColor();
		String userIcon = getUserIcon();

		if(userColor.equals("g")) {
			goldenUsername.setVisibility(View.VISIBLE);
			goldenUsername.setText(user.getDisplayName());
			new GoldenAdapter().applyFor(goldenUsername);
		} else if(userColor.equals("e")) {
			epicUsername.setVisibility(View.VISIBLE);
			epicUsername.setText(user.getDisplayName());
			new EpicAdapter().applyFor(epicUsername);
		} else {
			username.setVisibility(View.VISIBLE);
			username.setText(user.getDisplayName());
			username.setTextColor(getResources().getColor(MessageAdapter.getColorConstant(Integer.parseInt(userColor))));
		}

		if(!userIcon.equals("")) {
			icon.setVisibility(View.VISIBLE);
			Glide.with(ChatActivity.this).load(MessageAdapter.getIconConstant(userIcon)).into(icon);
		}

		email.setText(user.getEmail());

		Glide.with(ChatActivity.this).load(user.getPhotoUrl()).into(avatar);

		logoutButton.setOnClickListener(v -> {
			bottomDialog.dismiss();
			new Dialog(ChatActivity.this)
					.setTitle("Выйти?")
					.setText(user.getDisplayName() + ", Вы действительно хотите выйти из аккаунта " + user.getEmail() + "?")
					.setOnTouchOutside(() -> bottomDialog.show())
					.setCancelButton("Отмена", dialog -> bottomDialog.show())
					.setOkButton("Выйти", dialog -> {
						Auth.getInstance().getFirebaseAuth().signOut();
						Auth.getInstance().getGoogleSignInClient(ChatActivity.this).signOut().addOnCompleteListener(ChatActivity.this, task -> {
							startActivity(new Intent(ChatActivity.this, AuthActivity.class));
							overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
							finish();
						});
					})
					.show(true, true);
		});

		bottomDialog.setContentView(bottomDialogContainer);
	}

	private void startIntroduction() {
		SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
		if(preferences.getBoolean("isFirstTime", true)) {
			messageInputDivider.setVisibility(View.VISIBLE);
			messageInputContainer.setVisibility(View.VISIBLE);
			new Dialog(ChatActivity.this)
					.setTitle("Привет, " + user.getDisplayName() + "! \uD83D\uDC4B")
					.setText("Добро пожаловать в Skylire Live Chat! Здесь Вы в режиме реального времени можете общаться с другими пользователями.\n\nПри каждом входе в приложение Вам будут показаны " + messagesRenderLimit + " последних сообщений.")
					.setOkButton("Продолжить", dialog -> {})
					.setOnDismiss(() -> new MaterialTapTargetPrompt.Builder(ChatActivity.this)
							.setPrimaryTextColour(getResources().getColor(R.color.white))
							.setSecondaryTextColour(getResources().getColor(R.color.white))
							.setFocalColour(getResources().getColor(R.color.background_container))
							.setIconDrawableColourFilter(getResources().getColor(R.color.primary))
							.setBackgroundColour(getResources().getColor(R.color.primary))
							.setPrimaryTextTypeface(ResourcesCompat.getFont(ChatActivity.this, R.font.product_sans_bold))
							.setTarget(messageSendImage)
							.setPrimaryText("Отправить сообщение")
							.setSecondaryText("Нажмите сюда, чтобы отправить своё первое сообщение")
							.setPromptStateChangeListener((prompt, state) -> {
								if(state == MaterialTapTargetPrompt.STATE_DISMISSED || state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
									new MaterialTapTargetPrompt.Builder(ChatActivity.this)
											.setPrimaryTextColour(getResources().getColor(R.color.white))
											.setSecondaryTextColour(getResources().getColor(R.color.white))
											.setFocalColour(getResources().getColor(R.color.background_container))
											.setIconDrawableColourFilter(getResources().getColor(R.color.primary))
											.setBackgroundColour(getResources().getColor(R.color.primary))
											.setPrimaryTextTypeface(ResourcesCompat.getFont(ChatActivity.this, R.font.product_sans_bold))
											.setTarget(profileImage)
											.setPrimaryText("Профиль")
											.setSecondaryText("Нажмите сюда, чтобы посмотреть информацию о текущем профиле")
											.setPromptStateChangeListener((prompt2, state2) -> {
												if(state2 == MaterialTapTargetPrompt.STATE_DISMISSED || state2 == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
													SharedPreferences.Editor editor = preferences.edit();
													editor.putBoolean("isFirstTime", false);
													editor.apply();
													startMuteListener();
													startSuggestionListener();
												}
											})
											.show();
								}
							})
							.show())
					.show(true, true);
		} else {
			startMuteListener();
			startSuggestionListener();
		}
	}

	private void startMuteListener() {
		SharedPreferences preferences = getSharedPreferences("security", MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		mutedValueEventListener = new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Iterable<DataSnapshot> mutedDataSnapshotList = dataSnapshot.getChildren();
				boolean isMuted = false;
				boolean isGlobalMute = false;
				for(DataSnapshot snapshotItem : mutedDataSnapshotList) {
					String email = snapshotItem.getValue(String.class);
					if(email != null && (email.equals(user.getEmail()) || email.equals("*"))) {
						isMuted = true;
						isGlobalMute = email.equals("*");
						break;
					}
				}
				String mutedEmails = preferences.getString("muted_emails", "");
				if(isMuted) {
					if(!mutedEmails.contains(user.getEmail() + ";")) {
						editor.putString("muted_emails", mutedEmails + user.getEmail() + ";");
						editor.apply();
						new Dialog(ChatActivity.this)
								.setTitle("Ограничение")
								.setText(user.getDisplayName() + (isGlobalMute ? ", модерация Skylire ограничела всем пользователям доступ к отправке сообщений." : ", модерация Skylire ограничела Вам доступ к отправке сообщений, хотя Вы по-прежнему можете наблюдать за сообщениями других пользователей."))
								.show(true, true);
					}
					mute();
				} else {
					if(mutedEmails.contains(user.getEmail() + ";")) {
						editor.putString("muted_emails", mutedEmails.replace(user.getEmail() + ";", ""));
						editor.apply();
						new Dialog(ChatActivity.this)
								.setTitle("Ограничение снято")
								.setText(user.getDisplayName() + ", модерация Skylire открыла Вам доступ к отправке сообщений.")
								.show(true, true);
					}
					unmute();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {}
		};

		FBD_muted.addValueEventListener(mutedValueEventListener);
		isMutedValueEventListener = true;
	}

	private void mute() {
		messageInputContainer.setVisibility(View.GONE);
		messageInputDivider.setVisibility(View.GONE);
	}

	private void unmute() {
		messageInputContainer.setVisibility(View.VISIBLE);
		messageInputDivider.setVisibility(View.VISIBLE);
	}

	private void startSuggestionListener() {
		SharedPreferences preferences = getSharedPreferences("security", MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		suggestionValueEventListener = new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				Suggestion suggestion = dataSnapshot.getValue(Suggestion.class);
				if(suggestion != null) {
					if(suggestion.getEmail().equals(user.getEmail()) && !suggestion.getHash().equals(preferences.getString("received_suggestion", ""))) {
						String newColor = !suggestion.getColor().equals("") ? suggestion.getColor() : getUserColor();
						String newIcon = !suggestion.getIcon().equals("") ? suggestion.getIcon() : getUserIcon();
						editor.putString("received_suggestion", suggestion.getHash());
						editor.apply();
						boolean isBottomDialogWasOpen = bottomDialog.isShowing();
						if(isBottomDialogWasOpen) bottomDialog.dismiss();
						new Dialog(ChatActivity.this)
								.setTitle("Новая кастомизация")
								.setText(user.getDisplayName() + ", модерация Skylire предлагает Вам новый стиль отображаемого имени. Хотите принять его?")
								.setUsername(user.getDisplayName())
								.setUsernameColor(newColor)
								.setUsernameIcon(newIcon)
								.setCancelButton("Нет", dialog -> {})
								.setOkButton("Принять", dialog -> {
									updateUserColorAndIcon(newColor, newIcon);
									if(isBottomDialogWasOpen) bottomDialog.show();
								})
								.show(false, true);
					}
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {}
		};

		FBD_suggestion.addValueEventListener(suggestionValueEventListener);
		isSuggestionValueEventListener = true;
	}

	private void updateUserColorAndIcon(String color, String icon) {
		SharedPreferences preferences = getSharedPreferences("common", MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("color", color);
		editor.putString("icon", icon);
		editor.apply();

		initializeBottomDialog();
	}
}