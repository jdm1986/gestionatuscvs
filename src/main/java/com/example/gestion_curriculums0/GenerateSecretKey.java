package com.example.gestion_curriculums0;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Base64;

public class GenerateSecretKey {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Genera una clave para HS256
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded()); // Convierte la clave a Base64
        System.out.println(base64Key); // Imprime la clave en Base64
    }
}
