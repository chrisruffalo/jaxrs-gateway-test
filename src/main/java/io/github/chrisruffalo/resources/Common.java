package io.github.chrisruffalo.resources;

import io.github.chrisruffalo.Service;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

public abstract class Common {

    private static final int DEFAULT_BUFFER = 1024 * 1024; // 1M

    Service client;

    protected abstract Logger logger();

    protected abstract String getClientPath();

    @PostConstruct
    public void init() throws URISyntaxException {
        client = RestClientBuilder
                    .newBuilder()
                    .baseUri(new URI(String.format("http://localhost:8080/%s", getClientPath())))
                    .build(Service.class);
    }

    /**
     * Throws "Attempting a blocking read on io thread".
     *
     * @param file
     * @return
     */
    protected String normalForward(final InputStream file) {
        return client().upload(file);
    }

    /**
     * Throws "Attempting a blocking read on io thread".
     *
     * @param file
     * @return
     */
    protected String bufferedForward(final InputStream file) {
        return client().upload(new BufferedInputStream(file));
    }

    /**
     * Works but would be unusable in production due to getting multiple
     * large files at peak times.
     *
     * @param file
     * @return
     */
    protected String inMemoryForward(final InputStream file) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            file.transferTo(stream);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return client().upload(new ByteArrayInputStream(stream.toByteArray()));
    }

    /**
     * Works, sometimes, but it seems that transferTo doesn't work because eventually
     * it ends up waiting for bytes that will never come.
     *
     * @param file
     * @return
     */
    protected String streamForward(final InputStream file) {
        PipedInputStream target = new PipedInputStream();
        // stream on worker thread
        Infrastructure.getDefaultWorkerPool().execute(() -> {
            try (final OutputStream source = new PipedOutputStream(target)) {
                file.transferTo(source);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        return client().upload(target);
    }

    /**
     * This is where i started to get an inkling that you could potentially
     * do some vertx/mutiny operations to get what I wanted. I'm not well-versed
     * enough in reactive to use the vertx tools directly.
     *
     * This method works better but not perfectly. It can transfer about 4k-5k before
     * failing. (the test.sh script passes reliably at this point)
     *
     * @param file
     * @return
     */
    protected String sizeAwareForward(final HttpServerRequest request, final InputStream file) {
        String messageSizeString = request.getHeader("Content-Length");
        long messageSize = -1;
        try {
            messageSize = Long.parseLong(messageSizeString);
        } catch (Exception ex) {
            // no-op
        }
        // do this the safe way if no message size can be determined
        if (messageSize < 0) {
            logger().warn("message does not have content-length header");
            return inMemoryForward(file);
        }
        logger().infof("expecting to transfer %d bytes", messageSize);
        final long messageSizeBytes = messageSize;
        PipedInputStream target = new PipedInputStream();
        // stream on worker thread
        Infrastructure.getDefaultWorkerPool().execute(() -> {
            long remaining = messageSizeBytes;
            try (
                final OutputStream source = new PipedOutputStream(target)
            ) {
                while(remaining > 0) {
                    long transferred = transfer(file, source, messageSizeBytes);
                    remaining -= transferred;
                    logger().infof("transferred %d bytes (%d remain)", transferred, remaining);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        return client().upload(target);
    }

    /**
     * Transfer pump for worker thread, modified from InuptStream#transferTo to be
     * aware of the input size.
     *
     * @param in
     * @param out
     * @param bytes
     * @return
     * @throws IOException
     */
    protected long transfer(InputStream in, OutputStream out, long bytes) throws IOException {
        Objects.requireNonNull(in, "in");
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        int bufferSize = DEFAULT_BUFFER;
        if (bytes < bufferSize) {
            bufferSize = (int)bytes;
        }
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = in.read(buffer, 0, bufferSize)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
            long remaining = bytes - transferred;
            logger().infof("read %d / total transferred %d [buffer = %d] / remaining: %d", read, transferred, bufferSize, remaining);
            if (remaining < bufferSize) {
                bufferSize = (int)remaining;
            }
            if (remaining <= 0) {
                break;
            }
        }
        return transferred;
    }

    protected Service client() {
        return this.client;
    }

}
