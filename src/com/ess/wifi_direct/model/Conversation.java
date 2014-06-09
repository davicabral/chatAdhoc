package com.ess.wifi_direct.model;

import java.net.SocketAddress;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.davioliveira.cantalk.R;
import com.ess.cmd.models.AClientSocketMsg;

public class Conversation {

	private String mySenderName;
	private String destName;

	private SocketAddress destIP;
	private AClientSocketMsg socket;
	private ArrayList<Msg> msgs = new ArrayList<Msg>();
	private msgAdapter msgAdapter;
	
	private Runnable runOnNewItem;
	
	public Conversation(Context context, String mySenderName, String destName, AClientSocketMsg socket) {
		this.socket = socket;
		this.destName = destName;
		this.mySenderName = mySenderName;
		msgAdapter = new msgAdapter(context, msgs);
		destIP = socket.getRemoteIP();
	}
	
	public SocketAddress getDestIP() {
		return destIP;
	}
	
	public String getDestName() {
		return destName;
	}

	public msgAdapter getMsgAdapter() {
		return msgAdapter;
	}

	public void AddMsg(Msg msg){
		msgs.add(msg);
		msgAdapter.notifyDataSetChanged();
		if(runOnNewItem != null)
			new Thread(runOnNewItem).start();
	}
	
	public void SendAndAddMsg(Msg msg){
		try {
			socket.sendMsg(msg.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
		AddMsg(msg);
	}
	
	class msgAdapter extends ArrayAdapter<Msg> {

		public msgAdapter(Context context, ArrayList<Msg> users) {
	       super(context, R.layout.each_msg_left, users);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	       Msg msg = getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	          LayoutInflater li = LayoutInflater.from(getContext());
	          if(msg.getSenderName().equals(mySenderName))
	        	  convertView = li.inflate(R.layout.each_msg_left, parent, false);
	          else
	        	  convertView = li.inflate(R.layout.each_msg_right, parent, false);
	       }
	       // Lookup view for data population
	       TextView tvName = (TextView) convertView.findViewById(R.id.tvSender);
	       TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
	       TextView tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
	       // Populate the data into the template view using the data object
	       tvName.setText(msg.getSenderName());
	       tvTime.setText(msg.getTime());
	       tvMsg.setText(msg.getMsg());
	       // Return the completed view to render on screen
	       return convertView;
	   }
		
	}

	public void runOnNewItem(Runnable runnable) {
		runOnNewItem = runnable;
	}
	
}
