package net.trilogy.arch.adapter.jira;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.trilogy.arch.domain.ArchitectureDataStructure;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.FeatureStory;
import net.trilogy.arch.domain.architectureUpdate.FunctionalRequirement;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.domain.architectureUpdate.TddContainerByComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class JiraStory {
    private final String title;
    private final List<JiraTdd> tdds;
    private final List<JiraFunctionalRequirement> functionalRequirements;

    public JiraStory(ArchitectureUpdate au, ArchitectureDataStructure architecture, FeatureStory featureStory) throws InvalidStoryException {
        this.title = featureStory.getTitle();
        this.tdds = getTdds(au, architecture, featureStory);
        this.functionalRequirements = getFunctionalRequirements(au, featureStory);
    }

    private List<JiraFunctionalRequirement> getFunctionalRequirements(ArchitectureUpdate au, FeatureStory featureStory) throws InvalidStoryException {
        final var requirements = new ArrayList<JiraFunctionalRequirement>();
        for (var reqId : featureStory.getRequirementReferences()) {
            if (!au.getFunctionalRequirements().containsKey(reqId)) throw new InvalidStoryException();
            requirements.add(new JiraFunctionalRequirement(reqId, au.getFunctionalRequirements().get(reqId)));
        }
        return requirements;
    }

    private List<JiraTdd> getTdds(
            ArchitectureUpdate au,
            ArchitectureDataStructure architecture,
            FeatureStory featureStory
    ) throws InvalidStoryException {

        List<JiraTdd> tdds = new ArrayList<>();
        for (var tddId : featureStory.getTddReferences()) {
            var tdd = au.getTddContainersByComponent()
                    .stream()
                    .filter(container -> container.getTdds().containsKey(tddId))
                    .filter(container -> getComponentHeader(architecture, container).isPresent())
                    .map(container -> new JiraTdd(
                            tddId,
                            container.getTdds().get(tddId),
                            getComponentHeader(architecture, container).orElseThrow()
                    ))
                    .findAny()
                    .orElseThrow(InvalidStoryException::new);
            tdds.add(tdd);
        }

        return tdds;
    }

    private Optional<String> getComponentHeader(ArchitectureDataStructure architecture, TddContainerByComponent tddContainerByComponent) {
        if(tddContainerByComponent.isDeleted()){
            return Optional.of("[DELETED COMPONENT]");
        }
        try {
            return Optional.of(
                architecture.getModel()
                    .findEntityById(tddContainerByComponent.getComponentId().toString())
                    .getPath()
                    .getPath()
            );
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class JiraTdd {
        private final Tdd.Id id;
        private final Tdd tdd;
        private final String component;

        public String getId() {
            return id.toString();
        }

        public String getComponent() {
            return component;
        }

        public String getText() {
            return tdd.getText();
        }
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class JiraFunctionalRequirement {
        private final FunctionalRequirement.Id id;
        private final FunctionalRequirement functionalRequirement;

        public String getId() {
            return id.toString();
        }

        public String getText() {
            return functionalRequirement.getText();
        }

        public String getSource() {
            return functionalRequirement.getSource();
        }
    }

    public static class InvalidStoryException extends Exception {
    }
}
