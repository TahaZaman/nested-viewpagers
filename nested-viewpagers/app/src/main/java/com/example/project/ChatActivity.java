package com.example.project;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    public ListView chatList;
    public ArrayList<String> chat;
    String senderIp;
    int myPort;
    int sendPort;
    public ChatListAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        senderIp = getIntent().getStringExtra("sendtoip");
        myPort = getIntent().getIntExtra("myport",-1);
        sendPort = getIntent().getIntExtra("senderport",-1);
        chat = new ArrayList<>();
        chatList = (ListView) findViewById(R.id.chatList);

// get data from the table by the ListAdapter
        customAdapter= new ChatListAdapter(this, R.layout.chat_my_list_item, chat);
        chatList .setAdapter(customAdapter);

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String message = ((EditText) findViewById(R.id.messageSend)).getEditableText().toString();
                    JSONObject jon = new JSONObject();
                    jon.put("type", ServerThread.CHAT);
                    jon.put("message", message);
                    chat.add("my:" + message);
                    customAdapter.notifyDataSetChanged();
                    ((EditText) findViewById(R.id.messageSend)).setText("");
                    AsyncTask<Void, Void, Void> my_task = new SendMessage(senderIp, sendPort, jon.toString(), getApplicationContext());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                        my_task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
                    else
                        my_task.execute((Void[]) null);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });






    }


    @Override
    public void onStart() {
        super.onStart();
        if(serverThread == null)
            serverThread = new ServerThread(this,myPort);
        serverThread.start();
    }

    ServerThread serverThread = null;
    @Override
    public void onStop() {
        super.onStop();

        serverThread.tearDown();
        //serverThread.stop();
    }


}