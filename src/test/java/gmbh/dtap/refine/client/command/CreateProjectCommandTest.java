/*
 * Copyright 2019 DTAP GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gmbh.dtap.refine.client.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import gmbh.dtap.refine.client.RefineClient;
import gmbh.dtap.refine.client.UploadFormat;

/**
 * Unit Tests for {@link CreateProjectCommand}.
 */
public class CreateProjectCommandTest {

  @Mock
  private RefineClient refineClient;

	private CreateProjectCommand command;

    @BeforeEach
	public void setUp() throws MalformedURLException {
		refineClient = mock(RefineClient.class);
		when(refineClient.createUrl(anyString())).thenReturn(new URL("http://localhost:3333/"));

		command = RefineCommands.createProject()
			.token("test-token")
			.name("JSON Test (Main)")
			.file(new File("src/test/resources/addresses.csv"))
			.format(UploadFormat.SEPARATOR_BASED)
			.options(() -> "{ \"encoding\": \"UTF-8\", \"projectTags\": \"[foo]\", \"separator\": \",\"}")
			.build();
	}

	@Test
	public void should_execute() throws IOException {
		command.execute(refineClient);

		verify(refineClient).createUrl(anyString());
		verify(refineClient).execute(any(), any());
	}
}
