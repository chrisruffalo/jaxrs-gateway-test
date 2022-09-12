package io.github.chrisruffalo.resources.multipart;

import io.github.chrisruffalo.MultipartService;
import io.github.chrisruffalo.model.Upload;
import io.github.chrisruffalo.resources.Common;
import io.smallrye.common.annotation.Blocking;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.nio.file.Files;

@Path("/multipart/gateway")
@Blocking
public class MultipartGateway extends Common implements MultipartService {

    @Inject
    Logger logger;

    @Override
    public String upload(Upload upload) {
        ///return client().upload(upload.stream);
        logger().infof("uploaded: %s", upload.stream.uploadedFile());
        try (final InputStream source = Files.newInputStream(upload.stream.uploadedFile())) {
            return client().upload(source);
        } catch (Exception ex) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
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
