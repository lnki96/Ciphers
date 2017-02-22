package com.demo.lnki96.ciphers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by eric on 16/9/22.
 */
public class GlobalServerSocket {
    static final int defaultPortForReading = 8989;
    private static ServerSocket serverSocket;
    private static ServerSocket serverSocket2;
    private static Socket socket;
    private static Socket socket2;
    private static InputStream inputStream;
    private static OutputStream outputStream;


    public static ServerSocket getGlobalServerSocketInstance() {
        if(serverSocket==null){
            try {
                serverSocket=new ServerSocket(defaultPortForReading);
                serverSocket2=new ServerSocket(defaultPortForReading+1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return serverSocket;
    }
    public static Socket getGlobalSocketInstance(){
        if(socket==null){
            try {
                socket=serverSocket.accept();
                socket2=serverSocket2.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return socket;
    }

    public static Socket getSocket2() {
        return socket2;
    }

    public static InputStream getInputStreamInstance(){
        if(inputStream==null){
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return inputStream;
    }
    public static OutputStream getOutputStreamInstance(){
        if(outputStream==null){
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream;
    }

    public static int getDefaultPortForReading() {
        return defaultPortForReading;
    }
}
