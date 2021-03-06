package net.trilogy.arch.commands.architectureUpdate;

import net.trilogy.arch.adapter.git.GitInterface;
import net.trilogy.arch.facade.FilesFacade;
import picocli.CommandLine.Command;

@Command(name = "finalizeAndPublish", description = "Annotate AU, validate AU and publish AU Jira stories.", mixinStandardHelpOptions = true)
public class AuFinalizeAndPublishCommand extends AuPublishStoriesCommand {
    public AuFinalizeAndPublishCommand(FilesFacade filesFacade, GitInterface gitInterface) {
        super(filesFacade, gitInterface);
    }

    @Override
    public Integer call() {
        AuAnnotateCommand auAnnotateCommand = createAuAnnotateCommand();
        Integer annotateResults = auAnnotateCommand.call();
        if (annotateResults != 0) {
            return annotateResults;
        }

        Integer validateResults = createAuValidateCommand().call();
        if (validateResults != 0) {
            return validateResults;
        }
        return publishToJira();
    }

    Integer publishToJira() {
        return super.call();
    }

    AuValidateCommand createAuValidateCommand() {
        return new AuValidateCommand(
                getFilesFacade(),
                getGitInterface(),
                getSpec(),
                getArchitectureUpdateDirectory(),
                getProductArchitectureDirectory(),
                getBaseBranch());
    }

    AuAnnotateCommand createAuAnnotateCommand() {
        return new AuAnnotateCommand(
                getFilesFacade(),
                getSpec(),
                getArchitectureUpdateDirectory(),
                getProductArchitectureDirectory());
    }
}
