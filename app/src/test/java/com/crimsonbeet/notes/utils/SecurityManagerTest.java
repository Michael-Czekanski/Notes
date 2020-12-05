package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.EncryptedNote;
import com.crimsonbeet.notes.models.Note;

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

    @Test
    public void genKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] salt = securityManager.generateSalt();
        securityManager.genKey(password, salt);
    }

    @Test
    public void encryptString() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        byte[] nonce = securityManager.generateNonce(12);
        byte[] salt = securityManager.generateSalt();

        Key key = securityManager.genKey(password, salt);

        securityManager.encryptString(password, nonce, key);
    }

    @Test
    public void decryptString() throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        byte[] nonce = securityManager.generateNonce(12);
        byte[] salt = securityManager.generateSalt();

        Key key = securityManager.genKey(password, salt);

        byte[] encryptedPassword = securityManager.encryptString(password, nonce, key);
        String decryptedPassword = securityManager.decryptString(encryptedPassword, nonce, key);

        assertEquals(password, decryptedPassword);
    }

    @Test
    public void encryptNote() throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        Note note = new Note(1, "title", "123");

        EncryptedNote encryptedNote = securityManager.encryptNote(note, password);
    }

    @Test
    public void decryptNote() throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        Note note = new Note(1, "title", "123");

        EncryptedNote encryptedNote = securityManager.encryptNote(note, password);
        Note decryptedNote = securityManager.decryptNote(encryptedNote, password);

        assertEquals(note.getTitle(), decryptedNote.getTitle());
        assertEquals(note.getContent(), decryptedNote.getContent());
    }

    @Test
    public void encryptNonce() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        byte[] salt = securityManager.generateSalt();
        Key key = securityManager.genKey(password, salt);
        byte[] nonce = securityManager.generateNonce(12);

        securityManager.encryptNonce(nonce, key);
    }

    @Test
    public void decryptNonce() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
        byte[] salt = securityManager.generateSalt();
        Key key = securityManager.genKey(password, salt);
        byte[] nonce = securityManager.generateNonce(12);

        byte[] encryptedNonce = securityManager.encryptNonce(nonce, key);

        byte[] decryptedNonce = securityManager.decryptNonce(encryptedNonce, key);

        assertArrayEquals(nonce, decryptedNonce);
    }
}