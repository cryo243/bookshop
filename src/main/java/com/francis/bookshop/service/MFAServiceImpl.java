package com.francis.bookshop.service;


import com.francis.bookshop.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Base64;

@Service
public class MFAServiceImpl  implements MFAService {
    @Value("app.name")
    private String appName;

    private static final int TIME_STEP_SECONDS = 30;
    private static final int TOTP_DIGITS = 6;
    private static final String HMAC_ALGO = "HmacSHA1";

    public static boolean verifyCode(String base32Secret, int code) {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        long t = currentTimeSeconds / TIME_STEP_SECONDS;
        // allow a small time window for drift
        for (int i = -1; i <= 1; i++) {
            long counter = t + i;
            int generated = generateTOTP(base32Secret, counter);
            if (generated == code) return true;
        }
        return false;
    }

    private static int generateTOTP(String base32Secret, long counter) {
        try {
            byte[] key = Base64.getDecoder().decode(base32Secret);
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(counter);
            byte[] counterBytes = buffer.array();

            Mac mac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(key, HMAC_ALGO);
            mac.init(keySpec);

            byte[] hash = mac.doFinal(counterBytes);

            int offset = hash[hash.length - 1] & 0xF;
            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            return binary % (int) Math.pow(10, TOTP_DIGITS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String provisioningUri(UserDto user) {
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                appName,
                user.getUsername(),
                user.getSecret(),
                appName
        );
    }
}
