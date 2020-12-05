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

public class SecurityManager {

    private byte[] testSalt;
    private static final int SALT_SIZE = 24;

    /**
     * With a nonce of 96 bits (12 bytes), you can encrypt 2^32 blocks (a block is 128 bit (16 bytes) in size) without repeating the counter.
     * This is because IV is 128 bits (16 bytes) long, so there are 4 bytes left for zeros, which gives us 2^32 blocks ~ 68.GB
     */
    private static final int NONCE_SIZE = 96 / 8;

    public byte[] getTestNonce() {
        return testNonce;
    }


    private byte[] testNonce;

    private static final String cipherTransformation = "AES/CTR/NoPadding";


    /**
     * Generuje salt używaną do generowania klucza i noncję używaną do szyfrowania
     */
    public void mockFirstLaunch() {
        testSalt = generateSalt(SALT_SIZE);
        testNonce = generateNonce(NONCE_SIZE);
    }

    /**
     * Generates secure random salt.
     *
     * @param saltSize Salt size.
     * @return Secure random salt.
     */
    private byte[] generateSalt(int saltSize) {
        byte[] salt = new SecureRandom().generateSeed(saltSize);
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
    private byte[] generateNonce(int nonceSize) {
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
    public SecretKey mockGenKey(String passphrase, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final int iterations = 4096;
        final int keyLen = 256;
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, iterations, keyLen);

        SecretKey tmp = secretKeyFactory.generateSecret(keySpec);

        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    public byte[] getTestSalt() {
        return testSalt;
    }

    /**
     * Encrypts user password using AES in CTR mode of operation.
     *
     * @param password password to encrypt
     * @param nonce    is used to encrypt
     * @param key      should be generated from user password and salt
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] mockEncryptPassword2CTR(String password, byte[] nonce, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(cipherTransformation);

        byte[] iv = createIVWithNonce(nonce);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

        return cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decrypts user password which was encrypted using AES in CTR mode of operation.
     *
     * @param encryptedPassword password to decrypt
     * @param nonce             is used to decrypt
     * @param key               should be generated from user password and salt
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     */
    public String mockDecryptPassword2CTR(byte[] encryptedPassword, byte[] nonce, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(cipherTransformation);

        byte[] iv = createIVWithNonce(nonce);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

        return new String(cipher.doFinal(encryptedPassword));
    }


    /**
     * Szyfruje zawartosc notatki. Generuje nowe nonce oraz sól.
     *
     * @param note Notatka do zaszyfrowania
     * @return Zwraca notatkę z zaszyfrowaną wiadomością
     * @see EncryptedNote
     */
    public EncryptedNote mockEncryptNote(Note note, Key key) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        byte[] salt = generateSalt(SALT_SIZE);
        byte[] nonce = generateNonce(NONCE_SIZE);

        byte[] encryptedNonce = mockEncryptNonce(nonce, key);

        String encryptedTitle = new String(mockEncryptPassword2CTR(note.getContent(), nonce, key));
        String encryptedContent = new String(mockEncryptPassword2CTR(note.getTitle(), nonce, key));

        return new EncryptedNote(note.getId(), encryptedTitle,
                encryptedContent, salt, encryptedNonce);
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
    public byte[] mockEncryptNonce(byte[] nonce, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
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
    public byte[] mockDecryptNonce(byte[] encryptedNonce, Key key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(encryptedNonce);
    }


}
