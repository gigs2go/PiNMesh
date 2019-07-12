package com.gigs2go.pinmesh.http;
/**
 * Listen on a port and accept HTTP requests. 
 * Process request and extract/infer topic/cmd/params. 
 * Forward each request to mqtt topic.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigs2go.pinmesh.framework.Payload;
import com.gigs2go.pinmesh.mqtt.PahoPublisher;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class HttpAdapter extends NanoHTTPD {
	private static final Logger LOG = LoggerFactory.getLogger( HttpAdapter.class );

	private static HttpAdapter adapter = null;

	private static ObjectMapper jacksonMapper = new ObjectMapper();
	private PahoPublisher publisher;
	private String broker;
	private String clientId;
	private int port;
	private String root;

	public static void main( String[] args ) throws NumberFormatException, MqttException, IOException {
		Properties props = new Properties();
		// Need to use Classloader from this jar file
		InputStream is = jacksonMapper.getClass().getResourceAsStream( "/http.properties" );
		LOG.trace( "Got '{}'", is.toString() );
		props.load( is );
		is.close();
		LOG.trace( "Loaded : '{}'", props.toString() );
		adapter = new HttpAdapter( props.getProperty( "broker.address" ), props.getProperty( "client.id" ),
				props.getProperty( "topic.root" ), new Integer( props.getProperty( "http.listen.port" ) ) );
	}

	public HttpAdapter( String broker, String clientId, String root, int port ) throws MqttException, IOException {
		super( port );
		publisher = new PahoPublisher( broker, clientId, root + "/admin" );
		LOG.debug( "Adapter initialised : {}, {}, {}, {}", broker, clientId, root, port );
		this.broker = broker;
		this.clientId = clientId;
		this.root = root;
		this.port = port;
		start( NanoHTTPD.SOCKET_READ_TIMEOUT, false );
		LOG.debug( "Running at http://localhost:{}/", port );
	}

	@Override
	public Response serve( IHTTPSession session ) {
		Response result = null;
		LOG.trace( "Method : '{}'", session.getMethod() );
		LOG.trace( "URI : '{}'", session.getUri() );
		LOG.trace( "Params string : '{}'", session.getQueryParameterString() );
		// Get the topic and command
		String[] uriElements = session.getUri().split( "/" );
		// Validate - we only want to handle 'our' stuff
		try {
			if ( uriElements.length > 1 ) {
				if ( "mesh".equals( uriElements[ 1 ] ) ) {
					Map<String, List<String>> parameters = session.getParameters();
					LOG.trace( "Params Map : '{}'", parameters.toString() );
					handleRequest( uriElements, parameters );
					result = NanoHTTPD.newFixedLengthResponse( Status.OK, NanoHTTPD.MIME_PLAINTEXT, "Message handled" );
				}
			}
		} catch ( MqttException e ) {
			LOG.error( "Error handling request", e );
		}
		if ( result == null ) {
			result = NanoHTTPD.newFixedLengthResponse( Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Handled" );
		}

		return result;
	}

	private void handleRequest( String[] uriElements, Map<String, List<String>> parameters ) throws MqttException {
		StringBuilder topicBuilder = new StringBuilder();
		// Build topic String
		int lastElementIndex = uriElements.length - 1;
		for ( int i = 1; i < lastElementIndex; i++ ) {
			topicBuilder.append( "/" ).append( uriElements[ i ] );
		}
		String topic = topicBuilder.toString();
		String command = uriElements[ lastElementIndex ];
		Map<String, Object> message = new HashMap<>();
		message.put( "command", command );
		message.put( "parameters", parameters );

		try {
			String jsonString = jacksonMapper.writeValueAsString( message );
			LOG.trace( "Converted to '{}'", jsonString );
			Payload payload = new Payload( topic, jsonString.getBytes() );
			publisher.publish( payload );
		} catch ( JsonProcessingException e ) {
			LOG.error( "Mapping input map", e );
		}
	}
}
