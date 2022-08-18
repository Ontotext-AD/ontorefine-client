package com.ontotext.refine.client.command.export;

import static com.ontotext.refine.client.util.OrcJsonFactory.object;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Provides a engine options for the {@link ExportRowsCommand}.
 *
 * @author Antoniy Kunchev
 */
public enum Engines {

  ROW_BASED(object().put("mode", "row-based")),

  RECORD_BASED(object().put("mode", "record-based"));

  private final JsonNode engine;

  private Engines(final JsonNode engine) {
    this.engine = engine;
  }

  /**
   * Provides a string representation of the engine configuration.
   *
   * @return the string representation of the JSON that is the used for engine configuration of the
   *         refine tool
   */
  public String get() {
    return engine.toString();
  }
}
