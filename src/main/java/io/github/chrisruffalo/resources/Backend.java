package io.github.chrisruffalo.resources;

import io.github.chrisruffalo.Service;
import io.smallrye.common.annotation.Blocking;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RequestScoped
@javax.ws.rs.Path("/backend")
@Blocking
public class Backend implements Service {

    @Inject
    Logger logger;

    @Override
    public String upload(InputStream file) {
        try {
            final Path temp = Files.createTempFile("test-upload-", ".tst");
            logger.infof("created %s", temp);
            try(final OutputStream outputStream = Files.newOutputStream(temp)) {
                long bytes = file.transferTo(outputStream);
                logger.infof("transferred %d bytes to %s", bytes, temp);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return "ok\n\n";
    }
}
