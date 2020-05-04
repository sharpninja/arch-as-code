package net.trilogy.arch;

import net.trilogy.arch.adapter.FilesFacade;
import net.trilogy.arch.adapter.Jira.JiraApiFactory;
import net.trilogy.arch.adapter.in.google.GoogleDocsAuthorizedApiFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class TestHelper {
    public static Long TEST_WORKSPACE_ID = 49344L;

    public static final String MANIFEST_PATH_TO_TEST_GENERALLY = "/architecture/products/testspaces/data-structure.yml";
    public static final String MANIFEST_PATH_TO_TEST_DECISIONS = "/architecture/products/testspaces/data-structure.yml";
    public static final String MANIFEST_PATH_TO_TEST_MODEL_PEOPLE = "/architecture/products/testspaces/data-structure.yml";
    public static final String MANIFEST_PATH_TO_TEST_MODEL_SYSTEMS = "/architecture/products/testspaces/data-structure.yml";
    public static final String MANIFEST_PATH_TO_TEST_MODEL_CONTAINERS = "/architecture/products/testspaces/data-structure.yml";
    public static final String MANIFEST_PATH_TO_TEST_MODEL_COMPONENTS = "/architecture/products/testspaces/data-structure.yml";
    public static final String MANIFEST_PATH_TO_TEST_MODEL_DEPLOYMENT_NODES = "/view/bigBank/data-structure.yml";
    public static final String MANIFEST_PATH_TO_TEST_VIEWS = "/view/bigBank/data-structure.yml";
    public static final String MANIFEST_PATH_TO_TEST_METADATA = "/architecture/products/testspaces/data-structure.yml";

    public static final String JSON_STRUCTURIZR_BIG_BANK = "/structurizr/BigBank.json";
    public static final String JSON_STRUCTURIZR_THINK3_SOCOCO = "/structurizr/Think3-Sococo.c4model.json";
    public static final String JSON_STRUCTURIZR_EMPTY = "/structurizr/empty-workspace.json";
    public static final String JSON_STRUCTURIZR_NO_SYSTEM = "/structurizr/no-system-deployment-view-workspace.json";
    public static final String JSON_STRUCTURIZR_TRICKY_DEPLOYMENT_NODE_SCOPES = "/structurizr/deployment-node-scopes.json";
    public static final String JSON_STRUCTURIZR_MULTIPLE_RELATIONSHIPS = "/structurizr/multiple-relationships-with-same-source-and-destination.json";

    public static final String JSON_JIRA_GET_EPIC = "/jira/get-epic-response.json";

    public static final String ROOT_PATH_TO_TEST_PRODUCT_DOCUMENTATION = "/architecture/products/testspaces/";
    public static final String ROOT_PATH_TO_TEST_VALIDATION = "/validation/";
    public static final String ROOT_PATH_TO_TEST_AU_VALIDATION = "/auValidation/";
    public static final String ROOT_PATH_TO_TEST_AU_PUBLISH = "/auPublish/";
    public static final String ROOT_PATH_TO_TEST_VIEWS = "/view/bigBank/";

    public static Integer execute(String... args) throws Exception {
        var googleDocsApiFactory = new GoogleDocsAuthorizedApiFactory();
        var filesFacade = new FilesFacade();
        var jiraApiFactory = new JiraApiFactory();
        return new Application(googleDocsApiFactory, jiraApiFactory, filesFacade).execute(args);
    }

    public static Integer execute(Application application, String command) {
        return application.execute(command.split(" "));
    }

    public static String loadResource(Class<?> getClassOfCallingClass, String file) throws Exception {
        return Files.readString(Path.of(getClassOfCallingClass.getResource(file).toURI()));
    }
}
