package com.ess.wifi_direct;

import com.davioliveira.cantalk.CanTalkApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;

public class WifiDirectMgr {

	//WifiDirect variables
	private WifiP2pManager mManager;
	private Channel mChannel;
	private BroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	
	private CanTalkApp canTalkApp;
	private WifiDirectListener wifiDirectListener; 
	
	public WifiDirectMgr(CanTalkApp canTalkApp, WifiDirectListener wifiDirectListener) {
		this.canTalkApp = canTalkApp;
		this.wifiDirectListener = wifiDirectListener;
		configWifiDirect(); 
	}

	private void configWifiDirect() {
		mManager = (WifiP2pManager) canTalkApp.getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(canTalkApp, canTalkApp.getMainLooper(), null);
	    mReceiver = new WifiDirectBroadcastReceiver(canTalkApp, mManager, mChannel, wifiDirectListener);
	    //Intent Filter
	    mIntentFilter = new IntentFilter();
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	}

	/* register the broadcast receiver with the intent values to be matched */
	public void onResume() {
		try {
		    canTalkApp.registerReceiver(mReceiver, mIntentFilter);
			Log.i("Receiver", "Registred");
		} catch (Exception e) {
			Log.i("Receiver", "Not Registred");
		}
	    searchPeers();
	}

	public void onPause() {
		try {
			canTalkApp.unregisterReceiver(mReceiver);	
			Log.i("Receiver", "Disregistred");
		} catch (Exception e) {
			Log.i("Receiver", "Not Registred");
		}
	}

	/**
	 * Busca por dispositivos
	 */
	public void searchPeers() {
		mManager.discoverPeers(mChannel, new ActionListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public void connect(WifiP2pDevice device) {
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(canTalkApp, "Connect failed. Retry.",Toast.LENGTH_SHORT).show();
            }
        });
	}

	public void cancelConnect(){
        mManager.cancelConnect(mChannel, new ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(canTalkApp, "Connect failed. Retry.",Toast.LENGTH_SHORT).show();
            }
        });
	}
	
}
