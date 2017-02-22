package com.example.eric.cryptosystem;

import android.os.Environment;

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
public class FileTransportationServer {

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(8888); // 服务器端端口
        boolean flag = true; // 定义标记，可以一直死循环
        while (flag) { // 通过标记判断循环
            new Thread(new ServerThreadUtil(server.accept())).start(); // 启动线程
        }
        server.close(); // 关闭服务器
    }
    static class ServerThreadUtil implements Runnable {
        private final String DIRPATH = Environment.getExternalStorageDirectory()
                .toString() + File.separator + "mldnfile"
                + File.separator; // 目录路径
        private Socket client = null;
        private UploadFile upload = null;
        public ServerThreadUtil(Socket client) {
            this.client = client;
            System.out.println("新的客户端连接...");
        }
        @Override
        public void run() {
            try {
                PrintStream out = new PrintStream(this.client.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(
                        client.getInputStream()); // 反序列化
                this.upload = (UploadFile) ois.readObject(); // 读取对象
                System.out.println("文件标题：" + this.upload.getTitle());
                System.out.println("文件类型：" + this.upload.getMimeType());
                System.out.println("文件大小：" + this.upload.getContentLength());
                out.print(this.saveFile()) ;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    this.client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private boolean saveFile() throws Exception { // 负责文件内容的保存
            File file = new File(DIRPATH + UUID.randomUUID() + "."
                    + this.upload.getExt());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            OutputStream output = null;
            try {
                output = new FileOutputStream(file) ;
                output.write(this.upload.getContentData()) ;
                return true ;
            } catch (Exception e) {
                throw e;
            } finally {
                output.close();
            }
        }
        @SuppressWarnings("serial")
        public class UploadFile implements Serializable {
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
}

