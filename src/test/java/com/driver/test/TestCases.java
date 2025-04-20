package com.driver.test;

import com.driver.Application;
import com.driver.DeliveryPartner;
import com.driver.Order;
import com.driver.OrderController;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;

import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;

@SpringBootTest(classes = Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestCases {

    @Autowired
    private OrderController orderController;

    @BeforeEach
    public void setup() {
        // Adding initial data before each test
        Order order1 = new Order("O1", "10:00");
        orderController.addOrder(order1);

        Order order2 = new Order("O2", "12:00");
        orderController.addOrder(order2);

        orderController.addPartner("P1");
        orderController.addPartner("P2");

        orderController.addOrderPartnerPair("O1", "P1");
        orderController.addOrderPartnerPair("O2", "P2");
    }

    @Test
    @Order(1)
    public void testAddOrder() {
        Order order = orderController.getOrderById("O1").getBody();
        assertNotNull(order);
        assertEquals("O1", order.getId());
        assertEquals("10:00", order.getDeliveryTime());
    }

    @Test
    @Order(2)
    public void testAddPartner() {
        DeliveryPartner partner = orderController.getPartnerById("P1").getBody();
        assertNotNull(partner);
        assertEquals("P1", partner.getId());
    }

    @Test
    @Order(3)
    public void testOrderCountByPartner() {
        Integer count = orderController.getOrderCountByPartnerId("P1").getBody();
        assertEquals(1, count);
    }

    @Test
    @Order(4)
    public void testOrdersByPartnerId() {
        List<String> orders = orderController.getOrdersByPartnerId("P2").getBody();
        assertNotNull(orders);
        assertTrue(orders.contains("O2"));
    }

    @Test
    @Order(5)
    public void testGetAllOrders() {
        List<String> orders = orderController.getAllOrders().getBody();
        assertNotNull(orders);
        assertTrue(orders.contains("O1"));
        assertTrue(orders.contains("O2"));
    }

    @Test
    @Order(6)
    public void testUnassignedOrderCount() {
        Integer count = orderController.getCountOfUnassignedOrders().getBody();
        assertEquals(0, count);  // Since all orders are assigned
    }

    @Test
    @Order(7)
    public void testGetOrdersLeftAfterGivenTimeByPartnerId() {
        Integer count = orderController.getOrdersLeftAfterGivenTimeByPartnerId("12:00", "P1").getBody();
        assertEquals(0, count);  // As the only order assigned to P1 was before 12:00
    }

    @Test
    @Order(8)
    public void testLastDeliveryTimeByPartnerId() {
        String time = orderController.getLastDeliveryTimeByPartnerId("P2").getBody();
        assertEquals("12:00", time);  // The latest order for P2 is at 12:00
    }

    @Test
    @Order(9)
    public void testDeleteOrder() {
        orderController.deleteOrderById("O1");
        Order order = orderController.getOrderById("O1").getBody();
        assertNull(order);  // Order should be null after deletion
    }

    @Test
    @Order(10)
    public void testDeletePartner() {
        orderController.deletePartnerById("P1");
        DeliveryPartner partner = orderController.getPartnerById("P1").getBody();
        assertNull(partner);  // Partner P1 should be null after deletion
    }

    @Test
    @Order(11)
    public void testUnassignedOrderCountAfterDeletion() {
        Integer count = orderController.getCountOfUnassignedOrders().getBody();
        assertEquals(1, count);  // After deleting P1, O1 should be unassigned
    }
}