package com.gigs2go.framework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gigs2go.pinmesh.framework.Payload;

class PayloadTest
{
    private static final Logger LOG = LoggerFactory.getLogger( PayloadTest.class );

    @Test
    void testBasic()
    {
        Payload payload = new Payload( "test/topic", "dummy non-command message".getBytes() );
        assertNotNull( payload );
        LOG.info( "Payload is '{}'", payload.toString() );
        assertEquals( "test/topic", payload.getTopic() );
        assertEquals( 'C', payload.getType() );
        assertEquals( "dummy non-command message", new String( payload.getBytes() ) );
        System.out.println( payload );
    }

}
