package com.gigs2go.framework;

public interface Publisher
{
	static final String ADMIN_TOPIC = "gigs2go/admin";
    PublishResult publish( Payload payload ) throws Exception;
}
