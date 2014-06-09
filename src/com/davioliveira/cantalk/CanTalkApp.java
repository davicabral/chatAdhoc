package com.davioliveira.cantalk;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;

import android.app.Application;
import android.util.Log;

import com.ess.cmd.models.AClientSocketMsg;
import com.ess.socket.ClientSocketMsg;
import com.ess.socket.ClientSocketMsgListener;
import com.ess.socket.ServerClientSocketMsg;
import com.ess.socket.ServerSocketMsg;
import com.ess.socket.ServerSocketMsgListener;
import com.ess.wifi_direct.WifiDirectListener;
import com.ess.wifi_direct.WifiDirectMgr;
import com.ess.wifi_direct.model.Conversation;
import com.ess.wifi_direct.model.Msg;

public class CanTalkApp extends Application implements ServerSocketMsgListener, ClientSocketMsgListener{

	public static final int PORT = 5005;
	public WifiDirectMgr wifiDirectMgr;
	public boolean wifiDirectState;
	public String mySenderName;

	private ArrayList<Conversation> conversations = new ArrayList<Conversation>();
	private ServerSocketMsg serverSocket;
	private WifiDirectListener wifiDirectListener;

	public Conversation getConversation(SocketAddress destIP) {
		for (Conversation conversation : conversations) {
			if(conversation.getDestIP().equals(destIP))
				return conversation;
		}
		return null;
	}

	public void onReceiveMsg(SocketAddress ip, String msg) {
		final Conversation conversation = getConversation(ip);
		final Msg objMsg = new Msg(conversation.getDestName(), msg);
		wifiDirectListener.execOnUiThread(new Runnable() {
			@Override
			public void run() {
				conversation.AddMsg(objMsg);
			}
		});
	}
	
	public void initServer(WifiDirectListener wifiDirectListener) {
		this.wifiDirectListener = wifiDirectListener;
		serverSocket = new ServerSocketMsg(PORT, 10, this);
		serverSocket.startListen();
	}

	private void configNewConversation(SocketAddress ip, AClientSocketMsg socket) {
		if(getConversation(ip) == null){
			Conversation conversation = new Conversation(this, mySenderName, "destName", socket);
			conversations.add(conversation);
		}
		wifiDirectListener.onConnected(ip);
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
		onReceiveMsg(ip, msg);
	}

	public void initClient(final InetAddress groupOwnerAddress, WifiDirectListener wifiDirectListener) {
		this.wifiDirectListener = wifiDirectListener;
		final ClientSocketMsg clientSocketMsg = new ClientSocketMsg(this);
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
		onReceiveMsg(ip, msg);
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
