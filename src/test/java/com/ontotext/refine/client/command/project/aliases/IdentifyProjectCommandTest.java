package com.ontotext.refine.client.command.project.aliases;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ontotext.refine.client.command.BaseCommandTest;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

/**
 * Test for {@link IdentifyProjectCommand}.
 *
 * @author Antoniy Kunchev
 */
class IdentifyProjectCommandTest
    extends BaseCommandTest<IdentifyProjectResponse, IdentifyProjectCommand> {

  @Captor
  private ArgumentCaptor<HttpUriRequest> requestCaptor;

  private String value;

  @BeforeEach
  void defaultSetup() {
    value = PROJECT_ID;
  }

  @Override
  protected IdentifyProjectCommand command() {
    return RefineCommands.identifyProject().setValue(value).build();
  }

  @Override
  protected String getTestDir() {
    return "project-aliases/";
  }

  @Test
  void execute_successful() throws IOException {
    assertDoesNotThrow(() -> command().execute(client));

    verify(client).createUri(anyString());
    verify(client).execute(requestCaptor.capture(), any());

    HttpUriRequest request = requestCaptor.getValue();

    assertEquals("GET", request.getMethod());

    assertEquals(
        BASE_URI + "/project-aliases/identify?value=" + PROJECT_ID,
        request.getURI().toString());
  }

  @Test
  void execute_error() throws IOException {
    when(client.execute(any(), any())).thenThrow(new IOException("Test error"));

    RefineException exception =
        assertThrows(RefineException.class, () -> command().execute(client));

    assertEquals(
        "Failed to identify project using value: '" + PROJECT_ID + "' due to: Test error",
        exception.getMessage());
  }

  @Test
  void handleResponse_successful() throws IOException {
    IdentifyProjectResponse response =
        command().handleResponse(okResponse(loadResource("identifyProject_response.json")));

    assertNotNull(response);
    assertEquals(Long.valueOf(PROJECT_ID), response.getIdentifier());
    assertEquals("ontorefine:test-alias", response.getOriginal());
    assertEquals("test-alias", response.getAliases().iterator().next());
    assertEquals("ontorefine", response.getPrefix());
  }

  @Test
  void handleResponse_unexpectedStatusCode() throws IOException {
    HttpResponse response = mock(HttpResponse.class);
    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 404, "NOT FOUND"));

    assertThrows(RefineException.class, () -> command().handleResponse(response));
  }
}
