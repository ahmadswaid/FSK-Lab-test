/*
 * RAKIP Generic model
 * TODO
 *
 * OpenAPI spec version: 1.0.4
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package de.bund.bfr.metadata.swagger;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.bund.bfr.metadata.swagger.Assay;
import de.bund.bfr.metadata.swagger.Laboratory;
import de.bund.bfr.metadata.swagger.Study;
import de.bund.bfr.metadata.swagger.StudySample;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * OtherModelDataBackground
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-07-02T16:22:48.194+02:00")
public class OtherModelDataBackground {
  @SerializedName("study")
  private Study study = null;

  @SerializedName("studySample")
  private List<StudySample> studySample = null;

  @SerializedName("laboratory")
  private List<Laboratory> laboratory = null;

  @SerializedName("assay")
  private List<Assay> assay = null;

  public OtherModelDataBackground study(Study study) {
    this.study = study;
    return this;
  }

   /**
   * Get study
   * @return study
  **/
  @ApiModelProperty(required = true, value = "")
  public Study getStudy() {
    return study;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

  public OtherModelDataBackground studySample(List<StudySample> studySample) {
    this.studySample = studySample;
    return this;
  }

  public OtherModelDataBackground addStudySampleItem(StudySample studySampleItem) {
    if (this.studySample == null) {
      this.studySample = new ArrayList<StudySample>();
    }
    this.studySample.add(studySampleItem);
    return this;
  }

   /**
   * Get studySample
   * @return studySample
  **/
  @ApiModelProperty(value = "")
  public List<StudySample> getStudySample() {
    return studySample;
  }

  public void setStudySample(List<StudySample> studySample) {
    this.studySample = studySample;
  }

  public OtherModelDataBackground laboratory(List<Laboratory> laboratory) {
    this.laboratory = laboratory;
    return this;
  }

  public OtherModelDataBackground addLaboratoryItem(Laboratory laboratoryItem) {
    if (this.laboratory == null) {
      this.laboratory = new ArrayList<Laboratory>();
    }
    this.laboratory.add(laboratoryItem);
    return this;
  }

   /**
   * Get laboratory
   * @return laboratory
  **/
  @ApiModelProperty(value = "")
  public List<Laboratory> getLaboratory() {
    return laboratory;
  }

  public void setLaboratory(List<Laboratory> laboratory) {
    this.laboratory = laboratory;
  }

  public OtherModelDataBackground assay(List<Assay> assay) {
    this.assay = assay;
    return this;
  }

  public OtherModelDataBackground addAssayItem(Assay assayItem) {
    if (this.assay == null) {
      this.assay = new ArrayList<Assay>();
    }
    this.assay.add(assayItem);
    return this;
  }

   /**
   * Get assay
   * @return assay
  **/
  @ApiModelProperty(value = "")
  public List<Assay> getAssay() {
    return assay;
  }

  public void setAssay(List<Assay> assay) {
    this.assay = assay;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OtherModelDataBackground otherModelDataBackground = (OtherModelDataBackground) o;
    return Objects.equals(this.study, otherModelDataBackground.study) &&
        Objects.equals(this.studySample, otherModelDataBackground.studySample) &&
        Objects.equals(this.laboratory, otherModelDataBackground.laboratory) &&
        Objects.equals(this.assay, otherModelDataBackground.assay);
  }

  @Override
  public int hashCode() {
    return Objects.hash(study, studySample, laboratory, assay);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OtherModelDataBackground {\n");
    
    sb.append("    study: ").append(toIndentedString(study)).append("\n");
    sb.append("    studySample: ").append(toIndentedString(studySample)).append("\n");
    sb.append("    laboratory: ").append(toIndentedString(laboratory)).append("\n");
    sb.append("    assay: ").append(toIndentedString(assay)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

