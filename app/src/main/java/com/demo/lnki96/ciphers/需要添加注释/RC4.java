package com.example.eric.cryptosystem;

/**
 * Created by eric on 16/9/19.
 */
public class RC4 {

    /**
     * 生成RC4密文 or 解密RC4密文  <br>
     *
     * @param aInput
     *            明文 or 密文

     * @param2 aKey
     *            密钥
     *
     * @return 返回一个密文 or 明文
     */
        public static String MyRC4(String aInput, String aKey)
        {
            int[] iS = new int[256];
            byte[] iK = new byte[256];
            for (int i=0;i<256;i++)
                iS[i]=i;
            int j = 1;

            for (short i= 0;i<256;i++)
            {
                iK[i]=(byte)aKey.charAt((i % aKey.length()));
            }

            j=0;

            for (int i=0;i<255;i++)
            {
                j=(j+iS[i]+iK[i]) % 256;
                int temp = iS[i];
                iS[i]=iS[j];
                iS[j]=temp;
            }


            int i=0;
            j=0;
            char[] iInputChar = aInput.toCharArray();
            char[] iOutputChar = new char[iInputChar.length];
            for(short x = 0;x<iInputChar.length;x++)
            {
                i = (i+1) % 256;
                j = (j+iS[i]) % 256;
                int temp = iS[i];
                iS[i]=iS[j];
                iS[j]=temp;
                int t = (iS[i]+(iS[j] % 256)) % 256;
                int iY = iS[t];
                char iCY = (char)iY;
                iOutputChar[x] =(char)( iInputChar[x] ^ iCY) ;
            }
            return new String(iOutputChar);

        }
    }

