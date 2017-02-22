package com.example.eric.cryptosystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by lnki9 on 2016/8/25 0025.
 */
public class SocketServerActivity extends Activity implements View.OnClickListener {

    private String DHkeyInActivity;
    public Intent intentToStartServerFileMode;

    private TextView textView;
    private EditText dataField;

    private ServerAsyncForReceive serverAsyncForReceive = null;


    public boolean isReadyToSend = false;
    public boolean sendData = false;//由onClick触发,从而发送消息
    public boolean isListening = false;
    private boolean mode = true;//默认使用消息传输模式(true)

    public String dataToSend = null;//要发送给client的数据
    public String totalState = null;//记录整体的显示信息

    public WifiManager wifiManager = null;
    public WifiInfo currentWifiInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_server);

        DHkeyInActivity = getIntent().getStringExtra("DHkeyInActivity");
        intentToStartServerFileMode = new Intent(this, fileserver.class);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        currentWifiInfo = wifiManager.getConnectionInfo();

        textView = (TextView) findViewById(R.id.textViewShowState);
        textView.setMovementMethod(new ScrollingMovementMethod());
        dataField = (EditText) findViewById(R.id.editTextDHExchangeData);

        findViewById(R.id.btnStartServer).setOnClickListener(this);
        findViewById(R.id.btnStopServer).setOnClickListener(this);
        findViewById(R.id.btnDHExhange).setOnClickListener(this);
        findViewById(R.id.btnChooseMessageTransport).setOnClickListener(this);
        findViewById(R.id.btnChooseFileTransport).setOnClickListener(this);


        totalState = "IP of this device:" + intToIp(currentWifiInfo.getIpAddress()) + "\n";
        textView.setText(totalState);
        //启动接受线程,直接写在oncreate里面!
        if (serverAsyncForReceive == null) {
            //启动接受线程
            serverAsyncForReceive = new ServerAsyncForReceive();
            serverAsyncForReceive.execute(DHkeyInActivity);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStartServer:
                if (serverAsyncForReceive == null) {
                    //启动接受线程
//                    serverAsyncForReceive = new ServerAsyncForReceive();
//                    serverAsyncForReceive.execute(DHkeyInActivity);

                    new ServerAsyncForReceive().execute(DHkeyInActivity);
                }
                break;
            case R.id.btnStopServer:
                if (serverAsyncForReceive != null) {

                    isListening = false;//停止监听

                    serverAsyncForReceive.cancel(true);
                    //清空线程对象
                    serverAsyncForReceive = null;
                }
                break;
            case R.id.btnDHExhange:
                sendData = true;//允许发送消息
                dataToSend = dataField.getText().toString();//将edittext中的内容给传值变量
                break;
            case R.id.btnChooseMessageTransport:
                mode = true;
                break;
            case R.id.btnChooseFileTransport:
                mode = false;
                intentToStartServerFileMode.putExtra("DHkeyInActivity", DHkeyInActivity);
                if (serverAsyncForReceive != null) {

                    isListening = false;//停止监听

                    serverAsyncForReceive.cancel(true);
                    //清空线程对象
                    serverAsyncForReceive = null;
                }
                startActivity(intentToStartServerFileMode);

                break;

        }
    }


    public class ServerAsyncForReceive extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                final int defaultPortForReading = 8989;
                //不需要进行dh密钥交换
                boolean needForDHKeyExchange = false;

                //DH密钥交换的过程由服务端的监听线程实现
                BigInteger p, g, A, B, b, DHkey;


                String string = "";
                String state = "";
                String DHKeyInThread = strings[0];
                System.out.println("we have got the dhkey:" + strings[0]);
                state += "Listen Server is launching...\n";
                publishProgress(state);
                //测试
                //获取全局的ServerSocket实例
                ServerSocket serverSocketForReceive = GlobalServerSocket.getGlobalServerSocketInstense();
