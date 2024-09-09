package kr.okku.server.dto.oauth;
public record ApplePublicKey(String kty,
                             String kid,
                             String alg,
                             String n,
                             String e) {
}