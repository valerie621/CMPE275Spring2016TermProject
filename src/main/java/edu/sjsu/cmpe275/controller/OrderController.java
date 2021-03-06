package edu.sjsu.cmpe275.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sjsu.cmpe275.dao.MenuItemDao;
import edu.sjsu.cmpe275.dao.OrderDao;
import edu.sjsu.cmpe275.dao.OrderItemDao;
import edu.sjsu.cmpe275.dao.OrderItemRatingDao;
import edu.sjsu.cmpe275.dao.UserDao;
import edu.sjsu.cmpe275.domain.MenuItem;
import edu.sjsu.cmpe275.domain.Order;
import edu.sjsu.cmpe275.domain.OrderItem;
import edu.sjsu.cmpe275.domain.OrderItemRating;
import edu.sjsu.cmpe275.domain.User;
import edu.sjsu.cmpe275.service.MailService;
import edu.sjsu.cmpe275.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

//import static com.sun.tools.javac.jvm.ByteCodes.ret;

/**
 * Created by yutao on 5/5/16.
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    /** ONE_DAY_WORK is 15 hours long. unit is millisecond*/
    private static final long ONE_DAY_WORK = (21 - 6) * 60 * 60 * 1000;

    private static final long MAX_TIME = 30L * 24 * 60 * 60 * 1000;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MailService mailService;


    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderItemRatingDao ratingDao;

    @RequestMapping(value = "/getEarliestPickupTime", method = RequestMethod.POST)
    public @ResponseBody
    PickupTimeTO getEarliestPickupTime(@RequestBody String body) throws IOException {
        logger.debug("body: {}", body);
        logger.debug("type:{}",body.getClass().getName());


        List<OrderTO> orderTOList = new ObjectMapper().readValue(body, new TypeReference<List<OrderTO>>(){});

        /*
        test case:
        1. empty order. 1. order before 6am. 2. order after 6am before 9pm. 3. order after 9pm
         */

        int totalTime = calculateTotalTime(orderTOList);

        // order take too long time
        if (totalTime > ONE_DAY_WORK) {
            // TODO:
            return new PickupTimeTO(0L, "You order too much");
        }

        long earliestPickupTime = getEarliestPickupTime(totalTime);
        earliestPickupTime = roundToMinute(earliestPickupTime);

        return new PickupTimeTO(earliestPickupTime, "");
    }

    private long getEarliestPickupTime(int totalTime) {
        List<List<Order>> orderListOfAllChef = getOrderListOfAllChef(orderDao, Calendar.getInstance().getTime().getTime());

        // TODO: need to consider the case that order start after 30 days
        long earliestStartTime = Long.MAX_VALUE;
        long now = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        for (List<Order> list : orderListOfAllChef) {
            long startTime = findEarliestStartTime(now, totalTime, list);
            earliestStartTime = Math.min(earliestStartTime, startTime);
        }
        long earliestPickupTime = earliestStartTime + totalTime;
        logger.debug("getEarliestPickupTime earliestStartTime: {}, totalTime: {}, earliestPickupTime: {}", earliestStartTime,
                totalTime, earliestPickupTime);
        logger.debug("getEarliestPickupTime earliestStartTime: {}, totalTime: {}, earliestPickupTime: {}", timestampToString(earliestStartTime),
                totalTime, timestampToString(earliestPickupTime));
        return earliestPickupTime;
    }

    private List<List<Order>> getOrderListOfAllChef(OrderDao orderDao, long startTime) {
        List<Order> allOrderList = orderDao.findByFinishTimeGreaterThan(new Date(startTime));

        return splitOrderByChef(allOrderList);
    }

    private List<List<Order>> getOrderListOfAllChefByRange(OrderDao orderDao, long start, long end) {
        List<Order> allOrderList = orderDao.findByFinishTimeGreaterThanAndStartPrepareTimeLessThanEqual(
                new Date(start), new Date(end));

        return splitOrderByChef(allOrderList);
    }

    private List<List<Order>> splitOrderByChef(List<Order> allOrderList) {
        List<List<Order>> orderListOfAllChef = new ArrayList<>();
        List<Order> order0 = new ArrayList<>();
        List<Order> order1 = new ArrayList<>();
        List<Order> order2 = new ArrayList<>();
        for (Order order : allOrderList) {
            switch (order.getChiefId()) {
                case 0:
                    order0.add(order);
                    break;
                case 1:
                    order1.add(order);
                    break;
                case 2:
                    order2.add(order);
                    break;
                default:
                    order0.add(order);
                    break;
            }
        }
        orderListOfAllChef.add(order0);
        orderListOfAllChef.add(order1);
        orderListOfAllChef.add(order2);
        return orderListOfAllChef;
    }

    private static long localDateTimeToTimeStamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
    }

    private LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }

    private String timestampToString(long timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()).format(formatter);
    }

    private int calculateTotalTime(List<OrderTO> orderTOList) {
        int totalTime = 0;
        for (OrderTO orderTO : orderTOList) {
            totalTime += menuItemDao.findOne(orderTO.getMenuId()).getPreparationTime() * orderTO.getCount();
            logger.debug("prepareTime:{}",menuItemDao.findOne(orderTO.getMenuId()).getPreparationTime());
        }
        return totalTime * 60 * 1000;
    }

    /**
     *
     * @param startTime milliseconds
     * @param totalTime milliseconds
     * @param orderList
     * @return
     */
    private long findEarliestStartTime(long startTime, long totalTime, List<Order> orderList) {

        for (Order o : orderList) {
            if (startTime + totalTime < o.getStartPrepareTime().getTime()
                    && isInOpenHours(startTime, startTime + totalTime)) {
                return startTime;
            } else {
                startTime = o.getStartPrepareTime().getTime() + o.getTotalTime();
            }
        }

        // if orderList is empty, or reach to the last one
        // check if it is in open hours
        if (isInOpenHours(startTime, startTime + totalTime)) {
            return startTime;
        }

        // delay it to open hours
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), TimeZone.getDefault().toZoneId());
        LocalDateTime open = start.withHour(6).withMinute(0).withSecond(0).withNano(0);

        // if too early, delay it to 6am
        if (start.compareTo(open) < 0) {
            ZonedDateTime zonedDateTime = open.atZone(TimeZone.getDefault().toZoneId());
            return zonedDateTime.toInstant().toEpochMilli();
        }

        //if too late, delay it to 6am in next day
        LocalDateTime nextDayOpen = open.plusDays(1);
        return nextDayOpen.atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
    }

    private boolean isInOpenHours(long startTime, long endTime) {
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), TimeZone.getDefault().toZoneId());
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), TimeZone.getDefault().toZoneId());

        LocalDateTime open = start.withHour(6).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime close = start.withHour(21).withMinute(0).withSecond(0).withNano(0);
        if (start.compareTo(open) >= 0 && end.compareTo(close) <= 0) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public @ResponseBody
    BaseResultTO submit(@RequestBody SubmitOrderTO submitOrderTO,
                        HttpSession httpSession) {
        User user = (User)httpSession.getAttribute("USER");
        user = userDao.findOne(user.getId());
        Date orderTime = Calendar.getInstance().getTime();
        orderTime = new Date(roundToMinute(orderTime.getTime()));

        if (submitOrderTO.getPickupTime() - roundToMinute(orderTime.getTime()) > MAX_TIME) {
            return new BaseResultTO(1, "Can not exceed 30 days");
        }

        List<OrderTO> orderTOList = submitOrderTO.getOrderTOList();
        int totalTime = calculateTotalTime(orderTOList);

        long earliestPickupTime = roundToMinute(getEarliestPickupTime(totalTime));
        earliestPickupTime -= 60000;

        if (submitOrderTO.getPickupTime() < earliestPickupTime) {
            return new BaseResultTO(1, "Your pickup time is too early");
        }

        LocalDateTime idealEarliestStartTime = timestampToLocalDateTime(submitOrderTO.getPickupTime())
                .minusHours(1).minusSeconds(totalTime / 1000);

        LocalDateTime localDateTimeNow = LocalDateTime.now().withNano(0).withSecond(0);

        if (idealEarliestStartTime.isBefore(localDateTimeNow)) {
            idealEarliestStartTime = localDateTimeNow;
        }

        List<List<Order>> orderListOfAllChef = getOrderListOfAllChefByRange(orderDao,
                localDateTimeToTimeStamp(idealEarliestStartTime),
                submitOrderTO.getPickupTime());

        long earliestStartTime = Long.MAX_VALUE;
        int chefId = 0;
        for (int i = 0; i < 3; i++) {
            List<Order> list = orderListOfAllChef.get(i);
            long startTimeForThisChef = findEarliestStartTime(localDateTimeToTimeStamp(idealEarliestStartTime), totalTime, list);
            if (startTimeForThisChef < earliestStartTime) {
                chefId = i;
                earliestStartTime = startTimeForThisChef;
            }
        }
        long earliestFinishTime = earliestStartTime + totalTime;
        if (earliestFinishTime > submitOrderTO.getPickupTime()) {
            return new BaseResultTO(1, "We are full of orders. Please select another pickup time");
        }

        logger.debug("submit chefId: {}, earliestStartTime: {}, totalTime: {}, earliestPickupTime: {}", chefId, earliestStartTime,
                totalTime, earliestFinishTime);
        logger.debug("submit echefId: {}, arliestStartTime: {}, totalTime: {}, earliestPickupTime: {}", chefId,
                timestampToString(earliestStartTime),
                totalTime, timestampToString(earliestFinishTime));

        double totalPrice = 0.0;
        for (OrderTO orderTO : orderTOList) {
            totalPrice += menuItemDao.findOne(orderTO.getMenuId()).getUnitPrice() * orderTO.getCount();
        }
        List<OrderItem> orderItemList = new ArrayList<>();
        Order order = new Order(new Date(submitOrderTO.getPickupTime()), orderTime, totalPrice, totalTime,
                orderItemList, user, chefId,
                new Date(earliestStartTime), new Date(earliestStartTime + totalTime));
        orderDao.save(order);

        for (OrderTO orderTO : orderTOList) {
            MenuItem menuItem = menuItemDao.findOne(orderTO.getMenuId());
            OrderItem orderItem = new OrderItem(new Date(submitOrderTO.getPickupTime()), orderTime, user, order, menuItem,
                    new BigDecimal(menuItem.getUnitPrice()), menuItem.getPreparationTime() * orderTO.getCount(),
                    orderTO.getCount());
            orderItemDao.save(orderItem);
        }

        sendOrderMail(user, orderTOList);

        // if this order is starting, send on progress notification mail as well.
        if (earliestStartTime <= Calendar.getInstance().getTime().getTime()) {
            sendOnProgressMail(order);
        }

        return new BaseResultTO(0, "We've received your order. Have a nice day :)");
    }

    private void sendOrderMail(User user, List<OrderTO> orderTOList) {
        StringBuilder sb = new StringBuilder();
        sb.append("You've ordered ");
        for (OrderTO orderTO : orderTOList) {
            MenuItem menuItem = menuItemDao.findOne(orderTO.getMenuId());
            sb.append(orderTO.getCount()).append(" ").append(menuItem.getName()).append(", ");
        }
        String subject = "YummyTeam9.Food Email Order Confirmation";
        logger.debug("send mail async start {}", System.currentTimeMillis());
        mailService.send(user.getEmail(), subject, sb.toString());
        logger.debug("send mail async end {}", System.currentTimeMillis());
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd yyyy HH:mm:ss");
    private void sendOnProgressMail(Order order) {
        User user = order.getUser();
        String to = user.getEmail();
        Date finish = order.getFinishTime();
        String text = "order ready to pick up soon with predicted ready time: " + dateFormat.format(finish);
        mailService.send(to, "Order On Progress", text);
    }

    private long roundToMinute(long timestamp) {
        return timestamp / 60000 * 60000;
    }

    @RequestMapping(value = "/getOrderHistory", method = RequestMethod.GET)
    public
    @ResponseBody
    List<OrderHistory> orderHistory(HttpSession httpSession) {
        User user = (User)httpSession.getAttribute("USER");
        List<Order> orderList = orderDao.findByUser(user);

        List<OrderHistory> res = new ArrayList<>();
        for (Order order : orderList) {
            List<ItemAndCount> itemAndCountList = new ArrayList<>();
            for (OrderItem orderItem : order.getItemList()) {
            	OrderItemRating orderItemRating = orderItem.getOrderItemRating();
            	ItemAndCount itemAndCount = null;
            	if (orderItemRating == null) {
            		itemAndCount = new ItemAndCount(orderItem.getId(), orderItem.getItem().getName(), orderItem.getCount());
            	} else {
            		itemAndCount = new ItemAndCount(orderItem.getId(), orderItem.getItem().getName(), orderItem.getCount(), orderItemRating.getRating());
            	}
                itemAndCountList.add(itemAndCount);
            }
            int status = 0;
            long now = Instant.now().toEpochMilli();
            long startTime = order.getStartPrepareTime().getTime();
            long finishTime = order.getFinishTime().getTime();
            if (now < startTime) {
                status = 0;
            } else if (now >= startTime && now <= finishTime) {
                status = 1;
            } else {
                status = 2;
            }
            OrderHistory orderHistory = new OrderHistory(order.getId(), itemAndCountList, order.getTotalPrice(),
                    order.getPickUpTime(), status);
            res.add(orderHistory);
        }

        return res;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public
    @ResponseBody
    BaseResultTO cancelOrder(@RequestBody Long orderId, HttpSession httpSession) {
        User user = (User)httpSession.getAttribute("USER");
        logger.debug("Delete OrderId: {}", orderId);
        Order order = orderDao.findOne(orderId);
        if (order == null) {
            return new BaseResultTO(1, "Can find this order.");
        }
        long now = Instant.now().toEpochMilli();
        if (order.getStartPrepareTime().getTime() <= now) {
            return new BaseResultTO(1, "Order is in progress, cann't be canceled.");
        }
        List<OrderItem> orderItemList = order.getItemList();
        orderItemDao.delete(orderItemList);
        orderDao.delete(order);
        return new BaseResultTO(0, "Your order is canceled");
    }
    
    @RequestMapping(value = "/rating", method = RequestMethod.POST)
    @ResponseBody
    public Object rating(@RequestBody RatingTO ratingTO, HttpSession httpSession) {
    	User user = (User)httpSession.getAttribute("USER");
        OrderItem orderItem = this.orderItemDao.findOne(ratingTO.getOrderItem());
        int rating = ratingTO.getRating();
        if (rating < 0 || rating > 5) {
        	return BaseResultTO.generateBaseResultTO(0, "error:rating exceed limitation");
        }
        if (orderItem == null) {
        	return BaseResultTO.generateBaseResultTO(0, "error:cannot found order item id : " + ratingTO.getOrderItem());
        }
        if (!orderItem.getUser().getId().equals(user.getId())) {
        	return BaseResultTO.generateBaseResultTO(0, "error:you cannot rate this order item");
        }
        if (orderItem.getOrderItemRating() != null) {
        	return BaseResultTO.generateBaseResultTO(0, "error:This order item already rated");
        }
        OrderItemRating orderItemRating = new OrderItemRating();
        orderItemRating.setItem(orderItem.getItem());
        orderItemRating.setOrder(orderItem.getOrder());
        orderItemRating.setOrderItem(orderItem);
        orderItemRating.setRating(rating);
        orderItemRating.setUser(user);
        this.ratingDao.save(orderItemRating);
        orderItem.setOrderItemRating(orderItemRating);
        this.orderItemDao.save(orderItem);
        orderItem.getItem().setRating(orderItem.getItem().getRating() + rating);
        orderItem.getItem().setRateCount(orderItem.getItem().getRateCount() + 1);
        this.menuItemDao.save(orderItem.getItem());
        return BaseResultTO.generateBaseResultTO(1, "" + rating);
    }
    
    public static class RatingTO {
    	
    	private long orderItem;
    	
    	private int rating;

		public long getOrderItem() {
			return orderItem;
		}

		public void setOrderItem(long orderItem) {
			this.orderItem = orderItem;
		}

		public int getRating() {
			return rating;
		}

		public void setRating(int rating) {
			this.rating = rating;
		}
    	
    }

    static class SubmitOrderTO {
        private List<OrderTO> orderTOList;
        private long pickupTime;

        public SubmitOrderTO() {
        }

        public SubmitOrderTO(List<OrderTO> orderTOList, long pickupTime) {
            this.orderTOList = orderTOList;
            this.pickupTime = pickupTime;
        }

        public List<OrderTO> getOrderTOList() {
            return orderTOList;
        }

        public void setOrderTOList(List<OrderTO> orderTOList) {
            this.orderTOList = orderTOList;
        }

        public long getPickupTime() {
            return pickupTime;
        }

        public void setPickupTime(long pickupTime) {
            this.pickupTime = pickupTime;
        }
    }

    static class OrderTO {
        long menuId;
        int count;

        public OrderTO() {
        }

        public OrderTO(int menuId, int count) {
            this.menuId = menuId;
            this.count = count;
        }

        public long getMenuId() {
            return menuId;
        }

        public void setMenuId(long menuId) {
            this.menuId = menuId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    static class BaseResultTO {
        int code;
        String message;

        public BaseResultTO() {
        }

        public BaseResultTO(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        
        public static BaseResultTO generateBaseResultTO(int code, String message) {
        	return new BaseResultTO(code, message);
        }
    }

    static class PickupTimeTO {
        public PickupTimeTO() {
        }

        public PickupTimeTO(long earliestPickupTime, String errorMsg) {
            this.earliestPickupTime = earliestPickupTime;
            this.errorMsg = errorMsg;
        }

        private long earliestPickupTime;
        private String errorMsg;

        public long getEarliestPickupTime() {
            return earliestPickupTime;
        }

        public void setEarliestPickupTime(long earliestPickupTime) {
            this.earliestPickupTime = earliestPickupTime;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    static class ItemAndCount {
    	long id;
        String itemName;
        int count;
        int rating = -1;

        public ItemAndCount(long id,String itemName, int count) {
            this.id = id;
        	this.itemName = itemName;
            this.count = count;
        }
        
        public ItemAndCount(long id,String itemName, int count, int rating) {
            this.id = id;
        	this.itemName = itemName;
            this.count = count;
            this.rating = rating;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public int getRating() {
			return rating;
		}

		public void setRating(int rating) {
			this.rating = rating;
		}
        
        
    }

    static class OrderHistory {
        long orderId;
        List<ItemAndCount> itemAndCount;
        double totalPrice;
        Date pickupTime;

        /**
         * 0 not start, 1 processing,  2 done
         */
        int status;


        public OrderHistory(long orderId, List<OrderController.ItemAndCount> itemAndCount,
                            double totalPrice, Date pickupTime, int status) {
            this.orderId = orderId;
            this.itemAndCount = itemAndCount;
            this.totalPrice = totalPrice;
            this.pickupTime = pickupTime;
            this.status = status;
        }

        public long getOrderId() {
            return orderId;
        }

        public void setOrderId(long orderId) {
            this.orderId = orderId;
        }

        public List<OrderController.ItemAndCount> getItemAndCount() {
            return itemAndCount;
        }

        public void setItemAndCount(List<OrderController.ItemAndCount> itemAndCount) {
            this.itemAndCount = itemAndCount;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Date getPickupTime() {
            return pickupTime;
        }

        public void setPickupTime(Date pickupTime) {
            this.pickupTime = pickupTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}
