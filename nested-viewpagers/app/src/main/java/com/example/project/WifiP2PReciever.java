package com.example.project;

/**
 * Created by Taha on 6/22/2017.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WifiP2PReciever extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private ChatFragment fragment;


    public WifiP2PReciever(WifiP2pManager manager, WifiP2pManager.Channel channel,
                           ChatFragment fragment) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.fragment = fragment;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.d("PeerLIst", "onPeersAvailable: " + peers.getDeviceList());
                        //Toast.makeText(activity,"onPeersAvailable: " + peers.getDeviceList(), Toast.LENGTH_SHORT).show();
                        fragment.setWifiP2pDeviceList(peers);

                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed!  We should probably do something about
            // that.

            if (mManager != null) {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.d("PeerLIst", "onPeersAvailable: " + peers.getDeviceList());
                        //Toast.makeText(activity,"onPeersAvailable: " + peers.getDeviceList(), Toast.LENGTH_SHORT).show();
                        fragment.setWifiP2pDeviceList(peers);
                    }
                });
            }


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.
            mManager.requestConnectionInfo(mChannel, fragment);


        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        }
    }
}
