package edu.hm.hafner.analysis;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import edu.hm.hafner.util.TreeString;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static edu.hm.hafner.analysis.IssueTest.*;
import static edu.hm.hafner.analysis.assertions.Assertions.*;

/**
 * Unit test for {@link IssueBuilder}.
 *
 * @author Marcel Binder
 */
@SuppressFBWarnings("DMI")
class IssueBuilderTest {
    private static final String FILE_NAME = "C:/users/tester/file-name";
    static final String FILE_NAME_WITH_BACKSLASHES = "C:\\users\\tester/file-name";
    private static final Issue DEFAULT_ISSUE = new Issue(PATH_NAME, UNDEFINED_TS, 0, 0, 0, 0, new LineRangeList(),
            null, null, UNDEFINED_TS, null, null, EMPTY_TS, EMPTY, null, null, null, null, null);
    private static final Issue FILLED_ISSUE = new Issue(PATH_NAME, TreeString.valueOf(FILE_NAME), LINE_START,
            LINE_END, COLUMN_START, COLUMN_END,
            LINE_RANGES, CATEGORY, TYPE, TreeString.valueOf(PACKAGE_NAME), MODULE_NAME, SEVERITY,
            TreeString.valueOf(MESSAGE), DESCRIPTION, ORIGIN, ORIGIN_NAME, REFERENCE,
            FINGERPRINT, ADDITIONAL_PROPERTIES);
    private static final String RELATIVE_FILE = "relative.txt";

    @SuppressFBWarnings("DMI")
    @Test
    void shouldCreateAbsolutePath() {
        try (IssueBuilder builder = new IssueBuilder()) {

            builder.setFileName(RELATIVE_FILE);

            assertThat(builder.build())
                    .hasFileName(RELATIVE_FILE)
                    .hasBaseName(RELATIVE_FILE)
                    .hasFolder(UNDEFINED)
                    .hasPath(UNDEFINED);

            builder.setDirectory("/tmp");
            builder.setFileName(RELATIVE_FILE);

            assertThat(builder.build())
                    .hasFileName("/tmp/" + RELATIVE_FILE)
                    .hasBaseName(RELATIVE_FILE)
                    .hasFolder("tmp");

            builder.setFileName("/tmp/absolute.txt");
            assertThat(builder.build()).hasFileName("/tmp/absolute.txt");

            builder.setFileName("C:\\tmp\\absolute.txt");
            assertThat(builder.build()).hasFileName("C:/tmp/absolute.txt");

            builder.setFileName(null);
            assertThat(builder.build())
                    .hasFileName(UNDEFINED)
                    .hasBaseName(UNDEFINED)
                    .hasFolder(UNDEFINED);

            builder.setPathName("/path/to/source");
            builder.setDirectory("");
            builder.setFileName(RELATIVE_FILE);
            assertThat(builder.build())
                    .hasFileName(RELATIVE_FILE)
                    .hasBaseName(RELATIVE_FILE)
                    .hasFolder(UNDEFINED)
                    .hasPath("/path/to/source");
        }
    }

    @ParameterizedTest(name = "{index} => Full Path: {0} - Expected Base Name: file.txt")
    @ValueSource(strings = {
            "/path/to/file.txt",
            "./file.txt",
            "file.txt",
            "C:\\Programme\\Folder\\file.txt",
            "C:\\file.txt"
    })
    void shouldGetBaseName(final String fullPath) {
        try (IssueBuilder issueBuilder = new IssueBuilder()) {
            assertThat(issueBuilder.setFileName(fullPath).build()).hasBaseName("file.txt");
        }
    }

    @Test
    void shouldCreateDefaultIssueIfNothingSpecified() {
        try (IssueBuilder builder = new IssueBuilder()) {
            Issue issue = builder.build();

            assertThat(issue).isEqualTo(DEFAULT_ISSUE);
        }
    }

    @ParameterizedTest(name = "{index} => Input: [{0} - {1}] - Expected Output: [{2} - {3}]")
    @CsvSource({
            "1, 1, 1, 1",
            "1, 2, 1, 2",
            "2, 1, 1, 2",
            "0, 1, 1, 1",
            "0, 0, 0, 0",
            "0, -1, 0, 0",
            "1, -1, 1, 1",
            "1, 0, 1, 1",
            "-1, 0, 0, 0",
            "-1, 1, 1, 1",
            "-1, -1, 0, 0"})
    void shouldHaveValidLineRange(
            final int start, final int end, final int expectedStart, final int expectedEnd) {
        try (IssueBuilder builder = new IssueBuilder()) {
            builder.setLineStart(start).setLineEnd(end);
            assertThat(builder.build()).hasLineStart(expectedStart).hasLineEnd(expectedEnd);
        }
    }

