package io.github.chrisruffalo.model;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import javax.ws.rs.core.MediaType;

public class Upload {

    @RestForm("stream")
    @PartType(MediaType.APPLICATION_XML)
    public FileUpload stream;

}
