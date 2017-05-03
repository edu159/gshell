package com.example.eduardo.gshell;

/**
 * Created by case on 11/04/17.
 */

import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AESEncrypter_v2 {

    private static final byte[] SALT = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private Cipher ecipher;
    private Cipher dcipher;

    // temporary iv , must be 16 bytes long!
    private static final byte[] tmp_iv = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03,
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    AESEncrypter_v2(String passPhrase) throws Exception {

        // generate secret key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), SALT, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // read iv from file


        // create encrypt cipher
        ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        ecipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(tmp_iv));

        // decrypt cipher
        dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        dcipher.init(Cipher.DECRYPT_MODE,secret,new IvParameterSpec(tmp_iv));
    }

    public String encrypt(String encrypt) throws Exception {
        byte[] bytes = encrypt.getBytes("UTF8");
        byte[] encrypted = encrypt(bytes);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public byte[] encrypt(byte[] plain) throws Exception {
        return ecipher.doFinal(plain);
    }

    public String decrypt(String encrypt) throws Exception {
        byte[] bytes = Base64.decode(encrypt, Base64.DEFAULT);
        byte[] decrypted = decrypt(bytes);
        return new String(decrypted, "UTF8");
    }

    public byte[] decrypt(byte[] encrypt) throws Exception {
        return dcipher.doFinal(encrypt);
    }

    public String readline(int linenumber, String password, String filepath){
        /* read linenumber=[0,N-1] for N lines in filepath
        Return as string */

        // check to see that file exists
        File file = new File(filepath);

        if (!file.exists()){
            // clear all server classes and force input from user
            System.out.println("Oh dear. File does not exist!");
            return "null";
        }

        FileWriter writer = null;

        try{
            writer = new FileWriter(filepath,true);
        }
        catch(IOException error)
        {
            error.printStackTrace();
        }


        BufferedReader br = null;

        try{
            br = new BufferedReader(new FileReader(filepath));
        }
        catch(IOException error){
            error.printStackTrace();
        }

        String textline = null;
        String[] filelines = null;

        while (true){

            try{
                textline = br.readLine();
            }
            catch (IOException error){
                error.printStackTrace();
            }

            if (textline == null){
                // eof
                break;
            }
            else{
                // new array 1 element longer than filelines
                int length = 0;

                if (filelines != null){
                    length = filelines.length;
                }
                String[] tmp1 = new String[length+1];
                String[] tmp2 = new String[1];


                // current line
                tmp2[0] = textline;


                if (filelines != null){
                    System.arraycopy(filelines,0,tmp1,0,length);
                }
                System.arraycopy(tmp2,0,tmp1,length,1);

                // now change filelines to point to tmp2 and destroy tmp1,tmp2
                filelines = tmp1;

                tmp1 = null;
                tmp2 = null;

            }
        }

        // close file
        try{
            writer.close();
        }
        catch(IOException error){
            error.printStackTrace();
        }


        //check length of file with linenumber requested
        if (filelines!=null){
            if (linenumber>filelines.length-1){
                System.out.println("internal error, requested line does not exist");
                return "null";
            }
            else{
                // decrypt AES cipher using password
                try{
                    //return encrypter.decrypt(filelines[linenumber]);
                    return this.decrypt(filelines[linenumber]);
                }
                catch(Exception error){
                    error.printStackTrace();
                    return "null";
                }
            }
        }
        else{
            return "null";
        }

    }

    public void writeline(String line, int linenumber, String password, String filepath){
        /* read line and decrypt using AES cypher with password as the key */

        boolean corruptfile = false;
        boolean readotherentries = false;

        // check to see if file exists
        File file = new File(filepath);
        if (file.exists()){
            readotherentries = true;
        }
        else{
            if (linenumber==0){
                readotherentries = false;
            }
            else{
                // file should exist, need to clear server classes and start again
                corruptfile = true;
            }
        }

        if (corruptfile){
            // need to delete all server class instances and start again
            System.out.println("Oh dear.");
            return;
        }

        //PrintWriter writer = null;
        FileWriter writer = null;


        try{
            writer = new FileWriter(filepath,true);
        }
        catch(IOException error)
        {
            error.printStackTrace();
        }


        String[] filelines = null;

        if (readotherentries){
            // read all lines in string array & replace linenumber with line

            BufferedReader br = null;

            try{
                br = new BufferedReader(new FileReader(filepath));
            }
            catch(IOException error){
                error.printStackTrace();
            }

            String textline = null;
            while (true){

                try{
                    textline = br.readLine();
                }
                catch (IOException error){
                    error.printStackTrace();
                }

                if (textline == null){
                    // eof
                    break;
                }
                else{
                    // new array 1 element longer than filelines
                    int length = 0;

                    if (filelines != null){
                        length = filelines.length;
                    }
                    String[] tmp1 = new String[length+1];
                    String[] tmp2 = new String[1];


                    // current line
                    tmp2[0] = textline;


                    if (filelines != null){
                        System.arraycopy(filelines,0,tmp1,0,length);
                    }
                    System.arraycopy(tmp2,0,tmp1,length,1);

                    // now change filelines to point to tmp2 and destroy tmp1,tmp2
                    filelines = tmp1;


                    tmp1 = null;
                    tmp2 = null;

                }
            }

            // now overwrite desired line
            if (linenumber > filelines.length){
                System.out.println("Internal error with server flines indexing");
                return;
            }
            else if(linenumber == filelines.length){
                // linenumber = [0,N] for N lines currently in the file
                String[] tmp1 = new String[filelines.length +1];

                System.arraycopy(filelines,0,tmp1,0,filelines.length);

                filelines = tmp1;

                tmp1 = null;
            }
        }
        else{
            filelines = new String[1];
        }

        try{
            // encrypt
            filelines[linenumber] = this.encrypt(line);

            for (int ia =0;ia<filelines.length;ia++) System.out.println(filelines[ia]);
        }
        catch(Exception error){
            error.printStackTrace();
        }


        // close file
        try{
            writer.close();
        }
        catch(IOException error){
            error.printStackTrace();
        }



        // open file to overwrite
        writer = null;

        try{
            writer = new FileWriter(filepath,false);
        }
        catch(IOException error)
        {
            error.printStackTrace();
        }


        // write lines
        for (int ia=0;ia<filelines.length;ia++){
            try{
                writer.write(filelines[ia]+"\n");
            }
            catch(IOException error){
                error.printStackTrace();
            }
        }

        // close file
        try{
            writer.close();
        }
        catch(IOException error){
            error.printStackTrace();
        }

    }

    public void write_iv(String filepath, byte[] iv){
        // write byte[] array of iv to file

        try{
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(iv);
            fos.close();
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("FileNotFoundException : " + ex);
        }
        catch(IOException ioe)
        {
            System.out.println("IOException : " + ioe);
        }
    }

}
