/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.repositoryservices.igc.clientlibrary.model.generated.v117;

import org.odpi.openmetadata.adapters.repositoryservices.igc.clientlibrary.model.common.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.ArrayList;

/**
 * POJO for the 'stage_data_rule_definition' asset type in IGC, displayed as 'Stage Data Rule Definition' in the IGC UI.
 * <br><br>
 * (this code has been generated based on out-of-the-box IGC metadata types;
 *  if modifications are needed, eg. to handle custom attributes,
 *  extending from this class in your own custom class is the best approach.)
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class StageDataRuleDefinition extends Reference {

    @JsonIgnore public static final String IGC_TYPE_ID = "stage_data_rule_definition";

    /**
     * The 'name' property, displayed as 'Name' in the IGC UI.
     */
    protected String name;

    /**
     * The 'stage' property, displayed as 'Stage' in the IGC UI.
     * <br><br>
     * Will be a single {@link Reference} to a {@link Stage} object.
     */
    protected Reference stage;

    /**
     * The 'stage_logic' property, displayed as 'Stage Logic' in the IGC UI.
     */
    protected String stage_logic;

    /**
     * The 'based_on_rule' property, displayed as 'Based on Rule' in the IGC UI.
     * <br><br>
     * Will be a single {@link Reference} to a {@link PublishedDataRuleDefinition} object.
     */
    protected Reference based_on_rule;

    /**
     * The 'created_by' property, displayed as 'Created By' in the IGC UI.
     */
    protected String created_by;

    /**
     * The 'created_on' property, displayed as 'Created On' in the IGC UI.
     */
    protected Date created_on;

    /**
     * The 'modified_by' property, displayed as 'Modified By' in the IGC UI.
     */
    protected String modified_by;

    /**
     * The 'modified_on' property, displayed as 'Modified On' in the IGC UI.
     */
    protected Date modified_on;


    /** @see #name */ @JsonProperty("name")  public String getTheName() { return this.name; }
    /** @see #name */ @JsonProperty("name")  public void setTheName(String name) { this.name = name; }

    /** @see #stage */ @JsonProperty("stage")  public Reference getStage() { return this.stage; }
    /** @see #stage */ @JsonProperty("stage")  public void setStage(Reference stage) { this.stage = stage; }

    /** @see #stage_logic */ @JsonProperty("stage_logic")  public String getStageLogic() { return this.stage_logic; }
    /** @see #stage_logic */ @JsonProperty("stage_logic")  public void setStageLogic(String stage_logic) { this.stage_logic = stage_logic; }

    /** @see #based_on_rule */ @JsonProperty("based_on_rule")  public Reference getBasedOnRule() { return this.based_on_rule; }
    /** @see #based_on_rule */ @JsonProperty("based_on_rule")  public void setBasedOnRule(Reference based_on_rule) { this.based_on_rule = based_on_rule; }

    /** @see #created_by */ @JsonProperty("created_by")  public String getCreatedBy() { return this.created_by; }
    /** @see #created_by */ @JsonProperty("created_by")  public void setCreatedBy(String created_by) { this.created_by = created_by; }

    /** @see #created_on */ @JsonProperty("created_on")  public Date getCreatedOn() { return this.created_on; }
    /** @see #created_on */ @JsonProperty("created_on")  public void setCreatedOn(Date created_on) { this.created_on = created_on; }

    /** @see #modified_by */ @JsonProperty("modified_by")  public String getModifiedBy() { return this.modified_by; }
    /** @see #modified_by */ @JsonProperty("modified_by")  public void setModifiedBy(String modified_by) { this.modified_by = modified_by; }

    /** @see #modified_on */ @JsonProperty("modified_on")  public Date getModifiedOn() { return this.modified_on; }
    /** @see #modified_on */ @JsonProperty("modified_on")  public void setModifiedOn(Date modified_on) { this.modified_on = modified_on; }


    public static final Boolean isStageDataRuleDefinition(Object obj) { return (obj.getClass() == StageDataRuleDefinition.class); }

}
