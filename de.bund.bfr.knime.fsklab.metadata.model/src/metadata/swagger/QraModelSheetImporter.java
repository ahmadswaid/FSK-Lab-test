package metadata.swagger;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.threeten.bp.LocalDate;

import de.bund.bfr.metadata.swagger.Assay;
import de.bund.bfr.metadata.swagger.Contact;
import de.bund.bfr.metadata.swagger.DietaryAssessmentMethod;
import de.bund.bfr.metadata.swagger.ExposureModelScope;
import de.bund.bfr.metadata.swagger.GenericModelDataBackground;
import de.bund.bfr.metadata.swagger.GenericModelModelMath;
import de.bund.bfr.metadata.swagger.Laboratory;
import de.bund.bfr.metadata.swagger.Model;
import de.bund.bfr.metadata.swagger.ModelCategory;
import de.bund.bfr.metadata.swagger.Parameter;
import de.bund.bfr.metadata.swagger.PredictiveModelGeneralInformation;
import de.bund.bfr.metadata.swagger.QraModel;
import de.bund.bfr.metadata.swagger.QualityMeasures;
import de.bund.bfr.metadata.swagger.Reference;
import de.bund.bfr.metadata.swagger.Study;
import de.bund.bfr.metadata.swagger.StudySample;
import metadata.ParameterClassification;
import metadata.ParameterType;
import metadata.PublicationType;
import metadata.SwaggerUtil;

public class QraModelSheetImporter implements SheetImporter {

	protected int GENERAL_INFORMATION__NAME = 1;
	protected int GENERAL_INFORMATION__SOURCE = 2;
	protected int GENERAL_INFORMATION__IDENTIFIER = 3;
	protected int GENERAL_INFORMATION_CREATION_DATE = 6;
	protected int GENERAL_INFORMATION__RIGHTS = 8;
	protected int GENERAL_INFORMATION__AVAILABLE = 9;
	protected int GENERAL_INFORMATION__URL = 10;
	protected int GENERAL_INFORMATION__FORMAT = 11;
	protected int GENERAL_INFORMATION__LANGUAGE = 24;
	protected int GENERAL_INFORMATION__SOFTWARE = 25;
	protected int GENERAL_INFORMATION__LANGUAGE_WRITTEN_IN = 26;
	protected int GENERAL_INFORMATION__STATUS = 32;
	protected int GENERAL_INFORMATION__OBJECTIVE = 33;
	protected int GENERAL_INFORMATION__DESCRIPTION = 34;

	protected int MODEL_CATEGORY__MODEL_CLASS = 27;
	protected int MODEL_CATEGORY__MODEL_SUB_CLASS = 28;
	protected int MODEL_CATEGORY__CLASS_COMMENT = 29;
	protected int MODEL_CATEGORY__BASIC_PROCESS = 30;

	protected int QUALITY_MEASURES__SSE = 123;
	protected int QUALITY_MEASURES__MSE = 124;
	protected int QUALITY_MEASURES__RMSE = 125;
	protected int QUALITY_MEASURES__RSQUARE = 126;
	protected int QUALITY_MEASURES__AIC = 127;
	protected int QUALITY_MEASURES__BIC = 128;
	protected int SCOPE__GENERAL_COMMENT = 73;
	protected int SCOPE__TEMPORAL_INFORMATION = 74;

	protected int STUDY__STUDY_IDENTIFIER = 77;
	protected int STUDY__STUDY_TITLE = 78;
	protected int STUDY__STUDY_DESCRIPTION = 79;
	protected int STUDY__STUDY_DESIGN_TYPE = 80;
	protected int STUDY__STUDY_ASSAY_MEASUREMENT_TYPE = 81;
	protected int STUDY__STUDY_ASSAY_TECHNOLOGY_TYPE = 82;
	protected int STUDY__STUDY_ASSAY_TECHNOLOGY_PLATFORM = 83;
	protected int STUDY__ACCREDITATION_PROCEDURE_FOR_THE_ASSAY_TECHNOLOGY = 84;
	protected int STUDY__STUDY_PROTOCOL_NAME = 85;
	protected int STUDY__STUDY_PROTOCOL_TYPE = 86;
	protected int STUDY__STUDY_PROTOCOL_DESCRIPTION = 87;
	protected int STUDY__STUDY_PROTOCOL_URI = 88;
	protected int STUDY__STUDY_PROTOCOL_VERSION = 89;
	protected int STUDY__STUDY_PROTOCOL_PARAMETERS_NAME = 90;
	protected int STUDY__STUDY_PROTOCOL_COMPONENTS_NAME = 91;
	protected int STUDY__STUDY_PROTOCOL_COMPONENTS_TYPE = 92;

