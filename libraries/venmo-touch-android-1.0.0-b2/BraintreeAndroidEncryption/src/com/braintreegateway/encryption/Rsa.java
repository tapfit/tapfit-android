package com.braintreegateway.encryption;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.braintree.org.bouncycastle.asn1.ASN1InputStream;
import com.braintree.org.bouncycastle.asn1.DERObject;
import com.braintree.org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import com.braintree.org.bouncycastle.util.encoders.Base64;

public class Rsa {
    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final String PROVIDER = "BC";
    private String publicKeyString;

    public Rsa(String publicKeyString) {
        this.publicKeyString = publicKeyString;
    }

    public String encrypt(byte[] data) {
        Cipher rsa;
        try {
            rsa = Cipher.getInstance(TRANSFORMATION, PROVIDER);
            PublicKey publicKey = publicKey();
            rsa.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encodedData = Base64.encode(data);
            byte[] encryptedData = rsa.doFinal(encodedData);
            return new String(Base64.encode(encryptedData));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PublicKey publicKey() {
        byte[] decodedPublicKey = Base64.decode(publicKeyString);
        ASN1InputStream in = new ASN1InputStream(decodedPublicKey);
        try {
            DERObject obj = in.readObject();
            RSAPublicKeyStructure keyStruct = RSAPublicKeyStructure.getInstance(obj);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(keyStruct.getModulus(), keyStruct.getPublicExponent());
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, PROVIDER);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // Ignore, close quietly.
            }
        }
        return null;
    }
}
