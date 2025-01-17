package com.ontotext.refine.client.command;

import com.ontotext.refine.client.command.create.CreateProjectCommand;
import com.ontotext.refine.client.command.csrf.GetCsrfTokenCommand;
import com.ontotext.refine.client.command.delete.DeleteProjectCommand;
import com.ontotext.refine.client.command.export.ExportRowsCommand;
import com.ontotext.refine.client.command.models.GetProjectModelsCommand;
import com.ontotext.refine.client.command.operations.ApplyOperationsCommand;
import com.ontotext.refine.client.command.operations.GetOperationsCommand;
import com.ontotext.refine.client.command.preferences.GetPreferenceCommand;
import com.ontotext.refine.client.command.preferences.SetPreferenceCommand;
import com.ontotext.refine.client.command.processes.GetProcessesCommand;
import com.ontotext.refine.client.command.project.aliases.IdentifyProjectCommand;
import com.ontotext.refine.client.command.project.aliases.UpdateProjectAliasesCommand;
import com.ontotext.refine.client.command.project.configurations.GetProjectConfigurationsCommand;
import com.ontotext.refine.client.command.rdf.DefaultRdfExportCommand;
import com.ontotext.refine.client.command.rdf.GraphDbSparqlBasedRdfExportCommand;
import com.ontotext.refine.client.command.rdf.SparqlBasedRdfExportCommand;
import com.ontotext.refine.client.command.reconcile.GuessColumnTypeCommand;
import com.ontotext.refine.client.command.reconcile.ReconServiceRegistrationCommand;
import com.ontotext.refine.client.command.reconcile.ReconcileCommand;
import com.ontotext.refine.client.command.version.GetVersionCommand;

/**
 * Provides all of the available commands for the Refine tool.
 */
public interface RefineCommands {

  /**
   * Provides a builder instance for the {@link ApplyOperationsCommand}.
   *
   * @return new builder instance
   */
  static ApplyOperationsCommand.Builder applyOperations() {
    return new ApplyOperationsCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link CreateProjectCommand}.
   *
   * @return new builder instance
   */
  static CreateProjectCommand.Builder createProject() {
    return new CreateProjectCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link DeleteProjectCommand}.
   *
   * @return new builder instance
   */
  static DeleteProjectCommand.Builder deleteProject() {
    return new DeleteProjectCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link ExpressionPreviewCommand}.
   *
   * @return new builder instance
   */
  static ExpressionPreviewCommand.Builder expressionPreview() {
    return new ExpressionPreviewCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GetVersionCommand}.
   *
   * @return new builder instance
   */
  static GetVersionCommand.Builder getVersion() {
    return new GetVersionCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GetProjectMetadataCommand}.
   *
   * @return new builder instance
   */
  static GetProjectMetadataCommand.Builder getProjectMetadataCommand() {
    return new GetProjectMetadataCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GetCsrfTokenCommand}.
   *
   * @return new builder instance
   */
  static GetCsrfTokenCommand.Builder getCsrfToken() {
    return new GetCsrfTokenCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link ExportRowsCommand}.
   *
   * @return new builder instance
   */
  static ExportRowsCommand.Builder exportRows() {
    return new ExportRowsCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GetOperationsCommand}.
   *
   * @return new builder instance
   */
  static GetOperationsCommand.Builder getOperations() {
    return new GetOperationsCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link DefaultRdfExportCommand}.
   *
   * @return new builder instance
   */
  static DefaultRdfExportCommand.Builder exportRdf() {
    return new DefaultRdfExportCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link SparqlBasedRdfExportCommand}.
   *
   * @return new builder instance
   */
  static SparqlBasedRdfExportCommand.Builder exportAsRdf() {
    return new SparqlBasedRdfExportCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GraphDbSparqlBasedRdfExportCommand}.
   *
   * <p>CAUSION! The command may not work, because of the separation of the GraphDB and Ontotext
   * Refine tool. The logic is out-of-date, regardless of the fix that is implemented at the moment.
   *
   * @return new builder instance
   */
  static GraphDbSparqlBasedRdfExportCommand.Builder exportRdfUsingSparql() {
    return new GraphDbSparqlBasedRdfExportCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GuessColumnTypeCommand}.
   *
   * @return new builder instance
   */
  static GuessColumnTypeCommand.Builder guessTypeOfColumn() {
    return new GuessColumnTypeCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GetProcessesCommand}.
   *
   * @return new builder instance
   */
  static GetProcessesCommand.Builder getProcesses() {
    return new GetProcessesCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link ReconcileCommand}.
   *
   * @return new builder instance
   */
  static ReconcileCommand.Builder reconcile() {
    return new ReconcileCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GetProjectModelsCommand}.
   *
   * @return new builder instance
   */
  static GetProjectModelsCommand.Builder getProjectModels() {
    return new GetProjectModelsCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link ReconServiceRegistrationCommand}.
   *
   * @return new builder instance
   */
  static ReconServiceRegistrationCommand.Builder registerReconciliationService() {
    return new ReconServiceRegistrationCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link SetPreferenceCommand}.
   *
   * @return new builder instance
   */
  static SetPreferenceCommand.Builder setPreference() {
    return new SetPreferenceCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GetPreferenceCommand}.
   *
   * @return new builder instance
   */
  static GetPreferenceCommand.Builder getPreference() {
    return new GetPreferenceCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link GetProjectConfigurationsCommand}.<br>
   * The command requires 'project-configurations' extension.<br>
   * This extension is added by default to Ontotext Refine version 1.2 and above.
   *
   * @return new builder instance
   */
  static GetProjectConfigurationsCommand.Builder getProjectConfigurations() {
    return new GetProjectConfigurationsCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link IdentifyProjectCommand}.<br>
   * The command requires Ontotext Refine version 1.2 and above.
   *
   * @return new builder instance
   */
  static IdentifyProjectCommand.Builder identifyProject() {
    return new IdentifyProjectCommand.Builder();
  }

  /**
   * Provides a builder instance for the {@link UpdateProjectAliasesCommand}.<br>
   * The command requires Ontotext Refine version 1.2 and above.
   *
   * @return new builder instance
   */
  static UpdateProjectAliasesCommand.Builder updateProjectAliases() {
    return new UpdateProjectAliasesCommand.Builder();
  }
}
