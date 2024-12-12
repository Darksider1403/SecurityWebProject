package Model;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class SignatureDemo {
    public static void main(String[] args) throws Exception {
        // Your keys (stored as strings)
        String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApbtAvbX1QcyayV1hO7NyhBWIjO82ODruMcOhg13pBvU8NwWAukSk8FcT/4SpJ8tZrh+3pzTwfynm4ytpGdBmMhdJLd3ESDRIKROKkp872H/Mj5zO9Y0WLqZPlckMOF+h5E0Ru7beg3HTtOUWLlKuJWFDUDP/ciPpNqndms3ZoY3tvlGyHYF5Zi4flKHD3VCpm3ugrHQsQ7X6OcfdliAzQy2xdSv9FZUY8V3fWI81m7iFbODcZpICmzhXbLMdcsrjHCGU8dE4eShR3rP8GI96uMiXDELSSsq0S9EwmBLiU8Tk8/R7g8wIgQJFqteVPsOIhGUw8qAOfyGmvSj3C6ooEwIDAQAB";
        String privateKeyString = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQClu0C9tfVBzJrJXWE7s3KEFYiM7zY4Ou4xw6GDXekG9Tw3BYC6RKTwVxP/hKkny1muH7enNPB/KebjK2kZ0GYyF0kt3cRINEgpE4qSnzvYf8yPnM71jRYupk+VyQw4X6HkTRG7tt6DcdO05RYuUq4lYUNQM/9yI+k2qd2azdmhje2+UbIdgXlmLh+UocPdUKmbe6CsdCxDtfo5x92WIDNDLbF1K/0VlRjxXd9YjzWbuIVs4NxmkgKbOFdssx1yyuMcIZTx0Th5KFHes/wYj3q4yJcMQtJKyrRL0TCYEuJTxOTz9HuDzAiBAkWq15U+w4iEZTDyoA5/Iaa9KPcLqigTAgMBAAECggEACT/lLMazVHkgiRfZbbqQgUdZDVHrJcJBjwIgc94m5kjY6SfjzQGNxGLSbpKvL4Bi60aWj7zIULXm+Utv8eKMAZmub405VN+jc19wKXS8+Kf7+RFDCmNVwe7C8aonMMeYQnn8AQ0DaDSkddZ0oXXjMETjETPbEDoUMa/TUBN9uk1us9pUQD3l58FN6ZYBK6AYpt7LseFNt/3tfKBgEGqcyfI8jJ+MotX6sbQHHbMqB06CV1dKyC4RmyQfDH6jzbHRnUAGx+VyQlwOxJU9V2OH4WXQAuvMQrcej19Sd7XhbmwaKVbLD9MidhKcZe7YX707MhH7jWzLzdxvyQaZTg5JGQKBgQC1zna0PVw5VByVuokokkbyRSE3uYnefxW7Se5twmHXI09a/B4o8geU311dPZvoUYm5EocLOu+XloARNX+vkAkbrGsAAZ7RsksU98p5dOLz2BOjD0idoaLNJccy3WX4AhnwBrYcFauU1xbwYRUsOZcAHmr6+l7lzKvyVNV8fijdTQKBgQDpXWkuRMfZmzAB5HXfpw5c3a61VKUKsm4TgRyON6RVIg9LzCv/4QTtqScztlT0+ePXHlkzruw5zQDpG/MuT7wErDunZc+QkwyoqkDu3q823GOSoKCVHiESQFcVqdp9//+yc7iMFlJ/ekBEnaMUdGpolBabQ2w1yTCOWObC2m3q3wKBgQCGdfqI91C7/Z9ehaVnYjgiekpFAm4bJnaWG+nBN6BREwZfzlMoCUjeLAzLJwOl4JgxTqid/6qvvFlfYOfcmnSCCRTh3lDt2iV5joIrLe2kVsy/LlCQdLkJbMVUszX1JOi2AuCxCL3ryOBoTATyvm0VY2nP0VhaMPOCuIdkGZJziQKBgE6a20Htgw+mOT558MZb4/ed6CpGD62RuogxVigFUAIpDdPvOubeqaVP89d0VBuMTJV6MHu7MPCrP+JFOt1fmCpOxW02WvsMvRfW+TY8pfIpvBR4rH5t/p/CMlMmumT0pPE8D26IWlN8H0e9fV/3cpw1PYQg6sMXOJbKFTiPELUrAoGAXu2G9IF1je15MJVOEj4JHhiMtVsB4GTwBIyIWGpjW+GxuS/MRifURtFry557JHc1cmDVYJ5sKjRaFeaWezObBy6lJMNh4QEhBU8nP2GKWFcw7iMSYdv2IOgE6+wm3j41jwPpiibBFL8ojALpHfk+fEU9QAyfyzQUIzovAGmP2Tk=";

        // Convert base64 strings to keys
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Convert public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                Base64.getDecoder().decode(publicKeyString));
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // Convert private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(privateKeyString));
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Test data to sign
        String data = "Hello, World!";

        // Create signature
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] signatureBytes = signature.sign();

        // Verify signature
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        boolean verified = signature.verify(signatureBytes);

        // Print results
        System.out.println("Signature: " + Base64.getEncoder().encodeToString(signatureBytes));
        System.out.println("Signature verified: " + verified);
    }
}
