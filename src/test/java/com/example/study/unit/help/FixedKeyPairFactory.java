package com.example.study.unit.help;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class FixedKeyPairFactory {

    public static KeyPair loadFixedKeyPair() {
        try {
            String privateKeyPem = """
                -----BEGIN PRIVATE KEY-----
                MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC5rxW4XZgPkwOwM08IFDYWARC+vuAoL0KiRtFvQ3ugSJggTXNSYZZgBDz
                ymbsOPAPSvcOfgAB1kXTkfXjtCURTS0QHYxi72cAtrXCN1CETSRgjNwG8J+S1tmA218cTZipOJVYk1Xhi3KsX6TU4Gr0XPnJB0yhqkRjUcj
                r0CISNlItA4EKXGHa19cGpAvKAFFPEoj6sAFG4i3/5rlV65wkbzL8WfvEqSvLXq7G8h6jE9V3BnpwselM77wi4bWGLQ4q0ifFDC4CZPAx46
                9ErlZQpsHzXzjwnK5PSMf63NQ7628pP7kJQaAKg6uf3NgN7mPRKW4phfF3ol15LVnnfZ4CpAgMBAAECggEAAkGIkcYfVhca7R3eeLJoKtHF
                /o9KoWpblKWoH0IyYxLmuEPAojBZ155NkEQCZAyiHkIceZhgSQ1knm8Q1E5vxFROhglz8DpXzaZFDxAT+8xATRM6w+q3KmcGGbx66HcuDee
                Bua5XSVo1gPOeKCmZAQqK8FZMsCjFpLUjlrnwxFiToLwqfLmHIkDjQKLrAQv2ElTTzttNxXwzGuYcB1BGRN9R/lU1URgeUD+0mNFO25yf6O
                zmaIuxYAQABbxgCI03W4RUWiu4hQ7+Ygc8INzIiqlgtThyQ6nFGJWyVD7XGB04wduE8dk68RAHVINJKjsU2Ngt3bKgaUJYljZgoYR3QQKBg
                QDT7+2fpgbsv3NFUnAiaDWdc70VAZX/5wtGoNQ8TFieKGvuc98UQJEKm7tT0euFHVRXZaStOUfLIWaLZtNdXu79Y+XU6uf96llkLSneyj42
                7PSJMTqyvCJXBiE44lywZxrPNbgghzBl71j0uhZGmr+YsTvQlviumNXX3FP9823GCQKBgQDgSdxM+UMYs7WvDFS3gmFd40sySYZ9nHYl3T5
                ukRTH+tMS0QRyUQf7kTZsssdTWA/G7Ql/4wJUwZiiDjLObLZRwuBvdCqu4YS3PSvp1DphCFq84ezXG3O71tKCLD2dht9TAlS9Hcw3Po9+Mm
                WHLmnuOqw8rxu4/lT8U9qWUbONoQKBgQDQEcyToTdJNQhhNXcbgH2qsBchSWZPTzB1za2v6TBTTdHwz9diFaWP6aZ2uy/jhxDnoMWUvxip5
                4e6MmAuWeZ95Jaee2T5lxrdy61AqgN8fxPwoOFE2olIMJ9rLmjbUs4yQkYbwT2Vxe7qjMsT1BdoPBBCUAWjvC6tdwUK2/OYqQKBgGg5KkwC
                kHFMISMkwcE8ru4oDuthS0PXjvdtZfOPK276WVcnFb/hc6otAnNkaEeMZlh0EbQB0zV7eOflXSqbTeWUfyKLgsIdPcTAgkV4P2CTt0aKXbp
                foVoqK0weLsuivugd7y6qSBlr0tY7qP42TTdMwHi6jlFarjdK6ApuENfBAoGAfPbQ2JdpoVxw+mya9BMqOwWc9c6f+zXctxRUiIzWJmjTCX
                MosPfZR+nAGySHsqy8WHXAcm2+HijkGtxPFZf4bqFrMuV0NLYIAi6ED77/F0LmtkgLWrZlGgNMY8x0PKXVevSnVdvwkfmIKTOB8y42PY6Rb
                CxRwpA5QVqyx/4t7gI=
                -----END PRIVATE KEY-----
                """;

            String publicKeyPem = """
                -----BEGIN PUBLIC KEY-----
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAua8VuF2YD5MDsDNPCBQ2FgEQvr7gKC9CokbRb0N7oEiYIE1zUmGWYAQ88pm7Djw
                D0r3Dn4AAdZF05H147QlEU0tEB2MYu9nALa1wjdQhE0kYIzcBvCfktbZgNtfHE2YqTiVWJNV4YtyrF+k1OBq9Fz5yQdMoapEY1HI69AiEjZ
                SLQOBClxh2tfXBqQLygBRTxKI+rABRuIt/+a5VeucJG8y/Fn7xKkry16uxvIeoxPVdwZ6cLHpTO+8IuG1hi0OKtInxQwuAmTwMeOvRK5WUK
                bB81848JyuT0jH+tzUO+tvKT+5CUGgCoOrn9zYDe5j0SluKYXxd6JdeS1Z532eAqQIDAQAB
                -----END PUBLIC KEY-----
                """;

            // 1) PEM 헤더 제거 + Base64 디코딩
            PrivateKey privateKey = convertPemToPrivateKey(privateKeyPem);
            PublicKey publicKey = convertPemToPublicKey(publicKeyPem);

            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("RSA 고정 키 로딩 실패", e);
        }
    }

    private static PrivateKey convertPemToPrivateKey(String pem) throws Exception {
        String clean = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(clean);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    private static PublicKey convertPemToPublicKey(String pem) throws Exception {
        String clean = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(clean);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
}
