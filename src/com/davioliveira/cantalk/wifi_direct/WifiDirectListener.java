package com.davioliveira.cantalk.wifi_direct;

public interface WifiDirectListener {
	
	/**
	 * Mudança no estado do dispositivo de wifi-Direct
	 * @param state = true para ligado, false para desligado
	 */
	void onWifiDirectStateChange(boolean state); 
	
}
