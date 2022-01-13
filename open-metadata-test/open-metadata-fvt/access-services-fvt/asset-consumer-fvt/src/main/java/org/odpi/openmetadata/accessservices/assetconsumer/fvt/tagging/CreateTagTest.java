/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.accessservices.assetconsumer.fvt.tagging;

import org.odpi.openmetadata.accessservices.assetconsumer.client.AssetConsumer;
import org.odpi.openmetadata.accessservices.assetconsumer.client.rest.AssetConsumerRESTClient;
import org.odpi.openmetadata.accessservices.assetconsumer.elements.InformalTagElement;
import org.odpi.openmetadata.accessservices.assetconsumer.fvt.setup.AssetOwnerFactory;
import org.odpi.openmetadata.accessservices.assetconsumer.properties.InformalTagProperties;
import org.odpi.openmetadata.adminservices.configuration.registration.AccessServiceDescription;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.fvt.utilities.FVTResults;
import org.odpi.openmetadata.fvt.utilities.auditlog.FVTAuditLogDestination;
import org.odpi.openmetadata.fvt.utilities.exceptions.FVTUnexpectedCondition;

import java.util.List;


/**
 * CreateTagTest calls the AssetConsumerClient to create a tag and attach it to assets and schemas
 * and then retrieve the results.
 */
public class CreateTagTest
{
    private final static String testCaseName       = "CreateTagTest";

    private final static int    maxPageSize        = 100;

    /*
     * The tag name is constant - the guid is created as part of the test.
     */
    private final static String publicTag1Name         = "TestPublicTag1";
    private final static String publicTag1Description  = "PublicTag1 description";
    private final static String publicTag2Name         = "TestPublicTag2";
    private final static String publicTag2Description  = "PublicTag2 description";
    private final static String privateTagName         = "TestPrivateTag";
    private final static String privateTagDescription1 = "PrivateTag description1";
    private final static String privateTagDescription2 = "PrivateTag description2";

    private final static String differentUser = "newUserId";

    private final static String searchStringGetAll     = "PrivateTag description";
    private final static String searchStringGetNone    = "PrivateTag description";
    private final static String searchStringGetPrivate = "PrivateTag description";




    /**
     * Run all of the defined tests and capture the results.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformRootURL the network address of the server running the OMAS REST servers
     * @param userId calling user
     * @return  results of running test
     */
    public static FVTResults performFVT(String   serverName,
                                        String   serverPlatformRootURL,
                                        String   userId)
    {
        FVTResults results = new FVTResults(testCaseName);

        results.incrementNumberOfTests();
        try
        {
            CreateTagTest.runIt(serverPlatformRootURL, serverName, userId, results.getAuditLogDestination());
            results.incrementNumberOfSuccesses();
        }
        catch (Exception error)
        {
            results.addCapturedError(error);
        }

        System.out.println(results.toString());
        return results;
    }