    @ParameterizedTest(name = "{index} => Input: [{0} - {1}] - Expected Output: [{2} - {3}]")
    @CsvSource({
            "1, 1, 1, 1",
            "1, 2, 1, 2",
            "2, 1, 1, 2",
            "0, 1, 1, 1",
            "0, 0, 0, 0",
            "0, -1, 0, 0",
            "1, -1, 1, 1",
            "1, 0, 1, 1",
            "-1, 0, 0, 0",
            "-1, 1, 1, 1",
            "-1, -1, 0, 0"})
    void shouldHaveValidColumnRange(
            final int start, final int end, final int expectedStart, final int expectedEnd) {
        try (IssueBuilder builder = new IssueBuilder()) {
            builder.setColumnStart(start).setColumnEnd(end);
            assertThat(builder.build()).hasColumnStart(expectedStart).hasColumnEnd(expectedEnd);
        }
    }

    @Test
    void shouldMapStringNumbers() {
        try (IssueBuilder builder = new IssueBuilder()) {
            assertThat(builder.setLineStart("nix").build()).hasLineStart(0);
            assertThat(builder.setLineStart("-1").build()).hasLineStart(0);
            assertThat(builder.setLineStart("0").build()).hasLineStart(0);
            assertThat(builder.setLineStart("1").build()).hasLineStart(1);
        }
        try (IssueBuilder builder = new IssueBuilder()) {
            assertThat(builder.setLineEnd("nix").build()).hasLineEnd(0);
            assertThat(builder.setLineEnd("-1").build()).hasLineEnd(0);
            assertThat(builder.setLineEnd("0").build()).hasLineEnd(0);
            assertThat(builder.setLineEnd("1").build()).hasLineEnd(1);
        }
        try (IssueBuilder builder = new IssueBuilder()) {
            assertThat(builder.setColumnStart("nix").build()).hasColumnStart(0);
            assertThat(builder.setColumnStart("-1").build()).hasColumnStart(0);
            assertThat(builder.setColumnStart("0").build()).hasColumnStart(0);
            assertThat(builder.setColumnStart("1").build()).hasColumnStart(1);
        }
        try (IssueBuilder builder = new IssueBuilder()) {
            assertThat(builder.setColumnEnd("nix").build()).hasColumnEnd(0);
            assertThat(builder.setColumnEnd("-1").build()).hasColumnEnd(0);
            assertThat(builder.setColumnEnd("0").build()).hasColumnEnd(0);
            assertThat(builder.setColumnEnd("1").build()).hasColumnEnd(1);
        }
    }

    @Test
    @SuppressFBWarnings("DMI")
    void shouldCreateIssueWithAllPropertiesInitialized() {
        try (IssueBuilder builder = new IssueBuilder()) {
            Issue issue = builder
                    .setFileName(FILE_NAME)
                    .setLineStart(LINE_START)
                    .setLineEnd(LINE_END)
                    .setColumnStart(COLUMN_START)
                    .setColumnEnd(COLUMN_END)
                    .setCategory(CATEGORY)
                    .setType(TYPE)
                    .setPackageName(PACKAGE_NAME)
                    .setModuleName(MODULE_NAME)
                    .setSeverity(SEVERITY)
                    .setMessage(MESSAGE)
                    .setDescription(DESCRIPTION)
                    .setOrigin(ORIGIN)
                    .setOriginName(ORIGIN_NAME)
                    .setLineRanges(LINE_RANGES)
                    .setReference(REFERENCE)
                    .setFingerprint(FINGERPRINT)
                    .setAdditionalProperties(ADDITIONAL_PROPERTIES)
                    .build();

            assertThatIssueIsEqualToFilled(issue);
            assertThatIssueIsEqualToFilled(builder.copy(issue).build());
            assertThatIssueIsEqualToFilled(builder.build()); // same result because builder is not cleaned
            assertThatIssueIsEqualToFilled(builder.buildAndClean());

            try (IssueBuilder emptyBuilder = new IssueBuilder()) {
                emptyBuilder.setOrigin(ORIGIN);
                emptyBuilder.setOriginName(ORIGIN_NAME);
                assertThat(builder.build()).isEqualTo(emptyBuilder.build());
            }
        }
    }

    private void assertThatIssueIsEqualToFilled(final Issue issue) {
        assertThat(issue).isEqualTo(FILLED_ISSUE);
        assertThat(issue).hasFingerprint(FINGERPRINT);
        assertThat(issue).hasReference(REFERENCE);
    }

    @Test
    void shouldCopyAllPropertiesOfAnIssue() {
        try (IssueBuilder builder = new IssueBuilder()) {
            Issue copy = builder.copy(FILLED_ISSUE).build();

            assertThat(copy).isNotSameAs(FILLED_ISSUE);
            assertThatIssueIsEqualToFilled(copy);
        }
    }

    @Test
    void shouldCreateNewInstanceOnEveryCall() {
        try (IssueBuilder builder = new IssueBuilder()) {
            builder.copy(FILLED_ISSUE);
            Issue issue1 = builder.build();
            Issue issue2 = builder.build();

            assertThat(issue1).isNotSameAs(issue2);
            assertThat(issue1).isEqualTo(issue2);
        }
    }

