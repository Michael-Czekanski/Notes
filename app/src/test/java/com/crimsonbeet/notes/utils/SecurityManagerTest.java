package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.EncryptedNote;
import com.crimsonbeet.notes.models.Note;

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
        byte[] salt = securityManager.getPasswordSalt();
        byte[] nonce = securityManager.getPasswordNonce();

        Key key = securityManager.mockGenKey(password, salt);

        assertArrayEquals(securityManager.mockEncryptPassword2CTR(password, nonce, key), securityManager.mockEncryptPassword2CTR(password, nonce, key));
    }

    @Test
    public void mockDecryptPassword2CTR() throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        byte[] salt = securityManager.getPasswordSalt();
        byte[] nonce = securityManager.getPasswordNonce();

        Key key = securityManager.mockGenKey(password, salt);

        byte[] encryptedPassword = securityManager.mockEncryptPassword2CTR(password, nonce, key);
        String decryptedPassword = securityManager.mockDecryptPassword2CTR(encryptedPassword, nonce, key);

        assertEquals(password, decryptedPassword);
    }

    @Test
    public void mockEncryptNonce() throws Exception {
        byte[] salt = securityManager.getPasswordSalt();
        byte[] nonce = securityManager.getPasswordNonce();
        Key key = securityManager.mockGenKey(password, salt);

        assertArrayEquals(securityManager.mockEncryptNonce(nonce, key), securityManager.mockEncryptNonce(nonce, key));
    }

    @Test
    public void mockDecryptNonce() throws Exception {
        byte[] salt = securityManager.getPasswordSalt();
        byte[] nonce = securityManager.getPasswordNonce();
        Key key = securityManager.mockGenKey(password, salt);

        byte[] encryptedNonce = securityManager.mockEncryptNonce(nonce, key);
        byte[] decryptedNonce = securityManager.mockDecryptNonce(encryptedNonce, key);

        assertArrayEquals(nonce, decryptedNonce);
    }

    @Test
    public void mockEncryptNote() throws Exception {
        Note note = new Note(1, "title", "123");

        EncryptedNote encryptedNote = securityManager.mockEncryptNote(note, password);

        Key key = securityManager.mockGenKey(password, encryptedNote.getSalt());

        byte[] decryptedNonce = securityManager.mockDecryptNonce(encryptedNote.getEncryptedNonce(), key);
        String decryptedTitle = securityManager.mockDecryptPassword2CTR(encryptedNote.getEncryptedTitle(), decryptedNonce, key);
        String decryptedContent = securityManager.mockDecryptPassword2CTR(encryptedNote.getEncryptedContent(), decryptedNonce, key);

        assertEquals(note.getContent(), decryptedContent);
        assertEquals(note.getTitle(), decryptedTitle);
    }

    @Test
    public void mockDecryptNote() throws BadPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        Note note = new Note(1, "title", "123");

        EncryptedNote encryptedNote = securityManager.mockEncryptNote(note, password);

        Note decryptedNote = securityManager.mockDecryptNote(encryptedNote, password);

        assertEquals(note.getId(), decryptedNote.getId());
        assertEquals(note.getTitle(), decryptedNote.getTitle());
        assertEquals(note.getContent(), decryptedNote.getContent());
    }
}