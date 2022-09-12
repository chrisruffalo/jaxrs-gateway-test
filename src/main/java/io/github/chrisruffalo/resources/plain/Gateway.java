package io.github.chrisruffalo.resources.plain;

import io.github.chrisruffalo.Service;
import io.github.chrisruffalo.resources.Common;
import io.smallrye.common.annotation.Blocking;
import io.vertx.core.http.HttpServerRequest;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.io.InputStream;

@RequestScoped
@Path("/gateway")
@Blocking
public class Gateway extends Common implements Service {

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
        return "intermediate";
    }
}
