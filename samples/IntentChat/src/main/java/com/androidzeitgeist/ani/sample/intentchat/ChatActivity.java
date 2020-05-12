package com.androidzeitgeist.ani.sample.intentchat;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.androidzeitgeist.ani.afl.ByteUtil;
import com.androidzeitgeist.ani.discovery.Discovery;
import com.androidzeitgeist.ani.discovery.DiscoveryException;
import com.androidzeitgeist.ani.discovery.DiscoveryListener;
import com.androidzeitgeist.ani.internal.AndroidNetworkIntents;
import com.androidzeitgeist.ani.transmitter.Transmitter;
import com.androidzeitgeist.ani.transmitter.TransmitterException;

import static com.androidzeitgeist.ani.discovery.Discovery.SOCKET_GENRE_NORMAL;

public class ChatActivity extends Activity implements DiscoveryListener, OnEditorActionListener, OnClickListener {
    private static final String EXTRA_MESSAGE = "upd_message";

    private TextView chatView;
    private EditText inputView;
    private ImageButton sendButton;

    private Discovery discovery;
    private Transmitter transmitter;

    private boolean discoveryStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        //接收
        discovery = new Discovery(AndroidNetworkIntents.DISCOVERY_ADDRESS, AndroidNetworkIntents.DISCOVERY_PORT, SOCKET_GENRE_NORMAL);

        //接收监听
        discovery.setDisoveryListener(this);

        //发送
        transmitter = new Transmitter(AndroidNetworkIntents.TRANSMITTER_ADDRESS, AndroidNetworkIntents.TRANSMITTER_PORT);

        chatView = (TextView) findViewById(R.id.chat);

        inputView = (EditText) findViewById(R.id.input);
        inputView.setOnEditorActionListener(this);

        sendButton = (ImageButton) findViewById(R.id.send);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            //开始接收
            discovery.enable();

            discoveryStarted = true;
        } catch (DiscoveryException exception) {
            appendChatMessage("* (!) Could not start discovery: " + exception.getMessage());
            discoveryStarted = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (discoveryStarted) {
            discovery.disable();
        }
    }

    private void appendChatMessage(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                chatView.append(message + "\n");
            }
        });
    }

    private void appendChatMessageFromSender(String sender, String message) {
        appendChatMessage("<" + sender + "> " + message);
    }

    /**
     * 接收对象异常
     *
     * @param exception Actual exception that occured in the background thread
     */
    @Override
    public void onDiscoveryError(Exception exception) {
        appendChatMessage("* (!) Discovery error: " + exception.getMessage());
    }

    /**
     * 接收对象开始
     */
    @Override
    public void onDiscoveryStarted() {
        appendChatMessage("* (>) Discovery started");
    }

    /**
     * 接收对象停止
     */
    @Override
    public void onDiscoveryStopped() {
        appendChatMessage("* (<) Discovery stopped");
    }

    /**
     * 接收到消息
     *
     * @param address The IP address of the sender of the {@link Intent}.
     * @param intent  The received {@link Intent}.
     */
    @Override
    public void onIntentDiscovered(InetAddress address, Intent intent, byte[] dataIntent) {

        String message = null;
        try {
            message = new String(dataIntent, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String sender = address.getHostName();

        appendChatMessageFromSender(sender, message);
    }

    @Override
    public void onClick(View v) {
        sendChatMessage();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendChatMessage();
            return true;
        }

        return false;
    }

    public void sendChatMessage() {
        String message = inputView.getText().toString();

        if (message.length() == 0) {
            return; // No message to send
        }

        inputView.setText("");

        byte[] intent = new byte[0];
        try {
            intent = message.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        transmitIntentOnBackgroundThread(intent);
    }

    /**
     * 异步发送
     *
     * @param intent
     */
    private void transmitIntentOnBackgroundThread(final byte[] intent) {
        new Thread() {
            public void run() {
                transmitIntent(intent);
            }
        }.start();
    }

    /**
     * 同步发送
     *
     * @param intent
     */
    private void transmitIntent(final byte[] intent) {
        try {
            transmitter.transmit(intent);
        } catch (TransmitterException exception) {
            appendChatMessage("Could not transmit intent: " + exception.getMessage());
        }
    }
}
