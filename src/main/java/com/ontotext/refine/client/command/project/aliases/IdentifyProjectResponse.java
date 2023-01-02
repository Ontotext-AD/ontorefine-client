package com.ontotext.refine.client.command.project.aliases;

import java.util.Set;

/**
 * Represents the response from the {@link IdentifyProjectCommand}.
 *
 * @author Antoniy Kunchev
 */
public class IdentifyProjectResponse {

  private long identifier;
  private String original;
  private Set<String> aliases;
  private String prefix;

  IdentifyProjectResponse() {
    // default
  }

  public long getIdentifier() {
    return identifier;
  }

  public String getOriginal() {
    return original;
  }

  public Set<String> getAliases() {
    return aliases;
  }

  public String getPrefix() {
    return prefix;
  }
}
