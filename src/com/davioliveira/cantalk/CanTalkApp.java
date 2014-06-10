package com.davioliveira.cantalk;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ess.cmd.models.AClientSocketMsg;
import com.ess.socket.ClientSocketMsg;
import com.ess.socket.ClientSocketMsgListener;
import com.ess.socket.ServerClientSocketMsg;
import com.ess.socket.ServerSocketMsg;
import com.ess.socket.ServerSocketMsgListener;
import com.ess.wifi_direct.WifiDirectMgr;
import com.ess.wifi_direct.model.Conversation;
import com.ess.wifi_direct.model.Msg;
import com.google.gson.Gson;

public class CanTalkApp extends Application implements ServerSocketMsgListener, ClientSocketMsgListener{

	public static final int PORT = 5005;
	private static final String INIT_MSG = "initMsg";
	
	public WifiDirectMgr wifiDirectMgr;
	public boolean wifiDirectState;
	public String mySenderName;
	public AlertDialog alertDlgConnecting;
	public Conversation activeConversation;

	private ArrayList<Conversation> conversations = new ArrayList<Conversation>();
	private ServerSocketMsg serverSocket;
	private AClientSocketMsg clientSocket;

	public Conversation haveConversation(String senderName){
		for (Conversation conversation : conversations) {
			if(senderName.equals(conversation.getDestName()))
				return conversation;
		}
		return null;
	}
	
	public void dismissAlertDlg(){
		if(alertDlgConnecting != null)
			alertDlgConnecting.dismiss();
	}
	
	public void showMsgActivity(Conversation conversation){
		activeConversation = conversation;
		Intent i = new Intent(this, MsgActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	private void showMsgNotificaion(Msg msg, Conversation conversation){
		activeConversation = conversation;
		Intent i = new Intent(this, MsgActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, i, 0);
		
		Notification n = new NotificationCompat.Builder(getApplicationContext()).
				setContentTitle("Nova msg de " + msg.getSenderName()).
				setContentText(msg.getMsg()).
				setSmallIcon(R.drawable.ic_launcher).
				setContentIntent(pi).
				setAutoCancel(true).
				setVibrate(new long[]{100, 200, 100, 500}).
				build();
		NotificationManager notificationManager = 
				  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, n);
	}
	
	public Conversation getConversation(SocketAddress destIP) {
		for (Conversation conversation : conversations) {
			if(destIP.equals(conversation.getDestIP()))
				return conversation;
		}
		return null;
	}

	public void onReceiveMsg(SocketAddress ip, final Msg objMsg) {
		Conversation conversation = getConversation(ip);
		if(conversation == null){
			conversation = new Conversation(this, mySenderName, clientSocket);
			conversations.add(conversation);
		}
		if(INIT_MSG.equals(objMsg.getMsg())){
			Log.i("CON", "Init Msg");
			conversation.setDestName(objMsg.getSenderName());
			return;
		}
		conversation.AddMsg(objMsg);
		showMsgNotificaion(objMsg, conversation);
	}
	
	public void initServer() {
		serverSocket = new ServerSocketMsg(PORT, 10, this);
		serverSocket.startListen();
	}

	private void configNewConversation(SocketAddress ip, AClientSocketMsg socket) {
		Conversation conversation = getConversation(ip);
		if(conversation == null){
			conversation = new Conversation(this, mySenderName, socket);
			Msg msg = new Msg(mySenderName, INIT_MSG);
			try {
				socket.sendMsg(new Gson().toJson(msg));
			} catch (Exception e) {
				e.printStackTrace();
			}
			conversations.add(conversation);
		}
		if(alertDlgConnecting != null){
			dismissAlertDlg();
			showMsgActivity(conversation);	
		}
	}

	@Override
	public void onRemoteClientConect(SocketAddress ip) {
		Log.i("CON", "New client " + ip);
		ServerClientSocketMsg socket = serverSocket.getRemoteClientByIP(ip);
		configNewConversation(ip, socket);
	}

	@Override
	public void onRemoteClientDisconect(SocketAddress ip) {
	}

	@Override
	public void onRemoteClientMsg(SocketAddress ip, String msg) {
		Msg objMsg = new Gson().fromJson(msg, Msg.class);
		onReceiveMsg(ip, objMsg);
	}

	public void initClient(final InetAddress groupOwnerAddress) {
		final ClientSocketMsg clientSocketMsg = new ClientSocketMsg(this);
		this.clientSocket = clientSocketMsg;
		new Thread(new Runnable() {
			@Override
			public void run() {
				clientSocketMsg.connect(groupOwnerAddress.getHostAddress(), PORT);
				Log.i("socket", clientSocketMsg.getRemoteIP() + " - " + new InetSocketAddress(groupOwnerAddress, PORT));
				configNewConversation(new InetSocketAddress(groupOwnerAddress, PORT), clientSocketMsg);
			}
		}).start();
	}

	@Override
	public void onServerConnect() {
	}

	@Override
	public void onServerDisconnect() {
	}

	@Override
	public void onServerMsg(SocketAddress ip, String msg) {
		Msg objMsg = new Gson().fromJson(msg, Msg.class);
		onReceiveMsg(ip, objMsg);
	}

	public void clearSockets(){
		if(serverSocket != null){
			for(ServerClientSocketMsg serverClientSocketMsg : serverSocket.getRemoteClients())
				serverClientSocketMsg.stopRunning();
			serverSocket.stopRunning();
			serverSocket.stopListen();
		}
	}
	
}
