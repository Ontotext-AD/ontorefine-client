package com.ontotext.refine.client.command.rdf;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ontotext.refine.client.command.BaseCommandTest;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

/**
 * Test for {@link SparqlBasedRdfExportCommand}.
 *
 * @author Antoniy Kunchev
 */
class SparqlBasedRdfExportCommandTest
    extends BaseCommandTest<ExportRdfResponse, SparqlBasedRdfExportCommand> {

  @Captor
  private ArgumentCaptor<HttpUriRequest> requestCaptor;

  private String query;

  @BeforeEach
  void init() throws IOException {
    query = IOUtils.toString(loadResource("construct.sparql"), StandardCharsets.UTF_8);
  }

  @Override
  protected SparqlBasedRdfExportCommand command() {
    return commandBuilder().build();
  }

  private SparqlBasedRdfExportCommand.Builder commandBuilder() {
    return RefineCommands.exportAsRdf()
        .setProject(PROJECT_ID)
        .setPrefix("ontorefine:")
        .setQuery(query)
        .setFormat(ResultFormat.TURTLE);
  }

  @Override
  protected String getTestDir() {
    return "rdf/";
  }

  @Test
  void execute_successful() throws IOException {
    assertDoesNotThrow(() -> command().execute(client));

    verify(client).createUri(anyString());
    verify(client).execute(requestCaptor.capture(), any());

    HttpUriRequest request = requestCaptor.getValue();

    assertEquals(
        "text/turtle;charset=UTF-8",
        request.getFirstHeader(HttpHeaders.ACCEPT).getValue());
  }

  @Test
  void execute_failure() throws IOException {
    when(client.execute(any(), any())).thenThrow(new IOException("Test error"));

    RefineException exc = assertThrows(RefineException.class, () -> command().execute(client));

    assertEquals(
        "Export of RDF data failed for project: '" + PROJECT_ID + "' due to: Test error",
        exc.getMessage());

    verify(client).createUri(anyString());
    verify(client).execute(any(), any());
  }

  @Test
  void handleResponse_asFileStream() throws IOException {
    try (InputStream is = new ByteArrayInputStream("dummy RDF data".getBytes())) {
      SparqlBasedRdfExportCommand command = commandBuilder().setOutput(OutputType.FILE).build();
      ExportRdfResponse response =
          command.handleResponse(okResponse(is, BigInteger.valueOf(Integer.MAX_VALUE)));
      assertEquals("dummy RDF data", IOUtils.toString(response.getResultStream(), UTF_8));
    }
  }
}
