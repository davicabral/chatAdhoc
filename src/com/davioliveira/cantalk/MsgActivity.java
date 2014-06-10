package com.davioliveira.cantalk;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ess.wifi_direct.model.Msg;
import com.ess.wifi_direct.model.NewItemListener;

public class MsgActivity extends Activity implements OnClickListener, OnKeyListener, NewItemListener {

	private CanTalkApp canTalkApp;
	private ListView lvMsgs;
	private EditText etMsg;
	private ImageButton ibSend;

	private ArrayAdapter<Msg> msgAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg);
		
		canTalkApp = (CanTalkApp) getApplication();
		lvMsgs = (ListView) findViewById(R.id.lvMsgs);
		etMsg = (EditText) findViewById(R.id.etMsg);
		ibSend = (ImageButton) findViewById(R.id.ibSend);
		
		canTalkApp.activeConversation.setNewItemListener(this);
		msgAdapter = canTalkApp.activeConversation.getMsgAdapter();
		lvMsgs.setAdapter(msgAdapter);
		ibSend.setOnClickListener(this);
		etMsg.setOnKeyListener(this);

        scrollListToEnd();
	}

	private void scrollListToEnd() {
		lvMsgs.setSelection(msgAdapter.getCount() - 1);
	}

	@Override
	public void onClick(View v) {
		Msg msg = new Msg(canTalkApp.mySenderName, etMsg.getText().toString());
		canTalkApp.activeConversation.SendAndAddMsg(msg);
		etMsg.setText("");
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    // Checks whether a hardware keyboard is available
	    if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
	    	scrollListToEnd();
	    } 
	}
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_ENTER){
			onClick(ibSend);
			return true;
		}
		return false;
	}

	@Override
	public void newItemAdded() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				msgAdapter.notifyDataSetChanged();
	            scrollListToEnd();
			}
		});
	}
	
}
