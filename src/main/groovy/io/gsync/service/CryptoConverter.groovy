package io.gsync.service

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.persistence.AttributeConverter
import javax.persistence.Converter
import java.security.GeneralSecurityException
import java.security.Key

@Converter
class CryptoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] KEY = System.getenv("GSYNC_KEY")?.bytes ?: "NsA04KJDN8a6sdj2".bytes;

    @Override
    public String convertToDatabaseColumn(String data) {
        encrypt(data);
    }

    @Override
    public String convertToEntityAttribute(String encryptedData) {
        decrypt(encryptedData)
    }

    private String encrypt(String text) throws GeneralSecurityException {
        Key key = new SecretKeySpec(KEY, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        Base64.encoder.encodeToString(cipher.doFinal(text.getBytes()));
    }

    private String decrypt(String encryptedText) throws GeneralSecurityException {
        Key key = new SecretKeySpec(KEY, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        new String(cipher.doFinal(Base64.decoder.decode(encryptedText)));
    }
}