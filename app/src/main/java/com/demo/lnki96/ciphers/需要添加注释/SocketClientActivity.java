package com.example.eric.cryptosystem;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by lnki9 on 2016/8/25 0025.
 */

public class SocketClientActivity extends Activity {


    private class TcpThread extends Thread {
        String string;
        Message msg;
        Handler handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                synchronized (tcpThread) {//加锁
                    tcpThread.msg = new Message();
                    tcpThread.msg.what = msg.what;
                    string = msg.obj.toString();
                    tcpThread.notify();//解锁
                }
                super.handleMessage(msg);
            }
        };

        @Override
        public void run() {
            PrintWriter writer;
            BufferedReader reader;
            Vector<String> obj = new Vector<>();


            try {
                //创建socket
                String addrStr = addr.getText().toString();
                Integer portVal = Integer.valueOf(port.getText().toString());
                socket = new Socket(addrStr, portVal);//小米的机器在这一步的时候,会返回空对象,导致后面使用socket对象时产生异常
            } catch (IOException e) {
                e.printStackTrace();
            }

            //发送IP和端口号给mHandler,从而修改UI
            msg = new Message();
            msg.what = SOCKET_CONNECT;
            msg.obj = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
            mHandler.sendMessage(msg);

            try {
                //获取输入和输出流
                writer = new PrintWriter(socket.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true) {
                    try {
                        synchronized (this) {
                            wait();
                            writer.write(string + "\n");
                            writer.flush();
                        }

                        if (msg.what == SOCKET_QUIT) {
                            obj.add(socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                            obj.add(string);
                            msg.obj = obj;
                        }
                        //这里可能需要一个线程同步,即这个判断要在主线程将isNeedDHKeyExchange改为false后进行
                        if(isNeedDHKeyExchange){string = string + "," + reader.readLine();}//需要进行dh密钥交换时,这里还要返回B
                        msg.obj = string;
                        mHandler.sendMessage(msg);
                        if (msg.what == SOCKET_QUIT) break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                writer.close();
                reader.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    final int SOCKET_CONNECT = 0x0;
    final int SOCKET_SEND = 0x1;
    final int SOCKET_QUIT = 0x2;

    private View addrPort, processData;
    private EditText addr, port, data;
    private TextView log;
    private Socket socket;

    private final TcpThread tcpThread = new TcpThread();

    private boolean isNeedDHKeyExchange=true;
    //private String totalState="";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SOCKET_CONNECT:
                    Toast.makeText(SocketClientActivity.this, "Connected to " + msg.obj, Toast.LENGTH_SHORT).show();
                    addrPort.setVisibility(View.GONE);
                    processData.setVisibility(View.VISIBLE);
                    data.requestFocus();
                    data.setText("");
                    break;
                case SOCKET_SEND:
                    String stringFromThread = (String) msg.obj;
                    if(isNeedDHKeyExchange){
                        BigInteger p, g, A, B, b, DHkey,a;
                        p = BigInteger.valueOf(Integer.valueOf(stringFromThread.split(",")[0]));
                        g = BigInteger.valueOf(Integer.valueOf(stringFromThread.split(",")[1]));
                        A = BigInteger.valueOf(Integer.valueOf(stringFromThread.split(",")[2]));
                        B = BigInteger.valueOf(Integer.valueOf(stringFromThread.split(",")[3]));

                        a= BigInteger.valueOf(6);  //测试用随机数a=6,之后这里应该使用真正的随机数
                        DHkey=B.modPow(a,p);

                        log.setText(log.getText() + new java.util.Date().toString() + " - " + msg.obj.toString() + "\n"
                                + " - " + "p=" + p.toString() + "\n"
                                + " - " + "g=" + g.toString() + "\n"
                                + " - " + "A=" + A.toString() + "\n"
                                + " - " + "we received B=" + B.toString() + "\n"
                                + " - " + "finally the DHkey is:" + DHkey.toString() + "\n"

                        );
                        //DH密钥交换完毕,取消待交换状态
                        isNeedDHKeyExchange=false;
                        data.setText("");
                    }else {
                        log.setText(log.getText()+"I say:"+stringFromThread+"\n");
                        data.setText("");
                    }
                    break;
                case SOCKET_QUIT:
                    Vector obj = (Vector) msg.obj;
                    Toast.makeText(SocketClientActivity.this, "Disconnected from " + obj.get(0), Toast.LENGTH_SHORT).show();
                    if (obj.get(1).equals(getText(R.string.socket_cmd_shutdown)))
                        Toast.makeText(SocketClientActivity.this, "Shutdown server at " + obj.get(0) + " successfully.", Toast.LENGTH_SHORT).show();
                    processData.setVisibility(View.GONE);
                    addrPort.setVisibility(View.VISIBLE);
                    log.setText("");
                    addr.requestFocus();
                    addr.setText(getString(R.string.socket_addr_default));
                    addr.setSelection(getString(R.string.socket_addr_default).length());
                    port.setText(getString(R.string.socket_port_default));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_client);
        Button connBtn = (Button) findViewById(R.id.socket_connect);
        Button sendBtn = (Button) findViewById(R.id.socket_send);
        addrPort = findViewById(R.id.socket_addr_port);
        processData = findViewById(R.id.socket_process_data);
        addr = (EditText) findViewById(R.id.socket_addr);
        port = (EditText) findViewById(R.id.socket_port);
        data = (EditText) findViewById(R.id.socket_data);
        log = (TextView) findViewById(R.id.socket_log);
        addr.setSelection(getString(R.string.socket_addr_default).length());
        connBtn.setOnClickListener(connect);
        sendBtn.setOnClickListener(send);
    }

    View.OnClickListener connect = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new Thread(tcpThread).start();
        }
    };

    View.OnClickListener send = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String string;
            string = data.getText().toString();
            Message msg = new Message();
            msg.what = (string.equals(getString(R.string.socket_cmd_quit)) || string.equals(getString(R.string.socket_cmd_shutdown))) ? SOCKET_QUIT : SOCKET_SEND;
            msg.obj = string;
            tcpThread.handler.sendMessage(msg);
        }
    };
}
