package org.easywork.blockchain;

import lombok.Data;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
@Data
public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String blockchainAddress;

    public Wallet() {
        generateKeyPair();
        blockchainAddress = Utils.generateBlockchainAddress(publicKey);
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1"); // P-256 椭圆曲线
            keyGen.initialize(ecSpec, new SecureRandom());
            KeyPair keyPair = keyGen.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String sign(String data) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String data, String signatureStr, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes());
            byte[] sigBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(sigBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void print() {
        System.out.println("📬 Wallet Address: " + blockchainAddress);
        System.out.println("🔑 Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
    }
}