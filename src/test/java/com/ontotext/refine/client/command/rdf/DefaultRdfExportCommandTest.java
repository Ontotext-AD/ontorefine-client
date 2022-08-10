package com.ontotext.refine.client.command.rdf;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.ontotext.refine.client.command.BaseCommandTest;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

/**
 * Test for {@link DefaultRdfExportCommand}.
 *
 * @author Antoniy Kunchev
 */
class DefaultRdfExportCommandTest
    extends BaseCommandTest<ExportRdfResponse, DefaultRdfExportCommand> {

  @Captor
  private ArgumentCaptor<HttpUriRequest> requestCaptor;

  private String mapping;

  @BeforeEach
  void init() throws IOException {
    mapping = IOUtils.toString(loadResource("getOperations_response.json"), StandardCharsets.UTF_8);
  }

  @Override
  protected DefaultRdfExportCommand command() {
    return commandBuilder().build();
  }

  private DefaultRdfExportCommand.Builder commandBuilder() {
    return RefineCommands.exportRdf()
        .setProject(PROJECT_ID)
        .setMapping(mapping)
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
  void execute_failToExtractMapping() throws IOException {

    DefaultRdfExportCommand command = commandBuilder().setMapping("{}").build();
    RefineException exc = assertThrows(RefineException.class, () -> command.execute(client));

    assertEquals(
        "Export of RDF data for project: '" + PROJECT_ID
            + "' failed due to unavailable mapping. Please recheck if the"
            + " mapping you are providing is correct.",
        exc.getMessage());

    verifyNoInteractions(client);
  }

  @Test
  void handleResponse_asStringExplicit() throws IOException {
    byte[] bytes = "dummy RDF data".getBytes();
    try (InputStream is = new ByteArrayInputStream(bytes)) {
      DefaultRdfExportCommand command =
          commandBuilder().setFormat(ResultFormat.TURTLE).setOutput(OutputType.STRING).build();
      ExportRdfResponse response =
          command.handleResponse(okResponse(is, BigInteger.valueOf(bytes.length)));
      assertEquals("dummy RDF data", response.getResult());
    }
  }

  @Test
  void handleResponse_asStringExceedingBufferSize() throws IOException {
    byte[] bytes = "dummy RDF data".getBytes();
    try (InputStream is = new ByteArrayInputStream(bytes)) {
      DefaultRdfExportCommand command =
          commandBuilder().setFormat(ResultFormat.TURTLE).setOutput(OutputType.STRING).build();
      ExportRdfResponse response =
          command.handleResponse(okResponse(is, BigInteger.valueOf(Integer.MAX_VALUE)));
      assertEquals("dummy RDF data", response.getResult());
    }
  }

  @Test
  void handleResponse_asFileExplicit() throws IOException {
    try (InputStream is = new ByteArrayInputStream("dummy RDF data".getBytes())) {
      DefaultRdfExportCommand command =
          commandBuilder().setFormat(ResultFormat.TURTLE).setOutput(OutputType.FILE).build();
      ExportRdfResponse response = command.handleResponse(okResponse(is));
      assertEquals("dummy RDF data", FileUtils.readFileToString(response.getResultFile(), UTF_8));
      FileUtils.deleteQuietly(response.getResultFile());
    }
  }

  @Test
  void handleResponse_asFileStream() throws IOException {
    try (InputStream is = new ByteArrayInputStream("dummy RDF data".getBytes())) {
      ExportRdfResponse response =
          command().handleResponse(okResponse(is, BigInteger.valueOf(Integer.MAX_VALUE)));
      assertEquals("dummy RDF data", IOUtils.toString(response.getResultStream(), UTF_8));
    }
  }

  @Test
  void handleResponse_asStringStream() throws IOException {
    byte[] bytes = "dummy RDF data".getBytes();
    try (InputStream is = new ByteArrayInputStream(bytes)) {
      ExportRdfResponse response =
          command().handleResponse(okResponse(is, BigInteger.valueOf(bytes.length)));
      assertEquals("dummy RDF data", IOUtils.toString(response.getResultStream(), UTF_8));
    }
  }
}