//                ServerSocket serverSocketForReceive = new ServerSocket(defaultPortForReading);
                state += "Listen Socket: " + InetAddress.getLocalHost() + ":" + serverSocketForReceive.getLocalPort() + "\n";
                publishProgress(state);
                isListening = true;

                //当监听状态开启时
                while (isListening) {
                    if (this.isCancelled()) {
                        return null;
                    }
                    state += "Message Listen Server is waiting...\n";
                    publishProgress(state);

                    //会停在这里,等待再次连接
                    Socket socketForReceive = GlobalServerSocket.getGlobalSocketInstense();
//                    Socket socketForReceive = serverSocketForReceive.accept();
                    state += "Listen Client accepted from IP: " + socketForReceive.getInetAddress().toString().substring(1) + "\n";
                    publishProgress(state);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(GlobalServerSocket.getInputStreamInstance()));
                    OutputStream writer = socketForReceive.getOutputStream();
                    while (true) {
                        if (this.isCancelled()) {
                            return null;
                        }
                        //消息传输模式
                        if (mode) {
                            //等待读取,此处为阻断的
                            //byte[] a=reader.readLine();
                            string = reader.readLine();
                            if (string == null)
                                break;

                            //主动断开连接的情况
                            if (string.equals("-quit") || string.equals("-shutdown")) {
                                state += "Listen Client disconnected from IP: " + socketForReceive.getInetAddress().getHostAddress() + "\n";
                                publishProgress(state);
                                writer.close();
                                reader.close();
                                socketForReceive.close();
                                break;
                            }

                            //进行DH密钥交换
                            if (needForDHKeyExchange) {
                                //向客户端返回B,这里先用一个自定义的p,g,A字符串来模拟客户端的输入,之后这里应该被替换为真正接受到的数据
                                // String pesiDataString = "23,5,8";//p=23,g=5,A=8

                                p = BigInteger.valueOf(Integer.valueOf(string.split(",")[0]));
                                g = BigInteger.valueOf(Integer.valueOf(string.split(",")[1]));
                                A = BigInteger.valueOf(Integer.valueOf(string.split(",")[2]));

                                b = BigInteger.valueOf(15);  //测试用随机数B=15,之后这里应该使用真正的随机数
                                B = g.modPow(b, p);
                                DHkey = A.modPow(b, p);

                                state += new java.util.Date() + "\n" + ":"
                                        + " - " + "p=" + p.toString() + "\n"
                                        + " - " + "g=" + g.toString() + "\n"
                                        + " - " + "A=" + A.toString() + "\n"
                                        + " - " + "we will give them B=" + B.toString() + "\n"
                                        + " - " + "finally the DHkeyInActivity is:" + DHkey.toString() + "\n";
                                publishProgress(state);


                                //返回B给Client
                                writer.write((new StringBuffer(B.toString()).toString() + "\n").getBytes());
                                //DH密钥交换完毕,needForDHKeyExchange置为false
                                needForDHKeyExchange = false;
                            } else {
                                //不需要进行dh密钥交换时,只是单纯的把收到的信息显示在屏幕上

                                //des对收到的信息进行解密
                                string = string.split("\n")[0];
                                CryptWithDES_H CryptWithDES_H = new CryptWithDES_H(DHkeyInActivity);
                                byte[] cipherText = string.getBytes("ISO-8859-1");
                                byte[] plainText = CryptWithDES_H.DesEncrypt(cipherText, 0);
                                state += "we received:" + string + "(the plaintext is:" + new String(plainText, "ISO-8859-1") + ")" + "\n";
                                publishProgress(state);
                            }
                        }


                    }
                    if (string.equals("-shutdown")) {
                        state += "Listen Server is shutting down...\n";
                        publishProgress(state);
                        serverSocketForReceive.close();
                        break;
                    }
                }
                state += "Listen Server terminated.\n";
                publishProgress(state);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            totalState = values[0];
            textView.setText(totalState);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            textView.setText("The Server is stopped.");
        }
    }


    public static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + ((i >> 24) & 0xFF);
    }

}
