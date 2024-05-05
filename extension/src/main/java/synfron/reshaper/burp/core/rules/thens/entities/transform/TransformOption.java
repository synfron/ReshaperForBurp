package synfron.reshaper.burp.core.rules.thens.entities.transform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public enum TransformOption {
    Base64(Base64Transformer.class),
    Escape("Escape", EscapeTransformer.class),
    JwtDecode("JWT Decode", JwtDecodeTransformer.class),
    Case(CaseTransformer.class),
    Hash(HashTransformer.class),
    Hex(HexTransformer.class),
    Integer(IntegerTransformer.class),
    Trim(TrimTransformer.class);

    private String name;

    private final Class<? extends ITransformer> transformerClass;

    public String getName() {
        return StringUtils.defaultString(name, name());
    }

    @Override
    public String toString() {
        return getName();
    }
}
