package com.trading_engine.order_validation.resources;

import com.trading_engine.order_validation.models.OrderItem;
import com.trading_engine.order_validation.models.Stockdata;
import com.trading_engine.order_validation.models.ValidatedOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;




@RestController
@RequestMapping("/orders")
public class OrderValidatorResource {
    @Autowired
    private  RestTemplate restTemplate;

    @RequestMapping("/{userId}")
    public List<ValidatedOrderItem> getOrders(@PathVariable("userId") String userId){
        List<OrderItem> orderItems = Arrays.asList(
                new OrderItem( "IBM", 56, 22.3, "BUY" ),
                new OrderItem( "TSLA", 5, 2.3, "BUY" ),
                new OrderItem( "MSFT", 6, 23, "SEll" ),
                new OrderItem( "IBM", 50, 50.3, "BUY" )
        );


        return orderItems.stream().map(orderItem ->{
            Stockdata stockdata = restTemplate.getForObject("http://localhost:8081/md/"+orderItem.getProduct()  , Stockdata.class);
            if (orderItem.getSide()=="BUY"&& stockdata.getBuy_Limit()>=orderItem.getQuantity()){
            return new ValidatedOrderItem(orderItem, "Validated");
            }
            else if(orderItem.getSide()=="SELL" && stockdata.getSell_Limit()>=orderItem.getQuantity()){
                return new ValidatedOrderItem(orderItem, "Validated");}
            else
                return new ValidatedOrderItem(orderItem, " Not Valid");
        }).collect(Collectors.toList());

    }
}
