package com.francis.bookshop.utility;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;


@Converter()
public class AesEncryptingConverter implements AttributeConverter<String, String> {

    private final SecretKey key;

    public AesEncryptingConverter(@Value("${aes.key-base64}") String keyB64) {
        byte[] k = Base64.getDecoder().decode(keyB64);
        if (k.length != 32) {
            // I am using AES-256 and it requires a 32-byte key
            throw new IllegalArgumentException("Invalid AES key length, must be 32 bytes");
        }
        this.key = new SecretKeySpec(k, "AES");
    }

//    public AesEncryptingConverter(@Value("${aes.key-base64}") String keyB64) {
//        byte[] k = Base64.getDecoder().decode(keyB64);
//        if (k.length != 32) {
//            // I am using AES-256 and it requires a 32-byte key
//            throw new IllegalArgumentException("Invalid AES key length, must be 32 bytes");
//        }
//        this.key = new SecretKeySpec(k, "AES");
//    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            byte[] iv = new byte[12]; // GCM standard IV length
            SecureRandom.getInstanceStrong().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] cipherText = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Error during AES encryption", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            byte[] decoded = Base64.getDecoder().decode(dbData);
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);

            int ivLength = byteBuffer.getInt();
            if (ivLength < 12 || ivLength >= 16) {
                throw new IllegalArgumentException("Invalid IV length in stored data");
            }
            byte[] iv = new byte[ivLength];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Error during AES decryption", e);
        }
    }
}
