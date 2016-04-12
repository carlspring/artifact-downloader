package org.carlspring.maven.artifact.downloader;

import org.junit.Before;
import org.junit.Ignore;
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

    public static final String TARGET_DIR = "target/test-resources";
    public static final String TARGET_DIR2 = "target/repository";
    public static final String DOWNLOAD_DIR = "target/test-download";


    @Before
    public void setUp() throws Exception
    {
        File dir = new File(TARGET_DIR);
        if (!dir.exists())
        {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        String files[] = {
                "com/foo/bar/1.0/bar-1.0.jar",
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

        for (String s : files) {
            File file = new File(TARGET_DIR2 + "/" + s);
            if(!file.exists()) {
                file.getParentFile().mkdirs();
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
        // downloader.download(new URL("http://download.opensuse.org/distribution/leap/42.1/iso/openSUSE-Leap-42.1-NET-x86_64.iso"),
        //                     new File(TARGET_DIR + "/openSUSE-Leap-42.1-NET-x86_64.iso"));
        downloader.download(new URL("http://localhost:48080/storages/storage0/test-releases/com/foo/bar/1.0/bar-1.0.jar?length=89128960"),
                            new File(TARGET_DIR + "/bar-1.0.jar"));
    }

    @Test
    public void testRecursiveArtifactDownloading()
            throws IOException, URISyntaxException
    {
        ArtifactDownloader downloader = new ArtifactDownloader();
        downloader.setVerbose(true);

        downloader.download(new URL("http://localhost:48080/storages/storage0/test-releases/com/foo/"),
                new File(DOWNLOAD_DIR + "/"));
    }
}
