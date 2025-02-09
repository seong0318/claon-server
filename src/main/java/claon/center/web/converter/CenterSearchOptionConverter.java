package claon.center.web.converter;

import claon.center.domain.enums.CenterSearchOption;
import claon.common.exception.BadRequestException;
import claon.common.exception.ErrorCode;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class CenterSearchOptionConverter implements Converter<String, CenterSearchOption> {
    @Override
    public CenterSearchOption convert(String source) {
        return Arrays.stream(CenterSearchOption.values())
                .filter(v -> source.equalsIgnoreCase(v.getValue()))
                .findFirst()
                .orElseThrow(
                        () -> new BadRequestException(
                                ErrorCode.WRONG_SEARCH_OPTION,
                                "잘못된 검색 입니다."
                        )
                );
    }
}
