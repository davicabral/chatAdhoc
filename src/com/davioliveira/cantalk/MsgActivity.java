package com.davioliveira.cantalk;

import java.net.SocketAddress;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ess.wifi_direct.model.Conversation;
import com.ess.wifi_direct.model.Msg;

public class MsgActivity extends Activity implements OnClickListener, OnKeyListener {

	public static final String DEST_IP = "destIP";
	
	private CanTalkApp canTalkApp;
	private ListView lvMsgs;
	private EditText etMsg;
	private ImageButton ibSend;

	private Conversation conversation;

	private ArrayAdapter<Msg> msgAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg);
		
		canTalkApp = (CanTalkApp) getApplication();
		lvMsgs = (ListView) findViewById(R.id.lvMsgs);
		etMsg = (EditText) findViewById(R.id.etMsg);
		ibSend = (ImageButton) findViewById(R.id.ibSend);
		
		SocketAddress destIP = (SocketAddress) getIntent().getSerializableExtra(DEST_IP);
		conversation = canTalkApp.getConversation(destIP);
		conversation.runOnNewItem(new Runnable() {
			@Override
			public void run() {
				lvMsgs.post(new Runnable() {
			        @Override
			        public void run() {
			            lvMsgs.setSelection(msgAdapter.getCount() - 1);
			        }
			    });
			}
		});
		msgAdapter = conversation.getMsgAdapter();
		lvMsgs.setAdapter(msgAdapter);
		ibSend.setOnClickListener(this);
		etMsg.setOnKeyListener(this);
	}

	@Override
	public void onClick(View v) {
		Msg msg = new Msg(canTalkApp.mySenderName, etMsg.getText().toString());
		conversation.SendAndAddMsg(msg);
		etMsg.setText("");
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_ENTER){
			onClick(ibSend);
			return true;
		}
		return false;
	}
	
}