    /**
     * Run all of the tests in this class.
     *
     * @param serverPlatformRootURL root url of the server
     * @param serverName name of the server
     * @param userId calling user
     * @param auditLogDestination logging destination
     * @throws FVTUnexpectedCondition the test case failed
     */
    private static void runIt(String                 serverPlatformRootURL,
                              String                 serverName,
                              String                 userId,
                              FVTAuditLogDestination auditLogDestination) throws FVTUnexpectedCondition
    {
        CreateTagTest thisTest = new CreateTagTest();

        AuditLog auditLog = new AuditLog(auditLogDestination,
                                         AccessServiceDescription.ASSET_CONSUMER_OMAS.getAccessServiceCode(),
                                         AccessServiceDescription.ASSET_CONSUMER_OMAS.getAccessServiceName(),
                                         AccessServiceDescription.ASSET_CONSUMER_OMAS.getAccessServiceDescription(),
                                         AccessServiceDescription.ASSET_CONSUMER_OMAS.getAccessServiceWiki());

        AssetConsumer client = thisTest.getAssetConsumerClient(serverName, serverPlatformRootURL, auditLog);
        AssetOwnerFactory factory = new AssetOwnerFactory(testCaseName, serverName, serverPlatformRootURL, auditLog);

        String assetGUID = factory.getAsset(userId);
        System.out.println("AssetGUID: " + assetGUID);
        String schemaTypeGUID = factory.getSchemaType(userId, assetGUID);
        System.out.println("SchemaTypeGUID: " + schemaTypeGUID);
        String asset2GUID = factory.getAssetFromTemplate(userId, assetGUID);
        System.out.println("Asset2GUID: " + asset2GUID);
        String publicTag1GUID = thisTest.getTagTest(client, userId, publicTag1Name, publicTag1Description, false, "getPublicTag", "PublicTag1");
        System.out.println("PublicTag1GUID: " + publicTag1GUID);
        String publicTag2GUID = thisTest.getTagTest(client, userId, publicTag2Name, publicTag2Description, false, "getPublicTag", "PublicTag2");
        System.out.println("PublicTag2GUID: " + publicTag2GUID);
        String privateTagGUID = thisTest.getTagTest(client, userId, privateTagName, privateTagDescription1, true, "getPrivateTag", "PrivateTag");
        System.out.println("PrivateTagGUID: " + privateTagGUID);
    }



    /**
     * Create and return an asset consumer client.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformRootURL the network address of the server running the OMAS REST servers
     * @param auditLog logging destination
     * @return client
     * @throws FVTUnexpectedCondition the test case failed
     */
    private AssetConsumer getAssetConsumerClient(String   serverName,
                                                 String   serverPlatformRootURL,
                                                 AuditLog auditLog) throws FVTUnexpectedCondition
    {
        final String activityName = "getAssetConsumerClient";

        try
        {
            AssetConsumerRESTClient restClient = new AssetConsumerRESTClient(serverName, serverPlatformRootURL);

            return new AssetConsumer(serverName, serverPlatformRootURL, restClient, maxPageSize, auditLog);
        }
        catch (Exception unexpectedError)
        {
            throw new FVTUnexpectedCondition(testCaseName, activityName, unexpectedError);
        }
    }



