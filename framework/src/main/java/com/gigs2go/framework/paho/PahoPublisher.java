package com.gigs2go.framework.paho;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gigs2go.framework.CommandPayload;
import com.gigs2go.framework.Payload;
import com.gigs2go.framework.PublishResult;
import com.gigs2go.framework.Publisher;

public class PahoPublisher implements Publisher
{
    private static final Logger LOG = LoggerFactory.getLogger( PahoPublisher.class );
    
    private MqttAsyncClient client = null;
    private String clientId;
    private String broker;

    public PahoPublisher(String broker, String clientId) throws MqttException
    {
        this.broker = broker;
        this.clientId = clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttAsyncClient( broker, clientId, persistence );
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession( true );
        // LastWillTestament (LWT)
        connOpts.setWill( ADMIN_TOPIC, (clientId + " Terminated").getBytes(), 0, false );
        LOG.debug( "Connecting to broker: '{}'", broker );
        IMqttToken token = client.connect( connOpts );
        token.waitForCompletion();
        LOG.debug( "Connected" );
    }

    @Override
    public PublishResult publish( Payload payload ) throws MqttException
    {
        PublishResult result = new PublishResult();
        int qos = 0;

        LOG.trace( "Publishing '{}' to '{}'", payload, payload.getTopic() );
        MqttMessage message = new MqttMessage( payload.getBytes() );
        message.setQos( qos );
        IMqttDeliveryToken token = client.publish( payload.getTopic(), message );
        LOG.trace( "Message published : '{}'", new String( token.getMessage().getPayload() ) );
        return result;
    }

	public String getClientId() {
		return clientId;
	}

	public String getBroker() {
		return broker;
	}
}