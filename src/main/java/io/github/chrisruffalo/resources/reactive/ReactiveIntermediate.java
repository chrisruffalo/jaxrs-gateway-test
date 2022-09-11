package io.github.chrisruffalo.resources.reactive;

import io.github.chrisruffalo.ReactiveService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.core.http.HttpClientResponse;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;

@RequestScoped
@Path("/reactive/intermediate")
public class ReactiveIntermediate implements ReactiveService {

    /**
     * Intermediate example, should work the same as the gateway
     * but talk to the backend.
     *
     * @param buffer input
     * @return the response from the next service
     */
    @Override
    public Uni<String> reactiveUpload(final Buffer buffer) {
        HttpClient client = Vertx.vertx().createHttpClient();
        return client.request(HttpMethod.POST, "http://localhost:8080/backend/upload")
                .onItem().transformToUni(httpClientRequest -> httpClientRequest.send(new io.vertx.mutiny.core.buffer.Buffer(buffer)))
                .onItem().transformToUni(HttpClientResponse::body).onItem()
                .transform(io.vertx.mutiny.core.buffer.Buffer::toString);
    }

}
