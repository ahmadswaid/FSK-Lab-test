/*
 ***************************************************************************************************
 * Copyright (c) 2020 Federal Institute for Risk Assessment (BfR), Germany
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors: Department Biological Safety - BfR
 *************************************************************************************************
 */
package de.bund.bfr.knime.fsklab.v2_0.fskdbview;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.FlowVariableModel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.FlowVariable;

/**
 * This is an implementation of the node dialog of the "FSKDBView" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows creation of a simple
 * dialog with standard components.
 * 
 */
public class FSKDBViewNodeDialog extends DefaultNodeSettingsPane {
  DialogComponentString repository ;
  DialogComponentNumber maxSelectionNumber;
  protected FSKDBViewNodeDialog() {
    
    /*
     * The DefaultNodeSettingsPane provides methods to add simple standard components to the dialog
     * pane via the addDialogComponent(...) method.
     * 
     */
    // First, create a new settings model using the create method from the node model.
    FlowVariableModel repositoryVariable = createFlowVariableModel(
        FSKDBViewNodeModel.KEY_REPOSITORY_LOCATION, FlowVariable.Type.STRING);

    FlowVariableModel maxSelectionNumberVariable =
        createFlowVariableModel(FSKDBViewNodeModel.MAX_SELECTION_NUMBER, FlowVariable.Type.INTEGER);

    // Add a new String component to the dialog for Repository URL.
    SettingsModelString m_repositoryLocationSettings =
        FSKDBViewNodeModel.createRepositoryLocationSettingsModel();
    repository =  new DialogComponentString(m_repositoryLocationSettings,
        "Repository URL", true, 30, repositoryVariable);
    addDialogComponent(repository);
    
    // Add a new Number component to the dialog for the number of models allowed to be selected.
    SettingsModelNumber m_maxSelectionNumberSettings =
        FSKDBViewNodeModel.createMaxSelectionNumberSettingsModel();
    maxSelectionNumber = new DialogComponentNumber(m_maxSelectionNumberSettings,
        "Max Selection Number", 1, maxSelectionNumberVariable);
    addDialogComponent(maxSelectionNumber);
  }
  @Override
  public void saveAdditionalSettingsTo(final NodeSettingsWO settings)
      throws InvalidSettingsException {
    repository.saveSettingsTo(settings);
    maxSelectionNumber.saveSettingsTo(settings);
  }
  
  public void loadAdditionalSettingsFrom(final NodeSettingsRO settings,
      final DataTableSpec[] specs) throws NotConfigurableException {
    repository.loadSettingsFrom(settings, specs);
    maxSelectionNumber.loadSettingsFrom(settings, specs);
    
  }
}
