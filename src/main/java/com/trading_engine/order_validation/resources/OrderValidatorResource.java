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

import static java.lang.Math.abs;


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
            Stockdata stockdata1 = restTemplate.getForObject("https://exchange.matraining.com/md/"+orderItem.getProduct()  , Stockdata.class);
            Stockdata stockdata2 = restTemplate.getForObject("https://exchange2.matraining.com/md/"+orderItem.getProduct()  , Stockdata.class);
            Stockdata AvData =
                    new Stockdata(
                            (stockdata1.getBid_Price()+stockdata2.getBid_Price())/2,
                            (stockdata1.getAsk_Price()+stockdata2.getAsk_Price())/2,
                            stockdata2.getBuy_Limit(),
                            orderItem.getProduct(),
                            stockdata1.getSell_Limit(),
                            Math.max(stockdata1.getLast_Traded_Price(), stockdata2.getLast_Traded_Price()),
                            Math.max(stockdata1.getMax_Price_Shift(), stockdata2.getMax_Price_Shift())
                    );



            if (orderItem.getSide()=="BUY"){
                    if (AvData.getBuy_Limit()>=orderItem.getQuantity()){
                return new ValidatedOrderItem(orderItem, "Validated");
                }
                if (  AvData.getMax_Price_Shift()<(abs(orderItem.getPrice()-AvData.getSell_Limit()))
                        || ((orderItem.getPrice()/AvData.getBid_Price())*100 < 120)){
                    return new ValidatedOrderItem(orderItem, "Validated");
                }
                return new ValidatedOrderItem(orderItem, "Not Valid");
            }
            else if(orderItem.getSide()=="SELL"){
                if(AvData.getSell_Limit()>=orderItem.getQuantity()){
                return new ValidatedOrderItem(orderItem, "Validated");
                }
                if (
                        AvData.getMax_Price_Shift()<(abs(orderItem.getPrice()-AvData.getAsk_Price()))
                        || (orderItem.getPrice()/AvData.getAsk_Price()*100)> AvData.getAsk_Price()*1.2 )
                {
                    return new ValidatedOrderItem(orderItem, "Validated");
                }
              return new ValidatedOrderItem(orderItem, "Not Valid");
            }
            else
                return new ValidatedOrderItem(orderItem, "Not Valid");
        }).collect(Collectors.toList());

    }
}
