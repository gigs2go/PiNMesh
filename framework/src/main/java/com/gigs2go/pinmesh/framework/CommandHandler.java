package com.gigs2go.pinmesh.framework;

public interface CommandHandler {
    PublishResult handle(Payload payload);
}
