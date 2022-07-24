/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.proteus1989.airexplorerdecrypter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to decrypt Air Explorer files
 *
 * @author Antonio
 */
public class AirExplorerDecrypter {

    /**
     * Utility method to get original file name from encryted string.
     *
     * @param name     the encrypted file name. '.cloudencoded2' extension is
     *                 optional.
     * @param password the password used to encrypt the file.
     * @return original file name.
     * @throws IOException It is thrown if an IOException happens.
     */
    public static String decryptName(String name, String password) throws IOException {
        try {
            byte[] buffer = Base64.getDecoder().decode(name.replace("_", "+").replace("-", "/").replace(".cloudencoded2", ""));
            byte[] bytes = new PasswordDeriveBytes(password, null).GetBytes(32);

            final SecretKeySpec skeySpec = new SecretKeySpec(bytes, "AES");
            Cipher rijndaelManaged = Cipher.getInstance("AES/CBC/NoPadding");
            rijndaelManaged.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec("za893eji340y89hn".getBytes(StandardCharsets.US_ASCII)));

            String out = new String(rijndaelManaged.doFinal(buffer), StandardCharsets.UTF_8).trim();
            if (out.endsWith("-vrfy-"))
                out = out.substring(0, out.length() - "-vrfy-".length());

            return out;
        } catch (DigestException | InvalidKeyException
                 | NoSuchAlgorithmException | InvalidAlgorithmParameterException | BadPaddingException |
                 IllegalBlockSizeException | NoSuchPaddingException e) {
            throw new IOException("Error decrypting file name", e);
        }

    }

    /**
     * Decrypts the given file at the same directory.
     *
     * @param srcFile Encrypted file.
     * @param pass    The file password.
     * @throws IOException Input/Output exceptions
     */
    public static void decrypt(File srcFile, String pass) throws IOException {
        decrypt(srcFile, srcFile.getParentFile(), pass);
    }

    /**
     * Decrypts the given file.
     *
     * @param srcFile   Encrypted file.
     * @param dstFolder Destination folder.
     * @param pass      The file password.
     * @throws IOException Input/Output exceptions
     */
    public static void decrypt(File srcFile, File dstFolder, String pass) throws IOException {
        if (!srcFile.exists())
            throw new IOException("File " + srcFile + " does not exist");

        if (!srcFile.isFile())
            throw new IOException(srcFile + " is not a file");

        if (!dstFolder.isDirectory())
            throw new IOException("Custom output folder is not a directory");

        if (!dstFolder.canWrite())
            throw new IOException("Output folder is not writable");

        String sourceName = srcFile.getName();
        if (!(sourceName.toLowerCase().endsWith(".cloudencoded2") || sourceName.toLowerCase().endsWith(".cloudencoded")))
            throw new IOException("File extension must finish in .cloudencoded or .cloudencoded2");

        String originalName;
        if (sourceName.toLowerCase().endsWith(".cloudencoded2"))
            originalName = decryptName(sourceName, pass);
        else
            originalName = sourceName.replace(".cloudencoded", "");

        FileInputStream fis = new FileInputStream(srcFile);
        AirExplorerInputStream stream = new AirExplorerInputStream(fis, pass);

        consume(stream, Files.newOutputStream(Paths.get(dstFolder + File.separator + originalName)));

    }

    /**
     * Given an input stream, decrypts the file and write it in output stream.
     *
     * @param is   Encrypted file input stream.
     * @param os   Decrypted file output stream.
     * @param pass The file password.
     * @throws IOException Input/Output exceptions
     */
    public static void decrypt(InputStream is, OutputStream os, String pass) throws IOException {
        AirExplorerInputStream stream = new AirExplorerInputStream(is, pass);
        consume(stream, os);
    }

    /**
     * Given an AirExplorerInputStream and an output stream read the input
     * stream and write it into output stream.
     *
     * @param is Air explorer input stream.
     * @param os Decrypted file output stream.
     * @throws IOException Input/Output exceptions
     */
    private static void consume(AirExplorerInputStream is, OutputStream os) throws IOException {
        byte[] data = new byte[1024];
        int bytesRead = is.read(data);

        while (bytesRead != -1) {
            os.write(data, 0, bytesRead);
            bytesRead = is.read(data);
        }

        try {
            is.close();
        } catch (Exception e) {
            Logger.getLogger(AirExplorerInputStream.class.getName()).log(Level.SEVERE, "Error closing the input stream");
        }

        try {
            os.close();
        } catch (Exception e) {
            Logger.getLogger(AirExplorerInputStream.class.getName()).log(Level.SEVERE, "Error closing the output stream");
        }
    }
}
