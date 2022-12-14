package io.github.chrisruffalo;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface RequestService {

    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.TEXT_PLAIN)
    Uni<String> upload(HttpServerRequest request);

}
