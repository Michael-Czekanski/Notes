package com.crimsonbeet.notes.utils;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SecurityManagerTest {

    private final SecurityManager securityManager = new SecurityManager();
    private final String password = "password";


    @Before
    public void setUp() {
        securityManager.mockFirstLaunch();
    }

    @Test
    public void mockEncryptPassword2CTR() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        byte[] salt = securityManager.getTestSalt();
        byte[] nonce = securityManager.getTestNonce();

        Key key = securityManager.mockGenKey(password, salt);

        assertArrayEquals(securityManager.mockEncryptPassword2CTR(password, nonce, key), securityManager.mockEncryptPassword2CTR(password, nonce, key));
    }

    @Test
    public void mockDecryptPassword2CTR() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        byte[] salt = securityManager.getTestSalt();
        byte[] nonce = securityManager.getTestNonce();

        Key key = securityManager.mockGenKey(password, salt);

        byte[] encryptedPassword = securityManager.mockEncryptPassword2CTR(password, nonce, key);
        String decryptedPassword = securityManager.mockDecryptPassword2CTR(encryptedPassword, nonce, key);

        assertEquals(password, decryptedPassword);
    }

    @Test
    public void mockEncryptNonce() throws Exception {
        byte[] salt = securityManager.getTestSalt();
        byte[] nonce = securityManager.getTestNonce();
        Key key = securityManager.mockGenKey(password, salt);

        assertArrayEquals(securityManager.mockEncryptNonce(nonce, key), securityManager.mockEncryptNonce(nonce, key));
    }

    @Test
    public void mockDecryptNonce() throws Exception {
        byte[] salt = securityManager.getTestSalt();
        byte[] nonce = securityManager.getTestNonce();
        Key key = securityManager.mockGenKey(password, salt);

        byte[] encryptedNonce = securityManager.mockEncryptNonce(nonce, key);
        byte[] decryptedNonce = securityManager.mockDecryptNonce(encryptedNonce, key);

        assertArrayEquals(nonce, decryptedNonce);
    }
}