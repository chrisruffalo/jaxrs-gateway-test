package io.github.chrisruffalo;

import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@Path("/")
@RegisterRestClient(configKey = "reactive-service")
public interface ReactiveService {

    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    Uni<String> reactiveUpload(InputStream inputStream);

}
