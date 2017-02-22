package com.example.eric.cryptosystem;

/**
 * Created by eric on 16/9/18.
 */
import java.util.Scanner;
public class Caesear {


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
                int key = sc1.nextInt();// 将下一个输入项转换成int类型
                Encryption(s, key);// 调用Encryption方法
            } else if (s1.equalsIgnoreCase("J")) {
                System.out.println("请输入密文：");
                Scanner sc = new Scanner(System.in);
                String s = sc.nextLine();
                System.out.println("请输入密钥：");
                Scanner sc1 = new Scanner(System.in);
                int key = sc1.nextInt();
                Decrypt(s, key);// 调用Encryption方法
            }
        }

    /**
     * 生成Caesear密文  <br>
     *
     * @param str
     *            明文

     * @param2 k
     *            密钥k
     *
     * @return 返回密文
     */
        public static String Encryption(String str, int k) {// 加密
            String string = "";
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c >= 'a' && c <= 'z') // 如果字符串中的某个字符是小写字母
                {
                    c += k % 26; // 移动key%26位
                    if (c < 'a')
                        c += 26; // 向左超界
                    if (c > 'z')
                        c -= 26; // 向右超界
                } else if (c >= 'A' && c <= 'Z') // 如果字符串中的某个字符是大写字母
                {
                    c += k % 26;
                    if (c < 'A')
                        c += 26;// 同上
                    if (c > 'Z')
                        c -= 26;// 同上
                }
                string += c;// 将加密后的字符连成字符串
            }
            System.out.println(str + " 加密后为：" + string);
            return string;
        }
        public static String Decrypt(String str, int n) {// 解密
            int k = Integer.parseInt("-" + n);
            String string = "";
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c >= 'a' && c <= 'z') // 如果字符串中的某个字符是小写字母
                {
                    c += k % 26; // 移动key%26位
                    if (c < 'a')
                        c += 26; // 向左超界
                    if (c > 'z')
                        c -= 26; // 向右超界
                } else if (c >= 'A' && c <= 'Z') // 如果字符串中的某个字符是大写字母
                {
                    c += k % 26;
                    if (c < 'A')
                        c += 26;// 同上
                    if (c > 'Z')
                        c -= 26;// 同上
                }
                string += c;// 将解密后的字符连成字符串
            }
            System.out.println(str + " 解密后为：" + string);
            return string;
        }
    }

