package com.gigs2go.framework.testpaho;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gigs2go.pinmesh.framework.CommandHandler;
import com.gigs2go.pinmesh.framework.Payload;
import com.gigs2go.pinmesh.framework.PublishResult;
import com.gigs2go.pinmesh.mqtt.PahoListener;
import com.gigs2go.pinmesh.mqtt.PahoPublisher;

public class TestPaho
{
    private static final Logger LOG = LoggerFactory.getLogger( TestPaho.class );

    public static void main( String[] args )
    {
        String broker = "tcp://127.0.0.1:1883";
        String topic = "test/topic";
        try
        {
            // Dummy controller
            CommandHandler controller = new CommandHandler()
            {
                public PublishResult handle( Payload payload )
                {
                    LOG.debug( "Dummy Handler handled '{}'", payload );
                    return new PublishResult();
                }
            };

            PahoListener listener = new PahoListener( broker, "PahoListener", topic, controller );
            PahoPublisher publisher = new PahoPublisher( broker, "PahoPublisher", "/mesh/admin" );
            Payload payload;
            for ( int i = 0; i < 5; i++ )
            {
                payload = new Payload( topic, ("steer value=" + Integer.toString( i * 200 )).getBytes() );
                publisher.publish( payload );
            }
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
