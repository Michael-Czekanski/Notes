package com.crimsonbeet.notes.utils;

import com.crimsonbeet.notes.models.EncryptedNote;
import com.crimsonbeet.notes.models.Note;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * SecurityManager manages security like encrypting password and notes.
 */
public class SecurityManager {
    private static final int SALT_SIZE = 24;

    /**
     * With a nonce of 96 bits (12 bytes), you can encrypt 2^32 blocks (a block is 128 bit (16 bytes) in size) without repeating the counter.
     * This is because IV is 128 bits (16 bytes) long, so there are 4 bytes left for zeros, which gives us 2^32 blocks ~ 68.GB
     */
    private static final int NONCE_SIZE = 96 / 8;

    private static final String cipherTransformation = "AES/CTR/NoPadding";

    /**
     * Generates secure random salt of size 24 bytes.
     *
     * @return Secure random salt.
     */
    public byte[] generateSalt() {
        byte[] salt = new SecureRandom().generateSeed(SALT_SIZE);
        return salt;
    }

    /**
     * Generates secure random nonce.
     *
     * @param nonceSize Recommended size is 12 bytes, because IV is 16 bytes size ->
     *                  which gives us 4 bytes for counter -> which gives us possibility to
     *                  encrypt 2^32 blocks of data without repeating the counter ~ 68.GB <br>
     *                  <b>It shouldn't be more than 15 bytes</b>
     * @return
     */
    public byte[] generateNonce(int nonceSize) {
        byte[] nonce = new byte[nonceSize];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    /**
     * Creates IV with nonce -> [nonce - 00000...0]
     *
     * @param nonce
     * @return Created IV.
     */
    private byte[] createIVWithNonce(byte[] nonce) {
        byte[] iv = new byte[128 / 8];
        System.arraycopy(nonce, 0, iv, 0, nonce.length);
        return iv;
    }

    /**
     * Generates key used to encrypt and decrypt data. Key is generated using PBKDF2 with HmacSHA1. <br>
     * keyLen is 256 bits <br>
     * iterations 4096 <br>
     * It uses user password and salt. <br>
     *
     * @param passphrase
     * @param salt
     * @return generated key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public SecretKey genKey(String passphrase, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final int iterations = 4096;
        final int keyLen = 256;
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, keyLen);

        SecretKey tmp = secretKeyFactory.generateSecret(keySpec);

        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * Encrypts user password using AES in CTR mode of operation.
     *
     * @param string String to encrypt
     * @param nonce  Is used to encrypt
     * @param key    Should be generated from user password and salt
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] encryptString(String string, byte[] nonce, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);

        byte[] iv = createIVWithNonce(nonce);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        return cipher.doFinal(string.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decrypts user password which was encrypted using AES in CTR mode of operation.
     *
     * @param encryptedString String to decrypt.
     * @param nonce           Is used to decrypt.
     * @param key             Should be generated from user password and salt.
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     */
    public String decryptString(byte[] encryptedString, byte[] nonce, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);

        byte[] iv = createIVWithNonce(nonce);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        return new String(cipher.doFinal(encryptedString));
    }

    /**
     * Encrypts note.
     *
     * @param note         Note to encrypt.
     * @param userPassword Used to generate key to encrypt note.
     * @return Encrypted note.
     * @see EncryptedNote
     */
    public EncryptedNote encryptNote(Note note, String userPassword) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        byte[] salt = generateSalt();
        Key key = genKey(userPassword, salt);

        byte[] nonce = generateNonce(NONCE_SIZE);
        byte[] encryptedNonce = encryptNonce(nonce, key);
        byte[] encryptedTitle = encryptString(note.getTitle(), nonce, key);
        byte[] encryptedContent = encryptString(note.getContent(), nonce, key);

        return new EncryptedNote(note.getId(), encryptedTitle,
                encryptedContent, salt, encryptedNonce);
    }

    /**
     * Decrypts encrypted note.
     *
     * @param encryptedNote Note to decrypt.
     * @param userPassword  Used to create key to decrypt.
     * @return Decrypted note.
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     */
    public Note decryptNote(EncryptedNote encryptedNote, String userPassword) throws InvalidKeySpecException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        Key key = genKey(userPassword, encryptedNote.getSalt());

        byte[] decryptedNonce = decryptNonce(encryptedNote.getEncryptedNonce(), key);

        String decryptedTitle = decryptString(encryptedNote.getEncryptedTitle(), decryptedNonce, key);
        String decryptedContent = decryptString(encryptedNote.getEncryptedContent(), decryptedNonce, key);

        return new Note(encryptedNote.getId(), decryptedTitle, decryptedContent);
    }

    /**
     * Encrypts nonce using AES in ECB mode. <br>
     * ECB is unsafe unless you use it to encrypt one block of data, nonce fits in one block of data.
     * (nonce is 12 bytes, one block is 16 bytes)
     *
     * @param nonce Nonce to encrypt.
     * @param key   Key used to encrypt.
     * @return Encrypted nonce.
     */
    public byte[] encryptNonce(byte[] nonce, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(nonce);
    }

    /**
     * Decrypts nonce using AES in ECB mode. <br>
     * ECB is unsafe unless you use it to encrypt one block of data, nonce fits in one block of data.
     * (nonce is 12 bytes, one block is 16 bytes)
     *
     * @param encryptedNonce Encrypted nonce to decrypt.
     * @param key            Key used to decrypt.
     * @return Decrypted nonce.
     */
    public byte[] decryptNonce(byte[] encryptedNonce, Key key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(encryptedNonce);
    }
}
