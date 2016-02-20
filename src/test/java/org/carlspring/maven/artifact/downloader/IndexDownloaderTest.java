package org.carlspring.maven.artifact.downloader;

import org.junit.Test;

/**
 * @author carlspring
 */
public class IndexDownloaderTest
{

    private IndexDownloader downloader;


    @Test
    public void testDownloading() throws Exception
    {
        downloader = new IndexDownloader();
        downloader.setIndexingContextId("central-context");
        downloader.setRepositoryId("central");
        downloader.setRepositoryURL("http://repo1.maven.org/maven2");
        downloader.setIndexLocalCacheDir("indexes/central-cache");
        downloader.setIndexDir("indexes/central-index");
        downloader.download();
    }
}
