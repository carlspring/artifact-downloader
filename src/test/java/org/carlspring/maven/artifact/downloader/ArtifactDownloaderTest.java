package org.carlspring.maven.artifact.downloader;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

/**
 * @author carlspring
 */
public class ArtifactDownloaderTest
{

    private static final String TARGET_DIR = "target/test-resources";

    private static final String REPOSITORY_DIR = "target/storages/storage0/test-releases";

    private static final String DOWNLOADS_DIR = "target/test-downloads";

    private String host = System.getProperty("strongbox.host") != null ?
                          System.getProperty("strongbox.host") : "localhost";

    private int port = System.getProperty("strongbox.port") != null ?
                       Integer.parseInt(System.getProperty("strongbox.port")) : 48080;

    @Before
    public void setUp() throws Exception
    {
        File dir = new File(TARGET_DIR);
        if (!dir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        File downloadsDir = new File(DOWNLOADS_DIR + "/");
        if (!downloadsDir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            downloadsDir.mkdirs();
        }

        String files[] = { "com/foo/bar/1.0/bar-1.0.jar",
                           "com/foo/bar/1.0/bar-1.0.jar.md5",
                           "com/foo/bar/1.0/bar-1.0.jar.sha1",
                           "com/foo/bar/1.0/bar-1.0.pom",
                           "com/foo/bar/1.0/bar-1.0.pom.md5",
                           "com/foo/bar/1.0/bar-1.0.pom.sha1",
                           "com/foo/bar/1.1/bar-1.1.jar",
                           "com/foo/bar/1.1/bar-1.1.jar.md5",
                           "com/foo/bar/1.1/bar-1.1.jar.sha1",
                           "com/foo/bar/1.1/bar-1.1.pom",
                           "com/foo/bar/1.1/bar-1.1.pom.md5",
                           "com/foo/bar/1.1/bar-1.1.pom.sha1",
                           "com/foo/blah/1.0/blah-1.0.jar",
                           "com/foo/blah/1.0/blah-1.0.jar.md5",
                           "com/foo/blah/1.0/blah-1.0.jar.sha1",
                           "com/foo/blah/1.0/blah-1.0.pom",
                           "com/foo/blah/1.0/blah-1.0.pom.md5",
                           "com/foo/blah/1.0/blah-1.0.pom.sha1"
        };

        Random r = new Random();

        for (String fileName : files)
        {
            File file = new File(REPOSITORY_DIR + "/" + fileName);
            if (!file.exists())
            {
                if (!file.getParentFile().exists())
                {
                    file.getParentFile().mkdirs();
                }

                file.createNewFile();

                int randomNumber = r.ints(1, 1, 20).findFirst().getAsInt();

                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.setLength(1024 * 1024 * randomNumber);
                raf.close();
            }
        }
    }

    @Test
    public void testArtifactDownloading()
            throws IOException, URISyntaxException
    {
        ArtifactDownloader downloader = new ArtifactDownloader();
        downloader.setVerbose(true);
        downloader.download(new URL("http://" + host + ":" + port +
                                    "/storages/storage0/test-releases/com/foo/bar/1.0/bar-1.0.jar?length=89128960"),
                            new File(TARGET_DIR + "/bar-1.0.jar"));
    }

    @Test
    public void testRecursiveArtifactDownloading()
            throws IOException, URISyntaxException
    {
        ArtifactDownloader downloader = new ArtifactDownloader();
        downloader.setVerbose(true);

        downloader.download(new URL("http://" + host + ":" + port +
                                    "/storages/storage0/test-releases/com/foo/"),
                            new File(DOWNLOADS_DIR + "/"));
    }

}
