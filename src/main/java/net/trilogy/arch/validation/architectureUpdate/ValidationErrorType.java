package net.trilogy.arch.validation.architectureUpdate;

import lombok.Getter;

public enum ValidationErrorType {
    INVALID_TDD_REFERENCE_IN_DECISION_OR_REQUIREMENT("Invalid TDD Reference in Decision or Requirement", ValidationStage.TDD),
    MISSING_CAPABILITY("Missing Capability", ValidationStage.STORY),
    MISSING_TDD("Missing TDD", ValidationStage.TDD),
    TDD_WITHOUT_CAUSE("TDD without cause", ValidationStage.TDD),
    INVALID_TDD_REFERENCE_IN_STORY("Invalid TDD Reference in Story", ValidationStage.STORY),
    INVALID_COMPONENT_REFERENCE("Invalid Component Reference", ValidationStage.TDD);

    @Getter private final String label;
    @Getter private final ValidationStage stage;

    ValidationErrorType(String label, ValidationStage stage) {
        this.label = label;
        this.stage = stage;
    }

    @Override
    public String toString() {
        return label;
    }
}
