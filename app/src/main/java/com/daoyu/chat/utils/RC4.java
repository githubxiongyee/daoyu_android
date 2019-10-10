package com.daoyu.chat.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/12/4.
 */

public class RC4 {

    private static final String encryptKey = "NuHjhg%&^fxF57cGnm";

    private final byte[] S = new byte[256];
    private final byte[] T = new byte[256];

    public RC4() {
        init();
    }

    private void init() {
        byte[] key = new byte[0];
        try {
            key = encryptKey.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int keyLen = key.length;
        for (int i = 0; i < 256; i++) {
            S[i] = (byte) i;
            T[i] = key[i % keyLen];
        }
        int j = 0;
        byte tmp;
        for (int i = 0; i < 256; i++) {
            j = (j + S[i] + T[i]) & 0xFF;
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
        }
    }

    public byte[] encrypt(final byte[] encryptBytes) {
        final byte[] cipherText = new byte[encryptBytes.length];
        int i = 0, j = 0, k, t;
        byte tmp;
        for (int counter = 0; counter < encryptBytes.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
            t = (S[i] + S[j]) & 0xFF;
            k = S[t];
            cipherText[counter] = (byte) (encryptBytes[counter] ^ k);
        }
        return cipherText;
    }

    public byte[] decrypt(final byte[] decryptBytes) {
        return encrypt(decryptBytes);
    }
}
