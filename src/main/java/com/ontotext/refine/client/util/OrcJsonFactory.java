package com.ontotext.refine.client.util;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Utility containing convenient methods for building JSON documents using the Jackson API.
 *
 * @author Antoniy Kunchev
 */
public enum OrcJsonFactory {
  ;

  /**
   * Shortcut method providing access to the {@link JsonNodeFactory#instance}.
   *
   * @return standard {@link JsonNodeFactory} instance
   */
  public static JsonNodeFactory get() {
    return JsonNodeFactory.instance;
  }

  /**
   * Shortcut method for {@link JsonNodeFactory#objectNode()}.
   *
   * @return new {@link ObjectNode} instance
   */
  public static ObjectNode object() {
    return get().objectNode();
  }
}
