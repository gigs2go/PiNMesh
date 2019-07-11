package com.gigs2go.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tim
 *
 */
public class Payload
{
    private static final Logger LOG = LoggerFactory.getLogger( Payload.class );
    private String topic;
    private Character type = 'C';
    private byte[] bytes;

    public Payload(String topic)
    {
        this.topic = topic;
    }

    public Payload(String topic, byte[] bytes)
    {
        this.topic = topic;
        this.bytes = bytes;
    }

    public void setBytes( byte[] bytes )
    {
        this.bytes = bytes;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public String getTopic()
    {
        return topic;
    }

    public Character getType()
    {
        return type;
    }

}
