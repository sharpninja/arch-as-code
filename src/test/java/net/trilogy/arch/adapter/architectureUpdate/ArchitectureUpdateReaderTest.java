package net.trilogy.arch.adapter.architectureUpdate;

import net.trilogy.arch.TestHelper;
import net.trilogy.arch.domain.architectureUpdate.ArchitectureUpdate;
import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.domain.architectureUpdate.Tdd.TddId;
import net.trilogy.arch.domain.architectureUpdate.TddContainerByComponent;
import net.trilogy.arch.domain.architectureUpdate.TddContent;
import net.trilogy.arch.facade.FilesFacade;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

public class ArchitectureUpdateReaderTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();
    private Path auDir;

    @Before
    public void setUp() {
        Path rootDir = TestHelper.getPath(getClass(), TestHelper.ROOT_PATH_TO_TEST_AU_DIRECTORY_STRUCTURE);
        auDir = rootDir.resolve("architecture-updates/sample/");
    }

    @Test
    public void shouldReadProductArchitectureAndMarkdownFiles() throws Exception {
        ArchitectureUpdate architectureUpdate = new ArchitectureUpdateReader(new FilesFacade()).load(auDir);

        collector.checkThat(architectureUpdate.getName(), equalTo("test"));
        collector.checkThat(architectureUpdate.getTddContents().get(0), equalTo(new TddContent("" +
                "## TDD 1.2\n" +
                "### Content\n" +
                "**Lorem ipsum** dolor sit amet:\n" +
                "* consectetur adipiscing elit\n" +
                "* sed do eiusmod tempor *incididunt ut labore* et dolore magna aliqua\n" +
                "* et ligula ullamcorper malesuada proin libero nunc consequat\n",
                "TDD 1.2 : Component-16.md")));
    }

    @Test
    public void shouldOnlyLoadProperlyNamedTddContentFiles() throws Exception {
        ArchitectureUpdate architectureUpdate = new ArchitectureUpdateReader(new FilesFacade()).load(auDir);
        List<String> names = architectureUpdate.getTddContents().stream()
                .map(TddContent::getFilename)
                .collect(Collectors.toList());

        collector.checkThat(names.size(), equalTo(1));

        String strayFileInAuDirectory = "notProperlyNamedTddContentFile.txt";
        collector.checkThat(names.get(0), not(containsString(strayFileInAuDirectory)));
    }

    @Test
    public void shouldAssignTddContentToTddWithMatchingIds() throws Exception {
        ArchitectureUpdate architectureUpdate = new ArchitectureUpdateReader(new FilesFacade()).load(auDir);

        assertThat(architectureUpdate.getTddContainersByComponent().size(), equalTo(1));
        TddContainerByComponent tddContainerByComponent = architectureUpdate.getTddContainersByComponent().get(0);

        assertThat(tddContainerByComponent.getComponentId().getId(), equalTo("16"));
        assertThat(tddContainerByComponent.getTdds().entrySet().size(), equalTo(3));
        Tdd tdd = tddContainerByComponent.getTdds().get(new TddId("TDD 1.2"));
        assertTrue(tdd.getContent().isPresent());
    }
}
