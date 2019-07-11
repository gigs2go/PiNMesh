package com.gigs2go.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameworkUtils {
	private static final Logger LOG = LoggerFactory.getLogger( FrameworkUtils.class );
	
	private static final Class<?> clazz = new Object().getClass();

	public static Properties loadProperties( String file ) throws IOException {
		Properties props = new Properties();
		LOG.trace( "Loading properties from '{}'", file );
		//System.
		InputStream is = ClassLoader.getSystemResourceAsStream( file );
		LOG.trace( "Got '{}'", is.toString() );
		props.load( is );
		LOG.trace( "Loaded : '{}'", props.toString() );
		return props;
	}

}
