package io.github.chrisruffalo;

import io.github.chrisruffalo.model.Upload;
import org.jboss.resteasy.reactive.MultipartForm;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface MultipartService {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    String upload(@MultipartForm Upload upload);

}
