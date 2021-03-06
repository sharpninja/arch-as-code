package net.trilogy.arch.adapter.architectureUpdate;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.domain.architectureUpdate.YamlTdd.TddId;
import net.trilogy.arch.domain.architectureUpdate.TddContent;
import net.trilogy.arch.facade.FilesFacade;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.IOException;
import java.nio.file.Path;

import static net.trilogy.arch.TestHelper.ROOT_PATH_TO_TEST_AU_DIRECTORY_STRUCTURE;
import static net.trilogy.arch.Util.first;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

public class ArchitectureUpdateReaderTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    private Path auDir;

    @Before
    public void setUp() {
        final var rootDir = TestHelper.getPath(getClass(), ROOT_PATH_TO_TEST_AU_DIRECTORY_STRUCTURE);
        auDir = rootDir.resolve("architecture-updates/sample/");
    }

    @Test
    public void shouldAssignTddContentToTddWithMatchingIds() throws Exception {
        final var architectureUpdate = new ArchitectureUpdateReader(new FilesFacade()).loadArchitectureUpdate(auDir);

        assertThat(architectureUpdate.getTddContainersByComponent().size(), equalTo(1));
        final var tddContainerByComponent = first(architectureUpdate.getTddContainersByComponent());

        assertThat(tddContainerByComponent.getComponentId().getId(), equalTo("16"));
        assertThat(tddContainerByComponent.getTdds().entrySet().size(), equalTo(3));
        final var tdd = tddContainerByComponent.getTdds().get(new TddId("TDD 1.2"));
        assertNotNull(tdd.getContent());
    }

    @Test
    public void tdd_include_file_contents() throws IOException {
        final var architectureUpdate = new ArchitectureUpdateReader(new FilesFacade()).loadArchitectureUpdate(auDir);

        final var tdd = first(architectureUpdate.getTddContainersByComponent()).getTdds().get(new TddId("TDD 1.2"));
        assertNotNull(tdd);
        final var expectedContents = tdd.getContent();
        assertNotNull(expectedContents);
        collector.checkThat(expectedContents, equalTo(new TddContent("" +
                "## TDD 1.2\n" +
                "### Content\n" +
                "**Lorem ipsum** dolor sit amet:\n" +
                "* consectetur adipiscing elit\n" +
                "* sed do eiusmod tempor *incididunt ut labore* et dolore magna aliqua\n" +
                "* et ligula ullamcorper malesuada proin libero nunc consequat\n",
                "TDD 1.2 : Component-16.md")));
    }
}
