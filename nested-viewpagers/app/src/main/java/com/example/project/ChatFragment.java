package com.example.project;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashSet;

import static android.content.Context.WIFI_SERVICE;
import static android.os.Looper.getMainLooper;
import static com.example.project.ServerThread.HANDSHAKE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {


    public ChatFragment() {
        // Required empty public constructor
    }


    public static String getDottedDecimalIP(byte[] ipAddr) {
        //convert to dotted decimal notation:
        String ipAddrStr = "";
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i] & 0xFF;
        }
        return ipAddrStr;
    }


    public static String getDottedDecimalIP(int ipAddr) {

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddr = Integer.reverseBytes(ipAddr);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddr).toByteArray();

        //convert to dotted decimal notation:
        String ipAddrStr = getDottedDecimalIP(ipByteArray);
        return ipAddrStr;
    }
    public static int getNextFreePort() {
        int localPort = -1;
        try {
            ServerSocket s = new ServerSocket(0);
            localPort = s.getLocalPort();

            //closing the port
            if (s != null && !s.isClosed()) {
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.v("DXDXD", Build.MANUFACTURER + ": free port requested: " + localPort);

        return localPort;
    }


    public static boolean connectionComplete = false;

    public static String getWiFiIPAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ip = getDottedDecimalIP(wm.getConnectionInfo().getIpAddress());
        return ip;
    }



    private final IntentFilter intentFilter = new IntentFilter();
    RecyclerView recyclerView;
    ChatRecylerViewAdapter chatRecylerViewAdapter;
    ArrayList<WifiP2pDevice> wifiP2pDeviceList = new ArrayList<>();
    WifiP2pManager mManager;
    BroadcastReceiver wifiP2PReciever;
    WifiP2pManager.Channel mChannel;

    public void setWifiP2pDeviceList(WifiP2pDeviceList wifiP2pDeviceList ){

        this.wifiP2pDeviceList.clear();
        this.wifiP2pDeviceList.addAll(wifiP2pDeviceList.getDeviceList());
        Log.d(TAG, "setWifiP2pDeviceList: " + this.wifiP2pDeviceList);
        chatRecylerViewAdapter.notifyDataSetChanged();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.chatRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getContext(), getMainLooper(), null);

        wifiP2PReciever = new WifiP2PReciever(mManager, mChannel, this);

        chatRecylerViewAdapter = new ChatRecylerViewAdapter(getActivity(),wifiP2pDeviceList,mManager,mChannel);
        recyclerView.setAdapter(chatRecylerViewAdapter);

        Intent serviceIntent = new Intent(getContext(), PeerDiscoveryService.class);
        getContext().startService(serviceIntent);
        getContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);


        return view;
    }
    PeerDiscoveryService peerDiscoveryService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("run2221", "onServiceConnected: ");
            PeerDiscoveryService.LocalBinder localBinder = (PeerDiscoveryService.LocalBinder) service;
            peerDiscoveryService = localBinder.getService();
            peerDiscoveryService.registerActivity(mManager, mChannel);
            peerDiscoveryService.startDiscovery();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");

        }
    };
    public String TAG = "run2221";

    int port = 8667;
    String groupOwnerAddress;
    String sendToaddr;

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        Log.e("test", "..onConnectionInfoAvailable.." + info);
        try {
            groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
            WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
            Log.d("coninfo: owner address", groupOwnerAddress);

            if (!info.isGroupOwner && info.groupFormed) {
                sendToaddr = groupOwnerAddress;
                String ip = getWiFiIPAddress(getContext());
                Log.d("coninfo: my address", ip);
                int portno = getNextFreePort();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", HANDSHAKE);
                jsonObject.put("ip", ip);
                jsonObject.put("port", portno);
                clientPort = getNextFreePort();
                jsonObject.put("res_port", clientPort);


                sendMessage(jsonObject.toString());
                ServerThread serverThread = new ServerThread(getContext(), clientPort);
                serverThread.start();

            } else if (info.groupFormed) {
                ServerThread serverThread = new ServerThread(getContext(), port);
                serverThread.start();
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("test", "Owner Info null\n" + e.toString());
        }

    }

    public void sendMessage(String message) {

        //Client Peer
        Toast.makeText(getContext(), "client sending message", Toast.LENGTH_SHORT).show();
        if (message == null)
            message = "Unknown";
        AsyncTask<Void, Void, Void> my_task = new SendMessage(sendToaddr, port, message, getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            my_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        else
            my_task.execute((Void[]) null);


    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(wifiP2PReciever, intentFilter);
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(wifiP2PReciever);
    }

    public static int clientPort = 8667;
}
