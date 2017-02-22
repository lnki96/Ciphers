package com.demo.lnki96.ciphers;

import java.security.KeyPairGenerator;


import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;

import javax.crypto.spec.*;
import javax.crypto.interfaces.*;

import java.io.*;
import java.math.*;

/*
本类的使用方法:
        //生成密钥对
        KeyPair kp = CryptWithRSA_H.generateRSAKeyPair(1024);

        //要加密的数据为byte[]类型
        byte[] plainText = "this is encrypted by rsa!".getBytes();

        //存放加密后结果的BigInteger变量dataAfterEncrypt
        BigInteger dataAfterEncrypt = null;

        //存放揭秘后结果的byte[] dataAfterDecrypt
        byte[] dataAfterDecrypt = null;

        try {
            //加密过程,注意,进行了密钥类型的转换:
            dataAfterEncrypt = CryptWithRSA_H.encrypt(plainText, (RSAPublicKey) kp.getPublic());
            //解密过程,注意,进行了密钥类型的转换:
            dataAfterDecrypt = CryptWithRSA_H.decrypt(dataAfterEncrypt, (RSAPrivateKey) kp.getPrivate());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        //获取RS中所用的参数
        BigInteger elements[]=CryptWithRSA_H.getRSAElements(kp);

        //对结果的测试
        System.out.println("data after encrypt:" + dataAfterEncrypt.toString());
        System.out.println("data after decrypt:"+new String(dataAfterDecrypt));
        System.out.println("n="+new String(String.valueOf(elements[0])));
        System.out.println("e="+new String(String.valueOf(elements[1])));
        System.out.println("d="+new String(String.valueOf(elements[2])));
 */
public class CryptWithRSA_H {
    public CryptWithRSA_H() {
    }

    private static String RSA = "RSA";

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength 密钥长度，范围：512～2048<br>
     *                  一般1024
     * @return 返回一个KeyPair对象
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {

        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        kpg.initialize(keyLength);
        return kpg.generateKeyPair();
    }

    /**
     * 用公钥加密 <br>
     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param dataToEncrypt
     *            需加密数据的byte数组
     * @param pbk
     *            公钥
     * @return 返回一个BigInteger,即位经过加密计算后的明文
     */
    public static BigInteger encrypt(byte[] dataToEncrypt,RSAPublicKey pbk) throws Exception {

        //获取公钥中的e和n,从而才能够进行加密的计算
        BigInteger e = pbk.getPublicExponent();
        BigInteger n = pbk.getModulus();

        // 获取明文m,直接使用要加密的byte[]作为BigInteger构造方法的参数即可
        BigInteger m = new BigInteger(dataToEncrypt);

        // 计算密文c
        BigInteger c = m.modPow(e, n);

        return c;
    }

    /**
     * 用私钥解密 <br>
     *
     * @param cipherText
     *            需解密的BigInteger型密文
     * @param prk
     *            私钥
     * @return 返回一个byte[],即为解密后的明文
     */
    public static byte[] decrypt(BigInteger cipherText,RSAPrivateKey prk) throws Exception {

        // 获取私钥参数
        BigInteger d = prk.getPrivateExponent();
        BigInteger n = prk.getModulus();

        //解密运算
        BigInteger m = cipherText.modPow(d, n);

        byte[] mt = m.toByteArray();
        return mt;
    }

    /**
     * 显示RSA中所用的n,e,d参数 <br>
     *
     * @param kp
     *            RSA中所用到的KeyPair对象
     * @return 返回BigInteger数组{n, e, d}
     */
    public static BigInteger[] getRSAElements(KeyPair kp){
        BigInteger[] rsaElements=null;
        RSAPublicKey pbk= (RSAPublicKey) kp.getPublic();
        RSAPrivateKey prk= (RSAPrivateKey) kp.getPrivate();
        BigInteger n,e,d;
        n=pbk.getModulus();
        e=pbk.getPublicExponent();
        d=prk.getPrivateExponent();
        rsaElements= new BigInteger[]{n, e, d};
        return rsaElements;
    }


    //把公钥和私钥保存在文件中,这个方法暂时没有用上
    public static void generateKey() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            PublicKey pbkey = kp.getPublic();
            PrivateKey prkey = kp.getPrivate();
            // 保存公钥
            FileOutputStream f1 = new FileOutputStream("pubkey.dat");
            ObjectOutputStream b1 = new ObjectOutputStream(f1);
            b1.writeObject(pbkey);
            // 保存私钥
            FileOutputStream f2 = new FileOutputStream("privatekey.dat");
            ObjectOutputStream b2 = new ObjectOutputStream(f2);
            b2.writeObject(prkey);
        } catch (Exception e) {
        }
    }

}