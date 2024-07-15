package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.OrderResponse;
import com.project.shopapp.services.impl.IOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception{
        //tìm xem userId có tồn tại hay ko
       User user = userRepository
               .findById(orderDTO.getUserId()).
               orElseThrow(() -> new DateTimeException("Cannt fild userId: " + orderDTO.getUserId()));
       //Convert orderDTO -> order
        //Dùng thu viện Model
        //Tạo một luồng bằng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        // Kiểm tra đặt hàng trên ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Data must be at least today");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DateTimeException("Cannt find userId:" + id));
        return order;
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO)
        throws DataNotFoundException{
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new DateTimeException("Cannt find order id: " + id));

        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DateTimeException("Cannt find order id: " + id));

        //Taoj mot luong bang anh xa rieng de kiem soat viec anh xa
        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        modelMapper.map(orderDTO,existingOrder);
        existingOrder.setUser(existingUser);
        existingOrder.setStatus(OrderStatus.PENDING);
        return orderRepository.save(existingOrder);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(null);
        //KO xoa cung - > soft delete
        if (order != null){
            order.setActive(false);
            orderRepository.save(order);
        }

    }

    @Override
    public List<Order> findOrdersById (Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
