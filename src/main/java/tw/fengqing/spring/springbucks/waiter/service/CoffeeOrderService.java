package tw.fengqing.spring.springbucks.waiter.service;

import tw.fengqing.spring.springbucks.waiter.model.Coffee;
import tw.fengqing.spring.springbucks.waiter.model.CoffeeOrder;
import tw.fengqing.spring.springbucks.waiter.model.OrderState;
import tw.fengqing.spring.springbucks.waiter.repository.CoffeeOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@Transactional
@Slf4j
public class CoffeeOrderService {
    @Autowired
    private CoffeeOrderRepository orderRepository;

  public CoffeeOrder get(Long id) {
        // 查詢不存在ID或該筆訂單未完成交易時，會拋出 EntityNotFoundException 異常
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found, ID: " + id));
    }

    public CoffeeOrder createOrder(String customer, Coffee...coffee) {
        CoffeeOrder order = CoffeeOrder.builder()
                .customer(customer)
                .items(new ArrayList<>(Arrays.asList(coffee)))
                .state(OrderState.INIT)
                .build();
        CoffeeOrder saved = orderRepository.save(order);
        log.info("New Order: {}", saved);
        return saved;
    }

    public boolean updateState(CoffeeOrder order, OrderState state) {
        if (state.compareTo(order.getState()) <= 0) {
            log.warn("Wrong State order: {}, {}", state, order.getState());
            return false;
        }
        order.setState(state);
        orderRepository.save(order);
        log.info("Updated Order: {}", order);
        return true;
    }
}
