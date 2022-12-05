package com.ontotext.refine.client.command.project.configurations;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;

/**
 * Holds the response from the {@link GetProjectConfigurationsCommand}.
 *
 * @author Antoniy Kunchev
 */
public class GetProjectConfigurationsResponse {

  private String project;
  private JsonNode content;

  public String getProject() {
    return project;
  }

  public GetProjectConfigurationsResponse setProject(String project) {
    this.project = project;
    return this;
  }

  public JsonNode getContent() {
    return content;
  }

  public GetProjectConfigurationsResponse setContent(JsonNode content) {
    this.content = content;
    return this;
  }

  /**
   * Retrieves the project import options from the result content.
   *
   * @return import options if found, otherwise empty optional
   */
  public Optional<JsonNode> getImportOptions() {
    return Optional.ofNullable(content.get("importOptions"));
  }

  /**
   * Retrieves the project operations history from the result content.
   *
   * @return operations history if found, otherwise empty optional
   */
  public Optional<JsonNode> getOperations() {
    return Optional.ofNullable(content.get("operations"));
  }
}