    /**
     * Create a tag, retrieve it by different methods and return its GUID.  Also test the retrieve methods
     *
     * @param client interface to Asset Consumer OMAS
     * @param userId calling user
     * @param tagName name of tag to store
     * @param tagDescription description of tag to store
     * @param isPrivate should this be a private tag or not?
     * @param testCaseName name of the test case
     * @param tagTypeName name of tag in reporting output
     * @return GUID of privateTag
     * @throws FVTUnexpectedCondition the test case failed
     */
    private String getTagTest(AssetConsumer client,
                              String        userId,
                              String        tagName,
                              String        tagDescription,
                              boolean       isPrivate,
                              String        testCaseName,
                              String        tagTypeName) throws FVTUnexpectedCondition
    {
        try
        {
            String activityName = testCaseName + "::create" + tagTypeName;
            System.out.println();
            String tagGUID;

            if (isPrivate)
            {
                tagGUID = client.createPrivateTag(userId, tagName, tagDescription);
            }
            else
            {
                tagGUID = client.createPublicTag(userId, tagName, tagDescription);
            }
            if (tagGUID == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no GUID for create of" +  tagTypeName + ")");
            }

            activityName = testCaseName + "::getByGUIDAfterCreate" + tagTypeName;
            InformalTagElement    retrievedElement = client.getTag(userId, tagGUID);

            this.validateTag(retrievedElement, userId, tagName, tagDescription, isPrivate, activityName, tagTypeName);

            activityName = testCaseName + "::getByNameAfterCreate" + tagTypeName;
            List<InformalTagElement> tagList = client.getTagsByName(userId, tagName, 0, maxPageSize);

            if (tagList == null)
            {
                Thread.sleep(600);
                tagList = client.getTagsByName(userId, tagName, 0, maxPageSize);
            }
            if (tagList == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no tag for RetrieveByName of " + tagTypeName + ")");
            }
            else if (tagList.isEmpty())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Empty list for RetrieveByName of " + tagTypeName + ")");
            }
            else if (tagList.size() != 1)
            {
                throw new FVTUnexpectedCondition(testCaseName,
                                                 activityName + "(" + tagTypeName + " list for RetrieveByName contains" + tagList.size() +
                                                         " elements)");
            }

            retrievedElement = tagList.get(0);

            this.validateTag(retrievedElement, userId, tagName, tagDescription, isPrivate, activityName, tagTypeName);

            if (isPrivate)
            {
                activityName = testCaseName + "::getHiddenPrivateTag";
                try
                {
                    retrievedElement = client.getTag(differentUser, tagGUID);
                    if (retrievedElement == null)
                    {
                        throw new FVTUnexpectedCondition(testCaseName, activityName + "(Null Private tag returned to wrong user");
                    }
                    else
                    {
                        throw new FVTUnexpectedCondition(testCaseName,
                                                         activityName + "(Private tag returned to wrong user: " + retrievedElement.toString() + ")");
                    }
                }
                catch (InvalidParameterException notFound)
                {
                    // expected because this is a private tag
                }
            }

            return tagGUID;
        }
        catch (FVTUnexpectedCondition testCaseError)
        {
            throw testCaseError;
        }
        catch (Exception unexpectedError)
        {
            throw new FVTUnexpectedCondition(testCaseName, testCaseName, unexpectedError);
        }
    }




    /**
     * Create a tag, retrieve it by different methods and return its GUID.  Also test the retrieve methods
     *
     * @param retrievedElement element to test
     * @param userId calling user
     * @param tagName name of tag to store
     * @param tagDescription description of tag to store
     * @param isPrivate should this be a private tag or not?
     * @param activityName name of the test case
     * @param tagTypeName name of tag in reporting output
     * @return GUID of privateTag
     * @throws FVTUnexpectedCondition the test case failed
     */
    private void validateTag(InformalTagElement retrievedElement,
                             String             userId,
                             String             tagName,
                             String             tagDescription,
                             boolean            isPrivate,
                             String             activityName,
                             String             tagTypeName) throws FVTUnexpectedCondition
    {

        InformalTagProperties retrievedTag     = retrievedElement.getInformalTagProperties();

        if (retrievedTag == null)
        {
            throw new FVTUnexpectedCondition(testCaseName, activityName + "(no tag from Retrieve of " + tagTypeName + " by GUID)");
        }

        if (! tagName.equals(retrievedTag.getName()))
        {
            throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from Retrieve of " + tagTypeName + " by GUID)");
        }
        if (! tagDescription.equals(retrievedTag.getDescription()))
        {
            throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from Retrieve of " + tagTypeName + " by GUID)");
        }
        if (isPrivate)
        {
            if (! retrievedTag.getIsPrivateTag())
            {
                throw new FVTUnexpectedCondition(testCaseName,
                                                 activityName + "(Returned as public tag by Retrieve of " + tagTypeName + " by GUID)");
            }
        }
        else
        {
            if (retrievedTag.getIsPrivateTag())
            {
                throw new FVTUnexpectedCondition(testCaseName,
                                                 activityName + "(Returned as private tag by Retrieve of " + tagTypeName + " by GUID)");
            }
        }

        if (! userId.equals(retrievedTag.getUser()))
        {
            throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from Retrieve of " + tagTypeName + " by GUID)");
        }
    }


    /**
     * Create a public tag and return its GUID.  Also test the retrieve methods
     *
     * @param client interface to Asset Consumer OMAS
     * @param userId calling user
     * @return GUID of privateTag
     * @throws FVTUnexpectedCondition the test case failed
     */
    private String getPublicTag(AssetConsumer client,
                                String        userId) throws FVTUnexpectedCondition
    {
        final String methodName = "getPublicTag";


        try
        {
            String activityName = methodName + "::createPublicTag";
            System.out.println();
            String publicTagGUID = client.createPublicTag(userId, publicTag1Name, publicTag1Description);
            if (publicTagGUID == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no GUID for create of public tag 1)");
            }

            activityName = methodName + "::getAfterCreatePublicTag1";
            InformalTagElement    retrievedElement = client.getTag(userId, publicTagGUID);
            InformalTagProperties retrievedTag     = retrievedElement.getInformalTagProperties();

            if (retrievedTag == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no PublicTag1 from Retrieve of public tag by GUID)");
            }

            if (! publicTag1Name.equals(retrievedTag.getName()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from Retrieve of public tag 1 by GUID)");
            }
            if (! publicTag1Description.equals(retrievedTag.getDescription()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from Retrieve of public tag 1 by GUID)");
            }
            if (retrievedTag.getIsPrivateTag())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Returned as private tag by Retrieve of public tag 1)");
            }
            if (! userId.equals(retrievedTag.getUser()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from Retrieve of private tag)");
            }

            activityName = methodName + "::getByNameAfterCreatePublicTag1";
            List<InformalTagElement> privateTagList = client.getTagsByName(userId, publicTag1Name, 0, maxPageSize);

            if (privateTagList == null)
            {
                Thread.sleep(600);
                privateTagList = client.getTagsByName(userId, publicTag1Name, 0, maxPageSize);
            }
            if (privateTagList == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no PrivateTag for RetrieveByName of private tag)");
            }
            else if (privateTagList.isEmpty())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Empty list for RetrieveByName of private tag)");
            }
            else if (privateTagList.size() != 1)
            {
                throw new FVTUnexpectedCondition(testCaseName,
                                                 activityName + "(PrivateTag list for RetrieveByName contains" + privateTagList.size() +
                                                 " elements)");
            }

            retrievedElement = privateTagList.get(0);
            retrievedTag = retrievedElement.getInformalTagProperties();

            if (! privateTagName.equals(retrievedTag.getName()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from RetrieveByName of private tag)");
            }
            if (! privateTagDescription1.equals(retrievedTag.getDescription()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from RetrieveByName of private tag)");
            }
            if (! retrievedTag.getIsPrivateTag())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Returned as public tag by RetrieveByName of private tag)");
            }
            if (! userId.equals(retrievedTag.getUser()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from RetrieveByName of private tag)");
            }

            activityName = methodName + "::getHiddenPrivateTag";
            try
            {
                retrievedElement = client.getTag(differentUser, publicTagGUID);
                if (retrievedElement == null)
                {
                    throw new FVTUnexpectedCondition(testCaseName, activityName + "(Null Private tag returned to wrong user");
                }
                else
                {
                    throw new FVTUnexpectedCondition(testCaseName,
                                                     activityName + "(Private tag returned to wrong user: " + retrievedElement.toString() + ")");
                }
            }
            catch (InvalidParameterException notFound)
            {
                // expected because this is a private tag
            }

            activityName = methodName + "::createPublicTag1";
            String publicTag1GUID = client.createPublicTag(userId, publicTag1Name, publicTag1Description);
            if (publicTag1GUID == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no GUID for create of public tag 1)");
            }

            activityName = methodName + "::getTagAfterCreatePublicTag1";
            retrievedElement = client.getTag(userId, publicTag1GUID);
            retrievedTag     = retrievedElement.getInformalTagProperties();

            if (retrievedTag == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no PublicTag from Retrieve of public tag 1)");
            }

            if (! publicTag1Name.equals(retrievedTag.getName()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from Retrieve of public tag 1)");
            }
            if (! publicTag1Description.equals(retrievedTag.getDescription()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from Retrieve of public tag 1)");
            }
            if (! userId.equals(retrievedTag.getUser()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from Retrieve of public tag 1)");
            }

            activityName = methodName + "::getTagByNameAfterCreatePublicTag1";
            List<InformalTagElement> retrievedTagList = client.getTagsByName(userId, publicTag1Name, 0, maxPageSize);

            if (retrievedTagList == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Null result for RetrieveByName of public tag 1)");
            }
            else if (retrievedTagList.isEmpty())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Empty list for RetrieveByName of public tag 1)");
            }
            else if (retrievedTagList.size() != 1)
            {
                throw new FVTUnexpectedCondition(testCaseName,
                                                 activityName + "(Public tag 1 list for RetrieveByName contains" + retrievedTagList.size() +
                                                         " elements)");
            }

            retrievedElement = retrievedTagList.get(0);
            retrievedTag = retrievedElement.getInformalTagProperties();

            if (! publicTag1Name.equals(retrievedTag.getName()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from RetrieveByName of public tag 1)");
            }
            if (! publicTag1Description.equals(retrievedTag.getDescription()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from RetrieveByName of public tag 1)");
            }
            if (! userId.equals(retrievedTag.getUser()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from RetrieveByName of public tag 1)");
            }

            activityName = methodName + "::getTagAfterCreatePublicTag2";
            String publicTag2GUID = client.createPublicTag(userId, publicTag2Name, publicTag2Description);
            if (publicTag2GUID == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no GUID for create of public tag 2)");
            }

            return publicTag1GUID;
        }
        catch (FVTUnexpectedCondition testCaseError)
        {
            throw testCaseError;
        }
        catch (Exception unexpectedError)
        {
            throw new FVTUnexpectedCondition(testCaseName, methodName, unexpectedError);
        }
    }


    /**
     * Create a public tag and return its GUID.  Also test the retrieve methods
     *
     * @param client interface to Asset Consumer OMAS
     * @param userId calling user
     * @return GUID of privateTag
     * @throws FVTUnexpectedCondition the test case failed
     */
    private String getPrivateTag(AssetConsumer client,
                                String        userId) throws FVTUnexpectedCondition
    {
        final String methodName = "getPublicTag";


        try
        {
            String activityName = methodName + "::createPrivateTag";
            System.out.println();
            String privateTagGUID = client.createPrivateTag(userId, privateTagName, privateTagDescription1);
            if (privateTagGUID == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no GUID for create of private tag)");
            }

            activityName = methodName + "::getAfterCreatePrivateTag";
            InformalTagElement    retrievedElement = client.getTag(userId, privateTagGUID);
            InformalTagProperties retrievedTag     = retrievedElement.getInformalTagProperties();

            if (retrievedTag == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no PrivateTag from Retrieve of private tag)");
            }

            if (! privateTagName.equals(retrievedTag.getName()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from Retrieve of private tag)");
            }
            if (! privateTagDescription1.equals(retrievedTag.getDescription()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from Retrieve of private tag)");
            }
            if (! retrievedTag.getIsPrivateTag())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Returned as public tag by Retrieve of private tag)");
            }
            if (! userId.equals(retrievedTag.getUser()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from Retrieve of private tag)");
            }

            activityName = methodName + "::getByNameAfterCreatePrivateTag";
            List<InformalTagElement> privateTagList = client.getTagsByName(userId, privateTagName, 0, maxPageSize);

            if (privateTagList == null)
            {
                Thread.sleep(600);
                privateTagList = client.getTagsByName(userId, privateTagName, 0, maxPageSize);
            }
            if (privateTagList == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no PrivateTag for RetrieveByName of private tag)");
            }
            else if (privateTagList.isEmpty())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Empty list for RetrieveByName of private tag)");
            }
            else if (privateTagList.size() != 1)
            {
                throw new FVTUnexpectedCondition(testCaseName,
                                                 activityName + "(PrivateTag list for RetrieveByName contains" + privateTagList.size() +
                                                         " elements)");
            }

            retrievedElement = privateTagList.get(0);
            retrievedTag = retrievedElement.getInformalTagProperties();

            if (! privateTagName.equals(retrievedTag.getName()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from RetrieveByName of private tag)");
            }
            if (! privateTagDescription1.equals(retrievedTag.getDescription()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from RetrieveByName of private tag)");
            }
            if (! retrievedTag.getIsPrivateTag())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Returned as public tag by RetrieveByName of private tag)");
            }
            if (! userId.equals(retrievedTag.getUser()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from RetrieveByName of private tag)");
            }

            activityName = methodName + "::getHiddenPrivateTag";
            try
            {
                retrievedElement = client.getTag(differentUser, privateTagGUID);
                if (retrievedElement == null)
                {
                    throw new FVTUnexpectedCondition(testCaseName, activityName + "(Null Private tag returned to wrong user");
                }
                else
                {
                    throw new FVTUnexpectedCondition(testCaseName,
                                                     activityName + "(Private tag returned to wrong user: " + retrievedElement.toString() + ")");
                }
            }
            catch (InvalidParameterException notFound)
            {
                // expected because this is a private tag
            }

            activityName = methodName + "::createPublicTag1";
            String publicTag1GUID = client.createPublicTag(userId, publicTag1Name, publicTag1Description);
            if (publicTag1GUID == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no GUID for create of public tag 1)");
            }

            activityName = methodName + "::getTagAfterCreatePublicTag1";
            retrievedElement = client.getTag(userId, publicTag1GUID);
            retrievedTag     = retrievedElement.getInformalTagProperties();

            if (retrievedTag == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no PublicTag from Retrieve of public tag 1)");
            }

            if (! publicTag1Name.equals(retrievedTag.getName()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from Retrieve of public tag 1)");
            }
            if (! publicTag1Description.equals(retrievedTag.getDescription()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from Retrieve of public tag 1)");
            }
            if (! userId.equals(retrievedTag.getUser()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from Retrieve of public tag 1)");
            }

            activityName = methodName + "::getTagByNameAfterCreatePublicTag1";
            List<InformalTagElement> retrievedTagList = client.getTagsByName(userId, publicTag1Name, 0, maxPageSize);

            if (retrievedTagList == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Null result for RetrieveByName of public tag 1)");
            }
            else if (retrievedTagList.isEmpty())
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Empty list for RetrieveByName of public tag 1)");
            }
            else if (retrievedTagList.size() != 1)
            {
                throw new FVTUnexpectedCondition(testCaseName,
                                                 activityName + "(Public tag 1 list for RetrieveByName contains" + retrievedTagList.size() +
                                                         " elements)");
            }

            retrievedElement = retrievedTagList.get(0);
            retrievedTag = retrievedElement.getInformalTagProperties();

            if (! publicTag1Name.equals(retrievedTag.getName()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad tag name from RetrieveByName of public tag 1)");
            }
            if (! publicTag1Description.equals(retrievedTag.getDescription()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad description from RetrieveByName of public tag 1)");
            }
            if (! userId.equals(retrievedTag.getUser()))
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(Bad user from RetrieveByName of public tag 1)");
            }

            activityName = methodName + "::getTagAfterCreatePublicTag2";
            String publicTag2GUID = client.createPublicTag(userId, publicTag2Name, publicTag2Description);
            if (publicTag2GUID == null)
            {
                throw new FVTUnexpectedCondition(testCaseName, activityName + "(no GUID for create of public tag 2)");
            }

            return publicTag1GUID;
        }
        catch (FVTUnexpectedCondition testCaseError)
        {
            throw testCaseError;
        }
        catch (Exception unexpectedError)
        {
            throw new FVTUnexpectedCondition(testCaseName, methodName, unexpectedError);
        }
    }
}