	protected int GI_CREATOR_ROW = 3;
	protected int GI_REFERENCE_ROW = 14;
	protected int SCOPE_PRODHAZPOP_ROW = 38;
	protected int BG_STUDY_SAMPLE_ROW = 96;
	protected int BG_DIET_ASSESS_ROW = 102;
	protected int BG_EVENT_ROW = 108;
	protected int BG_LABORATORY_ROW = 109;
	protected int BG_ASSAY_ROW = 115;
	protected int BG_QUALITY_MEAS_ROW = 123;
	protected int BG_Model_EQ_ROW = 123;

	protected int MM_PARAMETER_ROW = 133;
	protected int MM_FITTING_PROCEDURE_ROW = 149;
	
	/** Columns for each of the properties of Creator. */
	private final HashMap<String, Integer> creatorColumns;

	/** Columns for each of the properties of Creator. */
	private final HashMap<String, Integer> authorColumns;
	
	/** Columns for each of the properties of DietaryAssessmentMethod. */
	private final HashMap<String, Integer> methodColumns;
	
	/** Columns for each of the properties of Laboratory. */
	private final HashMap<String, Integer> laboratoryColumns;
	
	/** Columns for each of the properties of Product. */
	private final HashMap<String, Integer> productColumns;
	
	public QraModelSheetImporter() {
		
		methodColumns = new HashMap<>();
		methodColumns.put("collectionTool", L);
		methodColumns.put("numberOfNonConsecutiveOneDay", M);
		methodColumns.put("softwareTool", N);
		methodColumns.put("numberOfFoodItems", O);
		methodColumns.put("recordTypes", P);
		methodColumns.put("foodDescriptors", Q);
		
		laboratoryColumns = new HashMap<>();
		laboratoryColumns.put("accreditation", L);
		laboratoryColumns.put("name", M);
		laboratoryColumns.put("country", N);
		
		productColumns = new HashMap<>();
		productColumns.put("name", L);
		productColumns.put("description", M);
		productColumns.put("unit", N);
		productColumns.put("productionMethod", O);
		productColumns.put("packaging", P);
		productColumns.put("treatment", Q);
		productColumns.put("originCountry", R);
		productColumns.put("originArea", S);
		productColumns.put("fisheriesArea", T);
		productColumns.put("productionDate", U);
		productColumns.put("expiryDate", V);
		
		creatorColumns = new HashMap<>();
		creatorColumns.put("mail", S);
		creatorColumns.put("title", L);
		creatorColumns.put("familyName", P);
		creatorColumns.put("givenName", N);
		creatorColumns.put("telephone", R);
		creatorColumns.put("streetAddress", X);
		creatorColumns.put("country", T);
		creatorColumns.put("city", U);
		creatorColumns.put("zipCode", V);
		creatorColumns.put("region", Z);
		creatorColumns.put("organization", Q);

		authorColumns = new HashMap<>();
		authorColumns.put("title", AB);
		authorColumns.put("name", AC);
		authorColumns.put("givenName", AD);
		authorColumns.put("additionalName", AE);
		authorColumns.put("familyName", AF);
		authorColumns.put("organization", AG);
		authorColumns.put("telephone", AH);
		authorColumns.put("mail", AI);
		authorColumns.put("country", AJ);
		authorColumns.put("city", AK);
		authorColumns.put("zipCode", AL);
		authorColumns.put("postOfficeBox", AM);
		authorColumns.put("streetAddress", AN);
		authorColumns.put("extendedAddress", AO);
		authorColumns.put("region", AP);
	}

