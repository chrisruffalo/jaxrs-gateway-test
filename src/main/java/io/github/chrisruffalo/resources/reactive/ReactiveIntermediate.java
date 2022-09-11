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

    @Override
    public Uni<String> reactiveUpload(final Buffer inputStream) {
        HttpClient client = Vertx.vertx().createHttpClient();
        return client.request(HttpMethod.POST, "http://localhost:8080/backend/upload")
                .onItem().transformToUni(httpClientRequest -> httpClientRequest.send(new io.vertx.mutiny.core.buffer.Buffer(inputStream)))
                .onItem().transformToUni(HttpClientResponse::body).onItem()
                .transform(io.vertx.mutiny.core.buffer.Buffer::toString);
    }

}
