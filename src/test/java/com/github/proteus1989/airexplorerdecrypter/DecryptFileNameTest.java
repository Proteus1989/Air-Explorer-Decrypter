package com.github.proteus1989.airexplorerdecrypter;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DecryptFileNameTest {
    @Test
    public void decryptFileNameTest() throws IOException {
        final String actualName = "my_encrypted_file.txt";
        final String encryptedName = "4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2";
        final String validPass = "my_strong_pass";
        final String wrongPass = "my_wrong_pass";

        assertEquals(actualName, AirExplorerDecrypter.decryptName(encryptedName, validPass));
        assertNotEquals(actualName, AirExplorerDecrypter.decryptName(encryptedName, wrongPass));
    }
}
