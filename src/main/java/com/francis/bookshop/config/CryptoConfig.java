package com.francis.bookshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {

  public static String AES_KEY_B64;

  public CryptoConfig(@Value("${aes.key-base64}") String keyB64) {
    AES_KEY_B64 = keyB64;
  }
}