	private GenericModelDataBackground retrieveBackground(Sheet sheet) {
		GenericModelDataBackground background = new GenericModelDataBackground();

		try {
			Study study = retrieveStudy(sheet);
			background.setStudy(study);
		} catch (Exception exception) {
		}

		for (int numrow = BG_STUDY_SAMPLE_ROW; numrow < (BG_STUDY_SAMPLE_ROW + 3); numrow++) {
			try {
				StudySample sample = ImporterUtils.retrieveStudySample(sheet.getRow(numrow));
				background.addStudySampleItem(sample);
			} catch (Exception exception) {
			}
		}

		for (int numrow = BG_DIET_ASSESS_ROW; numrow < (BG_DIET_ASSESS_ROW + 3); numrow++) {
			try {
				Row row = sheet.getRow(numrow);
				DietaryAssessmentMethod method = ImporterUtils.retrieveDietaryAssessmentMethod(row, methodColumns);
				background.addDietaryAssessmentMethodItem(method);
			} catch (Exception exception) {
			}
		}

		for (int numrow = BG_LABORATORY_ROW; numrow < (BG_LABORATORY_ROW + 3); numrow++) {
			try {
				Row row = sheet.getRow(numrow);
				Laboratory laboratory = ImporterUtils.retrieveLaboratory(row, laboratoryColumns);
				background.addLaboratoryItem(laboratory);
			} catch (Exception exception) {
			}
		}

		for (int numrow = BG_ASSAY_ROW; numrow < (BG_ASSAY_ROW + 3); numrow++) {
			try {
				Assay assay = ImporterUtils.retrieveAssay(sheet.getRow(numrow));
				background.addAssayItem(assay);
			} catch (Exception exception) {
				// ignore errors since Assay is optional
			}
		}

		return background;
	}

	private GenericModelModelMath retrieveModelMath(Sheet sheet) {

		GenericModelModelMath math = new GenericModelModelMath();

		for (int rownum = MM_PARAMETER_ROW; rownum < sheet.getLastRowNum(); rownum++) {
			try {
				Row row = sheet.getRow(rownum);
				Parameter param = retrieveParameter(row);
				math.addParameterItem(param);
			} catch (Exception exception) {
				System.out.println(exception.getMessage());
				// ...
			}
		}

		try {
			QualityMeasures measures = retrieveQualityMeasures(sheet);
			math.addQualityMeasuresItem(measures);
		} catch (Exception exception) {
			// ...
		}

		Cell fittingProcedureCell = sheet.getRow(MM_FITTING_PROCEDURE_ROW).getCell(J);
		if (fittingProcedureCell.getCellTypeEnum() == CellType.STRING) {
			math.setFittingProcedure(fittingProcedureCell.getStringCellValue());
		}

		return math;
	}

