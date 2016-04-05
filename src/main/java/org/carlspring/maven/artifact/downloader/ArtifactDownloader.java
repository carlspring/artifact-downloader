package org.carlspring.maven.artifact.downloader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author carlspring
 */
public class ArtifactDownloader
{

    private boolean verbose;

    private ProgressOutputMode outputMode = ProgressOutputMode.PERCENT;


    public void download(URL url, File file)
            throws IOException,
                   URISyntaxException
    {
        FileOutputStream fos = new FileOutputStream(file);
        download(url, fos);
    }

    public DownloadResult download(URL url, OutputStream os)
            throws IOException,
                   URISyntaxException
    {
        DownloadResult result = new DownloadResult();

        CloseableHttpClient client = null;

        try
        {
            client = HttpClients.createDefault();

            HttpGet httpget = new HttpGet(url.toURI());
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                InputStream is = entity.getContent();

                long contentLength = entity.getContentLength();

                int numberOfBytesRead;
                int totalBytesRead = 0;
                byte[] bytes = new byte[4096];

                long startTime = System.currentTimeMillis();

                while ((numberOfBytesRead = is.read(bytes, 0, bytes.length)) != -1)
                {
                    // Write the artifact
                    os.write(bytes, 0, numberOfBytesRead);

                    totalBytesRead += numberOfBytesRead;

                    long duration = System.currentTimeMillis() - startTime;

                    result.setContentLength(contentLength);
                    result.setDownloadLength(totalBytesRead);
                    result.setDuration(duration);
                    result.setResponseCode(response.getStatusLine().getStatusCode());

                    if (verbose && contentLength > 0)
                    {
                        if (totalBytesRead != contentLength)
                        {
                            if (outputMode.equals(ProgressOutputMode.BYTES))
                            {
                                System.out.print("\rDownloaded " + totalBytesRead + "/" + contentLength + " bytes...");
                            }
                            else
                            {
                                BigDecimal percent = totalBytesRead > 0 ?
                                                     new BigDecimal((100d * totalBytesRead) / contentLength) :
                                                     new BigDecimal(0);

                                System.out.print("\rDownloaded " + percent.intValue() + "/100 %...");
                            }
                        }
                        else
                        {
                            System.out.println("\rDownloaded " + totalBytesRead + "/" + contentLength + " bytes in " +
                                               duration + " ms " +
                                               "from " + url.toString() + " " +
                                               "at an average speed of " +
                                               (duration > 0 ? (contentLength / (duration / 1000)) : contentLength) +
                                               " bytes/s.");
                        }
                    }

                    os.flush();
                }
            }
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (client != null)
            {
                try
                {
                    client.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public boolean isVerbose()
    {
        return verbose;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }

}
