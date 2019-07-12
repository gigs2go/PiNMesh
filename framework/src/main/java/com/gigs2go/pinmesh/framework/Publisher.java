package com.gigs2go.pinmesh.framework;

public interface Publisher
{
	static final String ADMIN_TOPIC = "admin";
    PublishResult publish( Payload payload ) throws Exception;
}