	private PredictiveModelGeneralInformation retrieveGeneralInformation(Sheet sheet) {

		PredictiveModelGeneralInformation information = new PredictiveModelGeneralInformation();

		Cell nameCell = sheet.getRow(GENERAL_INFORMATION__NAME).getCell(J);
		if (nameCell.getCellTypeEnum() == CellType.STRING) {
			information.setName(nameCell.getStringCellValue());
		}

		Cell sourceCell = sheet.getRow(GENERAL_INFORMATION__SOURCE).getCell(J);
		if (sourceCell.getCellTypeEnum() == CellType.STRING) {
			information.setSource(sourceCell.getStringCellValue());
		}

		Cell identifierCell = sheet.getRow(GENERAL_INFORMATION__IDENTIFIER).getCell(J);
		if (identifierCell.getCellTypeEnum() == CellType.STRING) {
			information.setIdentifier(identifierCell.getStringCellValue());
		}

		for (int numRow = GI_CREATOR_ROW; numRow < GI_CREATOR_ROW + 6; numRow++) {

			Row row = sheet.getRow(numRow);

			try {
				Contact author = ImporterUtils.retrieveContact(row, authorColumns);
				information.addAuthorItem(author);
			} catch (Exception exception) {
			}

			try {
				Contact creator = ImporterUtils.retrieveContact(row, creatorColumns);
				information.addCreatorItem(creator);
			} catch (Exception exception) {
			}
		}

		Cell creationDateCell = sheet.getRow(GENERAL_INFORMATION_CREATION_DATE).getCell(J);
		if (creationDateCell.getCellTypeEnum() == CellType.NUMERIC) {
			LocalDate localDate = ImporterUtils.retrieveDate(creationDateCell);
			information.setCreationDate(localDate);
		}

		// TODO: modificationDate

		Cell rightsCell = sheet.getRow(GENERAL_INFORMATION__RIGHTS).getCell(J);
		if (rightsCell.getCellTypeEnum() == CellType.STRING) {
			information.setRights(rightsCell.getStringCellValue());
		}

		Cell isAvailableCell = sheet.getRow(GENERAL_INFORMATION__AVAILABLE).getCell(J);
		if (isAvailableCell.getCellTypeEnum() == CellType.STRING) {
			information.setAvailability(isAvailableCell.getStringCellValue());
		}

		Cell urlCell = sheet.getRow(GENERAL_INFORMATION__URL).getCell(J);
		if (urlCell.getCellTypeEnum() == CellType.STRING) {
			information.setUrl(urlCell.getStringCellValue());
		}

		Cell formatCell = sheet.getRow(GENERAL_INFORMATION__FORMAT).getCell(J);
		if (formatCell.getCellTypeEnum() == CellType.STRING) {
			information.setFormat(formatCell.getStringCellValue());
		}

		// reference (1..n)
		for (int numRow = this.GI_REFERENCE_ROW; numRow < (this.GI_REFERENCE_ROW + 3); numRow++) {
			try {
				Reference reference = retrieveReference(sheet.getRow(numRow));
				information.addReferenceItem(reference);
			} catch (Exception exception) {
			}
		}

		Cell languageCell = sheet.getRow(GENERAL_INFORMATION__LANGUAGE).getCell(J);
		if (languageCell.getCellTypeEnum() == CellType.STRING) {
			information.setLanguage(languageCell.getStringCellValue());
		}

		Cell softwareCell = sheet.getRow(GENERAL_INFORMATION__SOFTWARE).getCell(J);
		if (softwareCell.getCellTypeEnum() == CellType.STRING) {
			information.setSoftware(softwareCell.getStringCellValue());
		}

		Cell languageWrittenInCell = sheet.getRow(GENERAL_INFORMATION__LANGUAGE_WRITTEN_IN).getCell(J);
		if (languageWrittenInCell.getCellTypeEnum() == CellType.STRING) {
			information.setLanguageWrittenIn(languageWrittenInCell.getStringCellValue());
		}

		// model category (0..n)
		try {
			ModelCategory category = retrieveModelCategory(sheet);
			information.setModelCategory(category);
		} catch (Exception exception) {
		}

		Cell statusCell = sheet.getRow(GENERAL_INFORMATION__STATUS).getCell(J);
		if (statusCell.getCellTypeEnum() == CellType.STRING) {
			information.setStatus(statusCell.getStringCellValue());
		}

		Cell objectiveCell = sheet.getRow(GENERAL_INFORMATION__OBJECTIVE).getCell(J);
		if (objectiveCell.getCellTypeEnum() == CellType.STRING) {
			information.setObjective(objectiveCell.getStringCellValue());
		}

		Cell descriptionCell = sheet.getRow(GENERAL_INFORMATION__DESCRIPTION).getCell(J);
		if (descriptionCell.getCellTypeEnum() == CellType.STRING) {
			information.setDescription(descriptionCell.getStringCellValue());
		}

		return information;

	}

