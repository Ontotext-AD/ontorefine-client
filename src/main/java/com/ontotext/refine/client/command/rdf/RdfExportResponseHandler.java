package com.ontotext.refine.client.command.rdf;

import static com.ontotext.refine.client.command.rdf.RdfExportUtils.createTempFile;
import static com.ontotext.refine.client.util.HttpParser.HTTP_PARSER;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpStatus.SC_OK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;

/**
 * Utility class for providing handling of the {@link HttpResponse} from the RDF export commands.
 *
 * @author Antoniy Kunchev
 */
class RdfExportResponseHandler {

  private RdfExportResponseHandler() {
    throw new UnsupportedOperationException("Utility classes should not be instantiated.");
  }

  /**
   * Handles the response from RDF export command.
   *
   * @param project identifier which data was exported
   * @param response that was returned from the request execution
   * @param output the type of the output that should be used for the result
   * @return {@link ExportRdfResponse} object containing the result from the export command
   * @throws IOException when there is a problem during the response handling
   */
  static ExportRdfResponse handle(String project, HttpResponse response, OutputType output)
      throws IOException {
    HTTP_PARSER.assureStatusCode(response, SC_OK);

    HttpEntity entity = response.getEntity();
    try (InputStream stream = entity.getContent()) {
      ExportRdfResponse rdfResponse = new ExportRdfResponse().setProject(project);

      if (OutputType.FILE.equals(output)) {
        return toFile(stream, rdfResponse);
      }

      long length = Math.max(entity.getContentLength(), getFromHeader(response));
      boolean buffer = canBuffer(length);
      if (OutputType.STRING.equals(output) && buffer) {
        return toStr(stream, rdfResponse);
      }

      return buffer ? toStr(stream, rdfResponse) : toFile(stream, rdfResponse);
    }
  }

  private static long getFromHeader(HttpResponse response) {
    Header header = response.getFirstHeader(HttpHeaders.CONTENT_LENGTH);
    return header == null ? 0 : Long.valueOf(header.getValue());
  }

  private static boolean canBuffer(long length) {
    return length > 0 && length < Integer.MAX_VALUE;
  }

  private static ExportRdfResponse toStr(InputStream stream, ExportRdfResponse response)
      throws IOException {
    return response.setResult(IOUtils.toString(stream, UTF_8));
  }

  private static ExportRdfResponse toFile(InputStream stream, ExportRdfResponse response)
      throws IOException {
    File tempFile = createTempFile(response.getProject()).toFile();
    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      IOUtils.copyLarge(stream, fos);
    }
    return response.setResultFile(tempFile);
  }
}
