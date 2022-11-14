package com.wfsample.delivery;

import com.wfsample.common.dto.DeliveryStatusDTO;
import com.wfsample.common.dto.PackedShirtsDTO;
import com.wfsample.service.DeliveryApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Controller for delivery service which is responsible for dispatching shirts returning tracking
 * number for a given order.
 *
 * @author Hao Song (songhao@vmware.com).
 */
@Component
public class DeliveryController implements DeliveryApi {
    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);
    /*
     * TODO: Add a gauge to monitor the size of dispatch queue.
     * Also, consider adding relevant ApplicationTags for this metric.
     */
    private static Queue<PackedShirtsDTO> dispatchQueue;

    public DeliveryController() {
        dispatchQueue = new ConcurrentLinkedDeque<>();
    }

    @Override
    public Response dispatch(String orderNum, PackedShirtsDTO packedShirts) {
        if (orderNum.isEmpty()) {
            /*
             * TODO: Try to emitting an error metrics with relevant ApplicationTags to Wavefront.
             */
            String msg = "Invalid Order Num";
            logger.warn(msg);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }
        if (packedShirts == null || packedShirts.getShirts() == null ||
                packedShirts.getShirts().size() == 0) {
            /*
             * TODO: Try to emitting an error metrics with relevant ApplicationTags to Wavefront.
             */
            String msg = "No shirts to deliver";
            logger.warn(msg);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }
        dispatchQueue.add(packedShirts);
        String trackingNum = UUID.randomUUID().toString();
        System.out.println("Tracking number of Order:" + orderNum + " is " + trackingNum);
        return Response.ok(new DeliveryStatusDTO(orderNum, trackingNum,
                "shirts delivery dispatched")).build();
    }

    @Scheduled(fixedRate = 30000)
    private void processQueue() {
        System.out.println("Processing " + dispatchQueue.size() + " in the Dispatch Queue!");
        while (!dispatchQueue.isEmpty()) {
            deliverPackedShirts(dispatchQueue.poll());
        }
    }

    private void deliverPackedShirts(PackedShirtsDTO packedShirtsDTO) {
        for (int i = 0; i < packedShirtsDTO.getShirts().size(); i++) {
            /*
             * TODO: Try to Increment a delta counter when shirts are delivered.
             * Also, consider adding relevant ApplicationTags for this metric.
             */
        }
        System.out.println(packedShirtsDTO.getShirts().size() + " shirts delivered!");
    }

    @Override
    public Response retrieve(String orderNum) {
        if (orderNum.isEmpty()) {
            /*
             * TODO: Try to emitting an error metrics with relevant ApplicationTags to Wavefront.
             */
            String msg = "Invalid Order Num";
            logger.warn(msg);
            return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
        }
        return Response.ok("Order: " + orderNum + " returned").build();
    }
}
