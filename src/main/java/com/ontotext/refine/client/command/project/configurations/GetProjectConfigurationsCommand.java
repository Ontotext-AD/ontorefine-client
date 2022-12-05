package com.ontotext.refine.client.command.project.configurations;

import static com.ontotext.refine.client.command.RefineCommand.Constants.PROJECT;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommand;
import com.ontotext.refine.client.command.operations.GetOperationsCommand;
import com.ontotext.refine.client.exceptions.RefineException;
import com.ontotext.refine.client.util.HttpParser;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang3.Validate;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

/**
 * A command that retrieves the configurations for specified project. The command depends on the
 * 'project-configurations` extension in order to work.
 *
 * @author Antoniy Kunchev
 */
public class GetProjectConfigurationsCommand
    implements RefineCommand<GetProjectConfigurationsResponse> {

  private final String project;

  private GetProjectConfigurationsCommand(String project) {
    this.project = project;
  }

  @Override
  public String endpoint() {
    return "/orefine/command/project-configurations/export";
  }

  @Override
  public GetProjectConfigurationsResponse execute(RefineClient client) throws RefineException {
    try {
      HttpUriRequest request = RequestBuilder
          .get(client.createUri(endpoint()))
          .addParameter(PROJECT, project)
          .build();

      return client.execute(request, this);
    } catch (IOException ioe) {
      String error = String.format(
          "Failed to retrieve the configurations of project: '%s' due to: %s",
          project,
          ioe.getMessage());
      throw new RefineException(error);
    }
  }

  @Override
  public GetProjectConfigurationsResponse handleResponse(HttpResponse response) throws IOException {
    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
      throw new RefineException("Unable to retrieve the project configurations. Ensure that the"
          + " extension is installed on the Refine instance that you are trying to access.");
    }

    HttpParser.HTTP_PARSER.assureStatusCode(response, HttpStatus.SC_OK);
    try (InputStream stream = response.getEntity().getContent()) {
      JsonMapper mapper = new JsonMapper();
      return new GetProjectConfigurationsResponse()
          .setContent(mapper.readTree(stream))
          .setProject(project);
    }
  }

  /**
   * Builder for {@link GetProjectConfigurationsCommand}.
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
    public GetProjectConfigurationsCommand build() {
      Validate.notBlank(project, "Missing 'project' argument");
      return new GetProjectConfigurationsCommand(project);
    }
  }
}
