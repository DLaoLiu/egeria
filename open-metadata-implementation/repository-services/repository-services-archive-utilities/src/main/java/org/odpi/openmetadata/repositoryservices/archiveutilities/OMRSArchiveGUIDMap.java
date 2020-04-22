/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.repositoryservices.archiveutilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * OMRSArchiveGUIDMap is a utility to create a persisted list of GUIDs used by an archive builder.
 * This helps the archive builder use the same GUIDs each time the archive is built.  GUIDs for
 * new elements are created automatically.
 */
public class OMRSArchiveGUIDMap
{
    private static final Logger log = LoggerFactory.getLogger(OMRSArchiveGUIDMap.class);

    private String              guidMapFileName;
    private Map<String, String> idToGUIDMap;
    private Set<String> guids = new HashSet<>();

    /**
     * Constructor for the GUIDMap.
     *
     * @param guidMapFileName name of the file name where the GUIDs are stashed.
     */
    public OMRSArchiveGUIDMap(String guidMapFileName)
    {
        this.guidMapFileName = guidMapFileName;

        this.loadGUIDs();
    }


    /**
     * Load up the saved GUIds into a new HashMap.
     */
    @SuppressWarnings("unchecked")
    private void loadGUIDs()
    {
        File         idFile = new File(guidMapFileName);
        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            log.debug("Retrieving Id to GUID Map");

            String idFileContents = FileUtils.readFileToString(idFile, "UTF-8");

            idToGUIDMap = objectMapper.readValue(idFileContents, Map.class);
        }
        catch (Throwable   error)
        {
            idToGUIDMap = new HashMap<>();
        }

        log.debug("Id to GUID Map contains: " + idToGUIDMap);
    }


    /**
     * Fix the guid for a particular name.
     *
     * @param id identifier associated with the GUID
     * @param guid fixed guid value
     */
    public  void setGUID(String  id, String  guid)
    {
        idToGUIDMap.put(id, guid);
        // TODO if the supplied guid is a duplicate of one in the archive then there will be an issue
        guids.add(guid);
    }


    /**
     * Retrieve the guid for an element based on its id. The method ensures that guids in the archive are unique.
     *
     * @param id id of element
     * @return guid mapped to Id
     */
    public String  getGUID(String id)
    {
        String guid = idToGUIDMap.get(id);

        if (guid == null)
        {
            boolean duplicate =true;
            int count =0;
            while (duplicate) {
                guid = UUID.randomUUID().toString();
                if (guids.contains(guid)) {
                    log.debug("Guid clash - generating a new one");
                } else {
                    duplicate = false;
                }
                if (count ==10) {
                    throw new RuntimeException("Duplicate guid generated; tried 10 times");
                }
                count++;
            }
            guids.add(guid);
            idToGUIDMap.put(id, guid);
        }

        return guid;
    }


    /**
     * Save the map to a file
     */
    public void  saveGUIDs()
    {
        File         idFile = new File(guidMapFileName);
        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            if (idToGUIDMap.isEmpty())
            {
                log.debug("Deleting id file because map is empty: " + guidMapFileName);

                idFile.delete();
            }
            else
            {
                log.debug("Writing id file " + guidMapFileName);

                String mapContents = objectMapper.writeValueAsString(idToGUIDMap);

                FileUtils.writeStringToFile(idFile, mapContents, (String)null,false);
            }

        }
        catch (Throwable  exc)
        {
            log.error("Unusable Map Store :(", exc);

        }
    }


    /**
     * Return the size of the map.
     *
     * @return int (zero if the map is null)
     */
    public int getSize()
    {
        if (idToGUIDMap != null)
        {
            return idToGUIDMap.size();
        }
        else
        {
            return 0;
        }
    }
}
