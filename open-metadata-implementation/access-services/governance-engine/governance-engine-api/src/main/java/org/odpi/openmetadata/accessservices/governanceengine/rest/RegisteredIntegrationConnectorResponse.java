/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.accessservices.governanceengine.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.odpi.openmetadata.accessservices.governanceengine.metadataelements.RegisteredIntegrationConnectorElement;

import java.util.Arrays;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;


/**
 * RegisteredIntegrationConnectorResponse is the response structure used on the Governance Engine OMAS REST API calls that returns a
 * RegisteredIntegrationConnectorElement object as a response.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class RegisteredIntegrationConnectorResponse extends GovernanceEngineOMASAPIResponse
{
    private static final long    serialVersionUID = 1L;

    private RegisteredIntegrationConnectorElement registeredIntegrationConnector = null;

    /**
     * Default constructor
     */
    public RegisteredIntegrationConnectorResponse()
    {
        super();
    }


    /**
     * Copy/clone constructor
     *
     * @param template object to copy
     */
    public RegisteredIntegrationConnectorResponse(RegisteredIntegrationConnectorResponse template)
    {
        super(template);

        if (template != null)
        {
            this.registeredIntegrationConnector = template.getRegisteredIntegrationConnector();
        }
    }


    /**
     * Return the properties object.
     *
     * @return properties object
     */
    public RegisteredIntegrationConnectorElement getRegisteredIntegrationConnector()
    {
        if (registeredIntegrationConnector == null)
        {
            return null;
        }
        else
        {
            return registeredIntegrationConnector;
        }
    }


    /**
     * Set up the properties object.
     *
     * @param registeredIntegrationConnector  properties object
     */
    public void setRegisteredIntegrationConnector(RegisteredIntegrationConnectorElement registeredIntegrationConnector)
    {
        this.registeredIntegrationConnector = registeredIntegrationConnector;
    }


    /**
     * JSON-style toString
     *
     * @return return string containing the property names and values
     */
    @Override
    public String toString()
    {
        return "RegisteredIntegrationConnectorResponse{" +
                "registeredIntegrationConnector=" + registeredIntegrationConnector +
                ", exceptionClassName='" + getExceptionClassName() + '\'' +
                ", exceptionCausedBy='" + getExceptionCausedBy() + '\'' +
                ", actionDescription='" + getActionDescription() + '\'' +
                ", relatedHTTPCode=" + getRelatedHTTPCode() +
                ", exceptionErrorMessage='" + getExceptionErrorMessage() + '\'' +
                ", exceptionErrorMessageId='" + getExceptionErrorMessageId() + '\'' +
                ", exceptionErrorMessageParameters=" + Arrays.toString(getExceptionErrorMessageParameters()) +
                ", exceptionSystemAction='" + getExceptionSystemAction() + '\'' +
                ", exceptionUserAction='" + getExceptionUserAction() + '\'' +
                ", exceptionProperties=" + getExceptionProperties() +
                '}';
    }


    /**
     * Return comparison result based on the content of the properties.
     *
     * @param objectToCompare test object
     * @return result of comparison
     */
    @Override
    public boolean equals(Object objectToCompare)
    {
        if (this == objectToCompare)
        {
            return true;
        }
        if (objectToCompare == null || getClass() != objectToCompare.getClass())
        {
            return false;
        }
        if (!super.equals(objectToCompare))
        {
            return false;
        }
        RegisteredIntegrationConnectorResponse that = (RegisteredIntegrationConnectorResponse) objectToCompare;
        return Objects.equals(getRegisteredIntegrationConnector(), that.getRegisteredIntegrationConnector());
    }

    
    /**
     * Return hash code for this object
     *
     * @return int hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), getRegisteredIntegrationConnector());
    }
}
