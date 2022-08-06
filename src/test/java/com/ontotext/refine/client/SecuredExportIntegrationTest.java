package com.ontotext.refine.client;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.create.CreateProjectResponse;
import com.ontotext.refine.client.command.delete.DeleteProjectResponse;
import com.ontotext.refine.client.command.rdf.ExportRdfResponse;
import com.ontotext.refine.client.command.rdf.GraphDbSparqlBasedRdfExportCommand;
import com.ontotext.refine.client.command.rdf.OutputType;
import com.ontotext.refine.client.command.rdf.ResultFormat;
import com.ontotext.refine.client.util.RdfTestUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.Test;

/**
 * Integration tests related to export commands. The test contains different tests which are
 * executing their own scenario and verifying the result. The scenarios are combining execution of
 * different commands. The tests is using real secured GraphDB with OntoRefine instance.
 *
 * @author Antoniy Kunchev
 * @see IntegrationTest
 */
class SecuredExportIntegrationTest extends CommandIntegrationTest {

  private static final String RESTAURANTS_CSV = "integration/reduced_netherlands_restaurants.csv";

  // enables the security functionalities for the GDB and the client
  private SecuredExportIntegrationTest() {
    security(Turn.ON);
  }

  /**
   * Note the scenario uses reduced dataset in order to complete the operations quickly!
   *
   * <p>Scenario 1:<br>
   * <br>
   * 1. Creates project with name 'Scenario-1-{test-class-name}' using the
   * 'reduced_netherlands_resturants.csv'<br>
   * 4. Export the data in RDF format with specific SPARQL query<br>
   * 5. Delete the project
   */
  @Test
  void exportRdfUsingSparql() throws Exception {
    String projectName = "Scenario-1-" + SecuredExportIntegrationTest.class.getSimpleName();
    CreateProjectResponse createResponse = createProject(projectName, RESTAURANTS_CSV);
    String projectId = createResponse.getProjectId();
    assertNotNull(projectId);

    waitForProcessesCompletion(projectId);

    String expectedFilePath =
        "/integration/expected/scenario_1_reduced_netherlands_restaurants_exportRdfUsingSparql.ttl";
    InputStream expected = getClass().getResourceAsStream(expectedFilePath);

    ExportRdfResponse exportRdfResponse = exportAsRdfUsingSparql(projectId);

    boolean areEqual =
        RdfTestUtils.compareAsRdf(
            expected,
            new ByteArrayInputStream(exportRdfResponse.getResult().getBytes()),
            RDFFormat.TURTLE);

    assertTrue("The expected result is different then the acual one.", areEqual);

    DeleteProjectResponse deleteResponse = deleteProject(projectId);
    assertEquals(ResponseCode.OK, deleteResponse.getCode());
  }

  private ExportRdfResponse exportAsRdfUsingSparql(String projectId) throws IOException {
    String query = IOUtils.resourceToString(
        "/integration/netherlands_restaurants_construct_query.sparql",
        StandardCharsets.UTF_8);
    TestGraphDbSparqlBasedRdfExportCommand command = new TestGraphDbSparqlBasedRdfExportCommand(
        projectId, "#project_placeholder#", query, ResultFormat.TURTLE, REPO_NAME, null);
    return command.execute(getClient());
  }

  // hack for the proxy in order to keep the test and the logic around it, because it is relevant
  private class TestGraphDbSparqlBasedRdfExportCommand extends GraphDbSparqlBasedRdfExportCommand {

    private TestGraphDbSparqlBasedRdfExportCommand(
        String project,
        String placeholder,
        String query,
        ResultFormat format,
        String repository,
        OutputType output) {
      super(project, placeholder, query, format, repository, output);

      // verifies the input parameters, also Sonar test coverage
      GraphDbSparqlBasedRdfExportCommand.Builder builder = RefineCommands
          .exportRdfUsingSparql()
          .setProject(project)
          .setFormat(ResultFormat.TURTLE)
          .setQuery(query)
          .setRepository(REPO_NAME);
      assertDoesNotThrow(() -> builder.build());
    }

    @Override
    public String endpoint() {
      return "/repositories/{repo}";
    }
  }
}
