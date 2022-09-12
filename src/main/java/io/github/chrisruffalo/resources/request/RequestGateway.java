package io.github.chrisruffalo.resources.request;

import io.github.chrisruffalo.RequestService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.streams.ReadStream;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.core.http.HttpClientResponse;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;

@RequestScoped
@Path("/request/gateway")
public class RequestGateway implements RequestService {

    @Override
    public Uni<String> upload(HttpServerRequest request) {
        final io.vertx.mutiny.core.http.HttpServerRequest mutinyRequest = new io.vertx.mutiny.core.http.HttpServerRequest(request);
        final HttpClient client = Vertx.vertx().createHttpClient();
        return client.request(HttpMethod.POST, 8080, "localhost", "/backend/upload")
                .emitOn(Infrastructure.getDefaultWorkerPool())
                .onItem().transformToUni(httpClientRequest -> httpClientRequest.send(mutinyRequest))
                .onItem().transformToUni(HttpClientResponse::body).onItem()
                .transform(io.vertx.mutiny.core.buffer.Buffer::toString);
    }
}
