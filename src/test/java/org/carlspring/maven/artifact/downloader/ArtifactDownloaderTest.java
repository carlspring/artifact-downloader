package org.carlspring.maven.artifact.downloader;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author carlspring
 */
public class ArtifactDownloaderTest
{

    public static final String TARGET_DIR = "target/test-resources";


    @Before
    public void setUp() throws Exception
    {
        File dir = new File(TARGET_DIR);
        if (!dir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
    }

    @Test
    public void testArtifactDownloading()
            throws IOException, URISyntaxException
    {
        ArtifactDownloader downloader = new ArtifactDownloader();
        downloader.setVerbose(true);
        // downloader.download(new URL("http://download.opensuse.org/distribution/leap/42.1/iso/openSUSE-Leap-42.1-NET-x86_64.iso"),
        //                     new File(TARGET_DIR + "/openSUSE-Leap-42.1-NET-x86_64.iso"));
        downloader.download(new URL("http://localhost:48080/storages/storage0/test-releases/com/foo/bar/1.0/bar-1.0.jar?length=89128960"),
                            new File(TARGET_DIR + "/bar-1.0.jar"));
    }

}
