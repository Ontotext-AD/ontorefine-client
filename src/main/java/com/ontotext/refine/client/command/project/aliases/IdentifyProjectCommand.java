package com.ontotext.refine.client.command.project.aliases;

import static org.apache.commons.lang3.Validate.notBlank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommand;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

/**
 * A command that identifies project from provided value. The value could be project ID or alias.
 * The command will handle prefixed values as well.<br>
 * This command is not compatible with pure OpenRefine instance, it will work only with Ontotext
 * Refine version 1.2 and above.
 *
 * @author Antoniy Kunchev
 */
public class IdentifyProjectCommand implements RefineCommand<IdentifyProjectResponse> {

  private final String value;

  private IdentifyProjectCommand(String value) {
    this.value = value;
  }

  @Override
  public String endpoint() {
    return "/project-aliases/identify";
  }

  @Override
  public IdentifyProjectResponse execute(RefineClient client) throws RefineException {
    try {
      HttpUriRequest request = RequestBuilder
          .get(client.createUri(endpoint()))
          .addParameter("value", value)
          .build();

      return client.execute(request, this);
    } catch (IOException ioe) {
      String error = String.format("Failed to identify project using value: '%s' due to: %s",
          value,
          ioe.getMessage());
      throw new RefineException(error);
    }
  }

  @Override
  public IdentifyProjectResponse handleResponse(HttpResponse response) throws IOException {
    if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
      throw new RefineException(response.getStatusLine().getReasonPhrase());
    }

    try (InputStream is = response.getEntity().getContent()) {
      return new ObjectMapper().readValue(is, IdentifyProjectResponse.class);
    }
  }

  /**
   * Builder for the {@link IdentifyProjectCommand}.
   *
   * @author Antoniy Kunchev
   */
  public static class Builder {

    private String value;

    public Builder setValue(String value) {
      this.value = value;
      return this;
    }

    /**
     * Validates the provided data and builds the command.
     *
     * @return the command
     */
    public IdentifyProjectCommand build() {
      notBlank(value, "The 'value' argument should not be blank.");
      return new IdentifyProjectCommand(value);
    }
  }
}
