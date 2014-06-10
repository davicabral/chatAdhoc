package com.ess.wifi_direct;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

import com.davioliveira.cantalk.CanTalkApp;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver 
		implements ConnectionInfoListener, PeerListListener {

	private CanTalkApp canTalkApp;
    private WifiP2pManager mManager;
    private Channel mChannel;
    private WifiDirectListener wifiDirectListener;

    public WifiDirectBroadcastReceiver(CanTalkApp canTalkApp, WifiP2pManager manager, Channel channel,
            WifiDirectListener wifiDirectListener) {
        super();
		this.canTalkApp = canTalkApp;
        this.mManager = manager;
        this.mChannel = channel;
        this.wifiDirectListener = wifiDirectListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            boolean ativo = (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED);
			wifiDirectListener.onWifiDirectStateChange(ativo);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	// request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, this);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        	Log.i("CON", "CON_CHANGE");
        	if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // We are connected with the other device, request connection
                // info to find group owner IP
                mManager.requestConnectionInfo(mChannel, this);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) { 
      	  // this device details has changed(name, connected, etc)
        	WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
        	canTalkApp.mySenderName = wifiP2pDevice.deviceName;
        }
    }

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		wifiDirectListener.onPeersListAvailable(peers.getDeviceList());
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		// InetAddress from WifiP2pInfo struct.
//		wifiDirectListener.onConnected();
        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a server thread and accepting
            // incoming connections.
        	Log.i("CON", "initServer");
        	Toast.makeText(canTalkApp, "Init Server", Toast.LENGTH_SHORT).show();
        	canTalkApp.initServer();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, you'll want
            // to create a client thread that connects to the group owner.
        	Log.i("CON", "initClient");
        	Toast.makeText(canTalkApp, "Init Client", Toast.LENGTH_SHORT).show();
        	canTalkApp.initClient(info.groupOwnerAddress);
        }
	}

}

