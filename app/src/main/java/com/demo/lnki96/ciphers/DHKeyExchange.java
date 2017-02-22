package com.demo.lnki96.ciphers;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by eric on 16/9/23.
 */
public class DHKeyExchange {

    //p
    public static BigInteger getp() {
        Random rd = new Random();
        BigInteger p = new BigInteger(30, 100, rd);
        return p;
    }

    public static BigInteger getg() {
        return BigInteger.valueOf(5);
    }


    //A，B
    public static BigInteger getAB(BigInteger g, BigInteger ab, BigInteger p) {

        BigInteger m = g.modPow(ab, p);
        return m;
    }

    public static BigInteger getRandomab() {
        Random rd2 = new Random();
        BigInteger ab = new BigInteger(9, rd2);
        return ab;
    }

    //key
    public static BigInteger getkey(BigInteger AB, BigInteger ab, BigInteger p) {

        BigInteger key = AB.modPow(ab, p);
        return key;
    }


}