	private ExposureModelScope retrieveScope(Sheet sheet) {

		ExposureModelScope scope = new ExposureModelScope();

		for (int numrow = this.SCOPE_PRODHAZPOP_ROW; numrow <= (this.SCOPE_PRODHAZPOP_ROW + 11); numrow++) {

			Row row = sheet.getRow(numrow);

			try {
				scope.addProductItem(ImporterUtils.retrieveProduct(row, productColumns));
			} catch (IllegalArgumentException exception) {
				// ignore exception since products are optional (*)
			}

			try {
				scope.addHazardItem(ImporterUtils.retrieveHazard(row));
			} catch (IllegalArgumentException exception) {
				// ignore exception since products are optional (*)
			}

			try {
				scope.addPopulationGroupItem(ImporterUtils.retrievePopulationGroup(row));
			} catch (IllegalArgumentException exception) {
				// ignore exception since population groups are optional (*)
			}
		}

		Cell generalCommentCell = sheet.getRow(SCOPE__GENERAL_COMMENT).getCell(J);
		if (generalCommentCell.getCellTypeEnum() == CellType.STRING) {
			scope.setGeneralComment(generalCommentCell.getStringCellValue());
		}

		Cell temporalInformationCell = sheet.getRow(SCOPE__TEMPORAL_INFORMATION).getCell(J);
		if (temporalInformationCell.getCellTypeEnum() == CellType.STRING) {
			scope.setTemporalInformation(temporalInformationCell.getStringCellValue());
		}

		// TODO: Spatial information

		return scope;
	}

	private Reference retrieveReference(Row row) {

		// Check mandatory properties and throw exception if missing
		if (row.getCell(L).getCellTypeEnum() != CellType.STRING) {
			throw new IllegalArgumentException("Missing Is reference description?");
		}
		if (row.getCell(P).getCellTypeEnum() != CellType.STRING) {
			throw new IllegalArgumentException("Missing DOI");
		}

		Reference reference = new Reference();
		reference.setIsReferenceDescription(row.getCell(L).getStringCellValue().equals("Yes"));
		reference.setDoi(row.getCell(P).getStringCellValue());

		// publication type
		Cell typeCell = row.getCell(M);
		if (typeCell.getCellTypeEnum() == CellType.STRING) {
			PublicationType type = PublicationType.get(typeCell.getStringCellValue());
			if (type != null) {
				reference.setPublicationType(SwaggerUtil.PUBLICATION_TYPE.get(type));
			}
		}

		Cell dateCell = row.getCell(N);
		if (dateCell.getCellTypeEnum() == CellType.NUMERIC) {
			LocalDate localDate = ImporterUtils.retrieveDate(dateCell);
			reference.setDate(localDate);
		}

		Cell pmidCell = row.getCell(O);
		if (pmidCell.getCellTypeEnum() == CellType.STRING) {
			reference.setPmid(pmidCell.getStringCellValue());
		}

		Cell authorListCell = row.getCell(Q);
		if (authorListCell.getCellTypeEnum() == CellType.STRING) {
			reference.setAuthorList(authorListCell.getStringCellValue());
		}

		Cell titleCell = row.getCell(R);
		if (titleCell.getCellTypeEnum() == CellType.STRING) {
			reference.setTitle(titleCell.getStringCellValue());
		}

		Cell abstractCell = row.getCell(S);
		if (abstractCell.getCellTypeEnum() == CellType.STRING) {
			reference.setAbstract(abstractCell.getStringCellValue());
		}
		// journal
		// volume
		// issue

		Cell statusCell = row.getCell(U);
		if (statusCell.getCellTypeEnum() == CellType.STRING) {
			reference.setStatus(statusCell.getStringCellValue());
		}

		Cell websiteCell = row.getCell(V);
		if (websiteCell.getCellTypeEnum() == CellType.STRING) {
			reference.setWebsite(websiteCell.getStringCellValue());
		}

		Cell commentCell = row.getCell(W);
		if (commentCell.getCellTypeEnum() == CellType.STRING) {
			reference.setComment(commentCell.getStringCellValue());
		}

		return reference;
	}

