package synfron.reshaper.burp.core.rules.diagnostics;

import lombok.Getter;

public enum DiagnosticEntityType {
    StartEvent(0, 1),
    EndEvent(0, -1),
    StartRule(1, 1),
    EndRule(1, -1),
    When(2, 1),
    Then(2, 1),
    StartGroupContainer(2, 1),
    EndGroupContainer(2, -1),
    StartGroup(3, 1),
    EndGroup(3, -1);

    @Getter
    private final int level;
    @Getter
    private final int levelChange;

    DiagnosticEntityType(int level, int levelChange) {
        this.level = level;
        this.levelChange = levelChange;
    }
}
