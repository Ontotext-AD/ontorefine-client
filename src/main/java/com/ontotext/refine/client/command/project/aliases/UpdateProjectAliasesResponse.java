package com.ontotext.refine.client.command.project.aliases;

import com.ontotext.refine.client.RefineResponse;
import com.ontotext.refine.client.ResponseCode;

/**
 * Represents the response from the {@link UpdateProjectAliasesCommand}.
 *
 * @author Antoniy Kunchev
 */
public class UpdateProjectAliasesResponse extends RefineResponse {

  private UpdateProjectAliasesResponse(ResponseCode code, String message) {
    super(code, message);
  }

  /**
   * Creates an successful response instance.
   *
   * @return response with code OK
   */
  static UpdateProjectAliasesResponse ok() {
    return new UpdateProjectAliasesResponse(ResponseCode.OK, null);
  }

  /**
   * Creates an error response instance.
   *
   * @param message provides details for the cause of the error
   * @return response with code ERROR and details for the cause
   */
  static UpdateProjectAliasesResponse error(String message) {
    return new UpdateProjectAliasesResponse(ResponseCode.ERROR, message);
  }
}
