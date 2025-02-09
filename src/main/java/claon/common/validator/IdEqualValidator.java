package claon.common.validator;

import claon.common.exception.ErrorCode;
import claon.common.exception.UnauthorizedException;

public class IdEqualValidator extends Validator {
    private final String srcId;
    private final String dstId;

    public IdEqualValidator(String srcId, String dstId) {
        this.srcId = srcId;
        this.dstId = dstId;
    }

    public static IdEqualValidator of(String srcId, String dstId) {
        return new IdEqualValidator(srcId, dstId);
    }

    @Override
    public void validate() {
        if (!srcId.equals(dstId)) {
            throw new UnauthorizedException(
                    ErrorCode.NOT_ACCESSIBLE,
                    "접근 권한이 없습니다."
            );
        }

        if (this.next != null) {
            this.next.validate();
        }
    }
}
