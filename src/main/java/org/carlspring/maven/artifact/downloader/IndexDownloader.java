package org.carlspring.maven.artifact.downloader;

import org.apache.maven.index.Indexer;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.updater.*;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.AbstractTransferListener;
import org.codehaus.plexus.*;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author carlspring
 */
public class IndexDownloader
{

    private final PlexusContainer plexusContainer;

    private final Indexer indexer;

    private final IndexUpdater indexUpdater;

    private final Wagon httpWagon;

    private IndexingContext centralContext;

    private String indexingContextId;

    private String repositoryId;

    private String repositoryURL;

    private String indexLocalCacheDir;

    private String indexDir;


    public IndexDownloader()
            throws PlexusContainerException,
                   ComponentLookupException
    {
        // Here we create the Plexus container, the Maven default IoC container
        final DefaultContainerConfiguration config = new DefaultContainerConfiguration();
        config.setClassPathScanning(PlexusConstants.SCANNING_INDEX);
        this.plexusContainer = new DefaultPlexusContainer(config);

        // Lookup the indexer components from Plexus
        this.indexer = plexusContainer.lookup(Indexer.class);
        this.indexUpdater = plexusContainer.lookup(IndexUpdater.class);

        // Lookup the Wagon used to remotely fetch the index
        this.httpWagon = plexusContainer.lookup(Wagon.class, "http");
    }

    public void download() throws IOException, ComponentLookupException
    {
        // Files where local cache is (if any) and Lucene Index should be located
        File centralLocalCache = new File(indexLocalCacheDir);
        File centralIndexDir = new File(indexDir);

        // Creators we want to use (search for fields it defines)
        List<IndexCreator> indexers = new ArrayList<>();
        indexers.add(plexusContainer.lookup(IndexCreator.class, "min"));
        indexers.add(plexusContainer.lookup(IndexCreator.class, "jarContent"));
        indexers.add(plexusContainer.lookup(IndexCreator.class, "maven-plugin"));

        // Create context for central repository index
        centralContext = indexer.createIndexingContext(indexingContextId,
                                                       repositoryId,
                                                       centralLocalCache,
                                                       centralIndexDir,
                                                       repositoryURL,
                                                       null,
                                                       true,
                                                       true,
                                                       indexers);

        // Update the index (incremental update will happen if this is not 1st run and files are not deleted)
        // This whole block below should not be executed on every app start, but rather controlled by some configuration
        // since this block will always emit at least one HTTP GET. Maven Central indexes are updated once a week, but
        // other index sources might have different index publishing frequency.
        // Preferred frequency is once a week.

        System.out.println("Updating Index...");
        System.out.println("This might take a while on first run, so please be patient!");
        // Create ResourceFetcher implementation to be used with IndexUpdateRequest
        // Here, we use Wagon based one as shorthand, but all we need is a ResourceFetcher implementation
        TransferListener listener = new AbstractTransferListener()
        {
            public void transferStarted(TransferEvent transferEvent)
            {
                System.out.print("  Downloading " + transferEvent.getResource().getName());
            }

            public void transferProgress(TransferEvent transferEvent, byte[] buffer, int length)
            {
            }

            public void transferCompleted(TransferEvent transferEvent)
            {
                System.out.println(" - Done");
            }
        };
        ResourceFetcher resourceFetcher = new WagonHelper.WagonFetcher(httpWagon, listener, null, null);

        Date centralContextCurrentTimestamp = centralContext.getTimestamp();
        IndexUpdateRequest updateRequest = new IndexUpdateRequest(centralContext, resourceFetcher);
        IndexUpdateResult updateResult = indexUpdater.fetchAndUpdateIndex(updateRequest);
        if (updateResult.isFullUpdate())
        {
            System.out.println("Performed full update!");
        }
        else if (updateResult.getTimestamp().equals(centralContextCurrentTimestamp))
        {
            System.out.println("No update needed, index is up to date!");
        }
        else
        {
            System.out.println("Performed an incremental update, with changes covering the period between " +
                               centralContextCurrentTimestamp + " - " + updateResult.getTimestamp() + ".");
        }

        //resourceFetcher.retrieve(something something something else);
        System.out.println();
    }

    public String getIndexingContextId()
    {
        return indexingContextId;
    }

    public void setIndexingContextId(String indexingContextId)
    {
        this.indexingContextId = indexingContextId;
    }

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    public String getRepositoryURL()
    {
        return repositoryURL;
    }

    public void setRepositoryURL(String repositoryURL)
    {
        this.repositoryURL = repositoryURL;
    }

    public String getIndexLocalCacheDir()
    {
        return indexLocalCacheDir;
    }

    public void setIndexLocalCacheDir(String indexLocalCacheDir)
    {
        this.indexLocalCacheDir = indexLocalCacheDir;
    }

    public String getIndexDir()
    {
        return indexDir;
    }

    public void setIndexDir(String indexDir)
    {
        this.indexDir = indexDir;
    }

    public Indexer getIndexer()
    {
        return indexer;
    }

    public IndexingContext getIndexingContext()
    {
        return centralContext;
    }

}
