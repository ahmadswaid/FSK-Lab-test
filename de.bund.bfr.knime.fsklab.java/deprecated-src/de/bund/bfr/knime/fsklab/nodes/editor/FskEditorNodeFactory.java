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
package de.bund.bfr.knime.fsklab.nodes.editor;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import de.bund.bfr.knime.fsklab.nodes.editor.FskEditorNodeDialog;
import de.bund.bfr.knime.fsklab.nodes.editor.FskEditorNodeModel;

@Deprecated
public class FskEditorNodeFactory extends NodeFactory<FskEditorNodeModel> {
  
  /** {@inheritDoc} */
  @Override
  public FskEditorNodeModel createNodeModel() {
    return new FskEditorNodeModel();
  }
  
  /** {@inheritDoc} */
  @Override
  protected int getNrNodeViews() {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public NodeView<FskEditorNodeModel> createNodeView(int viewIndex,
      FskEditorNodeModel nodeModel) {
    return null;
  }
  
  /** {@inheritDoc} */
  @Override
  protected boolean hasDialog() {
    return true;
  }
  
  /** {@inheritDoc} */
  @Override
  protected NodeDialogPane createNodeDialogPane() {
    return new FskEditorNodeDialog();
  }
}
