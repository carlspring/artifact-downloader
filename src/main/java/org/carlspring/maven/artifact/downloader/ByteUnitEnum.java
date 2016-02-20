package org.carlspring.maven.artifact.downloader;

/**
 * An IEC-based standard representation.
 * 
 * @author carlspring
 */
public enum ByteUnitEnum
{

    BYTES("B"),
    KILOBYTES("kB"), // 1000 bytes
    MEGABYTES("MB"), // 1000^2 kB
    GIGABYTES("GB"), // 1000^2 MB
    TERABYTES("TB"), // 1000^2 GB
    PETABYTE("PB"),  // 1000^2 TB
    EXABYTE("EB"),   // 1000^2 PB
    ZETTABYTE("ZB"), // 1000^2 EB
    YOTABYTE("YB");  // 1000^2 ZB

    String value;

    
    ByteUnitEnum(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
    
}
