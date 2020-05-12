package com.androidzeitgeist.ani.afl;

import java.net.DatagramSocket;

import javax.sql.DataSource;

public class AflSocketManager {

    private static AflSocketManager sInstance;

    private DatagramSocket mDatagraSocket;

    private AflSocketManager(){}

    public static AflSocketManager getInstance(){
        synchronized (AflSocketManager.class){
            if (sInstance == null){
                synchronized (AflSocketManager.class){
                    if (sInstance == null){
                        sInstance = new AflSocketManager();
                    }
                }
            }
        }
        return sInstance;
    }

    public void setSocket(DatagramSocket datagraSocket){
        this.mDatagraSocket = datagraSocket;
    }

    public DatagramSocket getSocket(){
        return this.mDatagraSocket;
    }

}
