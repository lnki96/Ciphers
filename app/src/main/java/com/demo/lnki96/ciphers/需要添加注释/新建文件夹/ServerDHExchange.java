package com.example.eric.cryptosystem;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by eric on 16/9/22.
 */
public class ServerDHExchange extends Activity implements View.OnClickListener{
    private TextView dhServerTextView;

    public StartSocketAndDHExchange startSocketAndDHExchange;

    public String DHkey=null;
    Intent intentMessageMode;
    Intent intentFileMode;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.server_dh_exchange);

        intentMessageMode=new Intent(this,SocketServerActivity.class);

        dhServerTextView= (TextView) findViewById(R.id.tvDHProcedure);


        findViewById(R.id.btnStartServerDH).setOnClickListener(this);
        findViewById(R.id.btnDHStartFileMode).setOnClickListener(this);
        findViewById(R.id.btnDHStartMessageMode).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnStartServerDH:
                if (startSocketAndDHExchange == null) {
                    //启动接受线程
                    startSocketAndDHExchange = new StartSocketAndDHExchange();
                    startSocketAndDHExchange.execute();
                }
                break;
            case R.id.btnDHStartMessageMode:
                startActivity(intentMessageMode);
                break;
            case R.id.btnDHStartFileMode:

                break;
        }
    }

    public class StartSocketAndDHExchange extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                boolean needForDHKeyExchange=true;

                //DH密钥交换的过程由服务端的监听线程实现
                BigInteger p, g, A, B, b, DHkey;

                String string = "";
                String state = "";
                state += "Listen Server is launching...\n";
                publishProgress(state,"");
                //测试
                //获取全局的ServerSocket实例
                ServerSocket serverSocketForReceive = GlobalServerSocket.getGlobalServerSocketInstense();
//                ServerSocket serverSocketForReceive = new ServerSocket(defaultPortForReading);
                state += "Listen Socket: " + InetAddress.getLocalHost() + ":" + serverSocketForReceive.getLocalPort() + "\n";
                publishProgress(state,"");

                //当监听状态开启时
                //while (true) {
                    if (this.isCancelled()) {
                        return null;
                    }
                    state += "Message Listen Server is waiting...\n";
                    publishProgress(state,"");

                    //等待客户端的连接
                    Socket socketForReceive = GlobalServerSocket.getGlobalSocketInstense();
                    state += "Listen Client accepted from IP: " + socketForReceive.getInetAddress().toString().substring(1) + "\n";
                    publishProgress(state,"");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(GlobalServerSocket.getInputStreamInstance()));
                    OutputStream writer = socketForReceive.getOutputStream();
                    //while (true) {
                        if (this.isCancelled()) {
                            return null;
                        }

                            //等待读取,此处为阻断的
                            string = reader.readLine();

                            //主动断开连接的情况
                            if (string.equals("-quit") || string.equals("-shutdown")) {
                                state += "Listen Client disconnected from IP: " + socketForReceive.getInetAddress().getHostAddress() + "\n";
                                publishProgress(state,"");
                                writer.close();
                                reader.close();
                                socketForReceive.close();
                                //break;
                            }

                            //进行DH密钥交换
                            if(needForDHKeyExchange) {
                                //向客户端返回B,这里先用一个自定义的p,g,A字符串来模拟客户端的输入,之后这里应该被替换为真正接受到的数据
                                // String pesiDataString = "23,5,8";//p=23,g=5,A=8

                                p = BigInteger.valueOf(Integer.valueOf(string.split(",")[0]));
                                g = BigInteger.valueOf(Integer.valueOf(string.split(",")[1]));
                                A = BigInteger.valueOf(Integer.valueOf(string.split(",")[2]));

//                                b = BigInteger.valueOf(15);  //测试用随机数B=15,之后这里应该使用真正的随机数
//                                B = g.modPow(b, p);
//                                DHkey = A.modPow(b, p);

                                b = DHKeyExchange.getRandomab();  //测试用随机数B=15,之后这里应该使用真正的随机数
                                B = DHKeyExchange.getAB(g,b,p);
                                DHkey = DHKeyExchange.getkey(A,b,p);

                                state += new java.util.Date()+"\n"+":"
                                        + " - " + "p=" + p.toString() + "\n"
                                        + " - " + "g=" + g.toString() + "\n"
                                        + " - " + "A=" + A.toString() + "\n"
                                        + " - " + "we will give them B=" + B.toString() + "\n"
                                        + " - " + "finally the DHkey is:" + DHkey.toString() + "\n";
                                publishProgress(state,DHkey.toString());


                                //返回B给Client
                                writer.write((new StringBuffer(B.toString()).toString() + "\n").getBytes());
                                //DH密钥交换完毕,needForDHKeyExchange置为false
                                needForDHKeyExchange=false;
                            }else {
                                //不需要进行dh密钥交换时,只是单纯的把收到的信息显示在屏幕上
                                state+="we received:"+string+"\n";
                                publishProgress(state,"");
                            }

                    //}
//                    if (string.equals("-shutdown")) {
//                        state += "Listen Server is shutting down...\n";
//                        publishProgress(state,"");
//                        serverSocketForReceive.close();
//                        //break;
//                    }
                //}
                state += "Listen Server terminated.\n";
                publishProgress(state,"");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String totalState = values[0];
            dhServerTextView.setText(totalState);
            if(values[1]!=""){
                DHkey=values[1];
            }
            intentMessageMode.putExtra("DHkeyInActivity",DHkey);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dhServerTextView.setText("The Server is stopped.");
        }
    }
}
