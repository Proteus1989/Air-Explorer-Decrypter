package com.suapp.airexplorerdecrypter;

import java.io.IOException;
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
public class DecryptFileNameTest
{
    
    public DecryptFileNameTest()
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
     public void decryptFileNameTest() throws IOException 
     {
         final String actualName = "my_encrypted_file.txt";
         final String encryptedName = "4aad7bBqwQGCr7kbatwHRClR2lPF8sSQQAXgJ2YR1wk=.cloudencoded2";
         final String validPass = "my_strong_pass";
         final String wrongPass = "my_wrong_pass";
         
         assertTrue(AirExplorerDecrypterAPI.decryptName(encryptedName, validPass).equals(actualName));
         assertFalse(AirExplorerDecrypterAPI.decryptName(encryptedName, wrongPass).equals(actualName));
     }
}
