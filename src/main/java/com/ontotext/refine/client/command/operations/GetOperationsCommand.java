package com.ontotext.refine.client.command.operations;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommand;
import com.ontotext.refine.client.exceptions.RefineException;
import com.ontotext.refine.client.util.HttpParser;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;


/**
 * A command that retrieves the operations history for specified project.
 *
 * @author Antoniy Kunchev
 */
public class GetOperationsCommand implements RefineCommand<GetOperationsResponse> {

  private final String project;

  private GetOperationsCommand(String project) {
    this.project = project;
  }

  @Override
  public String endpoint() {
    return "/orefine/command/core/get-operations";
  }

  @Override
  public GetOperationsResponse execute(RefineClient client) throws RefineException {
    try {
      URL url = client.createUrl(endpoint() + "?project=" + project);
      return client.execute(RequestBuilder.get(url.toString()).build(), this);
    } catch (IOException ioe) {
      String error = String.format(
          "Failed to retrieve the operations for project: '%s' due to: %s",
          project,
          ioe.getMessage());
      throw new RefineException(error);
    }
  }

  @Override
  public GetOperationsResponse handleResponse(HttpResponse response) throws IOException {
    HttpParser.HTTP_PARSER.assureStatusCode(response, HttpStatus.SC_OK);
    JsonMapper mapper = new JsonMapper();
    return new GetOperationsResponse()
        .setContent(mapper.readTree(response.getEntity().getContent()))
        .setProject(project);
  }

  /**
   * Builder for {@link GetOperationsCommand}.
   *
   * @author Antoniy Kunchev
   */
  public static class Builder {

    private String project;

    public Builder setProject(String project) {
      this.project = project;
      return this;
    }

    /**
     * Builds the {@link GetOperationsCommand}.
     *
     * @return a command
     */
    public GetOperationsCommand build() {
      Validate.notBlank(project, "Missing 'project' argument");
      return new GetOperationsCommand(project);
    }
  }
}
