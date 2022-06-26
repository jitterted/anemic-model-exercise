package it.gabrieletondi.telldontask.usecase;

import it.gabrieletondi.telldontask.domain.Order;
import it.gabrieletondi.telldontask.domain.OrderStatus;
import it.gabrieletondi.telldontask.doubles.TestOrderRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrderApprovalUseCaseTest {
    private final TestOrderRepository orderRepository = new TestOrderRepository();
    private final OrderApprovalUseCase useCase = new OrderApprovalUseCase(orderRepository);

    @Test
    void approvedExistingOrder() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.CREATED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(true);

        useCase.run(request);

        final Order savedOrder = orderRepository.getSavedOrder();
        assertThat(savedOrder.getStatus())
            .isEqualByComparingTo(OrderStatus.APPROVED);
    }

    @Test
    void rejectedExistingOrder() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.CREATED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(false);

        useCase.run(request);

        final Order savedOrder = orderRepository.getSavedOrder();
        assertThat(savedOrder.getStatus())
            .isEqualByComparingTo(OrderStatus.REJECTED);
    }

    @Test
    void cannotApproveRejectedOrder() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.REJECTED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(true);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isExactlyInstanceOf(RejectedOrderCannotBeApprovedException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
    }

    @Test
    void cannotRejectApprovedOrder() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.APPROVED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(false);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isExactlyInstanceOf(ApprovedOrderCannotBeRejectedException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
    }

    @Test
    void shippedOrdersCannotBeApproved() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.SHIPPED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(true);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isExactlyInstanceOf(ShippedOrdersCannotBeChangedException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
    }

    @Test
    void shippedOrdersCannotBeRejected() throws Exception {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.SHIPPED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(false);

        assertThatThrownBy(() -> {
            useCase.run(request);
        }).isExactlyInstanceOf(ShippedOrdersCannotBeChangedException.class);

        assertThat(orderRepository.getSavedOrder())
            .isNull();
    }
}
