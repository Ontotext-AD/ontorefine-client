package com.ontotext.refine.client.command.project.configurations;

import static com.ontotext.refine.client.util.JsonParser.JSON_PARSER;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.client.command.BaseCommandTest;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.IOException;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link GetProjectConfigurationsCommand}.
 *
 * @author Antoniy Kunchev
 */
class GetProjectConfigurationsCommandTest
    extends BaseCommandTest<GetProjectConfigurationsResponse, GetProjectConfigurationsCommand> {

  @Override
  protected GetProjectConfigurationsCommand command() {
    return RefineCommands.getProjectConfigurations().setProject(PROJECT_ID).build();
  }

  @Override
  protected String getTestDir() {
    return "project-configurations/";
  }

  @Test
  void execute_successful() throws IOException {
    assertDoesNotThrow(() -> command().execute(client));

    verify(client).createUri(anyString());
    verify(client).execute(any(), any());
  }

  @Test
  void execute_failure() throws IOException {
    when(client.execute(any(), any())).thenThrow(new IOException("Test error"));

    RefineException exception =
        assertThrows(RefineException.class, () -> command().execute(client));

    assertEquals(
        "Failed to retrieve the configurations of project: '1234567890987' due to: Test error",
        exception.getMessage());

    verify(client).createUri(anyString());
    verify(client).execute(any(), any());
  }

  @Test
  void handleResponse_notFound() throws IOException {
    BasicHttpResponse response =
        new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 404, "NOT FOUND"));
    assertThrows(RefineException.class, () -> command().handleResponse(response));
  }

  @Test
  void handleResponse_successful() throws IOException {
    GetProjectConfigurationsResponse response = command()
        .handleResponse(okResponse(loadResource("getProjectConfigurations_response.json")));

    JsonNode expected =
        JSON_PARSER.parseJson(loadResource("getProjectConfigurations_expected.json"));
    assertEquals(expected, response.getContent());

    assertTrue(response.getImportOptions().isPresent());
    assertTrue(response.getOperations().isPresent());
  }
}
