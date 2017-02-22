
package com.example.eric.cryptosystem;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by eric on 16/9/21.
 */
public class fileserver extends Activity {

    public static String DHkeyInFileServer;
    private TextView tv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.file_server);

        DHkeyInFileServer=getIntent().getStringExtra("DHkeyInActivity");

        tv= (TextView) findViewById(R.id.fileServerTextView);
        new Thread(new ServerThreadUtil(GlobalServerSocket.getSocket2())).start(); // 启动线程
    }


    static class ServerThreadUtil implements Runnable {
        //构造方法,获取socket
        public ServerThreadUtil(Socket client) {
            this.client = client;
            System.out.println("新的客户端连接...");
        }
        private Socket client = null;
        private fileclient.UploadFile upload = null;
        private final String DIRPATH = Environment.getExternalStorageDirectory()
                .toString()
                + File.separator; // 目录路径

        @Override
        public void run() {
            byte[] inputByte = null;
            int length = 0;
            DataInputStream dis = null;
            FileOutputStream fos = null;
            FileOutputStream fos2 = null;
            CryptWithDES_H CryptWithDES_H = new CryptWithDES_H(DHkeyInFileServer);
            String filePath = Environment.getExternalStorageDirectory()
                    .toString()+ File.separator+"aaaaaaaaa"+ File.separator;// 目录路径
            try {
                try {
                    dis = new DataInputStream(client.getInputStream());
                    File f = new File(filePath);
                    if(!f.exists()){
                        f.mkdir();
                    }
				/*
				 * 文件存储位置
				 */
                    fos = new FileOutputStream(new File(filePath+"deciphered.txt"));
                    fos2=new FileOutputStream(new File(filePath+"ciphered.txt"));
                    inputByte = new byte[1024];
                    byte[] decrypted=null;
                    System.out.println("开始接收数据...");
                    while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {

                        decrypted=CryptWithDES_H.DesEncrypt(inputByte,0);

                        fos.write(decrypted, 0, length);
                        fos.flush();
                        fos2.write(inputByte, 0, length);
                        fos2.flush();
                    }
//                    byte[] encrypted=CryptWithDES_H.DesEncrypt(decrypted,0);

                    System.out.println("完成接收："+filePath);
                } finally {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}

