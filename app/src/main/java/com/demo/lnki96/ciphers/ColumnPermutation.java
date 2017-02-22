package com.demo.lnki96.ciphers;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
/**
 * Created by eric on 16/9/19.
 */
public class ColumnPermutation {

    /**
     * 列置换加密明文  <br>
     *
     * @param text
     *            明文

     * @param2 keyword
     *            关键字
     *
     * @return 返回密文
     */
    public static String MyCp_Encryption(String text,String keyword){
            int lenp,lenk,i,j;
            char t;
            String temp,cipher;
            cipher=new String();
            lenk=keyword.length();
            lenp=text.length();
            if(0 != lenp%lenk){
                for(i=0;i<lenk-lenp%lenk;i++){
                    text+='x';
                }
            }
            lenp=text.length();
//            System.out.println("明文矩阵:");
//            for(i=0;i<lenp;i++){
//                if(0 ==(i % lenk))System.out.print("\r\n");
//                System.out.print(text.charAt(i));
//            }
            System.out.print("\r\n");
            temp=keyword;
            for (i = 0; i < lenk; i++) {
                for (j = i + 1; j < lenk; j++) {
                    if (temp.charAt(i)> temp.charAt(j)) {
                        //swap(temp.charAt(i), temp.charAt(j));
                        t=temp.charAt(i);
                        temp.replace(temp.charAt(i),temp.charAt(j));
                        temp.replace(temp.charAt(j),t);
                    }
                }
            }
            int[] seq=new int[1024];
            for (i = 0; i < lenk; i++) {
                for (j = 0; j < lenk; j++) {
                    if (keyword.charAt(i) == temp.charAt(j)) {
                        seq[i] = j + 1;
                        temp.replace(temp.charAt(j),'*');
                        break;
                    }
                }
            }
            for (i = 0; i < lenk; i++) {
                for (j = 0; j < lenp / lenk; j++) {
                    cipher += text.charAt(seq[i] + j*lenk - 1);
                }
            }
            //System.out.println("\n结果：\n");
            //System.out.println("明文"+ text);
            //System.out.println("密文"+ cipher);
            return cipher;
        }


    /**
     * 解密列置换密文  <br>
     *
     * @param cipher
     *            密文

     * @param2 keyword
     *            关键字
     *
     * @return 返回明文
     */
        public static String MyCp_Decryption(String cipher,String keyword){
            int lenc, lenk, i, j;
            char t;
            String plaintext,temp;
            plaintext=new String();
            lenk = keyword.length();
            lenc = cipher.length();
            temp = keyword;
            for (i = 0; i < lenk; i++) {
                for (j = i + 1; j < lenk; j++) {
                    if (temp.charAt((i)) > temp.charAt(j)){
                        t=temp.charAt(i);
                        temp.replace(temp.charAt(i),temp.charAt(j));
                        temp.replace(temp.charAt(j),t);
                    }
                }
            }
            int[] seq=new int[1024];
            int[] seq2=new int[1024];
            for (i = 0; i < lenk; i++) {
                for (j = 0; j < lenk; j++) {
                    if (keyword.charAt(i) == temp.charAt(j)) {
                        seq[i] = j + 1;
                        temp.replace(temp.charAt(j),'*');
                        break;
                    }
                }
            }
            for (i = 0; i < lenk; i++)seq2[seq[i]-1] = i+1;

            for (i = 0; i < lenc/lenk; i++) {
                for (j = 0; j < lenk; j++) {
                    plaintext += cipher.charAt((lenc/lenk)*(seq2[j]-1)+i);
                }
            }
            return plaintext;
        }
    }


