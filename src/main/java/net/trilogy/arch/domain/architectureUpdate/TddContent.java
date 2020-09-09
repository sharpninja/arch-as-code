package net.trilogy.arch.domain.architectureUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.trilogy.arch.facade.FilesFacade;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import static com.google.common.io.Files.getFileExtension;

@Getter
@ToString
@EqualsAndHashCode
public class TddContent {
    public static final int TDD_MATCHER_GROUP = 1;
    public static final int COMPONENT_ID_MATCHER_GROUP = 2;

    private static final String REGEX = "(.*) : Component-([a-zA-Z\\d]+)";
    private static final Pattern pattern = Pattern.compile(REGEX);

    private final String content;
    // TODO: Why isn't this a JDK Path object?
    private final String filename;

    public TddContent(String content, String filename) {
        this.content = content;
        this.filename = filename;
    }

    public static boolean isContentType(File file) {
        if (file == null) return false;
        if (file.isDirectory()) return false;

        String fileExtension = getFileExtension(file.getName());

        // Supported content types
        return fileExtension.equals("md") || fileExtension.equals("txt");
    }

    public static boolean isTddContentName(File file) {
        if (file == null) return false;
        if (file.isDirectory()) return false;

        return pattern.matcher(file.getName()).find();
    }

    public static TddContent createCreateFromFile(File file, FilesFacade filesFacade) {
        if (file == null) return null;

        String content;
        String filename;

        try {
            content = filesFacade.readString(file.toPath());
            filename = file.getName();
        } catch (IOException e) {
            return null;
        }

        return new TddContent(content, filename);
    }

    public String getTdd() {
        final var matcher = pattern.matcher(filename);
        return matcher.find() ? matcher.group(TDD_MATCHER_GROUP) : null;
    }

    public String getComponentId() {
        final var matcher = pattern.matcher(filename);
        return matcher.find() ? matcher.group(COMPONENT_ID_MATCHER_GROUP) : null;
    }
}
