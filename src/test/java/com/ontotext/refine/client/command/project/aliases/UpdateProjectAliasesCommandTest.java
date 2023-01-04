package com.ontotext.refine.client.command.project.aliases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ontotext.refine.client.ResponseCode;
import com.ontotext.refine.client.command.BaseCommandTest;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicStatusLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

/**
 * Test for {@link UpdateProjectAliasesCommand}.
 *
 * @author Antoniy Kunchev
 */
class UpdateProjectAliasesCommandTest
    extends BaseCommandTest<UpdateProjectAliasesResponse, UpdateProjectAliasesCommand> {

  @Captor
  private ArgumentCaptor<HttpUriRequest> requestCaptor;

  private String project;
  private String[] add;
  private String[] remove;

  @BeforeEach
  void beforeEach() {
    project = PROJECT_ID;
    add = new String[] {"added-test-alias"};
    remove = new String[] {"removed-test-alias"};
  }

  @Override
  protected UpdateProjectAliasesCommand command() {
    return RefineCommands
        .updateProjectAliases()
        .setProject(project)
        .setAdd(add)
        .setRemove(remove)
        .build();
  }

  @Test
  void execute_successful() throws IOException {
    assertDoesNotThrow(() -> command().execute(client));

    verify(client).createUri(anyString());
    verify(client).execute(requestCaptor.capture(), any());

    HttpUriRequest request = requestCaptor.getValue();

    assertEquals("POST", request.getMethod());

    assertEquals(BASE_URI + "/project-aliases", request.getURI().toString());

    // TODO: can check the request payload, but it will require type casting and other nasty hacks
  }

  @Test
  void execute_error() throws IOException {
    when(client.execute(any(), any())).thenThrow(new IOException("Test error"));

    RefineException exception =
        assertThrows(RefineException.class, () -> command().execute(client));

    assertEquals(
        "Failed to update the alias data of project: '" + PROJECT_ID + "' due to: Test error",
        exception.getMessage());
  }

  @Test
  void handleResponse_successful() throws IOException {
    UpdateProjectAliasesResponse response = command().handleResponse(okResponse(null));

    assertEquals(ResponseCode.OK, response.getCode());
  }

  @Test
  void handleResponse_unsuccessful() throws IOException {
    HttpResponse response = mock(HttpResponse.class);
    when(response.getStatusLine())
        .thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 404, "NOT FOUND"));

    ObjectNode json = JsonNodeFactory.instance.objectNode().put("message", "Test error");
    when(response.getEntity())
        .thenReturn(new StringEntity(json.toString(), ContentType.APPLICATION_JSON));

    UpdateProjectAliasesResponse updateResponse = command().handleResponse(response);

    assertEquals(ResponseCode.ERROR, updateResponse.getCode());
    assertEquals("Test error", updateResponse.getMessage());
  }
}
