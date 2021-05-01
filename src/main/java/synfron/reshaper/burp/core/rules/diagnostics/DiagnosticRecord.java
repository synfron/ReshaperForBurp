package synfron.reshaper.burp.core.rules.diagnostics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DiagnosticRecord {
    private DiagnosticEntityType entityType;
    private String log;
}
