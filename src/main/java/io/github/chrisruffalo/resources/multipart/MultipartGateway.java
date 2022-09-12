package io.github.chrisruffalo.resources.multipart;

import io.github.chrisruffalo.MultipartService;
import io.github.chrisruffalo.model.Upload;
import io.github.chrisruffalo.resources.Common;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.unchecked.Unchecked;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.nio.file.Files;

@Path("/multipart/gateway")
public class MultipartGateway extends Common implements MultipartService {

    @Inject
    Logger logger;

    @Override
    public Uni<String> upload(Upload upload) {
        ///return client().upload(upload.stream);
        logger.infof("starting upload");
        return Uni.createFrom().item(upload.stream.uploadedFile())
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transform(Unchecked.function(path -> {
                    logger.infof("uploading local file: %s", path);
                    return Files.newInputStream(path);
                }))
                .onItem().transform(stream -> {
                    return client().upload(stream);
                })
                .onFailure().transform(e -> new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected Logger logger() {
        return logger;
    }

    @Override
    protected String getClientPath() {
        return "backend";
    }
}
