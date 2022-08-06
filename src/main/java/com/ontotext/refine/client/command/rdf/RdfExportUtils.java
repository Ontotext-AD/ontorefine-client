package com.ontotext.refine.client.command.rdf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * Contains convenient methods used in RDF export commands.
 *
 * @author Antoniy Kunchev
 */
class RdfExportUtils {

  private RdfExportUtils() {
    throw new UnsupportedOperationException("Utility class.");
  }

  /**
   * Creates temporary file with name pattern:<br>
   * <i>project-{project identifier}-rdfExport-{java generated number}.tmp</i><br>
   * in temporary directory prefixed with: <br>
   * <i>ontorefine-client-{java generated number}</i>.
   *
   * @param project the identifer of the refine project, which data is exported
   * @return path to the created file
   * @throws IOException when there is an error during the file creation
   */
  static Path createTempFile(String project) throws IOException {
    Path tempDirectory = Files.createTempDirectory("ontorefine-client-");
    String suffix = "project-" + project + "-rdfExport-";
    return Files.createTempFile(tempDirectory, suffix, ".tmp");
  }

  /**
   * Provides value for accept header derived from the input format.
   *
   * @param format to be used to build the header value
   * @return string representation of the header value
   */
  static String getAcceptHeader(ResultFormat format) {
    RDFFormat rdfFormat = format.getRdfFormat();
    return rdfFormat.getDefaultMIMEType() + ";charset=" + rdfFormat.getCharset();
  }
}
