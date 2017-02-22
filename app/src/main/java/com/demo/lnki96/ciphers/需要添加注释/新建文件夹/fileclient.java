package com.example.eric.cryptosystem;

/**
 * Created by eric on 16/9/19.
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
public class fileclient extends Activity{

    public String DHkeyInFileClient;
    private Button send = null;
    private TextView info = null;
    private EditText et;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.file_transport_client);
        et= (EditText) findViewById(R.id.etFileServerIP);
        this.send = (Button) super.findViewById(R.id.send);
        this.info = (TextView) super.findViewById(R.id.info);
        this.send.setOnClickListener(new SendOnClickListener());

        DHkeyInFileClient=getIntent().getStringExtra("DHkey");

    }
    private class SendOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    int length = 0;
                    double sumL = 0;
                    byte[] sendBytes = null;
                    Socket socket = null;
                    DataOutputStream dos = null;
                    FileInputStream fis = null;
                    CryptWithDES_H CryptWithDES_H = new CryptWithDES_H(DHkeyInFileClient);
                    String filePath = Environment.getExternalStorageDirectory()
                            .toString()+ File.separator;// 目录路径
                    boolean bool = false;
                    try {
//                        File file = new File(filePath+"ynos.txt"); //要传输的文件路
                        File file = new File(filePath+"ynos.txt"); //要传输的文件路径

                        socket = GlobalClientSocket.getSocket2Instance();
                        dos = new DataOutputStream(socket.getOutputStream());
                        fis = new FileInputStream(file);
                        byte[] encrypted=null;
                        sendBytes = new byte[1024];


                        while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                            int i;
//                            for(i=0;sendBytes[i]!=0;i++){
//                                ;
//                            }
//                            for(int j=0;j<5;j++){
//                                sendBytes[i+j]=20;
//                            }
                            encrypted=CryptWithDES_H.DesEncrypt(sendBytes,1);
                            dos.write(encrypted, 0, length);
                            dos.flush();
                        }
                        byte[]decrypted=CryptWithDES_H.DesEncrypt(encrypted,0);
                        //System.out.println(new String(decrypted));
                        //虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较

                    } catch (Exception e) {
                        System.out.println("客户端文件传输异常");
                        bool = false;
                        e.printStackTrace();
                    } finally {


                    }
                    System.out.println("传输完毕!");
                    return null;
                }}.execute(et.getText().toString());


        }


            private UploadFile getUploadFile() throws Exception { // 包装了传送数据
            UploadFile myFile = new UploadFile();
            myFile.setTitle("yons"); // 设置标题
//            myFile.setMimeType("image/jpeg"); // 图片的类
                myFile.setMimeType("txt"); // 图片的类型

                File file = new File(Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "ynos.txt");
            InputStream input = null;
            try {
                input = new FileInputStream(file); // 从文件中读取
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte data[] = new byte[1];
                int len = 0;
                while ((len = input.read(data)) != -1) {
                    bos.write(data, 0, len);
                }
                byte[] encryptedData=bos.toByteArray();
                //在这里可以进行加密了

                myFile.setContentData(encryptedData);
                myFile.setContentLength(file.length());
                myFile.setExt("txt");
            } catch (Exception e) {
                throw e;
            } finally {
                //input.close();
            }
            return myFile;
        }
    }

    @SuppressWarnings("serial")
    public static class UploadFile implements Serializable {
        private String title ;
        private byte [] contentData ;
        private String mimeType ;
        private long contentLength ;
        private String ext ;
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public byte[] getContentData() {
            return contentData;
        }
        public void setContentData(byte[] contentData) {
            this.contentData = contentData;
        }
        public String getMimeType() {
            return mimeType;
        }
        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
        public long getContentLength() {
            return contentLength;
        }
        public void setContentLength(long contentLength) {
            this.contentLength = contentLength;
        }
        public String getExt() {
            return ext;
        }
        public void setExt(String ext) {
            this.ext = ext;
        }
    }
}
