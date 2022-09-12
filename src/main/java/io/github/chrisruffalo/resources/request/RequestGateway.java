package io.github.chrisruffalo.resources.request;

import io.github.chrisruffalo.RequestService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.core.http.HttpClientResponse;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.net.URI;

@RequestScoped
@Path("/request/gateway")
public class RequestGateway implements RequestService {

    @Inject
    Logger logger;

    protected URI target;

    @PostConstruct
    public void init() throws Exception {
        target = new URI("http://localhost:8080/request/intermediate/upload");
    }

    @Override
    public Uni<String> upload(HttpServerRequest request) {
        logger.infof("got request");
        final io.vertx.mutiny.core.http.HttpServerRequest mutinyRequest = new io.vertx.mutiny.core.http.HttpServerRequest(request);
        final RequestOptions options = new RequestOptions();
        options.setURI(target.getPath());
        options.setPort(target.getPort());
        options.setHost(target.getHost());
        options.setMethod(HttpMethod.POST);
        final HttpClient client = Vertx.vertx().createHttpClient();
        return client.request(options)
                .onItem().transformToUni(httpClientRequest -> httpClientRequest.send(mutinyRequest))
                .onItem().transformToUni(HttpClientResponse::body).onItem()
                .transform(io.vertx.mutiny.core.buffer.Buffer::toString);
    }
}
