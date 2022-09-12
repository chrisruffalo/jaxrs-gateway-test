package io.github.chrisruffalo.resources.request;

import io.github.chrisruffalo.RequestService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.core.http.HttpClientResponse;
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;

@RequestScoped
@Path("/request/intermediate")
public class RequestIntermediate implements RequestService {

    @Inject
    Logger logger;

    @Override
    public Uni<String> upload(HttpServerRequest request) {
        logger.infof("got request");
        final io.vertx.mutiny.core.http.HttpServerRequest mutinyRequest = new io.vertx.mutiny.core.http.HttpServerRequest(request);
        final HttpClient client = Vertx.vertx().createHttpClient();
        return client.request(HttpMethod.POST, 8080, "localhost", "/backend/upload")
                .onItem().transformToUni(httpClientRequest -> httpClientRequest.send(mutinyRequest))
                .onItem().transformToUni(HttpClientResponse::body).onItem()
                .transform(io.vertx.mutiny.core.buffer.Buffer::toString);
    }
}
