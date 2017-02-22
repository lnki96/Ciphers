
package com.example.eric.cryptosystem;
import java.util.Scanner;


public class Keyword {
    public static void main(String args[]) throws Exception {
        System.out.println("［A 加密］［J 解密］,Please Choose One");
        Scanner c = new Scanner(System.in);// 创建Scanner对象
        String s1 = c.nextLine();// 获取本行的字符串
        if (s1.equalsIgnoreCase("A")) {// 判断变量s1与A是否相等，忽略大小
            System.out.println("请输入明文：");
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            System.out.println("请输入密钥：");
            Scanner sc1 = new Scanner(System.in);
            String key = sc1.nextLine();
            Encryption(s, key);// 调用Encryption方法
        } else if (s1.equalsIgnoreCase("J")) {
            System.out.println("请输入密文：");
            Scanner sc = new Scanner(System.in);
            String s = sc.nextLine();
            System.out.println("请输入密钥：");
            Scanner sc1 = new Scanner(System.in);
            String key = sc1.nextLine();
            Decrypt(s, key);// 调用Encryption方法
        }
    }
    private static int length(Scanner sc1) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 生成Keyword密文  <br>
     *
     * @param str
     *            明文

     * @param2 key
     *            密钥key
     *
     * @return 返回密文
     */
    public static String Encryption(String str, String key) {// 加密
        String string = "";
        String a ="abcdefghijklmnopqrstuvwxyz";
        String[] aa = a.split("");
        String[] bb = key.split("");

        int n = key.length();
        //System.out.println(n);
        int flag = 0;
        for (int i = 0;i < 26; i++){
            flag = 0;
            for (int l = 0;l<n ;l++)
            {

                if (aa[i].equals(bb[l]) )
                {

                    flag = 1;
                }

            }
            if(flag == 0){
                key += aa[i];
            }
        }
        String[] cc = str.split("");
        //System.out.println(key);
        bb = key.split("");
        int m = str.length();
        for (int l = 0;l<m ;l++)
            for (int i = 0;i < 26; i++)
            {
                //System.out.println(cc[l]+aa[i]+bb[i]);
                //System.out.println(cc[l].equals(aa[i]));
                if(cc[l+1].equals(aa[i]) )
                    //System.out.println(cc[l]+aa[i]+bb[i]);
                    string += bb[i];
            }
        System.out.println(str + " 加密后为：" + string);
        return  string;

    }

    /**
     * 解密Keyword密文  <br>
     *
     * @param str
     *            密文

     * @param2 key
     *            密钥key
     *
     * @return 返回明文
     */
    public static String Decrypt(String str, String key) {// 解密

        String string = "";
        String a ="abcdefghijklmnopqrstuvwxyz";
        String[] aa = a.split("");
        String[] bb = key.split("");

        int n = key.length();
        //System.out.println(n);
        int flag = 0;
        for (int i = 0;i < 26; i++){
            flag = 0;
            for (int l = 0;l<n ;l++)
            {

                if (aa[i].equals(bb[l]) )
                {

                    flag = 1;
                }

            }
            if(flag == 0){
                key += aa[i];
            }
        }
        System.out.println(key);
        String[] cc = str.split("");
        bb = key.split("");
        int m = str.length();
        for (int p = 0;p<m ;p++)
            for (int q = 0;q < 26; q++)
            {
                //System.out.println(cc[l]+aa[i]+bb[i]);
                //System.out.println(cc[l].equals(aa[i]));
                if(cc[p].equals(bb[q]) )
                    //System.out.println(cc[l]+aa[i]+bb[i]);
                    string += aa[q];
            }
        System.out.println(str + " 解密后为：" + string);
        return string;
    }

}