package com.ontotext.refine.client.command.create;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpStatus.SC_MOVED_TEMPORARILY;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;

import com.ontotext.refine.client.Options;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.UploadFormat;
import com.ontotext.refine.client.command.RefineCommand;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * A command to create a project.
 */
public class CreateProjectCommand implements RefineCommand<CreateProjectResponse> {

  private final String name;
  private final File file;
  private final UploadFormat format;
  private final Options options;
  private final String token;

  /**
   * Constructor for {@link Builder}.
   *
   * @param name the project name
   * @param file the file containing the data to upload
   * @param format the optional upload format
   * @param options the optional options
   * @param token the csrf token
   */
  private CreateProjectCommand(
      String name, File file, UploadFormat format, Options options, String token) {
    this.name = name;
    this.file = file;
    this.format = format;
    this.options = options;
    this.token = token;
  }

  @Override
  public String endpoint() {
    return "/orefine/command/core/create-project-from-upload";
  }

  @Override
  public CreateProjectResponse execute(RefineClient client) throws RefineException {
    try {
      MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
      if (format != null) {
        multipartEntityBuilder.addTextBody("format", format.getValue(), TEXT_PLAIN);
      }

      if (options != null) {
        multipartEntityBuilder.addTextBody("options", options.asJson(), APPLICATION_JSON);
      }

      HttpEntity entity = multipartEntityBuilder
          .addBinaryBody("project-file", file)
          .addTextBody("project-name", name, TEXT_PLAIN)
          .build();

      HttpUriRequest request = RequestBuilder
          .post(client.createUri(endpoint()))
          .addParameter(Constants.CSRF_TOKEN, token)
          .setHeader(ACCEPT, APPLICATION_JSON.getMimeType())
          .setEntity(entity)
          .build();

      return client.execute(request, this);
    } catch (IOException ioe) {
      String error = String.format("Failed to create project due to: '%s'", ioe.getMessage());
      throw new RefineException(error);
    }
  }

  @Override
  public CreateProjectResponse handleResponse(HttpResponse response) throws IOException {
    // TODO: parse errors in refine are returned as HTML
    if (SC_MOVED_TEMPORARILY != response.getStatusLine().getStatusCode()) {
      throw new RefineException(
          "Failed to create refine project. Please ensure that all of the provided configurations"
              + " and data are correct.");
    }

    Header location = response.getFirstHeader("Location");
    if (location == null) {
      throw new RefineException(
          "No location header found. The header is required for the project identifier retrieval.");
    }

    URL url = new URL("http", "", location.getValue());
    return new CreateProjectResponse(url);
  }

  /**
   * The builder for {@link CreateProjectCommand}.
   */
  public static class Builder {

    private String name;
    private File file;
    private UploadFormat format;
    private Options options;
    private String token;

    /**
     * Sets the project name.
     *
     * @param name the project name
     * @return the builder for fluent usage
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Sets token.
     *
     * @param token the csrf token
     * @return the builder for fluent usage
     */
    public Builder token(String token) {
      this.token = token;
      return this;
    }

    /**
     * Sets the file containing the data to upload.
     *
     * @param file the file containing the data to upload
     * @return the builder for fluent usage
     */
    public Builder file(File file) {
      this.file = file;
      return this;
    }

    /**
     * Sets the optional upload format.
     *
     * @param format the optional upload format
     * @return the builder for fluent usage
     */
    public Builder format(UploadFormat format) {
      this.format = format;
      return this;
    }

    /**
     * Sets the optional options.
     *
     * @param options the optional options
     * @return the builder for fluent usage
     */
    public Builder options(Options options) {
      this.options = options;
      return this;
    }

    /**
     * Builds the command after validation.
     *
     * @return the command
     */
    public CreateProjectCommand build() {
      notBlank(name, "Missing 'name' argument");
      notNull(file, "Missing 'file' argument");
      notBlank(token, "Missing CSRF token");
      return new CreateProjectCommand(name, file, format, options, token);
    }
  }
}
