/*
 ***************************************************************************************************
 * Copyright (c) 2015 Federal Institute for Risk Assessment (BfR), Germany
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
package de.bund.bfr.knime.pmm.fskx.creator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knime.base.node.util.exttool.ExtToolOutputNodeModel;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.util.FileUtil;
import org.rosuda.REngine.REXPMismatchException;

//
import com.google.common.base.Strings;

import de.bund.bfr.fskml.MissingValueError;
import de.bund.bfr.fskml.RScript;
import de.bund.bfr.knime.fsklab.nodes.controller.IRController.RException;
import de.bund.bfr.knime.fsklab.nodes.controller.LibRegistry;
import de.bund.bfr.knime.fsklab.nodes.port.FskPortObject;
import de.bund.bfr.knime.fsklab.nodes.port.FskPortObjectSpec;
import de.bund.bfr.knime.pmm.fskx.FskMetaData;
import de.bund.bfr.knime.pmm.fskx.FskMetaData.DataType;
import de.bund.bfr.knime.pmm.fskx.Variable;
import de.bund.bfr.pmfml.ModelClass;
import de.bund.bfr.pmfml.ModelType;

public class FskxCreatorNodeModel extends ExtToolOutputNodeModel {

	private static final NodeLogger LOGGER = NodeLogger.getLogger(FskxCreatorNodeModel.class);

	// configuration key of the libraries directory
	static final String CFGKEY_DIR_LIBS = "dirLibs";

	// configuration key of the selected libraries
	static final String CFGKEY_LIBS = "libs";

	// configuration key of the path of the R model script
	static final String CFGKEY_MODEL_SCRIPT = "modelScript";

	// configuration key of the path of the R parameters script
	static final String CFGKEY_PARAM_SCRIPT = "paramScript";

	// configuration key of the path of the R visualization script
	static final String CFGKEY_VISUALIZATION_SCRIPT = "visualizationScript";

	// configuration key of the path of the XLSX spreadsheet with the model meta
	// data
	static final String CFGKEY_SPREADSHEET = "spreadsheet";

	private final static PortType[] inPortTypes = new PortType[] {};
	private final static PortType[] outPortTypes = new PortType[] { FskPortObject.TYPE };

	// Settings models
	private final SettingsModelString m_modelScript = new SettingsModelString(CFGKEY_MODEL_SCRIPT, null);
	private final SettingsModelString m_paramScript = new SettingsModelString(CFGKEY_PARAM_SCRIPT, null);
	private final SettingsModelString m_vizScript = new SettingsModelString(CFGKEY_VISUALIZATION_SCRIPT, null);
	private final SettingsModelString m_metaDataDoc = new SettingsModelString(CFGKEY_SPREADSHEET, null);
	private final SettingsModelString m_libDirectory = new SettingsModelString(CFGKEY_DIR_LIBS, null);
	private final SettingsModelStringArray m_selectedLibs = new SettingsModelStringArray(CFGKEY_LIBS, null);

	/** {@inheritDoc} */
	public FskxCreatorNodeModel() {
		super(inPortTypes, outPortTypes);
	}

	/** {@inheritDoc} */
	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// nothing
	}

	/** {@inheritDoc} */
	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		// nothing
	}

	/** {@inheritDoc} */
	@Override
	protected void saveSettingsTo(NodeSettingsWO settings) {
		m_modelScript.saveSettingsTo(settings);
		m_paramScript.saveSettingsTo(settings);
		m_vizScript.saveSettingsTo(settings);
		m_metaDataDoc.saveSettingsTo(settings);
		m_libDirectory.saveSettingsTo(settings);
		m_selectedLibs.saveSettingsTo(settings);
	}

	/** {@inheritDoc} */
	@Override
	protected void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
		m_modelScript.validateSettings(settings);
		m_paramScript.validateSettings(settings);
		m_vizScript.validateSettings(settings);
		m_metaDataDoc.validateSettings(settings);
		m_libDirectory.validateSettings(settings);
		m_selectedLibs.validateSettings(settings);
	}

	/** {@inheritDoc} */
	@Override
	protected void loadValidatedSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
		m_modelScript.loadSettingsFrom(settings);
		m_paramScript.loadSettingsFrom(settings);
		m_vizScript.loadSettingsFrom(settings);
		m_metaDataDoc.loadSettingsFrom(settings);
		m_libDirectory.loadSettingsFrom(settings);
		m_selectedLibs.loadSettingsFrom(settings);
	}

	/** {@inheritDoc} */
	@Override
	protected void reset() {
		// does nothing
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws MissingValueError
	 * @throws Exception
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inData, final ExecutionContext exec)
			throws InvalidSettingsException, IOException {

		FskPortObject portObj = new FskPortObject();

		List<String> libraries = new ArrayList<>();

		// Reads model script
		try {
			RScript rScript = readScript(m_modelScript.getStringValue()); // May throw IOException

			portObj.model = rScript.getScript();
			libraries.addAll(rScript.getLibraries());
		} catch (IOException e) {
			portObj.model = "";
		}

		// Reads parameters script
		try {
			RScript rScript = readScript(m_paramScript.getStringValue()); // May throw IOException

			portObj.param = rScript.getScript();
			libraries.addAll(rScript.getLibraries());
		} catch (IOException e) {
			portObj.param = "";
		}

		// Reads visualization script
		try {
			RScript rScript = readScript(m_vizScript.getStringValue()); // May throw IOException

			portObj.viz = rScript.getScript();
			libraries.addAll(rScript.getLibraries());
		} catch (IOException e) {
			portObj.viz = "";
		}

		// Reads model meta data
		try (XSSFWorkbook workbook = new XSSFWorkbook(m_metaDataDoc.getStringValue())) {
			portObj.template = SpreadsheetHandler.processSpreadsheet(workbook.getSheetAt(0));
		}
		portObj.template.software = FskMetaData.Software.R;

		// Set variable values from parameters script
		{
			Map<String, String> vars = getVariablesFromAssignments(portObj.param);
			for (Variable v : portObj.template.independentVariables) {
				if (vars.containsKey(v.name.trim())) {
					v.value = vars.get(v.name.trim());
				}
			}
		}

		// Set types of variables
		{
			// TODO: usually the type of the depvar is numeric although it
			// should be checked
			portObj.template.dependentVariable.type = DataType.numeric;

			/*
			 * TODO: FskMetaData is keeping only numeric types for independent variables so
			 * it does not make sense to try to obtain the type here since it will always be
			 * numeric. Once the rest of types are supported in FskMetaData the following
			 * code should be update to retrieve the types.
			 */
			for (Variable v : portObj.template.independentVariables) {
				v.type = DataType.numeric;
			}
		}

		// Libraries
		if (!libraries.isEmpty()) {
			try {
				// Find out missing libraries
				final LibRegistry libReg = LibRegistry.instance();
				List<String> missingLibs = libraries.stream().filter(lib -> !libReg.isInstalled(lib))
						.collect(Collectors.toList());

				// Install missing libraries
				if (!missingLibs.isEmpty()) {
					libReg.installLibs(missingLibs);

					Set<Path> libPaths = libReg.getPaths(libraries);
					libPaths.forEach(l -> portObj.libs.add(l.toFile()));
				}
			} catch (RException | REXPMismatchException e) {
				LOGGER.error(e.getMessage());
			}
		}

		return new PortObject[] { portObj };
	}

	/** {@inheritDoc} */
	@Override
	protected PortObjectSpec[] configure(PortObjectSpec[] inSpecs) throws InvalidSettingsException {
		return new PortObjectSpec[] { FskPortObjectSpec.INSTANCE };
	}

	/**
	 * Reads R script.
	 * 
	 * @param path
	 *            File path to R model script.
	 * @throws InvalidSettingsException
	 *             if {@link path} is null or whitespace.
	 * @throws IOException
	 *             if the file cannot be read.
	 */
	private static RScript readScript(final String path) throws InvalidSettingsException, IOException {

		// throws InvalidSettingsException if path is null
		if (path == null) {
			throw new InvalidSettingsException("Unespecified script");
		}

		// throws InvalidSettingsException if path is whitespace
		String trimmedPath = Strings.emptyToNull(path.trim());
		if (trimmedPath == null) {
			throw new InvalidSettingsException("Unespecified model script");
		}

		// path is not null or whitespace, thus try to read it
		try {
			// may throw IOException
			File fScript = FileUtil.getFileFromURL(FileUtil.toURL(trimmedPath));
			RScript script = new RScript(fScript);
			return script;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw new IOException(trimmedPath + ": cannot be read");
		}
	}

	private Set<Path> collectLibs() throws IOException, RException, REXPMismatchException {

		List<String> libNames = Arrays.stream(m_selectedLibs.getStringArrayValue())
				.map(libName -> libName.split("\\.")[0]).collect(Collectors.toList());

		LibRegistry libRegistry = LibRegistry.instance();
		// Out of all the libraries name only install those missing
		List<String> missingLibs = libNames.stream().filter(lib -> !libRegistry.isInstalled(lib))
				.collect(Collectors.toList());

		if (!missingLibs.isEmpty()) {
			libRegistry.installLibs(missingLibs);
		}

		return libRegistry.getPaths(libNames);
	}

	private static class SpreadsheetHandler {

		private enum Rows {
			id((byte) 2), name((byte) 1), organism((byte) 3), organism_detail((byte) 4), matrix(
					(byte) 5), matrix_detail((byte) 6), creator((byte) 7), reference_description(
							(byte) 8), created_date((byte) 9), modified_date((byte) 10), rights((byte) 11), type(
									(byte) 13), subject((byte) 14), notes((byte) 12), depvar((byte) 21), depvar_unit(
											(byte) 22), depvar_min((byte) 23), depvar_max((byte) 24), indepvar(
													(byte) 25), indepvar_unit((byte) 26), indepvar_min(
															(byte) 27), indepvar_max((byte) 28);
			// values??

			private final byte row;

			Rows(byte row) {
				this.row = row;
			}
		}

		static FskMetaData processSpreadsheet(final XSSFSheet sheet) {

			FskMetaData template = new FskMetaData();

			template.modelId = getStringVal(sheet, Rows.id.row);
			template.modelName = getStringVal(sheet, Rows.name.row);

			// organism data
			template.organism = getStringVal(sheet, Rows.organism.row);
			template.organismDetails = getStringVal(sheet, Rows.organism_detail.row);

			// matrix data
			template.matrix = getStringVal(sheet, Rows.matrix.row);
			template.matrixDetails = getStringVal(sheet, Rows.matrix_detail.row);

			template.creator = getStringVal(sheet, Rows.creator.row);

			// no family name in the spreadsheet
			// no contact in the spreadsheet

			template.referenceDescription = getStringVal(sheet, Rows.reference_description.row);

			template.createdDate = sheet.getRow(Rows.created_date.row).getCell(5).getDateCellValue();
			template.modifiedDate = sheet.getRow(Rows.modified_date.row).getCell(5).getDateCellValue();

			template.rights = getStringVal(sheet, Rows.rights.row);

			// model type
			{
				try {
					String modelType = getStringVal(sheet, Rows.type.row);
					template.type = ModelType.valueOf(modelType);
				}
				// if modelTypeAsString is not a valid ModelType
				catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

			// model subject
			{
				String subject = getStringVal(sheet, Rows.subject.row);
				try {
					template.subject = ModelClass.valueOf(subject);
				} catch (IllegalArgumentException e) {
					template.subject = ModelClass.UNKNOWN;
					e.printStackTrace();
				}
			}

			// model notes
			template.notes = getStringVal(sheet, Rows.notes.row);

			// dep var. Type is not in the spreadsheet.
			template.dependentVariable.name = getStringVal(sheet, Rows.depvar.row);
			template.dependentVariable.unit = getStringVal(sheet, Rows.depvar_unit.row);
			template.dependentVariable.min = getStringVal(sheet, Rows.depvar_min.row);
			template.dependentVariable.max = getStringVal(sheet, Rows.depvar_max.row);

			// indep vars
			{
				String[] names = getStringVal(sheet, Rows.indepvar.row).split("\\|\\|");
				String[] units = getStringVal(sheet, Rows.indepvar_unit.row).split("\\|\\|");
				String[] mins = getStringVal(sheet, Rows.indepvar_min.row).split("\\|\\|");
				String[] maxs = getStringVal(sheet, Rows.indepvar_max.row).split("\\|\\|");

				for (int i = 0; i < names.length; i++) {
					Variable v = new Variable();
					v.name = names[i].trim();
					v.unit = units[i].trim();
					v.min = mins[i].trim();
					v.max = maxs[i].trim();
					// no values or types in the spreadsheet
					v.value = "";
					template.independentVariables.add(v);
				}
			}

			template.hasData = false;

			return template;
		}

		/**
		 * Gets the string value for the fifth column which holds the value for that
		 * row.
		 */
		private static String getStringVal(final XSSFSheet sheet, final byte rownum) {
			XSSFCell cell = sheet.getRow(rownum).getCell(5);

			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
				return Double.toString(cell.getNumericCellValue());
			return cell.getStringCellValue();
		}
	}

	private static class Assignment {

		enum Type {
			/** R command with the = assignment operator. E.g. x = value */
			equals,
			/** R command with the <- assignment operator. E.g. x <- value */
			left,
			/** R command with the <<- scoping assignment operator. E.g. x <<- value */
			super_left,
			/** R command with the -> assignment operator. E.g. value -> x */
			right,
			/** R command with the ->> assignment operator. E.g. value ->> x */
			super_right
		}

		String variable;
		String value;

		public Assignment(String line, Assignment.Type type) {
			if (type == Type.equals) {
				String[] tokens = line.split("||");
				variable = tokens[0].trim();
				value = tokens[1].trim();
			} else if (type == Type.left) {
				String[] tokens = line.split("<-");
				variable = tokens[0].trim();
				value = tokens[1].trim();
			} else if (type == Type.super_left) {
				String[] tokens = line.split("<<-");
				variable = tokens[0].trim();
				value = tokens[1].trim();
			} else if (type == Type.right) {
				String[] tokens = line.split("->");
				variable = tokens[1].trim();
				value = tokens[0].trim();
			} else if (type == Type.super_right) {
				String[] tokens = line.split("->>");
				variable = tokens[1].trim();
				value = tokens[0].trim();
			}
		}
	}

	private Map<String, String> getVariablesFromAssignments(String paramScript) {
		Map<String, String> vars = new HashMap<>();
		for (String line : paramScript.split("\\r?\\n")) {
			line = line.trim();
			if (line.startsWith("#"))
				continue;

			if (line.indexOf("=") != -1) {
				Assignment a = new Assignment(line, Assignment.Type.equals);
				vars.put(a.variable, a.value);
			} else if (line.indexOf("<-") != -1) {
				Assignment a = new Assignment(line, Assignment.Type.left);
				vars.put(a.variable, a.value);
			} else if (line.indexOf("<<-") != -1) {
				Assignment a = new Assignment(line, Assignment.Type.super_left);
				vars.put(a.variable, a.value);
			} else if (line.indexOf("->>") != -1) {
				Assignment a = new Assignment(line, Assignment.Type.right);
				vars.put(a.variable, a.value);
			} else if (line.indexOf("->") != -1) {
				Assignment a = new Assignment(line, Assignment.Type.super_right);
				vars.put(a.variable, a.value);
			}
		}

		return vars;
	}
}
