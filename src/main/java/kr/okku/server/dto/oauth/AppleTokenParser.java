package kr.okku.server.dto.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleTokenParser {

    private static final String ID_TOKEN_SEPARATOR = "\\.";
    private static final int HEADER_INDEX = 0;

    private final ObjectMapper objectMapper;

    public Map<String, String> parseHeader(String idToken) {
        try {
            final String encodedHeader = idToken.split(ID_TOKEN_SEPARATOR)[HEADER_INDEX];
            final String decodedHeader = Arrays.toString(Base64.getUrlDecoder().decode(encodedHeader));

            return objectMapper.readValue(decodedHeader, Map.class);

        } catch (JsonMappingException e) {
            Sentry.captureException(e); // 예외 캡쳐
            throw new ErrorDomain(ErrorCode.APPLE_LOGIN_TOKEN_HEADER_MAPPING);
        } catch (JsonProcessingException e) {
            Sentry.captureException(e); // 예외 캡쳐
            throw new ErrorDomain(ErrorCode.APPLE_LOGIN_TOKEN_HEADER);
        } catch (ArrayIndexOutOfBoundsException e) {
            Sentry.captureException(e); // 예외 캡쳐
            throw new ErrorDomain(ErrorCode.APPLE_LOGIN_INVALID_TOKEN);
        }
    }
}