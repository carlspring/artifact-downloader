package org.carlspring.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.*;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.IOUtils;
import org.carlspring.commons.io.RandomInputStream;
import org.carlspring.commons.io.resource.ResourceCloser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;

/**
 * @author Martin Todorov
 */
@Path("/storages")
public class ArtifactRestlet
{

    private static final Logger logger = LoggerFactory.getLogger(ArtifactRestlet.class);


    @PUT
    @Path("{storageId}/{repositoryId}/{path:.*}")
    public Response upload(@PathParam("storageId") String storageId,
                           @PathParam("repositoryId") String repositoryId,
                           @PathParam("path") String path,
                           @Context HttpHeaders headers,
                           @Context HttpServletRequest request,
                           InputStream is)
            throws IOException,
                   NoSuchAlgorithmException
    {
        logger.debug("Deploying to " + storageId + "/" + repositoryId + "/" + path + "...");

        OutputStream os = null;
        try
        {
            final File file = new File("target/storages/" + storageId + File.separatorChar + repositoryId, path);

            if (!file.getParentFile().exists())
            {
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
            }

            logger.debug("Storing to " + file.getAbsolutePath() + "...");

            os = new FileOutputStream(file);

            IOUtils.copy(is, os);

            return Response.ok().build();
        }
        catch (IOException e)
        {
            return Response.status(Response.Status.FORBIDDEN).entity("Access denied!").build();
        }
        finally
        {
            ResourceCloser.close(os, logger);
        }
    }

    @GET
    @Path("{storageId}/{repositoryId}/{path:.*}")
    public Response download(@PathParam("storageId") String storageId,
                             @PathParam("repositoryId") String repositoryId,
                             @PathParam("path") String path,
                             @QueryParam("length") long length,
                             @Context HttpServletRequest request,
                             @Context HttpHeaders headers)
            throws IOException,
                   InstantiationException,
                   IllegalAccessException,
                   ClassNotFoundException
    {
        logger.debug(" repositoryId = " + repositoryId + ", path = " + path);

        if (length > 0)
        {
            // Create random data.
            logger.debug("Generating stream with " + length + " bytes.");

            return Response.ok(new RandomInputStream(length))
                           .header("Content-Length", length)
                           .build();
        }
        else
        {
            // TODO: Implement the directory browsing here.
            // TODO: If the path is a directory, serve an HTML with the contents of the directory.
            // TODO: If it's a file, serve the file.

            File file = new File("target/storages/" + storageId + "/" + repositoryId + "/" + path);
            if (file.isDirectory())
            {
                // TODO: If the path is a directory, serve an HTML with the contents of the directory.
                // TODO: Implement this HERE.

                // For the sake of compiling.
                return null;
            }
            else
            {
                InputStream is = new FileInputStream(file);
                return Response.ok(is).build();
            }
        }
    }

    @DELETE
    @Path("{storageId}/{repositoryId}/{path:.*}")
    public Response delete(@PathParam("storageId") String storageId,
                           @PathParam("repositoryId") String repositoryId,
                           @PathParam("path") String path)
            throws IOException
    {
        logger.debug("DELETE: " + path);
        logger.debug(" repository = " + repositoryId + ", path = " + path);

        return Response.ok().build();
    }

}
