package com.kkot.moneytransfer.api;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "hello Get";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String helloPost() {
        return "hello Post";
    }
}
