package com.gigs2go.framework;

public interface CommandHandler {
    PublishResult handle(Payload payload);
}
