package synfron.reshaper.burp.core.rules.thens.entities.generate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
public enum GenerateOption {
    Uuid("UUID"),
    Words,
    Bytes,
    Integer,
    IpAddress("IP Address"),
    Timestamp;

    private String name;

    public String getName() {
        return StringUtils.defaultString(name, name());
    }


    @Override
    public String toString() {
        return getName();
    }
}
