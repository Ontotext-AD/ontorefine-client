/*
 * Copyright 2019 DTAP GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ontotext.refine.client.command.delete;

import static com.ontotext.refine.client.command.RefineCommand.Constants.CSRF_TOKEN_PARAM;
import static com.ontotext.refine.client.util.HttpParser.HTTP_PARSER;
import static com.ontotext.refine.client.util.JsonParser.JSON_PARSER;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommand;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.IOException;
import java.net.URL;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * A command to delete a project.
 */
public class DeleteProjectCommand implements RefineCommand<DeleteProjectResponse> {

  private final String projectId;
  private final String token;

  /**
   * Constructor for {@link Builder}.
   *
   * @param projectId the project ID
   * @param token the CSRF token to be used
   */
  private DeleteProjectCommand(String projectId, String token) {
    this.projectId = projectId;
    this.token = token;
  }

  @Override
  public String endpoint() {
    return "/orefine/command/core/delete-project";
  }

  @Override
  public DeleteProjectResponse execute(RefineClient client) throws RefineException {
    try {
      URL url = client.createUrl(endpoint() + "?" + CSRF_TOKEN_PARAM + token);

      UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
          singletonList(new BasicNameValuePair("project", projectId)), Consts.UTF_8);

      HttpUriRequest request = RequestBuilder
          .post(url.toString())
          .setHeader(ACCEPT, APPLICATION_JSON.getMimeType())
          .setEntity(entity)
          .build();

      return client.execute(request, this);
    } catch (IOException ioe) {
      String error =
          String.format("Failed to delete project: '%s' due to: %s", projectId, ioe.getMessage());
      throw new RefineException(error);
    }
  }

  @Override
  public DeleteProjectResponse handleResponse(HttpResponse response) throws IOException {
    HTTP_PARSER.assureStatusCode(response, SC_OK);
    String responseBody = EntityUtils.toString(response.getEntity());
    return parseDeleteProjectResponse(responseBody);
  }

  private DeleteProjectResponse parseDeleteProjectResponse(String json) throws IOException {
    JsonNode node = JSON_PARSER.parseJson(json);
    String code = JSON_PARSER.findExistingPath(node, "code").asText();
    if ("ok".equals(code)) {
      return DeleteProjectResponse.ok();
    }

    if ("error".equals(code)) {
      String message = JSON_PARSER.findExistingPath(node, "message").asText();
      return DeleteProjectResponse.error(message);
    }

    throw new RefineException("Unexpected code: " + code);
  }

  /**
   * The builder for {@link DeleteProjectCommand}.
   */
  public static class Builder {

    private String projectId;
    private String token;

    /**
     * Sets the project ID.
     *
     * @param token the csrf token
     * @return the builder for fluent usage
     */
    public Builder token(String token) {
      this.token = token;
      return this;
    }

    /**
     * Sets the project ID.
     *
     * @param projectId the project ID
     * @return the builder for fluent usage
     */
    public Builder project(String projectId) {
      this.projectId = projectId;
      return this;
    }

    /**
     * Builds the command after validation.
     *
     * @return the command
     */
    public DeleteProjectCommand build() {
      notBlank(projectId, "Missing 'projectId' argument");
      notBlank(token, "Missing CSRF token");
      return new DeleteProjectCommand(projectId, token);
    }
  }
}
