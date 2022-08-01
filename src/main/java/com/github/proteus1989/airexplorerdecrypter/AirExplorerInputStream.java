package com.github.proteus1989.airexplorerdecrypter;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.DigestException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Input stream implementation able to decrypt Air Explorer encrypted files
 */
public final class AirExplorerInputStream extends InputStream {

    InputStream in;

    private long counter = 0;
    private Cipher rijndael;
    private byte[] salt;

    /**
     * Creates a wrapper that read the input stream and decrypt it on the fly.
     *
     * @param in       The encrypted input stream.
     * @param password The file password.
     * @throws IOException              Thrown when cipher either input stream fail.
     * @throws IllegalArgumentException This exception is thrown when an
     *                                  incorrect password is given.
     */
    public AirExplorerInputStream(InputStream in, String password) throws IllegalArgumentException, IOException {
        this.in = in;
        initCipher(password);
    }

    private static byte[] reverse(byte[] value) {
        final int length = value.length;
        byte[] res = new byte[length];
        for (int i = 0; i < length; i++)
            res[length - i - 1] = value[i];
        return res;
    }

    /**
     * Checks file header metadata and creates Cipher
     *
     * @param password File password.
     * @throws IOException              Thrown when either cipher or input stream fails.
     * @throws IllegalArgumentException This exception is thrown when an invalid password is provided.
     */
    private void initCipher(String password) throws IllegalArgumentException, IOException {
        try {
            DataInputStream binaryReader = new DataInputStream(in);

            // Checking version
            byte[] versionArray = new byte[8];
            binaryReader.readFully(versionArray);
            ByteBuffer.wrap(reverse(versionArray)).getLong();
            long value = ByteBuffer.wrap(reverse(versionArray)).getLong();
            if (value != 58261948629778432L) //58261948629778432L
                throw new IOException("Corrupted file");

            // Checking one 1
            byte[] one1Array = new byte[8];
            binaryReader.readFully(one1Array);
            value = ByteBuffer.wrap(reverse(one1Array)).getLong();
            if (value != 1L) //1L
                throw new IOException("Corrupted file");

            // Creating cipher
            byte[] secret = getSecret(password);
            salt = getSalt(secret);
            this.rijndael = createRijndaelCipher(secret);

            // Decrypting first 64 bytes block
            byte[] firstBlock = new byte[64];
            binaryReader.readFully(firstBlock);
            byte[] decryptedContent = decryptBlock(firstBlock);

            // Hashing to MD5
            byte[] hash = MessageDigest.getInstance("MD5").digest(decryptedContent);

            // Getting hash check block
            byte[] hashCheck = new byte[16];
            binaryReader.readFully(hashCheck);

            // Wrong password if hash is not equal to hashcheck
            if (!Arrays.equals(hash, hashCheck))
                throw new IllegalArgumentException("Wrong password");

        } catch (ShortBufferException | DigestException | NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidKeyException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Given the password return the cipher secret.
     *
     * @param password The file password.
     * @return The secret key.
     * @throws DigestException Exception creating the PasswordDeriveBytes.
     */
    private byte[] getSecret(String password) throws DigestException {
        byte[] rgbSalt = new byte[]
                {
                        (byte) 38, (byte) 25, (byte) 129, (byte) 78, (byte) 160, (byte) 109, (byte) 149, (byte) 52, (byte) 38, (byte) 117, (byte) 100, (byte) 5, (byte) 246
                };
        return new PasswordDeriveBytes(password, rgbSalt).getBytes(32);
    }

    /**
     * Given the secret key return the salt.
     *
     * @param secret The secret key.
     * @return The salt.
     */
    private byte[] getSalt(byte[] secret) {
        return new byte[]
                {
                        secret[2], secret[1], secret[4], secret[3], secret[5], secret[6], secret[7], secret[8], (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0
                };
    }

    /**
     * Creates the Cipher.
     *
     * @param secret The secret key.
     * @return The cipher prepared to decrypt.
     * @throws NoSuchAlgorithmException Cipher algorithm not found in the JDK.
     * @throws NoSuchPaddingException   Padding type not found in the JDK.
     * @throws InvalidKeyException      Invalid SecretKeySpec.
     */
    private Cipher createRijndaelCipher(byte[] secret) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        final SecretKeySpec skeySpec = new SecretKeySpec(secret, "AES");
        Cipher rijndaelManaged = Cipher.getInstance("AES/ECB/NoPadding");
        rijndaelManaged.init(Cipher.ENCRYPT_MODE, skeySpec);
        return rijndaelManaged;
    }

    /**
     * Decrypts an array block.
     *
     * @param encryptedByteArray Encrypted data.
     * @return Decrypted data.
     * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
     */
    private byte[] decryptBlock(byte[] encryptedByteArray) throws ShortBufferException {
        byte[] decryptedByteArray = new byte[encryptedByteArray.length];

        int inputCount = rijndael.getBlockSize();
        byte[] inputBuffer = new byte[salt.length];
        System.arraycopy(salt, 0, inputBuffer, 0, salt.length);

        long num = encryptedByteArray.length / inputCount + (encryptedByteArray.length % inputCount > 0 ? 1 : 0);
        for (long i = 0; i < num; i++) {
            byte[] outputBuffer = new byte[inputCount];
            byte[] longBytes = reverse(longToBytes(counter));
            longBytes = reverse(longBytes);
            System.arraycopy(longBytes, 0, inputBuffer, inputCount - 8, inputBuffer.length - (inputCount - 8));

            rijndael.update(inputBuffer, 0, inputCount, outputBuffer, 0);

            int index = (int) i * inputCount;

            byte[] tmp = decryptArray(encryptedByteArray, index, outputBuffer);
            System.arraycopy(tmp, 0, decryptedByteArray, index, tmp.length);

            counter++;
        }

        return decryptedByteArray;
    }

    private byte[] decryptArray(byte[] _param0, int _param1, byte[] _param2) {
        int length = Math.min(_param0.length - _param1, _param2.length);
        byte[] numArray = new byte[length];
        for (int index = 0; index < length; index++)
            numArray[index] = (byte) (_param0[_param1 + index] ^ _param2[index]);
        return numArray;
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int totalRead = 0;
        int read;

        do {
            read = in.read(b, off + totalRead, len - totalRead);
            totalRead += read != -1 ? read : 0;
        } while (read > -1 && totalRead < len);

        try {
            byte[] tmp = new byte[len];
            System.arraycopy(b, off, tmp, 0, len);
            tmp = decryptBlock(tmp);
            System.arraycopy(tmp, 0, b, off, len);
        } catch (ShortBufferException ex) {
            Logger.getLogger(AirExplorerInputStream.class.getName()).log(Level.SEVERE, null, ex);
        }

        return totalRead != 0 ? totalRead : -1;

    }

    @Override
    public void close() throws IOException {
        super.close();
        in.close();
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

}
