package org.carlspring.maven.artifact.downloader;

/**
 * @author carlspring
 */
public class DownloadResult
{

    private long contentLength;

    private long downloadLength;

    /**
     * The duration in milliseconds.
     */
    private long duration;

    private int responseCode;


    public DownloadResult()
    {
    }

    public long getContentLength()
    {
        return contentLength;
    }

    public void setContentLength(long contentLength)
    {
        this.contentLength = contentLength;
    }

    public long getDownloadLength()
    {
        return downloadLength;
    }

    public void setDownloadLength(long downloadLength)
    {
        this.downloadLength = downloadLength;
    }

    public long getDuration()
    {
        return duration;
    }

    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    public int getResponseCode()
    {
        return responseCode;
    }

    public void setResponseCode(int responseCode)
    {
        this.responseCode = responseCode;
    }

}