	private ModelCategory retrieveModelCategory(Sheet sheet) {
		// Check mandatory properties and throw exception if missing
		if (sheet.getRow(MODEL_CATEGORY__MODEL_CLASS).getCell(J).getCellTypeEnum() != CellType.STRING) {
			throw new IllegalArgumentException("Missing model class");
		}

		ModelCategory category = new ModelCategory();

		category.setModelClass(sheet.getRow(MODEL_CATEGORY__MODEL_CLASS).getCell(J).getStringCellValue());

		Cell subClassCell = sheet.getRow(MODEL_CATEGORY__MODEL_SUB_CLASS).getCell(J);
		if (subClassCell.getCellTypeEnum() == CellType.STRING) {
			category.addModelSubClassItem(subClassCell.getStringCellValue());
		}

		Cell modelClassCommentCell = sheet.getRow(MODEL_CATEGORY__CLASS_COMMENT).getCell(J);
		if (modelClassCommentCell.getCellTypeEnum() == CellType.STRING) {
			category.setModelClassComment(modelClassCommentCell.getStringCellValue());
		}

		Cell basicProcessCell = sheet.getRow(MODEL_CATEGORY__BASIC_PROCESS).getCell(J);
		if (basicProcessCell.getCellTypeEnum() == CellType.STRING) {
			category.addBasicProcessItem(basicProcessCell.getStringCellValue());
		}

		return category;
	}

	private Study retrieveStudy(Sheet sheet) {

		// Check first mandatory properties
		if (sheet.getRow(STUDY__STUDY_TITLE).getCell(J).getCellTypeEnum() != CellType.STRING) {
			throw new IllegalArgumentException("Missing study title");
		}

		Study study = new Study();

		Cell identifierCell = sheet.getRow(STUDY__STUDY_IDENTIFIER).getCell(J);
		if (identifierCell.getCellTypeEnum() == CellType.STRING) {
			study.setIdentifier(identifierCell.getStringCellValue());
		}

		study.setTitle(sheet.getRow(STUDY__STUDY_TITLE).getCell(J).getStringCellValue());

		Cell descriptionCell = sheet.getRow(STUDY__STUDY_DESCRIPTION).getCell(J);
		if (descriptionCell.getCellTypeEnum() == CellType.STRING) {
			study.setDescription(descriptionCell.getStringCellValue());
		}

		Cell designTypeCell = sheet.getRow(STUDY__STUDY_DESIGN_TYPE).getCell(J);
		if (designTypeCell.getCellTypeEnum() == CellType.STRING) {
			study.setDesignType(designTypeCell.getStringCellValue());
		}

		Cell measurementTypeCell = sheet.getRow(STUDY__STUDY_ASSAY_MEASUREMENT_TYPE).getCell(J);
		if (measurementTypeCell.getCellTypeEnum() == CellType.STRING) {
			study.setAssayMeasurementType(measurementTypeCell.getStringCellValue());
		}

		Cell technologyTypeCell = sheet.getRow(STUDY__STUDY_ASSAY_TECHNOLOGY_TYPE).getCell(J);
		if (technologyTypeCell.getCellTypeEnum() == CellType.STRING) {
			study.setAssayTechnologyType(technologyTypeCell.getStringCellValue());
		}

		Cell technologyPlatformCell = sheet.getRow(STUDY__STUDY_ASSAY_TECHNOLOGY_PLATFORM).getCell(J);
		if (technologyPlatformCell.getCellTypeEnum() == CellType.STRING) {
			study.setAssayTechnologyPlatform(technologyPlatformCell.getStringCellValue());
		}

		Cell accreditationProcedureCell = sheet.getRow(STUDY__ACCREDITATION_PROCEDURE_FOR_THE_ASSAY_TECHNOLOGY)
				.getCell(J);
		if (accreditationProcedureCell.getCellTypeEnum() == CellType.STRING) {
			study.setAccreditationProcedureForTheAssayTechnology(accreditationProcedureCell.getStringCellValue());
		}

		Cell protocolNameCell = sheet.getRow(STUDY__STUDY_PROTOCOL_NAME).getCell(J);
		if (protocolNameCell.getCellTypeEnum() == CellType.STRING) {
			study.setProtocolName(protocolNameCell.getStringCellValue());
		}

		Cell protocolTypeCell = sheet.getRow(STUDY__STUDY_PROTOCOL_TYPE).getCell(J);
		if (protocolTypeCell.getCellTypeEnum() == CellType.STRING) {
			study.setProtocolType(protocolTypeCell.getStringCellValue());
		}

		Cell protocolDescriptionCell = sheet.getRow(STUDY__STUDY_PROTOCOL_DESCRIPTION).getCell(J);
		if (protocolDescriptionCell.getCellTypeEnum() == CellType.STRING) {
			study.setProtocolDescription(protocolDescriptionCell.getStringCellValue());
		}

		Cell protocolURICell = sheet.getRow(STUDY__STUDY_PROTOCOL_URI).getCell(J);
		if (protocolURICell.getCellTypeEnum() == CellType.STRING) {
			study.setProtocolURI(protocolURICell.getStringCellValue());
		}

		Cell protocolVersionCell = sheet.getRow(STUDY__STUDY_PROTOCOL_VERSION).getCell(J);
		if (protocolVersionCell.getCellTypeEnum() == CellType.STRING) {
			study.setProtocolVersion(protocolVersionCell.getStringCellValue());
		}

		Cell parameterNameCell = sheet.getRow(STUDY__STUDY_PROTOCOL_PARAMETERS_NAME).getCell(J);
		if (parameterNameCell.getCellTypeEnum() == CellType.STRING) {
			study.setProtocolParametersName(parameterNameCell.getStringCellValue());
		}

		Cell componentNameCell = sheet.getRow(STUDY__STUDY_PROTOCOL_COMPONENTS_NAME).getCell(J);
		if (componentNameCell.getCellTypeEnum() == CellType.STRING) {
			study.setProtocolComponentsName(componentNameCell.getStringCellValue());
		}

		Cell componentTypeCell = sheet.getRow(STUDY__STUDY_PROTOCOL_COMPONENTS_TYPE).getCell(J);
		if (componentTypeCell.getCellTypeEnum() == CellType.STRING) {
			study.setProtocolComponentsType(componentTypeCell.getStringCellValue());
		}

		return study;
	}

