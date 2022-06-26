package it.gabrieletondi.telldontask.usecase;

import it.gabrieletondi.telldontask.domain.Order;
import it.gabrieletondi.telldontask.domain.OrderStatus;
import it.gabrieletondi.telldontask.repository.OrderRepository;

public class OrderApprovalUseCase {
    private final OrderRepository orderRepository;

    public OrderApprovalUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void run(OrderApprovalRequest request) {
        final Order order = orderRepository.getById(request.getOrderId());

        if (order.getStatus().equals(OrderStatus.SHIPPED)) {
            throw new ShippedOrdersCannotBeChangedException();
        }

        if (request.isApproved() && order.getStatus().equals(OrderStatus.REJECTED)) {
            throw new RejectedOrderCannotBeApprovedException();
        }

        if (!request.isApproved() && order.getStatus().equals(OrderStatus.APPROVED)) {
            throw new ApprovedOrderCannotBeRejectedException();
        }

        order.setStatus(request.isApproved() ? OrderStatus.APPROVED : OrderStatus.REJECTED);
        orderRepository.save(order);
    }
}
