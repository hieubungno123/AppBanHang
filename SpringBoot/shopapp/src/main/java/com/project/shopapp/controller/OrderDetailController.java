package com.project.shopapp.controller;

import com.project.shopapp.dtos.OrderDetailDTO;
import com.project.shopapp.models.OrderDetail;
import com.project.shopapp.responses.OrderDetailResponse;
import com.project.shopapp.services.impl.IOrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/order_details")
public class OrderDetailController {

    private final IOrderDetailService iOrderDetailService;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetai
            ){
        try {
            OrderDetail newOrderDetail = iOrderDetailService.createOrderDetail(orderDetai);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetailById(
            @Valid @PathVariable("id") Long id
    ){
        try {
            OrderDetail orderDetail = iOrderDetailService.getOrderDetailById(id);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
//            return ResponseEntity.ok(orderDetail);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // Lấy ra danh sách các order_detail của 1 order nào đó
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetailsByOrderId(
            @Valid @PathVariable("orderId") Long orderId
    ){
        try {
            List<OrderDetail> orderDetails = iOrderDetailService.getAllOrderDetails(orderId);
//            List<OrderDetailResponse> orderDetailResponses = orderDetails.
//                    stream()
//                    .map(orderDetail -> OrderDetailResponse.fromOrderDetail(orderDetail)).toList();
            List<OrderDetailResponse> orderDetailResponses = orderDetails
                    .stream()
                    .map(OrderDetailResponse::fromOrderDetail)
                    .toList();
            return ResponseEntity.ok(orderDetailResponses);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestBody OrderDetailDTO orderDetail
    ){
        try {
            OrderDetail updateOrderDetail = iOrderDetailService.updateOrderDetail(id,orderDetail);
//            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(updateOrderDetail));
            return ResponseEntity.ok(updateOrderDetail);

        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(
            @Valid @PathVariable("id") Long id
    ){
        iOrderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok().body("Delete Successfully");
    }

}
