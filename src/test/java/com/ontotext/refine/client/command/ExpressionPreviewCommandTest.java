package com.ontotext.refine.client.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.ResponseCode;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit Tests for {@link ExpressionPreviewCommand}.
 */
class ExpressionPreviewCommandTest {

  private static final Charset UTF_8 = Charset.forName("UTF-8");

  @Mock
  private RefineClient refineClient;

  private ExpressionPreviewCommand command;

  @BeforeEach
  void setUp() throws URISyntaxException {
    MockitoAnnotations.openMocks(this);

    when(refineClient.createUri(anyString())).thenReturn(new URI("http://localhost:3333/"));
    command = RefineCommands
        .expressionPreview()
        .token("test-token")
        .project("1234567890")
        .rowIndices(0)
        .expression("foo")
        .build();
  }

  @Test
  void should_execute() throws IOException {
    command.execute(refineClient);
    verify(refineClient).createUri(anyString());
    verify(refineClient).execute(any(), any());
  }

  @Test
  void should_parse_expression_preview_success_response()
      throws IOException, URISyntaxException {
    String responseBody = IOUtils
        .toString(getClass().getResource("/responseBody/expression-preview.json").toURI(), UTF_8);

    ExpressionPreviewResponse response = command.parseExpressionPreviewResponse(responseBody);
    assertNotNull(response);
    assertEquals(ResponseCode.OK, response.getCode());
    assertNull(response.getMessage());
    assertEquals(Arrays.asList("7", "5", "5", "9"), response.getExpressionPreviews());
  }

  @Test
  void should_parse_expression_preview_error_response()
      throws IOException, URISyntaxException {
    String responseBody =
        IOUtils.toString(getClass().getResource("/responseBody/code-error.json").toURI(), UTF_8);

    ExpressionPreviewResponse response = command.parseExpressionPreviewResponse(responseBody);
    assertNotNull(response);
    assertEquals(ResponseCode.ERROR, response.getCode());
    assertEquals("This is the error message.", response.getMessage());
  }
}
