package com.example.project;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taha on 6/22/2017.
 */

public class ChatRecylerViewAdapter extends
        RecyclerView.Adapter<ChatRecylerViewAdapter.ViewHolder> {
    private List<WifiP2pDevice> wifiP2pDeviceList;
    private Context mContext;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;



    public ChatRecylerViewAdapter(Context context, List<WifiP2pDevice> wifiP2pDeviceList, WifiP2pManager mManager, WifiP2pManager.Channel mChannel) {
        this.mContext = context;
        this.wifiP2pDeviceList = wifiP2pDeviceList;
        this.mChannel = mChannel;
        this.mManager = mManager;
    }


    @Override
    public ChatRecylerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View vitalView = inflater.inflate(R.layout.chat_recycler_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(vitalView);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ChatRecylerViewAdapter.ViewHolder viewHolder, int position) {

        final WifiP2pDevice device = wifiP2pDeviceList.get(position);
        String deviceName = device.deviceName;
        viewHolder.deviceName_tv.setText(deviceName);

        viewHolder.chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //WifiP2pDevice device;
                WifiP2pConfig config = new WifiP2pConfig();
                if (!device.deviceAddress.isEmpty() && !device.deviceAddress.equals(null)) {
                    config.deviceAddress = device.deviceAddress;
                    config.wps.setup = WpsInfo.PBC;
                    config.groupOwnerIntent = 4;
                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            //success logic
                            Log.d("ConnectPeers", "Success: connected");
                            Toast.makeText(mContext, "Success: connected", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reason) {
                            //failure logic
                            Log.d("ConnectPeers", "onFailure: failed");
                            Toast.makeText(mContext, "DiscoverPeeers Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        });



    }

    @Override
    public int getItemCount() {
        return wifiP2pDeviceList.size();
    }


    private Context getContext() {
        return mContext;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView deviceName_tv;
        public LinearLayout chatLayout;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            deviceName_tv = (TextView) itemView.findViewById(R.id.peerName_tv);
            chatLayout = (LinearLayout) itemView.findViewById(R.id.chatLayout);
        }
    }
}
