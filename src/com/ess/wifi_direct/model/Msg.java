package com.ess.wifi_direct.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Msg {

	private String senderName;
	private String time;
	private String msg;
	
	public Msg(String senderName, String msg) {
		this.senderName = senderName;
		this.msg = msg;
		Date date = new Date(System.currentTimeMillis());
		time = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(date);
	}

	public String getSenderName() {
		return senderName;
	}

	public String getTime() {
		return time;
	}

	public String getMsg() {
		return msg;
	}
	
}
