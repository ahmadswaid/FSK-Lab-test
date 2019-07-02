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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Laboratory
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-07-02T16:22:48.194+02:00")
public class Laboratory {
  @SerializedName("accreditation")
  private List<String> accreditation = new ArrayList<String>();

  @SerializedName("name")
  private String name = null;

  @SerializedName("country")
  private String country = null;

  public Laboratory accreditation(List<String> accreditation) {
    this.accreditation = accreditation;
    return this;
  }

  public Laboratory addAccreditationItem(String accreditationItem) {
    this.accreditation.add(accreditationItem);
    return this;
  }

   /**
   * Get accreditation
   * @return accreditation
  **/
  @ApiModelProperty(required = true, value = "")
  public List<String> getAccreditation() {
    return accreditation;
  }

  public void setAccreditation(List<String> accreditation) {
    this.accreditation = accreditation;
  }

  public Laboratory name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Laboratory code (National laboratory code if available) or Laboratory name
   * @return name
  **/
  @ApiModelProperty(value = "Laboratory code (National laboratory code if available) or Laboratory name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Laboratory country(String country) {
    this.country = country;
    return this;
  }

   /**
   * Country where the laboratory is placed. (ISO 3166-1-alpha-2)
   * @return country
  **/
  @ApiModelProperty(value = "Country where the laboratory is placed. (ISO 3166-1-alpha-2)")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Laboratory laboratory = (Laboratory) o;
    return Objects.equals(this.accreditation, laboratory.accreditation) &&
        Objects.equals(this.name, laboratory.name) &&
        Objects.equals(this.country, laboratory.country);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accreditation, name, country);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Laboratory {\n");
    
    sb.append("    accreditation: ").append(toIndentedString(accreditation)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    country: ").append(toIndentedString(country)).append("\n");
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