	private Parameter retrieveParameter(Row row) {

		// Check first mandatory properties
		if (row.getCell(L).getCellTypeEnum() == CellType.BLANK) {
			throw new IllegalArgumentException("Missing parameter id");
		}

		if (row.getCell(M).getCellTypeEnum() == CellType.BLANK) {
			throw new IllegalArgumentException("Missing parameter classification");
		}

		if (row.getCell(N).getCellTypeEnum() == CellType.BLANK) {
			throw new IllegalArgumentException("Missing parameter name");
		}

		if (row.getCell(P).getCellTypeEnum() == CellType.BLANK) {
			throw new IllegalArgumentException("Missing parameter unit");
		}

		if (row.getCell(R).getCellTypeEnum() == CellType.BLANK) {
			throw new IllegalArgumentException("Missing data type");
		}

		Parameter param = new Parameter();
		param.setId(row.getCell(L).getStringCellValue());

		ParameterClassification pc = ParameterClassification.get(row.getCell(M).getStringCellValue());
		if (pc != null) {
			param.setClassification(SwaggerUtil.CLASSIF.get(pc));
		}

		param.setName(row.getCell(N).getStringCellValue());

		Cell descriptionCell = row.getCell(O);
		if (descriptionCell.getCellTypeEnum() != CellType.BLANK) {
			param.setDescription(descriptionCell.getStringCellValue());
		}

		param.setUnit(row.getCell(P).getStringCellValue());

		Cell unitCategoryCell = row.getCell(Q);
		if (unitCategoryCell.getCellTypeEnum() != CellType.BLANK) {
			param.setUnitCategory(unitCategoryCell.getStringCellValue());
		}

		ParameterType parameterType = ParameterType.get(row.getCell(R).getStringCellValue());
		if (parameterType != null) {
			param.setDataType(SwaggerUtil.TYPES.get(parameterType));
		}

		Cell sourceCell = row.getCell(S);
		if (sourceCell.getCellTypeEnum() != CellType.BLANK) {
			param.setSource(sourceCell.getStringCellValue());
		}

		Cell subjectCell = row.getCell(T);
		if (subjectCell.getCellTypeEnum() != CellType.BLANK) {
			param.setSubject(subjectCell.getStringCellValue());
		}

		Cell distributionCell = row.getCell(U);
		if (distributionCell.getCellTypeEnum() != CellType.BLANK) {
			param.setDistribution(distributionCell.getStringCellValue());
		}

		Cell valueCell = row.getCell(V);
		if (valueCell.getCellTypeEnum() != CellType.BLANK) {

			if (valueCell.getCellTypeEnum() == CellType.NUMERIC) {
				Double doubleValue = valueCell.getNumericCellValue();
				if (parameterType == ParameterType.INTEGER) {
					param.setValue(Integer.toString(doubleValue.intValue()));
				} else if (parameterType == ParameterType.DOUBLE || parameterType == ParameterType.NUMBER) {
					param.setValue(Double.toString(doubleValue));
				}
			} else {
				param.setValue(valueCell.getStringCellValue());
			}
		}

		// TODO: reference

		Cell variabilitySubjectCell = row.getCell(X);
		if (variabilitySubjectCell.getCellTypeEnum() != CellType.BLANK) {
			param.setVariabilitySubject(variabilitySubjectCell.getStringCellValue());
		}

		Cell maxCell = row.getCell(Y);
		if (maxCell.getCellTypeEnum() != CellType.BLANK) {
			if (maxCell.getCellTypeEnum() != CellType.STRING)
				param.setMaxValue(String.valueOf(maxCell.getNumericCellValue()));
			else
				param.setMaxValue(maxCell.getStringCellValue());

		}

		Cell minCell = row.getCell(Z);
		if (minCell.getCellTypeEnum() != CellType.BLANK) {
			if (minCell.getCellTypeEnum() != CellType.STRING)
				param.setMinValue(String.valueOf(minCell.getNumericCellValue()));
			else
				param.setMinValue(minCell.getStringCellValue());
		}

		Cell errorCell = row.getCell(AA);
		if (errorCell.getCellTypeEnum() != CellType.BLANK) {
			if (errorCell.getCellTypeEnum() != CellType.STRING)
				param.setError(String.valueOf(errorCell.getNumericCellValue()));
			else
				param.setError(errorCell.getStringCellValue());
		}
		return param;
	}

