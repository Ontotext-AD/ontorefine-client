package com.ontotext.refine.client.command.rdf;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommand;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command that exports the data of specific project in RDF format using a SPARQL Construct query.
 * The command uses the SPARQL endpoint that Ontotext Refine tool provides, thus avoiding the
 * dependency to the GraphDB. <br>
 * Additionally the query doesn't need to be a template and contain <code>SERVICE</code> clause for
 * federation, it can be simple <code>CONSTRUCT</code> query.<br>
 * Example:
 *
 * <pre>
 * BASE &lt;http://example.com/base/&gt;
 *
 * CONSTRUCT {
 *   ?subject &lt;title&gt; ?o_title .
 * } WHERE {
 *   BIND(&lt;subject&gt; as ?subject)
 *   BIND(STR(?c_Title) as ?o_title)
 * }
 * </pre>
 *
 * @author Antoniy Kunchev
 */
public class SparqlBasedRdfExportCommand implements RefineCommand<ExportRdfResponse> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SparqlBasedRdfExportCommand.class);

  private static final String SPARQL_QUERY_CONTENT_TYPE = "application/sparql-query";
  private static final String ONTOREFINE_PREFIX = "ontorefine:";

  private final String project;
  private final String prefix;
  private final String query;
  private final ResultFormat format;
  private final OutputType output;

  private SparqlBasedRdfExportCommand(
      String project, String prefix, String query, ResultFormat format, OutputType output) {
    this.project = project;
    this.prefix = prefix;
    this.query = query;
    this.format = format;
    this.output = output;
  }

  @Override
  public String endpoint() {
    return "/repositories/";
  }

  @Override
  public ExportRdfResponse execute(RefineClient client) throws RefineException {
    try {
      String pfx = StringUtils.defaultIfBlank(prefix, ONTOREFINE_PREFIX);
      HttpUriRequest request = RequestBuilder
          .post(client.createUri(endpoint() + pfx + project))
          .addHeader(ACCEPT, RdfExportUtils.getAcceptHeader(format))
          .addHeader(CONTENT_TYPE, SPARQL_QUERY_CONTENT_TYPE)
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
    LOGGER.debug("The query that will be used for RDF export is: {}", query);
    return new BufferedHttpEntity(new StringEntity(query, UTF_8));
  }

  @Override
  public ExportRdfResponse handleResponse(HttpResponse response) throws IOException {
    return RdfExportResponseHandler.handle(project, response, output);
  }

  /**
   * Builder for the {@link SparqlBasedRdfExportCommand}.
   *
   * @author Antoniy Kunchev
   */
  public static class Builder {

    private String project;
    private String prefix;
    private String query;
    private ResultFormat format;
    private OutputType output;

    public Builder setProject(String project) {
      this.project = project;
      return this;
    }

    public Builder setPrefix(String prefix) {
      this.prefix = prefix;
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

    public Builder setOutput(OutputType output) {
      this.output = output;
      return this;
    }

    /**
     * Builds new {@link SparqlBasedRdfExportCommand}.
     *
     * @return new command
     */
    public SparqlBasedRdfExportCommand build() {
      notBlank(project, "Missing 'project' argument");
      notBlank(query, "Missing 'query' argument");
      notNull(format, "Missing 'format' argument");
      return new SparqlBasedRdfExportCommand(project, prefix, query, format, output);
    }
  }
}
