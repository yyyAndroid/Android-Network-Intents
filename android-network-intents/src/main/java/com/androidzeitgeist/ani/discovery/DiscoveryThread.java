/*
 * Copyright (C) 2013 Sebastian Kaspari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidzeitgeist.ani.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import android.content.Intent;
import android.util.Log;

import com.androidzeitgeist.ani.afl.AflSocketManager;

import static com.androidzeitgeist.ani.discovery.Discovery.SOCKET_GENRE_MULTICAST;
import static com.androidzeitgeist.ani.discovery.Discovery.SOCKET_GENRE_NORMAL;

/**
 * Internal class for handling the network connection of the {@link Discovery} class
 * on a background thread.
 */
class DiscoveryThread extends Thread {
    private static final String TAG = "ANI/DiscoveryThread";
    private static final int MAXIMUM_PACKET_BYTES = 1024 * 1024;

    public static int mSocketGenre = SOCKET_GENRE_MULTICAST;

    private String multicastAddress;
    private int port;
    private DatagramSocket socket;
    private DiscoveryListener listener;

    private volatile boolean running;

    /**
     * Create a new background thread that handles incoming Intents on the given
     * multicast address and port.
     * <p>
     * Do not instantiate this class yourself. Use the {@link Discovery} class
     * instead.
     *
     * @param multicastAddress
     * @param port
     * @param listener
     */
    /* package-private */ DiscoveryThread(int type, String multicastAddress, int port, DiscoveryListener listener) {
        this.mSocketGenre = type;
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.listener = listener;
    }

    public void run() {
        running = true;

        listener.onDiscoveryStarted();

        try {
            socket = createSocket();
            receiveIntents();
        } catch (IOException exception) {
            if (running) {
                listener.onDiscoveryError(exception);
            }
        } finally {
            closeSocket();
        }

        listener.onDiscoveryStopped();
    }

    protected DatagramSocket createSocket() throws UnknownHostException, IOException {
        InetAddress address = InetAddress.getByName(multicastAddress);
        if (AflSocketManager.getInstance().getSocket() == null){
            switch (mSocketGenre){
                case SOCKET_GENRE_NORMAL:
                    socket = new DatagramSocket(port);
                    break;
                case SOCKET_GENRE_MULTICAST:
                    socket = new MulticastSocket(port);
                    MulticastSocket multicastSocket = (MulticastSocket) socket;
                    multicastSocket.joinGroup(address);

            }
            AflSocketManager.getInstance().setSocket(socket);
        }

        return socket;
    }

    private void closeSocket() {
        if (socket != null) {
            AflSocketManager.getInstance().setSocket(null);
            socket.close();
        }
    }

    public void stopDiscovery() {
        running = false;

        closeSocket();
    }

    protected void receiveIntents() throws IOException {
        while (running) {
            DatagramPacket packet = new DatagramPacket(
                    new byte[MAXIMUM_PACKET_BYTES], MAXIMUM_PACKET_BYTES
            );

            try {
                socket.receive(packet);

                byte[] data = packet.getData();
                int length = packet.getLength();

                byte[] result = new byte[length];

                System.arraycopy(data, 0, result, 0, length);

                listener.onIntentDiscovered(packet.getAddress(), null, result);
            } catch (Exception exception) {
                Log.v(TAG, "Received UDP packet that could not be parsed as Intent");
            }
        }
    }
}
