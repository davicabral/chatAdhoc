package com.davioliveira.cantalk.wifi_direct;

import com.davioliveira.cantalk.adapters.AdapterContatos;
import com.davioliveira.cantalk.utils.Pessoa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class CanTalkWifiDirectMgr implements PeerListListener{

	//WifiDirect variables
	private boolean wifiDirectState;
	private WifiP2pManager mManager;
	private Channel mChannel;
	private BroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	
	private Context context;
	private WifiDirectListener wifiDirectListener; 
	private AdapterContatos adapterContatos;
	
	public CanTalkWifiDirectMgr(Context context, WifiDirectListener wifiDirectListener, 
			AdapterContatos adapterContatos) {
		this.context = context;
		this.wifiDirectListener = wifiDirectListener;
		this.adapterContatos = adapterContatos;
		configWifiDirect(); 
	}

	private void configWifiDirect() {
		mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
	    mChannel = mManager.initialize(context, context.getMainLooper(), null);
	    mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, wifiDirectListener, this);
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
		    context.registerReceiver(mReceiver, mIntentFilter);
			Log.i("Receiver", "Registred");
		} catch (Exception e) {
			Log.i("Receiver", "Not Registred");
		}
	    searchPeers();
	}

	public void onPause() {
		try {
			context.unregisterReceiver(mReceiver);	
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

	public boolean isWifiDirectState() {
		return wifiDirectState;
	}

	public void setWifiDirectState(boolean wifiDirectState) {
		this.wifiDirectState = wifiDirectState;
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		for (WifiP2pDevice peer : peers.getDeviceList()) {
			Pessoa pessoa = new Pessoa();
			Log.i("PEER", peer.deviceName);
			pessoa.setNome(peer.deviceName);
			adapterContatos.addContact(pessoa);
		}
	}

}
