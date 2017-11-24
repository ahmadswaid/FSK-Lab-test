/*
 ***************************************************************************************************
 * Copyright (c) 2017 Federal Institute for Risk Assessment (BfR), Germany
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
package de.bund.bfr.knime.fsklab.nodes;

import javax.swing.JFileChooser;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class ReaderNodeFactory extends NodeFactory<ReaderNodeModel> {

  @Override
  public ReaderNodeModel createNodeModel() {
    return new ReaderNodeModel();
  }

  @Override
  public int getNrNodeViews() {
    return 0;
  }

  @Override
  public NodeView<ReaderNodeModel> createNodeView(final int viewIndex,
      final ReaderNodeModel nodeModel) {
    return null;
  }

  @Override
  public boolean hasDialog() {
    return true;
  }

  @Override
  public NodeDialogPane createNodeDialogPane() {

    SettingsModelString filename = new SettingsModelString("filename", "");
    DialogComponentFileChooser filechooser = new DialogComponentFileChooser(filename,
        "filename-history", JFileChooser.OPEN_DIALOG, ".fskx");

    // Add widget
    DefaultNodeSettingsPane pane = new DefaultNodeSettingsPane();
    pane.createNewGroup("Data source");
    pane.addDialogComponent(filechooser);

    return pane;
  }
}
