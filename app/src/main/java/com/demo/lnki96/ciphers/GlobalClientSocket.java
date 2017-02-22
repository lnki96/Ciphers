package com.demo.lnki96.ciphers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by eric on 16/9/22.
 */
public class GlobalClientSocket {
    private static Socket socket;
    private static Socket socket2;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private static String IPAddress;
    private static Integer Port;
    public static Socket initClientSocket(String IPAddr,Integer port){

            try {
                socket=new Socket(IPAddr,port);
                socket2=new Socket(IPAddr,port+1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        IPAddress=IPAddr;
        Port=port;

        return socket;
    }
    public static Socket getSocketInstance(){
        if(socket!=null){
            return socket;
        }
        return null;
    }

    public static Socket getSocket2Instance() {
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

    public static String getIPAddress() {
        return IPAddress;
    }

    public static Integer getPort() {
        return Port;
    }
}

/*

private myWifiManager(){

    }
    private static WifiManager wifiManager;
    public static WifiManager getWifiManagerInstance(Context context){
        if(wifiManager==null){
            wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        }
        return wifiManager;
        //this is a comment
    }
 */
