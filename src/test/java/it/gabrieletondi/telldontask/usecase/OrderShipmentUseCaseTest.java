package it.gabrieletondi.telldontask.usecase;

import it.gabrieletondi.telldontask.domain.Order;
import it.gabrieletondi.telldontask.domain.OrderStatus;
import it.gabrieletondi.telldontask.doubles.TestOrderRepository;
import it.gabrieletondi.telldontask.doubles.TestShipmentService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrderShipmentUseCaseTest {
    private final TestOrderRepository orderRepository = new TestOrderRepository();
    private final TestShipmentService shipmentService = new TestShipmentService();
    private final OrderShipmentUseCase useCase = new OrderShipmentUseCase(orderRepository, shipmentService);

    @Test
    void shipApprovedOrder() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setId(1);
        initialOrder.setStatus(OrderStatus.APPROVED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(1);

        useCase.run(request);

        assertThat(orderRepository.getSavedOrder().getStatus())
            .isEqualByComparingTo(OrderStatus.SHIPPED);
        assertThat(shipmentService.getShippedOrder())
            .isEqualTo(initialOrder);
    }

    @Test
    void createdOrdersCannotBeShipped() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setId(1);
        initialOrder.setStatus(OrderStatus.CREATED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(1);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isInstanceOf(OrderCannotBeShippedException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
        assertThat(shipmentService.getShippedOrder())
            .isNull();
    }

    @Test
    void rejectedOrdersCannotBeShipped() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setId(1);
        initialOrder.setStatus(OrderStatus.REJECTED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(1);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isInstanceOf(OrderCannotBeShippedException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
        assertThat(shipmentService.getShippedOrder())
            .isNull();
    }

    @Test
    void shippedOrdersCannotBeShippedAgain() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setId(1);
        initialOrder.setStatus(OrderStatus.SHIPPED);
        orderRepository.addOrder(initialOrder);

        OrderShipmentRequest request = new OrderShipmentRequest();
        request.setOrderId(1);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isInstanceOf(OrderCannotBeShippedTwiceException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
        assertThat(shipmentService.getShippedOrder())
            .isNull();
    }
}
