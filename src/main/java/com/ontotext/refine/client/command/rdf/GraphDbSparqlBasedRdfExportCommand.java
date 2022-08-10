package com.ontotext.refine.client.command.rdf;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_FORM_URLENCODED;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommand;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.IOException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command that exports the data of specific project in RDF format using a SPARQL Construct query.
 * The query should be template containing specific placeholder for the project identifier. The
 * placeholder allows execution of the same query over multiple projects with same data
 * structure.<br>
 * The default placeholder set in the command is <code>#project_placeholder#</code>, but it can be
 * changed by providing different one, when building the command.
 *
 * <p>CAUSION! The command may not work, because of the separation of the GraphDB and Ontotext
 * Refine tool. The proxying of the request is an attempt to temporary fix it, but in general the
 * logic is out-of-date.
 *
 * @author Antoniy Kunchev
 */
public class GraphDbSparqlBasedRdfExportCommand implements RefineCommand<ExportRdfResponse> {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(GraphDbSparqlBasedRdfExportCommand.class);

  private final String project;
  private final String projectPlaceholder;
  private final String query;
  private final ResultFormat format;
  private final String repository;
  private final OutputType output;

  protected GraphDbSparqlBasedRdfExportCommand(
      String project,
      String projectPlaceholder,
      String query,
      ResultFormat format,
      String repository,
      OutputType output) {
    this.project = project;
    this.projectPlaceholder = projectPlaceholder;
    this.query = query;
    this.format = format;
    this.repository = repository;
    this.output = output;
  }

  @Override
  public String endpoint() {
    return "/graphdb-proxy/repositories/{repo}";
  }

  @Override
  public ExportRdfResponse execute(RefineClient client) throws RefineException {
    try {
      HttpUriRequest request = RequestBuilder
          .post(client.createUri(endpoint().replace("{repo}", repository)))
          .addHeader(ACCEPT, RdfExportUtils.getAcceptHeader(format))
          .addHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED.withCharset(UTF_8).toString())
          .setEntity(buildEntity())
          .build();

      return client.execute(request, this);
    } catch (IOException ioe) {
      throw new RefineException(
          "Export of RDF data failed for project: '%s' due to: %s",
          project,
          ioe.getMessage());
    }
  }

  /**
   * Builds the request entity with the expected content. The produced {@link HttpEntity} is
   * repeatable so that it can be used in retries.
   */
  private HttpEntity buildEntity() throws IOException {
    return new BufferedHttpEntity(new UrlEncodedFormEntity(buildRequestContent(), UTF_8));
  }

  /**
   * Replaces the placeholder for the project in the query template.<br>
   * Effectively:
   *
   * <pre>
   * SERVICE &lt;rdf-mapper:ontorefine:#project_placeholder#&gt;
   *
   * is transformed into
   *
   * SERVICE &lt;rdf-mapper:ontorefine:1958197932150&gt;
   * </pre>
   * And prepends the expected field for the request.
   */
  private List<NameValuePair> buildRequestContent() {
    String fixedQuery = query.replaceFirst(projectPlaceholder, project);
    LOGGER.debug("The query that will be used for RDF export is: {}", fixedQuery);
    return singletonList(new BasicNameValuePair("query", fixedQuery));
  }

  @Override
  public ExportRdfResponse handleResponse(HttpResponse response) throws IOException {
    return RdfExportResponseHandler.handle(project, response, output);
  }

  /**
   * Builder for {@link GraphDbSparqlBasedRdfExportCommand}.
   *
   * @author Antoniy Kunchev
   */
  public static class Builder {

    private String project;
    private String projectPlaceholder = "#project_placeholder#";
    private String query;
    private ResultFormat format;
    private String repository;
    private OutputType output;

    public Builder setProject(String project) {
      this.project = project;
      return this;
    }

    public Builder setProjectPlaceholder(String projectPlaceholder) {
      this.projectPlaceholder = projectPlaceholder;
      return this;
    }

    public Builder setQuery(String query) {
      this.query = query;
      return this;
    }

    public Builder setFormat(ResultFormat format) {
      this.format = format;
      return this;
    }

    public Builder setRepository(String repository) {
      this.repository = repository;
      return this;
    }

    public Builder setOutput(OutputType output) {
      this.output = output;
      return this;
    }

    /**
     * Builds a {@link GraphDbSparqlBasedRdfExportCommand}.
     *
     * @return a command
     */
    public GraphDbSparqlBasedRdfExportCommand build() {
      notBlank(project, "Missing 'project' argument");
      notBlank(projectPlaceholder, "The 'projectPlaceholder' argument should not be blank");
      notBlank(query, "Missing 'query' argument");
      notNull(format, "Missing 'format' argument");
      notBlank(repository, "Missing 'repository' argument");
      return new GraphDbSparqlBasedRdfExportCommand(
          project, projectPlaceholder, query, format, repository, output);
    }
  }
}
