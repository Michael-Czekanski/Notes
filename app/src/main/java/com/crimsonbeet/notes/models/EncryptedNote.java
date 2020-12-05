package com.crimsonbeet.notes.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Encrypted note is a class used to store notes safely.
 * It contains:
 * <ul>
 *     <li>id</li>
 *     <li><i>encryptedTitle</i></li>
 *     <li><i>encryptedContent</i></li>
 *     <li><i>salt</i> - used to create <b>encryption key</b></li>
 *     <li><i>encryptedNonce</i> - used to encrypt data.</li>
 * </ul>
 */
public class EncryptedNote implements Parcelable {
    private final int id;
    private final String encryptedTitle;
    private final String encryptedContent;
    /**
     * <b>salt</b> will be used to create key, key will be used to decrypt:
     * <ul>
     *     <li>nonce</li>
     *     <li>note content</li>
     *     <li>note title</li>
     * </ul>
     */
    private final byte[] salt;
    /**
     * <b>nonce</b> will be used to decrypt note content. It is encrypted using AES in ECB mode with usage of key (generated from salt and user password)
     */
    private final byte[] encryptedNonce;

    public EncryptedNote(int id, String encryptedTitle, String encryptedContent, byte[] salt, byte[] encryptedNonce) {
        this.id = id;
        this.encryptedTitle = encryptedTitle;
        this.encryptedContent = encryptedContent;
        this.salt = salt;
        this.encryptedNonce = encryptedNonce;
    }

    protected EncryptedNote(Parcel in) {
        id = in.readInt();
        encryptedTitle = in.readString();
        encryptedContent = in.readString();
        salt = in.createByteArray();
        encryptedNonce = in.createByteArray();
    }

    public static final Creator<EncryptedNote> CREATOR = new Creator<EncryptedNote>() {
        @Override
        public EncryptedNote createFromParcel(Parcel in) {
            return new EncryptedNote(in);
        }

        @Override
        public EncryptedNote[] newArray(int size) {
            return new EncryptedNote[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getEncryptedTitle() {
        return encryptedTitle;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getEncryptedNonce() {
        return encryptedNonce;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(encryptedTitle);
        parcel.writeString(encryptedContent);
        parcel.writeByteArray(salt);
        parcel.writeByteArray(encryptedNonce);
    }
}
