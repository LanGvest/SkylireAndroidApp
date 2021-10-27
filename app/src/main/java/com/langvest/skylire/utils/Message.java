package com.langvest.skylire.utils;

import android.text.format.DateFormat;
import java.util.Date;

public class Message {

    private String m = ""; // email
    private String i = ""; // icon
    private String n = ""; // username
    private String c = ""; // color
    private String t = ""; // text
    private long s = 0; // timestamp
    private String _key = "";
    private boolean _animate = true;
    private int _type = DEFAULT_TYPE;
    public static final int DEFAULT_TYPE = 0;
    public static final int WELCOME_TYPE = 1;

    public Message() {}

    public Message(int type, String username) {
        s = new Date().getTime();
        n = username;
        _type = type;
    }

    public Message(String text, String email, String username, String icon, String color) {
        s = new Date().getTime();
        t = validateText(text);
        m = email;
        i = icon;
        n = username;
        c = color;
    }

    private String validateText(String text) {
        String result = text.trim().replaceAll("\\n", " ").replaceAll(" +", " ");
        if(result.length() > 300) result = result.substring(0, 300).trim() + "...";
        return result;
    }

    public String getM() {
        return m;
    }

    public String getI() {
        return i;
    }

    public String getN() {
        return n;
    }

    public String getC() {
        return c;
    }

    public String getT() {
        return t;
    }

    public long getS() {
        return s;
    }

    public String _getKey() {
        return _key;
    }

    public void _setKey(String key) {
        _key = key;
    }

    public int _getType() {
        return _type;
    }

    public boolean _canAnimate() {
        return _animate;
    }

    public void _setAnimate(boolean animate) {
        _animate = animate;
    }

    public String _getMetaWithTime() {
        if(_type != DEFAULT_TYPE) return String.valueOf(_type);
        return m + "-" + c + "-" + DateFormat.format("yyyy-M-d-H-m", s) + "-" + i;

    }

    public String _getDateMeta() {
        return DateFormat.format("yyyy-M-d", s).toString();
    }
}