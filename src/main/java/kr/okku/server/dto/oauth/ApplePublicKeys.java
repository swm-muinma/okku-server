package kr.okku.server.dto.oauth;

import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;

import java.util.List;

public record ApplePublicKeys(
        List<ApplePublicKey> keys

) {
    public ApplePublicKeys {
        keys = List.copyOf(keys);
    }

    public ApplePublicKey getMatchingKey(final String alg, final String kid) {
        return keys.stream()
                .filter(key -> key.isSameAlg(alg) && key.isSameKid(kid))
                .findFirst()
                .orElseThrow(() -> new ErrorDomain(ErrorCode.APPLE_LOGIN_FAILED));
    }
}