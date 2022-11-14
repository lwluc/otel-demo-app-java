package com.wfsample.styling;

import com.wavefront.sdk.jaxrs.client.WavefrontJaxrsClientFilter;
import com.wfsample.common.BeachShirtsUtils;
import com.wfsample.common.dto.DeliveryStatusDTO;
import com.wfsample.common.dto.PackedShirtsDTO;
import com.wfsample.common.dto.ShirtDTO;
import com.wfsample.common.dto.ShirtStyleDTO;
import com.wfsample.service.DeliveryApi;
import com.wfsample.service.StylingApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

/**
 * Driver for styling service which manages different styles of shirts and takes orders for a shirts
 * of a given style.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class StylingController implements StylingApi {
    private static final Logger logger = LoggerFactory.getLogger(StylingService.class);
    private final DeliveryApi deliveryApi;
    private final List<ShirtStyleDTO> shirtStyleDTOS;

    StylingController() {
        String deliveryUrl = "http://delivery:50052";
        WavefrontJaxrsClientFilter wavefrontJaxrsFilter = null;
        // wavefrontJaxrsFilter = wfJaxrsClientFilter;
        this.deliveryApi = BeachShirtsUtils.createProxyClient(deliveryUrl, DeliveryApi.class, wavefrontJaxrsFilter);
        shirtStyleDTOS = new ArrayList<>();
        ShirtStyleDTO dto = new ShirtStyleDTO();
        dto.setName("style1");
        dto.setImageUrl("style1Image");
        ShirtStyleDTO dto2 = new ShirtStyleDTO();
        dto2.setName("style2");
        dto2.setImageUrl("style2Image");
        shirtStyleDTOS.add(dto);
        shirtStyleDTOS.add(dto2);
    }

    public List<ShirtStyleDTO> getAllStyles() {
        return this.shirtStyleDTOS;
    }

    public Response makeShirts(String id, int quantity) {
        /*
         * TODO: Try to report the value of quantity using WavefrontHistogram.
         *
         * Viewing the quantity requested by various clients as a minute distribution and then applying
         * statistical functions (median, mean, min, max, p95, p99 etc.) on that data is really useful
         * to understand the user trend.
         */
        String orderNum = UUID.randomUUID().toString();
        List<ShirtDTO> packedShirts = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            packedShirts.add(new ShirtDTO(new ShirtStyleDTO(id, id + "Image")));
        }
        PackedShirtsDTO packedShirtsDTO = new PackedShirtsDTO(packedShirts.stream().
                map(shirt -> new ShirtDTO(
                        new ShirtStyleDTO(shirt.getStyle().getName(), shirt.getStyle().getImageUrl()))).
                collect(toList()));
        Response deliveryResponse = deliveryApi.dispatch(orderNum, packedShirtsDTO);
        if (deliveryResponse.getStatus() < 400) {
            return Response.ok().entity(deliveryResponse.readEntity(DeliveryStatusDTO.class)).build();
        } else {
            String msg = "Failed to make shirts!";
            logger.warn(msg);
            return Response.status(deliveryResponse.getStatus()).entity(msg).build();
        }
    }
}
