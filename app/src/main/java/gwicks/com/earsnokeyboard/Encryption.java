package gwicks.com.earsnokeyboard;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gwicks on 25/08/2016.
 *
 * Standard Java Encryption Class, uses boiler plate Code found on Java Website.
 *
 * NOTE password is stored in BuildConfig, and you should change the IV to something else, obviously
 */

public class Encryption {

    private Context mContext;
    public final String TAG = "Encrypt";
    private String password = BuildConfig.Password;

    // Constructor added 17th October 17 - thinking maybe setting context in encrypt
    // method is not early enough, so it crashes because it is still null when it hits the first
    // mContext later in method!

    public Encryption() {
        mContext = AnyApplication.getInstance();
        Log.d(TAG, "Encryption: mContext = " + mContext);
        //mContext = getA
    }

    public String encrypt(String name, String path, String directoryName) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        // Here you read the cleartext.

        android.util.Log.d(TAG, "encrypt: in Encrytpion.java");


        // added 15th Feb 2020 to try and skip encryption

        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        File newFolder = new File(mContext.getExternalFilesDir(null) + directoryName);
        android.util.Log.d(TAG, "encrypt: after new folder");

        if(!newFolder.exists()){
            newFolder.mkdirs();
        }
        Log.d(TAG, "encrypt: Encryption is switched on: " + Constants.encryption);

        if(Constants.encryption == false){
            File toBeCopied = new File(path);
            String final_path_ue = mContext.getExternalFilesDir(null) + directoryName +  name + ".ue";
            File destination = new File(final_path_ue);
            try {
                FileUtils.copyFile(toBeCopied, destination);
                Log.d("LogUploadTask", "Copyting file to VideoDIARY");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return  final_path_ue;
        }




        String final_path = mContext.getExternalFilesDir(null) + directoryName +  name + ".encrypted";
        Log.d(TAG,"We are starting encrytopn!");
        android.util.Log.d(TAG, "encrypt: final_path = " + final_path);
        FileInputStream fis = new FileInputStream(path);
        Log.d(TAG,"We are starting encrytopn 2!");
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        FileOutputStream fos = new FileOutputStream(final_path);
        Log.d(TAG,"We are starting encrytopn 3!");

        // Length is 16 byte
        // Careful when taking user input!!! http://stackoverflow.com/a/3452620/1188357
        SecretKeySpec sks = new SecretKeySpec(password.getBytes("UTF-8"), "AES");

        // Create cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sks, ivSpec);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        //byte[] d = new byte[8];
        byte[] d = new byte[1024];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }

        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
        Log.d(TAG,"We are starting encrytopn 4 - FINISHED ENCRYTPTION! file at path: " + final_path);

        //decrypt(name, final_path);
        return final_path;
    }


/*
    Not used in code, just included in order to make it easier to test to make sure the decryption is working as intented
*/

    public void decrypt(String name, String path) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {


        String directoryName = "/videoDIARY/";

        byte[] iv = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        String final_path = mContext.getExternalFilesDir(null) + directoryName +  name + ".mp4";

        FileInputStream fis = new FileInputStream(path);

        FileOutputStream fos = new FileOutputStream(final_path);
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sks, ivSpec);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[1024];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }




}