	private QualityMeasures retrieveQualityMeasures(Sheet sheet) {
		QualityMeasures measures = new QualityMeasures();

		Cell sseCell = sheet.getRow(QUALITY_MEASURES__SSE).getCell(M);
		if (sseCell.getCellTypeEnum() == CellType.NUMERIC) {
			measures.setSse(BigDecimal.valueOf(sseCell.getNumericCellValue()));
		}

		Cell mseCell = sheet.getRow(QUALITY_MEASURES__MSE).getCell(M);
		if (mseCell.getCellTypeEnum() == CellType.NUMERIC) {
			measures.setMse(BigDecimal.valueOf(mseCell.getNumericCellValue()));
		}

		Cell rmseCell = sheet.getRow(QUALITY_MEASURES__RMSE).getCell(M);
		if (rmseCell.getCellTypeEnum() == CellType.NUMERIC) {
			measures.setRmse(BigDecimal.valueOf(rmseCell.getNumericCellValue()));
		}

		Cell rsquareCell = sheet.getRow(QUALITY_MEASURES__RSQUARE).getCell(M);
		if (rsquareCell.getCellTypeEnum() == CellType.NUMERIC) {
			measures.setRsquared(BigDecimal.valueOf(rsquareCell.getNumericCellValue()));
		}

		Cell aicCell = sheet.getRow(QUALITY_MEASURES__AIC).getCell(M);
		if (aicCell.getCellTypeEnum() == CellType.NUMERIC) {
			measures.setAic(BigDecimal.valueOf(aicCell.getNumericCellValue()));
		}

		Cell bicCell = sheet.getRow(QUALITY_MEASURES__BIC).getCell(M);
		if (bicCell.getCellTypeEnum() == CellType.NUMERIC) {
			measures.setBic(BigDecimal.valueOf(bicCell.getNumericCellValue()));
		}

		return measures;
	}

	@Override
	public Model retrieveModel(Sheet sheet) {
		
		QraModel model = new QraModel();
		model.setModelType("qraModel");
		model.setGeneralInformation(retrieveGeneralInformation(sheet));
		model.setScope(retrieveScope(sheet));
		model.setDataBackground(retrieveBackground(sheet));
		model.setModelMath(retrieveModelMath(sheet));
		
		return model;
	}
}
