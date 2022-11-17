package life.forever.cf.activtiy;


import android.util.Base64;

import life.forever.cf.publics.Constant;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class DF {

    private static final byte[] KEYS = "Starlight.dev202".getBytes();
    private static final byte[] IV = "Starlight2020112".getBytes();



    public static String decrypt(String content) {
        String result = Constant.BLANK;
        try {
            byte[] bytes = Base64.decode(content, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(KEYS, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            result = new String(cipher.doFinal(bytes)).trim();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
