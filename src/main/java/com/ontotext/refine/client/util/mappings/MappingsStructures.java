package com.ontotext.refine.client.util.mappings;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Contains class representations of the JSON object structures for the mapping JSON used for RDF
 * export.
 *
 * @author Antoniy Kunchev
 */
final class MappingsStructures {

  private MappingsStructures() {
    // utility
  }

  /**
   * Structure for materializing the mapping JSON and detect if it can be used directly or it needs
   * to be normalized.
   *
   * @author Antoniy Kunchev
   */
  static class MappingJson {

    @JsonProperty("baseIRI")
    private String baseIri;

    @JsonProperty
    private Map<String, String> namespaces;

    @JsonProperty
    private List<Object> subjectMappings;
  }

  /**
   * Structure for materializing the single operation from operations JSON.
   *
   * @author Antoniy Kunchev
   */
  static class OperationEntry {

    @JsonProperty
    private String description;

    @JsonProperty
    @JsonAlias({"op", "operation"})
    private Object operation;
  }

  /**
   * Relaxed structure for materializing the single operation from operations JSON. It will ignore
   * any unknown property.
   *
   * @author Antoniy Kunchev
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  static class RelaxedOperationEntry extends OperationEntry {
    // nothing more
  }

  /**
   * Structure for materializing the operations JSON.
   *
   * @author Antoniy Kunchev
   */
  static class OperationsJson {

    @JsonProperty
    private List<OperationEntry> entries;
  }
}