    @Test
    void shouldCollectLineRanges() {
        try (IssueBuilder builder = new IssueBuilder()) {
            builder.setLineStart(1).setLineEnd(2);
            LineRangeList lineRanges = new LineRangeList();
            lineRanges.add(new LineRange(3, 4));
            lineRanges.add(new LineRange(5, 6));
            builder.setLineRanges(lineRanges);

            Issue issue = builder.build();
            assertThat(issue).hasLineStart(1).hasLineEnd(2);
            assertThat(issue).hasOnlyLineRanges(new LineRange(3, 4), new LineRange(5, 6));

            try (IssueBuilder copy = new IssueBuilder()) {
                copy.copy(issue);
                assertThat(copy.build()).hasOnlyLineRanges(new LineRange(3, 4), new LineRange(5, 6));
            }
        }
    }

    @Test
    void shouldMoveLineRangeToAttributes() {
        try (IssueBuilder builder = new IssueBuilder()) {
            LineRangeList lineRanges = new LineRangeList();
            lineRanges.add(new LineRange(1, 2));
            builder.setLineRanges(lineRanges);

            Issue issue = builder.build();
            assertThat(issue).hasLineStart(1).hasLineEnd(2);
            assertThat(issue).hasNoLineRanges();
        }
    }

    @Test
    void shouldMoveLineRangeToAttributesEvenIfLineEndIsSet() {
        try (IssueBuilder builder = new IssueBuilder()) {
            builder.setLineEnd(2);
            LineRangeList lineRanges = new LineRangeList();
            lineRanges.add(new LineRange(1, 2));
            builder.setLineRanges(lineRanges);

            Issue issue = builder.build();
            assertThat(issue).hasLineStart(1).hasLineEnd(2);
            assertThat(issue).hasNoLineRanges();
        }
    }

    @Test
    void shouldCleanupLineRanges() {
        try (IssueBuilder builder = new IssueBuilder()) {
            builder.setLineStart(1).setLineEnd(2);
            LineRangeList lineRanges = new LineRangeList();
            lineRanges.add(new LineRange(1, 2));
            builder.setLineRanges(lineRanges);

            Issue issue = builder.build();
            assertThat(issue).hasLineStart(1).hasLineEnd(2);
            assertThat(issue).hasNoLineRanges();
        }
    }

    @Test
    void shouldNotCleanupDifferentLineRanges() {
        try (IssueBuilder builder = new IssueBuilder()) {
            builder.setLineStart(1).setLineEnd(2);
            LineRangeList lineRanges = new LineRangeList();
            lineRanges.add(new LineRange(1, 3));
            builder.setLineRanges(lineRanges);

            Issue issue = builder.build();
            assertThat(issue).hasLineStart(1).hasLineEnd(2);
            assertThat(issue).hasOnlyLineRanges(new LineRange(1, 3));
        }
    }

    @Test
    void shouldUseProvidedId() {
        try (IssueBuilder builder = new IssueBuilder()) {
            UUID id = UUID.randomUUID();
            builder.setId(id);

            assertThat(builder.build()).hasId(id);
            assertThat(builder.build().getId()).isNotEqualTo(id); // new random ID

            builder.setId(id);
            assertThat(builder.build()).hasId(id);
        }
    }

    @Test
    void testFileNameBackslashConversion() {
        try (IssueBuilder builder = new IssueBuilder()) {
            Issue issue = builder.setFileName(FILE_NAME_WITH_BACKSLASHES).build();

            assertThat(issue).hasFileName(FILE_NAME);
        }
    }

    @Test
    void shouldCacheFileName() {
        try (IssueBuilder builder = new IssueBuilder()) {
            Issue issue = builder.setFileName("fileName").build();
            Issue anotherIssue = builder.setFileName("fileName").build();

            assertThat(issue.getFileNameTreeString()).isSameAs(anotherIssue.getFileNameTreeString());
        }
    }

    @Test
    void shouldCachePackageName() {
        try (IssueBuilder builder = new IssueBuilder()) {
            Issue issue = builder.setPackageName("packageName").build();
            Issue anotherIssue = builder.setFileName("packageName").build();

            assertThat(issue.getPackageNameTreeString()).isSameAs(anotherIssue.getPackageNameTreeString());
        }
    }

    @Test
    void shouldCacheMessage() {
        try (IssueBuilder builder = new IssueBuilder()) {
            Issue issue = builder.setMessage("message").build();
            Issue anotherIssue = builder.setMessage("message").build();

            assertThat(issue.getMessageTreeString()).isSameAs(anotherIssue.getMessageTreeString());
        }
    }

    @Test
    void testMessageDescriptionStripped() {
        try (IssueBuilder builder = new IssueBuilder()) {
            Issue issue = builder.setMessage("    message  ").setDescription("    description  ").build();
            Issue anotherIssue = builder.setMessage("message").setDescription("description").build();

            assertThat(issue.getMessageTreeString()).isSameAs(anotherIssue.getMessageTreeString());
            assertThat(issue.getDescription()).isSameAs(anotherIssue.getDescription());
        }
    }
}
