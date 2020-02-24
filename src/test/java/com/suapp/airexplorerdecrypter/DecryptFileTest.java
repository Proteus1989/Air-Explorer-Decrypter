package com.suapp.airexplorerdecrypter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Antonio
 */
public class DecryptFileTest
{

    public DecryptFileTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void decryptFileSuccessfullyTest() throws IOException
    {
        BufferedInputStream is = new BufferedInputStream(new URL("https://raw.githubusercontent.com/Proteus1989/Air-Explorer-Decrypter/master/src/main/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2").openStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        final String validPass = "my_strong_pass";

        AirExplorerDecrypterAPI.decrytp(is, os, validPass);
        
        assertTrue(new String(os.toByteArray(), StandardCharsets.UTF_8).equals("Visit https://github.com/Proteus1989"));
    }
    
    @Test
    public void decryptFileErrorTest() throws IOException
    {
        BufferedInputStream is = new BufferedInputStream(new URL("https://raw.githubusercontent.com/Proteus1989/Air-Explorer-Decrypter/master/src/main/resources/4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2").openStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        
        final String wrongPass = "my_wrong_pass";

        try
        {
            AirExplorerDecrypterAPI.decrytp(is, os, wrongPass);
            assertTrue(new String(os.toByteArray(), StandardCharsets.UTF_8).equals("Visit https://github.com/Proteus1989"));
        } catch (IllegalArgumentException e)
        {
            assertEquals("Wrong password", e.getMessage());
        }
    }
}
