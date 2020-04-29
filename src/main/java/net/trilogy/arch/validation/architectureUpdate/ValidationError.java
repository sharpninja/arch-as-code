package net.trilogy.arch.validation.architectureUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.trilogy.arch.domain.architectureUpdate.Decision;
import net.trilogy.arch.domain.architectureUpdate.EntityReference;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;

@ToString
@Getter
@EqualsAndHashCode
public class ValidationError {
    private final ValidationErrorType validationErrorType;
    private final EntityReference element;
    private final String description;

    private ValidationError(ValidationErrorType validationErrorType, EntityReference element, String description) {
        this.validationErrorType = validationErrorType;
        this.element = element;
        this.description = description;
    }

    public static ValidationError forDecisionsMustHaveTdds(Decision.Id entityId) {
        return new ValidationError(
                ValidationErrorType.MISSING_TDD,
                entityId,
                String.format("Decision \"%s\" must have at least one TDD reference.", entityId.getId())
        );
    }

    public static ValidationError forTddsMustBeValidReferences(EntityReference entityId, Tdd.Id tddId) {
        return new ValidationError(
                ValidationErrorType.INVALID_TDD_REFERENCE_IN_DECISION_OR_REQUIREMENT,
                entityId,
                String.format("%s \"%s\" contains TDD reference \"%s\" that does not exist.", getEntityTypeString(entityId), entityId.getId(), tddId.getId())
        );
    }

    public static ValidationError forMustHaveStories(EntityReference entityId) {
        return new ValidationError(
                ValidationErrorType.MISSING_CAPABILITY,
                entityId,
                String.format("%s \"%s\" is not referred to by a story.", getEntityTypeString(entityId), entityId.getId())
        );
    }

    public static ValidationError forTddsMustHaveDecisionsOrRequirements(Tdd.Id tddId) {
        return new ValidationError(
                ValidationErrorType.TDD_WITHOUT_CAUSE,
                tddId,
                String.format("TDD \"%s\" is not referred to by a decision or functional requirement.", tddId.getId())
        );
    }

    public static ValidationError forStoriesTddsMustBeValidReferences(Tdd.Id id, String storyTitle) {
        return new ValidationError(
                ValidationErrorType.INVALID_TDD_REFERENCE_IN_STORY,
                id,
                String.format("Story \"%s\" contains TDD reference \"%s\" that does not exist.", storyTitle, id.getId())
        );
    }

    public static ValidationError forTddsComponentsMustBeValidReferences(Tdd.ComponentReference componentReference) {
        return new ValidationError(
                ValidationErrorType.INVALID_COMPONENT_REFERENCE,
                componentReference,
                String.format("Component id \"%s\" does not exist.", componentReference.getId())
        );
    }

    private static String getEntityTypeString(EntityReference entityId) {
        if (entityId instanceof Tdd.Id) {
           return "TDD";
        } else if (entityId instanceof FunctionalRequirement.Id) {
            return "Functional Requirement";
        }
        return "Entity";
    }

}
