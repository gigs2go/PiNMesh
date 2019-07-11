package com.gigs2go.framework.paho;

import java.util.concurrent.LinkedBlockingDeque;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gigs2go.framework.CommandHandler;
import com.gigs2go.framework.CommandPayload;
import com.gigs2go.framework.Listener;
import com.gigs2go.framework.Payload;

public class PahoListener implements MqttCallback, Listener
{
    private static final Logger LOG = LoggerFactory.getLogger( PahoListener.class );
    private MqttClient client = null;
    private MemoryPersistence persistence;
    private LinkedBlockingDeque<Payload> inputQueue = new LinkedBlockingDeque<>( 100 );

    public PahoListener(String broker, String clientId, String topic, CommandHandler commandHandler) throws MqttException
    {
        super();
        this.persistence = new MemoryPersistence();
        client = new MqttClient( broker, clientId, persistence );
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession( true );
        LOG.debug( "Connecting to broker: " + broker );
        client.connect( connOpts );
        client.setCallback( this );
        LOG.debug( "Subscribing to topic '{}'", topic );
        client.subscribe( topic, 0 );
        LOG.debug( "Connected" );
        LOG.trace( "Creating QueueHandler thread" );
        Thread thread = new Thread()
        {
            public void run()
            {
                try
                {
                    while ( true )
                    {
                        LOG.trace( "Waiting for Queue msg on topic '{}'", topic );
                        Payload payload = inputQueue.takeFirst();
                        LOG.trace( "Got Queue msg : '{}'", payload );
                        commandHandler.handle( payload );
                        LOG.trace( "Handled Queue msg : '{}'", payload );
                    }
                }
                catch ( InterruptedException e )
                {
                    LOG.error( "Listener", e );
                }

            }
        };

        thread.start();
    }

    @Override
    public void connectionLost( Throwable cause )
    {
        LOG.warn( "Connection Lost", cause );
    }

    @Override
    public void messageArrived( String topic, MqttMessage message ) throws Exception
    {
        Payload payload = new CommandPayload( topic, message.getPayload() );
        LOG.debug( "Message arrived. Handling : '{}'", payload );
        inputQueue.add( payload );
    }

    @Override
    public void deliveryComplete( IMqttDeliveryToken token )
    {
        LOG.info( "Delivery complete : '{}'", token );
    }
}