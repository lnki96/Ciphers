package com.example.eric.cryptosystem;

/**
 * Created by eric on 16/9/19.
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
public class FileTransportationClient extends Activity{
    private Button send = null;
    private TextView info = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.file_transport_client);
        this.send = (Button) super.findViewById(R.id.send);
        this.info = (TextView) super.findViewById(R.id.info);
        this.send.setOnClickListener(new SendOnClickListener());
    }
    private class SendOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            try {
                final Socket client = new Socket("192.168.1.104", 8899);
                BufferedReader buf = new BufferedReader(new InputStreamReader(
                        client.getInputStream())); // 读取返回的数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(
                                    client.getOutputStream());
                            UploadFile myFile = SendOnClickListener.this.getUploadFile();
                            oos.writeObject(myFile);//写文件对象
                            oos.close();
                        } catch (Exception e) {
                        }
                    }
                }).start();
                String result = buf.readLine(); // 接收返回信息
                System.out.println("**************** " + result);
                if ("true".equals(result)) {
                    FileTransportationClient.this.info.setText("操作成功！");
                } else {
                    FileTransportationClient.this.info.setText("操作失败！");
                }
                buf.close();
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }   private UploadFile getUploadFile() throws Exception { // 包装了传送数据
            UploadFile myFile = new UploadFile();
            myFile.setTitle("DISNEY公园"); // 设置标题
            myFile.setMimeType("image/jpeg"); // 图片的类型
            File file = new File(Environment.getExternalStorageDirectory()
                    .toString() + File.separator + "disney.jpg");
            InputStream input = null;
            try {
                input = new FileInputStream(file); // 从文件中读取
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte data[] = new byte[1024];
                int len = 0;
                while ((len = input.read(data)) != -1) {
                    bos.write(data, 0, len);
                }
                myFile.setContentData(bos.toByteArray());

                //在这里可以进行加密了
                myFile.setContentLength(file.length());
                myFile.setExt("jpg");
            } catch (Exception e) {
                throw e;
            } finally {
                input.close();
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
