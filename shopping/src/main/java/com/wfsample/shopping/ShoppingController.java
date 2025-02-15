package com.wfsample.shopping;

import com.wavefront.sdk.jaxrs.client.WavefrontJaxrsClientFilter;
import com.wfsample.common.BeachShirtsUtils;
import com.wfsample.common.dto.DeliveryStatusDTO;
import com.wfsample.common.dto.OrderDTO;
import com.wfsample.service.StylingApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Driver for Shopping service provides consumer facing APIs supporting activities like browsing
 * different styles of beachshirts, and ordering beachshirts.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
@Path("/shop")
@Produces(MediaType.APPLICATION_JSON)
public class ShoppingController {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingService.class);
    private final StylingApi stylingApi;

    ShoppingController() {
        String stylingUrl = "http://styling:50051";
        WavefrontJaxrsClientFilter wavefrontJaxrsFilter = null;
        // wavefrontJaxrsFilter = wfJaxrsClientFilter;
        this.stylingApi = BeachShirtsUtils.createProxyClient(stylingUrl, StylingApi.class, wavefrontJaxrsFilter);
    }

    @GET
    @Path("/menu")
    public Response getShoppingMenu(@Context HttpHeaders httpHeaders) {
        return Response.ok(stylingApi.getAllStyles()).build();
    }

    @POST
    @Path("/order")
    @Consumes(APPLICATION_JSON)
    public Response orderShirts(OrderDTO orderDTO, @Context HttpHeaders httpHeaders) {
        Response deliveryResponse = stylingApi.makeShirts(
                orderDTO.getStyleName(), orderDTO.getQuantity());
        if (deliveryResponse.getStatus() < 400) {
            DeliveryStatusDTO deliveryStatus = deliveryResponse.readEntity(DeliveryStatusDTO.class);
            return Response.ok().entity(deliveryStatus).build();
        } else {
            String msg = "Failed to order shirts!";
            logger.warn(msg);
            return Response.status(deliveryResponse.getStatus()).entity(msg).build();
        }
    }

    @OPTIONS
    @Path("/order")
    public Response options() {
        return Response.ok().build();
    }
}
