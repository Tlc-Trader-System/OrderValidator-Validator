package com.trading_engine.order_validation.models;

public class ValidatedOrderItem {
   private  OrderItem orderItem;
    private String status;

    public ValidatedOrderItem() {

    }

    public ValidatedOrderItem(OrderItem orderItem) {
        this.orderItem=orderItem;
    }

    public ValidatedOrderItem(OrderItem orderItem, String status) {
        this.orderItem=orderItem;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}


