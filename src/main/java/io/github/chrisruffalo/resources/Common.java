package io.github.chrisruffalo.resources;

import io.github.chrisruffalo.Service;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

public abstract class Common {

    Service client;

    protected abstract Logger logger();

    protected abstract String getClientPath();

    @PostConstruct
    public void init() throws URISyntaxException {
        client = RestClientBuilder
                    .newBuilder()
                    .baseUri(new URI(String.format("http://localhost:8080/%s", getClientPath())))
                    .build(Service.class);
    }

    protected Service client() {
        return this.client;
    }

}
