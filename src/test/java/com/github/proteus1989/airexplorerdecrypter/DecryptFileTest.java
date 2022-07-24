package com.github.proteus1989.airexplorerdecrypter;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio
 */
public class DecryptFileTest {

    public static final String RESOURCES_FILE = "src/test/resources/my_encrypted_file.txt";
    public static final String ROOT_FILE = "./my_encrypted_file.txt";

    @AfterClass
    public static void cleanup() {
        new File(RESOURCES_FILE).delete();
        new File(ROOT_FILE).delete();
    }

    @Test
    public void decryptFileSuccessfullyTest() throws IOException {
        BufferedInputStream is = new BufferedInputStream(new URL("https://raw.githubusercontent.com/Proteus1989/Air-Explorer-Decrypter/master/src/main/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2").openStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        final String validPass = "my_strong_pass";

        AirExplorerDecrypter.decrypt(is, os, validPass);

        assertEquals("Visit https://github.com/Proteus1989", new String(os.toByteArray(), UTF_8));
    }

    @Test
    public void decryptFileErrorTest() throws IOException {
        BufferedInputStream is = new BufferedInputStream(new URL("https://raw.githubusercontent.com/Proteus1989/Air-Explorer-Decrypter/master/src/main/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2").openStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        final String wrongPass = "my_wrong_pass";

        try {
            AirExplorerDecrypter.decrypt(is, os, wrongPass);
            assertEquals("Visit https://github.com/Proteus1989", new String(os.toByteArray(), UTF_8));
        } catch (IllegalArgumentException e) {
            assertEquals("Wrong password", e.getMessage());
        }
    }

    @Test
    public void decryptFileIntoAnotherDirectoryTest() throws IOException {
        File in = new File("src/test/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2");
        File out = new File("./");

        final String validPass = "my_strong_pass";

        AirExplorerDecrypter.decrypt(in, out, validPass);

        assertEquals("Visit https://github.com/Proteus1989", new String(Files.readAllBytes(new File(ROOT_FILE).toPath()), UTF_8));
    }
    @Test
    public void decryptFileIntoTheSameDirectoryTest() throws IOException {
        File in = new File("src/test/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2");

        final String validPass = "my_strong_pass";

        AirExplorerDecrypter.decrypt(in, validPass);

        assertEquals("Visit https://github.com/Proteus1989", new String(Files.readAllBytes(new File(RESOURCES_FILE).toPath()), UTF_8));
    }
}
