package com.demo.lnki96.ciphers;
import java.util.Scanner;

public class Autokey {

    static char[] wordTable = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z' };

    public static void main(String[] args) {

        char[][] vigenereTable = creatVigenereTable();
        System.out.println("［A 加密］［J 解密］,Please Choose One");
        Scanner c = new Scanner(System.in);// 创建Scanner对象
        String s1 = c.nextLine();// 获取本行的字符串
        if (s1.equalsIgnoreCase("A")) {
            Scanner sin = new Scanner(System.in);
            System.out.println("请输入你想进行加密的明文：");
            String mingwenStr = sin.nextLine();
            char[] mingwen = mingwenStr.toCharArray();
            int mingwenLength = mingwen.length;
            System.out.println("请设置一个密钥：");
            String miyaoStr = sin.nextLine();
            char[] miyao = new char[mingwenLength+1];
            miyao = miyaoStr.toCharArray();


            int miyaoLength = miyao.length;

            char[] miwen = jiami(mingwenLength, miyaoLength, mingwen, miyao,
                    vigenereTable);
            System.out.print("用维吉尼亚算法进行加密后的密文是：");
            for (int s = 0; s < mingwenLength; s++) {
                System.out.print(miwen[s]);
            }
        } else if (s1.equalsIgnoreCase("J")) {
            Scanner sin = new Scanner(System.in);
            System.out.println("请输入你想进行解密的密文：");
            String miwenStr = sin.nextLine();
            char[] miwen = miwenStr.toCharArray();
            int miwenLength = miwen.length;
            System.out.println("请设置一个密钥：");
            String miyaoStr = sin.nextLine();



            char[] miyao = new char[miwenLength+1];
            miyao = miyaoStr.toCharArray();
            int miyaoLength = miyao.length;
            char[] mingwen = jiemi(miwenLength, miyaoLength, miwen, miyao,
                    vigenereTable);
            System.out.print("用维吉尼亚算法进行解密后的明文是：");
            for (int s = 0; s < miwenLength; s++) {
                System.out.print(mingwen[s]);
            }
        }




    }

    /**
     * 生成VigenereTable  <br>
     *
     * @param void
     *

     * @return 二维字符数组
     */
    public static char[][] creatVigenereTable() {

        char[][] vigenereTable = new char[26][26];

        int i = 0;
        for (int x = 0; x < 26; x++) {
            for (int y = 0; y < 26; y++) {
                vigenereTable[x][y] = wordTable[i];
                i++;
            }
            i = x + 1;
        }
        return vigenereTable;
    }

    /**
     * 生成Autokey密文 <br>
     *
     * @param mingwenLength
     *          明文长度
     *
     * @param2 miyaoLength
     *          密钥长度
     *
     * @param3 mingwen
     *          明文
     *
     * @param4 miyao
     *          密钥
     *
     * @param5 vigenereTable
     *          维吉尼亚表格
     * @return 密文字符数组
     */
    public static char[] jiami(int mingwenLength, int miyaoLength,
                                char[] mingwen, char[] miyao, char[][] vigenereTable) {
        // 现在开始加密
        char[] miwen = new char[mingwenLength];


        if (miyaoLength == mingwenLength || miyaoLength > mingwenLength ){
            for (int mingwenl = 0; mingwenl < mingwenLength; mingwenl++) {

                for (int wordTablel = 0; wordTablel < 26; wordTablel++) {

                    if (mingwen[mingwenl] == wordTable[wordTablel]) {

                        for (int wordTablel2 = 0; wordTablel2 < 26; wordTablel2++) {

                            if (miyao[mingwenl] == wordTable[wordTablel2]) {

                                miwen[mingwenl] = vigenereTable[wordTablel][wordTablel2];

                            }
                        }
                    }
                }
            }
        }
        else{
            for (int mingwenl = 0; mingwenl < miyaoLength; mingwenl++) {

                for (int wordTablel = 0; wordTablel < 26; wordTablel++) {

                    if (mingwen[mingwenl] == wordTable[wordTablel]) {

                        for (int wordTablel2 = 0; wordTablel2 < 26; wordTablel2++) {

                            if (miyao[mingwenl] == wordTable[wordTablel2]) {

                                miwen[mingwenl] = vigenereTable[wordTablel][wordTablel2];

                            }
                        }
                    }
                }
            }
            for (int mingwenl = miyaoLength; mingwenl < mingwenLength; mingwenl++) {

                for (int wordTablel = 0; wordTablel < 26; wordTablel++) {

                    if (mingwen[mingwenl] == wordTable[wordTablel]) {

                        for (int wordTablel2 = 0; wordTablel2 < 26; wordTablel2++) {

                            if (miwen[mingwenl - miyaoLength] == wordTable[wordTablel2]) {

                                miwen[mingwenl] = vigenereTable[wordTablel][wordTablel2];

                            }
                        }
                    }
                }
            }

        }

        return miwen;
    }

    /**
     * 解密Autokey密文 <br>
     *
     * @param mingwenLength
     *          明文长度
     *
     * @param2 miyaoLength
     *          密钥长度
     *
     * @param3 mingwen
     *          明文
     *
     * @param4 miyao
     *          密钥
     *
     * @param5 vigenereTable
     *          维吉尼亚表格
     * @return 明文字符数组
     */
    public static char[] jiemi(int miwenLength, int miyaoLength,
                                char[] miwen, char[] miyao, char[][] vigenereTable) {
        //现在开始解密
        char[] mingwen = new char[miwenLength];

        if (miyaoLength == miwenLength || miyaoLength > miwenLength ){
            for (int miwenl = 0; miwenl < miwenLength; miwenl++) {

                for (int wordTablel = 0; wordTablel < 26; wordTablel++) {

                    if (miyao[miwenl] == wordTable[wordTablel]) {

                        for (int wordTablel2 = 0; wordTablel2 < 26; wordTablel2++) {

                            if (miwen[miwenl] == vigenereTable[wordTablel2][wordTablel]) {

                                mingwen[miwenl] = wordTable[wordTablel2];
                            }
                        }
                    }
                }
            }
        }
        else{
            for (int miwenl = 0; miwenl < miyaoLength; miwenl++) {

                for (int wordTablel = 0; wordTablel < 26; wordTablel++) {

                    if (miyao[miwenl] == wordTable[wordTablel]) {

                        for (int wordTablel2 = 0; wordTablel2 < 26; wordTablel2++) {

                            if (miwen[miwenl] == vigenereTable[wordTablel2][wordTablel]) {

                                mingwen[miwenl] = wordTable[wordTablel2];
                            }
                        }
                    }
                }
            }

            for (int miwenl = miyaoLength; miwenl < miwenLength; miwenl++) {



                for (int wordTablel = 0; wordTablel < 26; wordTablel++) {

                    if (miwen[miwenl - miyaoLength] == wordTable[wordTablel]) {

                        for (int wordTablel2 = 0; wordTablel2 < 26; wordTablel2++) {

                            if (miwen[miwenl] == vigenereTable[wordTablel2][wordTablel]) {

                                mingwen[miwenl] = wordTable[wordTablel2];
                            }
                        }
                    }
                }
            }

        }
        return mingwen;
    }








}//  最后一个大括号
    