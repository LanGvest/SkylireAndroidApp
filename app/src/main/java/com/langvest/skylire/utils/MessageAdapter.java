package com.langvest.skylire.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.langvest.skylire.R;
import com.romainpiel.shimmer.ShimmerTextView;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends ArrayAdapter<Message> {

    int listLayout;
    ArrayList<Message> list;
    Activity activity;
    private static final String PNG = "png";
    private static final String GIF = "gif";
    public static final String[] iconFormats = {
            PNG,
            PNG,
            GIF,
            PNG,
            GIF,
            PNG,
            PNG,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            PNG,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            PNG,
            GIF,
            GIF,
            PNG,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            PNG,
            GIF,
            PNG,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            GIF,
            PNG,
            PNG,
            PNG,
            PNG,
            PNG
    };

    public MessageAdapter(Activity activity, int listLayout, int field, ArrayList<Message> list) {
        super(activity, listLayout, field, list);
        this.activity = activity;
        this.listLayout = listLayout;
        this.list = list;
    }

    public static String getIconConstant(String index) {
        if(index.startsWith("http")) return index;
        return "https://skylire.langvest.by/icons/" + index + "." + iconFormats[Integer.parseInt(index)];
    }

    public static int getColorConstant(int index) {
        switch(index) {
            case 0: return R.color.color_0;
            case 1: return R.color.color_1;
            case 2: return R.color.color_2;
            case 3: return R.color.color_3;
            case 4: return R.color.color_4;
            case 5: return R.color.color_5;
            case 6: return R.color.color_6;
            case 7: return R.color.color_7;
            case 8: return R.color.color_8;
            case 9: return R.color.color_9;
            case 10: return R.color.color_10;
            case 11: return R.color.color_11;
            case 12: return R.color.color_12;
            case 13: return R.color.color_13;
            case 14: return R.color.color_14;
            case 15: return R.color.color_15;
            case 16: return R.color.color_16;
            case 17: return R.color.color_17;
            case 18: return R.color.color_18;
            default: return R.color.color_19;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View item = inflater.inflate(listLayout, parent, false);

        final float scale = activity.getResources().getDisplayMetrics().density;
        int $52dp = (int) (52*scale+0.5f);
        int $24dp = (int) (24*scale+0.5f);
        int $14dp = (int) (14*scale+0.5f);
        int $2dp = (int) (2*scale+0.5f);

        Message prevMessage = position > 0 ? list.get(position-1) : null;
        Message message = list.get(position);
        Message nextMessage = position+1 < list.toArray().length ? list.get(position+1) : null;

        LinearLayout mainContainer = item.findViewById(R.id.main_container);
        LinearLayout welcomeContainer = item.findViewById(R.id.welcome_container);
        LinearLayout metaContainer = item.findViewById(R.id.meta_container);
        LinearLayout dateContainer = item.findViewById(R.id.date_container);
        View dateDivider = item.findViewById(R.id.date_divider);
        TextView date = item.findViewById(R.id.date);
        TextView welcome = item.findViewById(R.id.welcome);
        ImageView icon = item.findViewById(R.id.icon);
        TextView username = item.findViewById(R.id.username);
        ShimmerTextView goldenUsername = item.findViewById(R.id.golden_username);
        ShimmerTextView epicUsername = item.findViewById(R.id.epic_username);
        TextView time = item.findViewById(R.id.time);
        TextView text = item.findViewById(R.id.message);

        if(message._getType() == Message.WELCOME_TYPE) {
            mainContainer.setVisibility(View.GONE);
            welcomeContainer.setVisibility(View.VISIBLE);
            welcome.setText(MessageFormat.format("{0}, {1}", message.getN(), activity.getResources().getString(R.string.welcome_message)));
        } else {
            if(position == 0) mainContainer.setPadding(mainContainer.getPaddingLeft(), 0, mainContainer.getPaddingRight(), mainContainer.getPaddingBottom());

            if(prevMessage != null && prevMessage._getType() == Message.WELCOME_TYPE) mainContainer.setPadding(mainContainer.getPaddingLeft(), $14dp, mainContainer.getPaddingRight(), mainContainer.getPaddingBottom());

            if(prevMessage != null && prevMessage._getMetaWithTime().equals(message._getMetaWithTime())) {
                metaContainer.setVisibility(View.GONE);
            } else {
                if(message.getC().equals("g")) {
                    goldenUsername.setVisibility(View.VISIBLE);
                    goldenUsername.setText(message.getN());
                    new GoldenAdapter().applyFor(goldenUsername);
                } else if(message.getC().equals("e")) {
                    epicUsername.setVisibility(View.VISIBLE);
                    epicUsername.setText(message.getN());
                    new EpicAdapter().applyFor(epicUsername);
                } else {
                    username.setVisibility(View.VISIBLE);
                    username.setText(message.getN());
                    username.setTextColor(activity.getResources().getColor(getColorConstant(Integer.parseInt(message.getC()))));
                }

                if(!message.getI().equals("")) {
                    icon.setVisibility(View.VISIBLE);
                    Glide.with(activity).load(getIconConstant(message.getI())).into(icon);
                }

                time.setText(DateFormat.format("H:mm", message.getS()));
            }

            if(nextMessage != null && nextMessage._getMetaWithTime().equals(message._getMetaWithTime())) mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), $2dp);

            text.setText(message.getT());
        }

        if(position == 0 || prevMessage != null && !prevMessage._getDateMeta().equals(message._getDateMeta())) {
            if(position == 0) {
                dateContainer.setPadding(0, $52dp+$14dp, 0, $24dp);
                dateDivider.setVisibility(View.GONE);
            }
            dateContainer.setVisibility(View.VISIBLE);
            String today = DateFormat.format("dd.MM.yyyy", new Date().getTime()).toString();
            String dateText = DateFormat.format("dd.MM.yyyy", message.getS()).toString();
            if(dateText.equals(today)) dateText = "Сегодня";
            date.setText(dateText);
        }

        if(message._canAnimate()) {
            message._setAnimate(false);
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.new_message);
            mainContainer.startAnimation(animation);
        }

        return item;
    }
}