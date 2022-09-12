package io.github.chrisruffalo.resources;

import io.github.chrisruffalo.Service;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.io.InputStream;

@RequestScoped
@Path("/intermediate")
@Blocking
public class Intermediate extends Common implements Service {

    @Inject
    Logger logger;

    @Inject
    HttpServerRequest request;

    @Override
    protected Logger logger() {
        return logger;
    }

    @Override
    public String upload(InputStream file) {
        return client().upload(file);
    }

    @Override
    protected String getClientPath() {
        return "backend";
    }
}
