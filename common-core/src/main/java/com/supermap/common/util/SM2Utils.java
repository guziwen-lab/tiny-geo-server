package com.supermap.common.util;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * @author gzw
 */
public class SM2Utils {

    public static void main(String[] args) {
        String[] strings = SM2Utils.generateKeyPair();
        System.err.println(strings[0]);
        System.err.println(strings[1]);
    }

    /**
     * 生成SM2秘钥对
     * String[0] 公钥
     * String[1] 私钥
     */
    public static String[] generateKeyPair() {
        return SM2.generateKeyPair();
    }

    /**
     * SM2签名
     * data 签名的数据
     * priKey 私钥
     */
    public static String sign(String data, String priKey) {
        SM2 sm2 = new SM2(priKey, null);
        return sm2.sign(data);
    }

    /**
     * SM2签名
     * sign 源数据
     * pubKey 公钥
     * sign 签名的数据
     */
    public static boolean verifySign(String msg, String pubKey, String sign) {
        SM2 sm2 = new SM2(null, pubKey);
        return sm2.verifySign(msg, sign);
    }

    /**
     * 加密
     * 公钥加密
     * plainText 要加密的文本
     * pubKey 公钥
     */
    public static String encrypt(String plainText, String pubKey) {
        SM2 sm2 = new SM2(null, pubKey);
        byte[] encryptByte = sm2.encrypt(plainText.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(encryptByte).toUpperCase();
    }

    /**
     * 解密
     * 私钥解密
     * plainText 要加密的文本
     * pubKey 公钥
     */
    public static String decrypt(String plainText, String priKey) {
        SM2 sm2 = new SM2(priKey, null);
        byte[] deCode = Hex.decode(plainText);
        byte[] decryptText = sm2.decrypt(deCode);
        return new String(decryptText, StandardCharsets.UTF_8);
    }

    private static class SM2 {

        private ECPrivateKeyParameters privateKeyParameters;

        private ECPublicKeyParameters publicKeyParameters;

        private static final X9ECParameters x9ECParameters = GMNamedCurves.getByName("sm2p256v1");

        public SM2(String priKey, String pubKey) {
            this.init(isEmpty(priKey) ? null : Hex.decode(priKey), isEmpty(pubKey) ? null : Hex.decode(pubKey));
        }

        private void init(byte[] priKey, byte[] pubKey) {
            SM2Param sm2Param = new SM2Param();
            if (null != priKey && this.privateKeyParameters == null) {
                this.privateKeyParameters = new ECPrivateKeyParameters(new BigInteger(1, priKey), sm2Param.ecc_bc_spec);
            }
            if (null != pubKey && this.publicKeyParameters == null) {
                this.publicKeyParameters = new ECPublicKeyParameters(sm2Param.ecc_curve.decodePoint(pubKey), sm2Param.ecc_bc_spec);
            }
        }

        /**
         * 加签
         */
        public String sign(String data) {
            byte[] msg = Strings.toByteArray(data);
            SM2Signer sm2Signer = new SM2Signer();
            sm2Signer.init(true, this.privateKeyParameters);
            sm2Signer.update(msg, 0, msg.length);
            try {
                return Hex.toHexString(sm2Signer.generateSignature());
            } catch (CryptoException e) {
                throw new RuntimeException(e);
            }
        }

        /*
         * 验签
         */
        public boolean verifySign(String data, String sign) {
            byte[] signHex = Hex.decode(sign);
            byte[] msgByte = Strings.toByteArray(data);
            SM2Signer sm2Signer = new SM2Signer();
            sm2Signer.init(false, this.publicKeyParameters);
            sm2Signer.update(msgByte, 0, msgByte.length);
            return sm2Signer.verifySignature(signHex);
        }

        /**
         * 加密
         */
        public byte[] encrypt(byte[] plainText) {
            SM2Engine engine = new SM2Engine();
            engine.init(true, new ParametersWithRandom(this.publicKeyParameters));
            try {
                return changeC1C2C3ToC1C3C2(engine.processBlock(plainText, 0, plainText.length));
            } catch (InvalidCipherTextException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * bc加解密使用旧标c1||c2||c3，此方法在加密后调用，将结果转化为c1||c3||c2
         */
        private static byte[] changeC1C2C3ToC1C3C2(byte[] c1c2c3) {
            int c1Len = (x9ECParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1; //sm2p256v1的这个固定65。可看GMNamedCurves、ECCurve代码。
            int c3Len = 32; //new SM3Digest().getDigestSize();
            byte[] result = new byte[c1c2c3.length];
            System.arraycopy(c1c2c3, 0, result, 0, c1Len); //c1
            System.arraycopy(c1c2c3, c1c2c3.length - c3Len, result, c1Len, c3Len); //c3
            System.arraycopy(c1c2c3, c1Len, result, c1Len + c3Len, c1c2c3.length - c1Len - c3Len); //c2
            return result;
        }

        /**
         * 解密
         */
        public byte[] decrypt(byte[] plainText) {
            byte[] plain = changeC1C3C2ToC1C2C3(plainText);
            SM2Engine engine = new SM2Engine();
            engine.init(false, this.privateKeyParameters);
            try {
                return engine.processBlock(plain, 0, plain.length);
            } catch (InvalidCipherTextException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * bc加解密使用旧标c1||c3||c2，此方法在解密前调用，将密文转化为c1||c2||c3再去解密
         */
        private static byte[] changeC1C3C2ToC1C2C3(byte[] c1c3c2) {
            int c1Len = (x9ECParameters.getCurve().getFieldSize() + 7) / 8 * 2 + 1; //sm2p256v1的这个固定65。可看GMNamedCurves、ECCurve代码。
            int c3Len = 32; //new SM3Digest().GetDigestSize();
            byte[] result = new byte[c1c3c2.length];
            System.arraycopy(c1c3c2, 0, result, 0, c1Len); //c1: 0->65
            System.arraycopy(c1c3c2, c1Len + c3Len, result, c1Len, c1c3c2.length - c1Len - c3Len); //c2
            System.arraycopy(c1c3c2, c1Len, result, c1c3c2.length - c3Len, c3Len); //c3
            return result;
        }

        public static String[] generateKeyPair() {
            AsymmetricCipherKeyPair kPair = genCipherKeyPair();
            ECPrivateKeyParameters ecPrivateKey = (ECPrivateKeyParameters) kPair.getPrivate();
            ECPublicKeyParameters ecPublicKey = (ECPublicKeyParameters) kPair.getPublic();
            BigInteger priKey = ecPrivateKey.getD();
            ECPoint pubKey = ecPublicKey.getQ();
            byte[] priByte = priKey.toByteArray();
            byte[] pubByte = pubKey.getEncoded(false);
            if (priByte.length == 33) {
                byte[] newPriByte = new byte[32];
                System.arraycopy(priByte, 1, newPriByte, 0, 32);
                priByte = newPriByte;
            }
            return new String[]{Hex.toHexString(pubByte), Hex.toHexString(priByte)};
        }

        /**
         * 生成引用
         */
        private static AsymmetricCipherKeyPair genCipherKeyPair() {
            SM2Param ecc_param = new SM2Param();
            ECDomainParameters ecDomainParameters = ecc_param.ecc_bc_spec;
            ECKeyGenerationParameters ecGenParam = new ECKeyGenerationParameters(ecDomainParameters, new SecureRandom());

            ECKeyPairGenerator ecKeyPairGenerator = new ECKeyPairGenerator();
            ecKeyPairGenerator.init(ecGenParam);
            return ecKeyPairGenerator.generateKeyPair();
        }

        private static boolean isEmpty(String str) {
            return str == null || str.isEmpty();
        }

        private static class SM2Param {

            public final String[] ecc_param = {
                    "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF",
                    "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC",
                    "28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93",
                    "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123",
                    "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7",
                    "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"};
            public final BigInteger ecc_p;
            public final BigInteger ecc_a;
            public final BigInteger ecc_b;
            public final BigInteger ecc_n;
            public final BigInteger ecc_gx;
            public final BigInteger ecc_gy;
            public final ECCurve ecc_curve;
            public final ECDomainParameters ecc_bc_spec;

            public SM2Param() {
                this.ecc_p = new BigInteger(ecc_param[0], 16);
                this.ecc_a = new BigInteger(ecc_param[1], 16);
                this.ecc_b = new BigInteger(ecc_param[2], 16);
                this.ecc_n = new BigInteger(ecc_param[3], 16);
                this.ecc_gx = new BigInteger(ecc_param[4], 16);
                this.ecc_gy = new BigInteger(ecc_param[5], 16);
                this.ecc_curve = new ECCurve.Fp(ecc_p, ecc_a, ecc_b, ecc_n, BigInteger.ONE);
                this.ecc_bc_spec = new ECDomainParameters(this.ecc_curve, this.ecc_curve.createPoint(this.ecc_gx, this.ecc_gy), this.ecc_n);
            }

        }

    }


}
