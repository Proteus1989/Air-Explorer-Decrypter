package com.github.proteus1989.airexplorerdecrypter;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio
 */
public class DecryptFileTest {
    @Test
    public void decryptFileSuccessfullyTest() throws IOException {
        BufferedInputStream is = new BufferedInputStream(new URL("https://raw.githubusercontent.com/Proteus1989/Air-Explorer-Decrypter/master/src/main/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2").openStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        final String validPass = "my_strong_pass";

        AirExplorerDecrypter.decrypt(is, os, validPass);

        assertEquals("Visit https://github.com/Proteus1989", new String(os.toByteArray(), StandardCharsets.UTF_8));
    }

    @Test
    public void decryptFileErrorTest() throws IOException {
        BufferedInputStream is = new BufferedInputStream(new URL("https://raw.githubusercontent.com/Proteus1989/Air-Explorer-Decrypter/master/src/main/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2").openStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        final String wrongPass = "my_wrong_pass";

        try {
            AirExplorerDecrypter.decrypt(is, os, wrongPass);
            assertEquals("Visit https://github.com/Proteus1989", new String(os.toByteArray(), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            assertEquals("Wrong password", e.getMessage());
        }
    }
}
