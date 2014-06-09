package com.ess.wifi_direct;

import java.net.SocketAddress;
import java.util.Collection;

import android.net.wifi.p2p.WifiP2pDevice;

public interface WifiDirectListener {
	
	void onConnected(SocketAddress destIP);
	
	/**
	 * Mudança no estado do dispositivo de wifi-Direct
	 * @param state = true para ligado, false para desligado
	 */ 
	void onWifiDirectStateChange(boolean state);
	void onPeersListAvailable(Collection<WifiP2pDevice> deviceList);
	void execOnUiThread(Runnable runnable);
	
}
