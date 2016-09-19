
package com.um.networkupgrade;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

/** For system restore */
public class SocketClient {

    // Service Name
    private String socketName = "configserver";

    // The end of the command whether to perform
    private boolean running = false;

    // Socket
    private LocalSocket localSocket = null;

    // Socket address
    private LocalSocketAddress localSocketAddress;

    // Input stream
    private InputStream inputStream;

    // Output stream
    private OutputStream outputStream;

    // Data output stream
    private DataOutputStream dos;

    // Command return value
    private String rec_data = null;

    public SocketClient() {
        running = true;
        connect();

    }

    /**
     * Socket connect
     */
    public void connect() {
        try {
            localSocket = new LocalSocket();
            localSocketAddress = new LocalSocketAddress(socketName,
                    LocalSocketAddress.Namespace.RESERVED);
            localSocket.connect(localSocketAddress);
            inputStream = localSocket.getInputStream();
            outputStream = localSocket.getOutputStream();
            System.out.println(outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * write messages
     *
     * @param msg
     */
    public void writeMsg(String msg) {
        try {
   
                Log.w("STR", msg);
            dos = new DataOutputStream(outputStream);
            int strLen = msg.getBytes().length;
            System.out.println(strLen);
            byte[] sendLen = intToBytes(strLen);
            byte[] allLen = new byte[msg.getBytes().length + 4];

            byte[] srcLen = msg.getBytes();

            for (int i = 0; i < (msg.getBytes().length + 4); i++) {
                if (i < 4) {
                    allLen[i] = sendLen[i];
                    System.out.println(i);
                } else {
                    System.out.println("=" + i);
                    allLen[i] = srcLen[i - 4];
                }
            }
            dos.write(allLen);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * int change to byte
     *
     * @param n int data
     * @return byte data
     */
    private byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        for (int i = 3; i >= 0; i--) {
            b[i] = (byte) (n >> (i * 8));
        }
        return b;
    }

    /**
     * Access to the command execution results
     *
     * @return
     */
    public int readNetResponseSync() {
        int result = -1;
        try {
            InputStream m_Rece = localSocket.getInputStream();
            byte[] data;
            int receiveLen = 0;
            while (running) {
                receiveLen = m_Rece.available();
                data = new byte[receiveLen];
                if (receiveLen != 0) {
                    m_Rece.read(data);
                    rec_data = new String(data);
                        Log.w("TAG", rec_data);
                    // success
                    if (rec_data.contains("execute ok")) {
                        result = 0;
                        running = false;
                    }
                    // fail
                    else if (rec_data.contains("failed execute")) {
                        result = -1;
                        running = false;
                    }
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            m_Rece.close();
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Socket Disconnect
     */
    public void close() {
        try {
            dos.close();
            inputStream.close();
            outputStream.close();
            localSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
