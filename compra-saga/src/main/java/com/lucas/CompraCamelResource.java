package com.lucas;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.camel.CamelContext;

@Path("/compra-camel")
public class CompraCamelResource {

    @Inject
    private CamelContext context;


    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response saga() {
        Long id = 0L;

        this.comprar(++id, 25);
        this.comprar(++id, 25);
        this.comprar(++id, 25);
        this.comprar(++id, 25);
        this.comprar(++id, 25);
        this.comprar(++id, 25);
        this.comprar(++id, 25);

        return Response.ok().build();
    }

    private void comprar(Long id, int valor) {
        context.createFluentProducerTemplate()
                .to("direct:saga")
                .withHeader("id", id)
                .withHeader("pedidoId", id)
                .withHeader("valor", valor)
                .request();
    }
}
