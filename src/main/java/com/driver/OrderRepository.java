package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<>();
        this.partnerMap = new HashMap<>();
        this.partnerToOrderMap = new HashMap<>();
        this.orderToPartnerMap = new HashMap<>();
    }

    public void saveOrder(Order order){
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, partner);
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            partnerToOrderMap.putIfAbsent(partnerId, new HashSet<>());
            partnerToOrderMap.get(partnerId).add(orderId);
            orderToPartnerMap.put(orderId, partnerId);

            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partnerToOrderMap.get(partnerId).size());
        }
    }

    public Order findOrderById(String orderId){
        return orderMap.getOrDefault(orderId, null);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        return partnerMap.getOrDefault(partnerId, null);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        return partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()).size();
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        return new ArrayList<>(partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()));
    }

    public List<String> findAllOrders(){
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId){
        if (partnerToOrderMap.containsKey(partnerId)) {
            for(String orderId : partnerToOrderMap.get(partnerId)) {
                orderToPartnerMap.remove(orderId);
            }
            partnerToOrderMap.remove(partnerId);
        }
        partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        orderMap.remove(orderId);
        String partnerId = orderToPartnerMap.get(orderId);
        if(partnerId != null) {
            partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()).remove(orderId);
            DeliveryPartner partner = partnerMap.get(partnerId);
            if (partner != null) {
                partner.setNumberOfOrders(partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()).size());
            }
        }
        orderToPartnerMap.remove(orderId);
    }

    public Integer findCountOfUnassignedOrders(){
        int count = 0;
        for(String orderId : orderMap.keySet()){
            if(!orderToPartnerMap.containsKey(orderId)){
                count++;
            }
        }
        return count;
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        int timeLimit = convertToMinutes(timeString);
        int count = 0;
        if(partnerToOrderMap.containsKey(partnerId)){
            for(String orderId : partnerToOrderMap.get(partnerId)){
                Order order = orderMap.get(orderId);
                if(order.getDeliveryTime() > timeLimit){
                    count++;
                }
            }
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        int latest = 0;
        if(partnerToOrderMap.containsKey(partnerId)){
            for(String orderId : partnerToOrderMap.get(partnerId)){
                Order order = orderMap.get(orderId);
                latest = Math.max(latest, order.getDeliveryTime());
            }
        }
        return convertToString(latest);
    }

    private int convertToMinutes(String timeString){
        String[] parts = timeString.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private String convertToString(int minutes){
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}