package com.um.videoplayer.model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

public class SocketClient {
    static final String LOG_TAG = "SocketClient";
    static final String SOCKET_NAME = "configserver";
    static final int SOCKET_OPEN_RETRY_MILLIS = 4 * 1000;
    static final int EVENT_SEND = 1;
    LocalSocket s = null;
    LocalSocketAddress l;
    InputStream is;
    OutputStream os;
    DataOutputStream dos;
    String rec_data = null;

    int result = 0;

    public SocketClient(Context comActivity) {
        connect();
        new Thread(local_receive).start();
    }

    public void connect() {
        try {
            s = new LocalSocket();
            l = new LocalSocketAddress(SOCKET_NAME,
                                       LocalSocketAddress.Namespace.RESERVED);
            s.connect(l);
            is = s.getInputStream();
            os = s.getOutputStream();
            //System.out.println(os);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMess(String s) {
        try {
            dos = new DataOutputStream(os);
            int strLen = s.getBytes().length;
            //System.out.println(strLen);
            byte[] sendLen = intToBytes2(strLen);
            byte[] allLen = new byte[s.getBytes().length + 4];
            byte[] srcLen = s.getBytes();

            for (int i = 0; i < (s.getBytes().length + 4); i++) {
                if (i < 4) {
                    allLen[i] = sendLen[i];
                    //System.out.println(i);
                }
                else {
                    allLen[i] = srcLen[i - 4];
                }
            }

            dos.write(allLen);
            dos.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] intToBytes2(int n) {
        byte[] b = new byte[4];

        for (int i = 3; i >= 0; i--) {
            b[i] = (byte)(n >> (i * 8));
        }

        return b;
    }

    private boolean _runFlag = true;
    private Object lck = new Object();

    public void setRunFlag(boolean vflag) {
        synchronized (lck) {
            this._runFlag = vflag;
        }
    }

    public boolean getRunFlag() {
        boolean r = false;

        synchronized (lck) {
            r = this._runFlag;
        }

        return r;
    }

    private boolean running = true;
    Thread local_receive = new Thread() {
        public void run() {
            try {
                byte[] data;
                int receiveLen = 0;

                while (getRunFlag() && running) {
                    receiveLen = is.available();
                    data = new byte[receiveLen];

                    if (receiveLen != 0) {
                        is.read(data);
                        rec_data = new String(data);
                        Log.w("TAG", rec_data);

                        if (rec_data.contains("execute ok")) {
                            running = false;
                        }
                        else if (rec_data.contains("failed execute")) {
                            running = false;
                        }
                    }

                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void close() {
        try {
            dos.close();
            is.close();
            os.close();
            s.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getData() {
        return result;
    }
}
