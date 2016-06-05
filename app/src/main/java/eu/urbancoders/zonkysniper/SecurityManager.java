package eu.urbancoders.zonkysniper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 05.06.2016
 */
public class SecurityManager {

    private static SecurityManager sInstance;
    private SecretKey mKey;
    private Context mContext;

    private SecurityManager(Context context) {
        String androidId = Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

//        String androidId = FirebaseInstanceId.getInstance().getToken();
        mContext = context.getApplicationContext();
        try {
            byte[] key = androidId.getBytes("UTF8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            mKey = new SecretKeySpec(key, "AES");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static SecurityManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SecurityManager(context);
        }
        return sInstance;
    }

    public String encryptString(String stringToEncrypt) {
        String output = stringToEncrypt;
        try {
            byte[] clearText = stringToEncrypt.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, mKey);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("iv", Base64.encodeToString(cipher.getIV(), Base64.NO_WRAP));
            editor.apply();
            output = new String(Base64.encode(cipher.doFinal(clearText),
                    Base64.NO_WRAP), "UTF8");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException |
                NoSuchPaddingException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String decryptString(String stringToDecrypt) {
        String output = stringToDecrypt;
        try {
            byte[] encryptedBytes = Base64.decode(stringToDecrypt, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            byte[] iv = Base64.decode(prefs.getString("iv", ""), Base64.NO_WRAP);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, mKey, ivSpec);
            output = new String(cipher.doFinal(encryptedBytes), "UTF8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | UnsupportedEncodingException
                | IllegalBlockSizeException | BadPaddingException
                | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return output;
    }
}
