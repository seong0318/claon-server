package claon.version.web.converter;

import claon.common.exception.BadRequestException;
import claon.common.exception.ErrorCode;
import claon.version.domain.enums.AppStore;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class AppStoreConverter implements Converter<String, AppStore> {
    @Override
    public AppStore convert(String source) {
        return Arrays.stream(AppStore.values())
                .filter(v -> source.equals(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_STORE,
                                "지원하지 않는 스토어 입니다."
                        )
                );
    }
}
