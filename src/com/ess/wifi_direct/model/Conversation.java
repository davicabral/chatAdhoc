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
import com.google.gson.Gson;

public class Conversation {

	private String mySenderName;
	private String destName;
	private ArrayList<Msg> msgs = new ArrayList<Msg>();
	private MsgAdapter msgAdapter;

	private SocketAddress destIP;
	private AClientSocketMsg socket;
	private NewItemListener newItemListener;
	
	public Conversation(Context context, String mySenderName, AClientSocketMsg socket) {
		this.socket = socket;
		this.mySenderName = mySenderName;
		msgAdapter = new MsgAdapter(context, msgs);
		destIP = socket.getRemoteIP();
	}
	
	public String getDestName(){
		return destName;
	}

	public void setDestName(String destName) {
		this.destName = destName;
	}

	public SocketAddress getDestIP() {
		return destIP;
	}

	public MsgAdapter getMsgAdapter() {
		return msgAdapter;
	}

	public void setNewItemListener(NewItemListener newItemListener) {
		this.newItemListener = newItemListener;
	}
	
	public void AddMsg(Msg msg){
		msgs.add(msg);
		if(newItemListener != null)
			newItemListener.newItemAdded();
	}
	
	public void SendAndAddMsg(Msg msg){
		try {
			socket.sendMsg(new Gson().toJson(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		AddMsg(msg);
	}
	
	class MsgAdapter extends ArrayAdapter<Msg> {

		public MsgAdapter(Context context, ArrayList<Msg> users) {
	       super(context, R.layout.each_msg_left, users);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	       Msg msg = getItem(position);    
           LayoutInflater li = LayoutInflater.from(getContext());
           if(msg.getSenderName().equals(mySenderName))
        	  convertView = li.inflate(R.layout.each_msg_right, parent, false);
           else
        	  convertView = li.inflate(R.layout.each_msg_left, parent, false);
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

}
