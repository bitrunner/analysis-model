package edu.hm.hafner.analysis.parser;

import edu.hm.hafner.analysis.AbstractParserTest;
import edu.hm.hafner.analysis.IssueParser;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.analysis.Severity;
import edu.hm.hafner.analysis.assertions.SoftAssertions;
import static j2html.TagCreator.a;
import static j2html.TagCreator.p;

class GrypeParserTest extends AbstractParserTest {
    protected GrypeParserTest() {
        super("grype-report.json");
    }

    @Override
    protected void assertThatIssuesArePresent(final Report report, final SoftAssertions softly) {
        softly.assertThat(report).hasSize(3).hasDuplicatesSize(0);
        softly.assertThat(report.get(0))
                .hasFileName("tomcat-jdbc/8.0.28/tomcat-jdbc-8.0.28.jar")
                .hasSeverity(Severity.WARNING_NORMAL)
                .hasCategory("Medium")
                .hasType("CVE-2015-5345")
                .hasMessage(
                        "The Mapper component in Apache Tomcat 6.x before 6.0.45, 7.x before 7.0.68, 8.x before 8.0.30, and 9.x before 9.0.0.M2 processes redirects before considering security constraints and Filters, which allows remote attackers to determine the existence of a directory via a URL that lacks a trailing / (slash) character.")
                .hasDescription(p().with(a()
                        .withHref("https://nvd.nist.gov/vuln/detail/CVE-2015-5345")
                        .withText("https://nvd.nist.gov/vuln/detail/CVE-2015-5345")).render());

        softly.assertThat(report.get(2))
                .hasFileName("tomcat-jdbc/8.0.28/tomcat-jdbc-8.0.28.jar")
                .hasSeverity(Severity.WARNING_HIGH)
                .hasCategory("High")
                .hasType("CVE-2016-8745")
                .hasMessage(
                        "A bug in the error handling of the send file code for the NIO HTTP connector in Apache Tomcat 9.0.0.M1 to 9.0.0.M13, 8.5.0 to 8.5.8, 8.0.0.RC1 to 8.0.39, 7.0.0 to 7.0.73 and 6.0.16 to 6.0.48 resulted in the current Processor object being added to the Processor cache multiple times. This in turn meant that the same Processor could be used for concurrent requests. Sharing a Processor can result in information leakage between requests including, not not limited to, session ID and the response body. The bug was first noticed in 8.5.x onwards where it appears the refactoring of the Connector code for 8.5.x onwards made it more likely that the bug was observed. Initially it was thought that the 8.5.x refactoring introduced the bug but further investigation has shown that the bug is present in all currently supported Tomcat versions.")

                .hasDescription(p().with(a()
                        .withHref("https://nvd.nist.gov/vuln/detail/CVE-2016-8745")
                        .withText("https://nvd.nist.gov/vuln/detail/CVE-2016-8745")).render());
    }

    @Override
    protected IssueParser createParser() {
        return new GrypeParser();
    }
}
