package kr.okku.server.domain;

import kr.okku.server.exception.ErrorCode;
import kr.okku.server.exception.ErrorDomain;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Utils {
    public void validatePickLimit(UserDomain user, List<PickDomain> picks) {
        if (!user.getIsPremium()) {
            if (picks.size() > 20) {
                throw new ErrorDomain(ErrorCode.MUST_INVITE,null);
            }
        }
    }
}
