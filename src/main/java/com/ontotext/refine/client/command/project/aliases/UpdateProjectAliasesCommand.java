package com.ontotext.refine.client.command.project.aliases;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommand;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.IOException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;

/**
 * A command for updating alias data for a given project. The command can add and/or remove multiple
 * aliases with one execution.<br>
 * This command is not compatible with pure OpenRefine instance, it will work only with Ontotext
 * Refine version 1.2 and above.
 *
 * @author Antoniy Kunchev
 */
public class UpdateProjectAliasesCommand implements RefineCommand<UpdateProjectAliasesResponse> {

  private final String project;
  private final String[] add;
  private final String[] remove;

  private UpdateProjectAliasesCommand(String project, String[] add, String[] remove) {
    this.project = project;
    this.add = add;
    this.remove = remove;
  }

  @Override
  public String endpoint() {
    return "/project-aliases";
  }

  @Override
  public UpdateProjectAliasesResponse execute(RefineClient client) throws RefineException {
    try {
      HttpUriRequest request = RequestBuilder
          .post(client.createUri(endpoint()))
          .setEntity(new StringEntity(buildPayload().toString(), APPLICATION_JSON))
          .setHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON.getMimeType())
          .build();

      return client.execute(request, this);
    } catch (IOException ioe) {
      String error = String.format(
          "Failed to update the alias data of project: '%s' due to: %s",
          project,
          ioe.getMessage());
      throw new RefineException(error);
    }
  }

  private JsonNode buildPayload() {
    ObjectNode entity = JsonNodeFactory.instance.objectNode().put("project", project);
    addIfNotNull(add, "added", entity);
    addIfNotNull(remove, "removed", entity);
    return entity;
  }

  private void addIfNotNull(String[] values, String key, ObjectNode entity) {
    if (values == null) {
      return;
    }

    ArrayNode array = JsonNodeFactory.instance.arrayNode();
    for (String elem : values) {
      array.add(elem);
    }

    entity.set(key, array);
  }

  @Override
  public UpdateProjectAliasesResponse handleResponse(HttpResponse response) throws IOException {
    StatusLine statusLine = response.getStatusLine();
    if (HttpStatus.SC_OK == statusLine.getStatusCode()) {
      return UpdateProjectAliasesResponse.ok();
    }
    return UpdateProjectAliasesResponse.error(statusLine.getReasonPhrase());
  }

  /**
   * Builder for the {@link UpdateProjectAliasesCommand}.
   *
   * @author Antoniy Kunchev
   */
  public static class Builder {

    private String project;
    private String[] add;
    private String[] remove;

    public Builder setProject(String project) {
      this.project = project;
      return this;
    }

    public Builder setAdd(String... add) {
      this.add = add;
      return this;
    }

    public Builder setRemove(String... remove) {
      this.remove = remove;
      return this;
    }

    /**
     * Validates the input data and builds the {@link UpdateProjectAliasesCommand}.
     *
     * @return a command
     */
    public UpdateProjectAliasesCommand build() {
      Validate.notBlank(project, "Missing 'project' argument");

      if (ArrayUtils.isEmpty(add) && ArrayUtils.isEmpty(remove)) {
        throw new IllegalArgumentException("Provide alias(es) to add or remove.");
      }

      return new UpdateProjectAliasesCommand(project, add, remove);
    }
  }
}
