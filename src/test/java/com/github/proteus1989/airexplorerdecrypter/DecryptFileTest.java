package com.github.proteus1989.airexplorerdecrypter;

import org.junit.AfterClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class DecryptFileTest {

    public static final String ENCRYPTED_FILE_PATH = "src/test/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2";
    public static final String OUTPUT_FILENAME = "my_encrypted_file.txt";
    public static final String OUTPUT_FILE_PATH = "src/test/resources/" + OUTPUT_FILENAME;

    @AfterClass
    public static void cleanup() {
        new File(OUTPUT_FILE_PATH).delete();
        new File(OUTPUT_FILENAME).delete();
    }

    @Test
    public void decryptFileSuccessfullyTest() throws IOException {
        InputStream is = Files.newInputStream(Paths.get(ENCRYPTED_FILE_PATH));
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        final String validPass = "my_strong_pass";

        AirExplorerDecrypter.decrypt(is, os, validPass);

        assertEquals("Visit https://github.com/Proteus1989", new String(os.toByteArray(), UTF_8));
    }

    @Test
    public void decryptFileErrorTest() throws IOException {
        InputStream is = Files.newInputStream(Paths.get(ENCRYPTED_FILE_PATH));
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
        File in = new File(ENCRYPTED_FILE_PATH);
        File out = new File("./");

        final String validPass = "my_strong_pass";

        AirExplorerDecrypter.decrypt(in, out, validPass);

        assertEquals("Visit https://github.com/Proteus1989", new String(Files.readAllBytes(new File(OUTPUT_FILENAME).toPath()), UTF_8));
    }

    @Test
    public void decryptFileIntoTheSameDirectoryTest() throws IOException {
        File in = new File("src/test/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2");

        final String validPass = "my_strong_pass";

        AirExplorerDecrypter.decrypt(in, validPass);

        assertEquals("Visit https://github.com/Proteus1989", new String(Files.readAllBytes(new File(OUTPUT_FILE_PATH).toPath()), UTF_8));
    }
}
