package io.github.chrisruffalo.resources.reactive;

import io.github.chrisruffalo.ReactiveService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.core.http.HttpClientResponse;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;

@RequestScoped
@Path("/reactive/gateway")
public class ReactiveGateway implements ReactiveService {

    private static final int BUFFER_SIZE = 1024 * 8; // use 8k buffer

    @Inject
    Logger logger;

    /**
     * No matter what I do here I seem to get a 415 "Unsupported Media Type" error
     *
     * @param inputStream input
     * @return the response from the next service
     */
    @Override
    public Uni<String> upload(final InputStream inputStream) {
        final HttpClient client = Vertx.vertx().createHttpClient();

        // 99% certain this isn't right
        Multi<Buffer> buffy = Multi.createFrom().emitter(e -> {
            final byte[] bytes = new byte[BUFFER_SIZE];
            try {
                int read = inputStream.read(bytes, 0, BUFFER_SIZE);
                if (read >= 0) {
                    final Buffer buffer = Buffer.buffer(read);
                    e.emit(buffer);
                    logger.infof("read %d bytes", read);
                    buffer.appendBytes(bytes, 0, read);
                } else {
                    e.complete();
                }
            } catch (IOException ex) {
                e.fail(ex);
            }
        });

        return client.request(HttpMethod.POST, 8080, "localhost", "/backend/upload")
                .onItem().transformToUni(httpClientRequest -> httpClientRequest.send(buffy))
                .onItem().transformToUni(HttpClientResponse::body).onItem()
                .transform(io.vertx.mutiny.core.buffer.Buffer::toString);
    }

}
