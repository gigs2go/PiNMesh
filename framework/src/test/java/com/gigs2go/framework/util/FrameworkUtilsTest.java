package com.gigs2go.framework.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FrameworkUtilsTest {
    private static final Logger LOG = LoggerFactory.getLogger( FrameworkUtilsTest.class );


	@Test
	void test() {
		try {
			Properties props = FrameworkUtils.loadProperties( "/testconfig.properties" );
			assertNotNull( props );
			LOG.debug( props.toString() );
		} catch ( IOException e ) {
			fail( "Exception", e );
		}

	}

}
