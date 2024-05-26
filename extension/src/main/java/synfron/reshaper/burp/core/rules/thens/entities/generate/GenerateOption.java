package synfron.reshaper.burp.core.rules.thens.entities.generate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
@AllArgsConstructor
public enum GenerateOption {
    Uuid("UUID", UuidGenerator.class),
    Words(WordGenerator.class),
    Password(PasswordGenerator.class),
    Bytes(BytesGenerator.class),
    Integer(IntegerGenerator.class),
    IpAddress("IP Address", IpAddressGenerator.class),
    Timestamp(TimestampGenerator.class),
    UnixTimestamp("UNIX Timestamp", UnixTimestampGenerator.class);

    private String name;
    @Getter
    private final Class<? extends IGenerator> generatorClass;

    public String getName() {
        return StringUtils.defaultString(name, name());
    }

    @Override
    public String toString() {
        return getName();
    }
}
