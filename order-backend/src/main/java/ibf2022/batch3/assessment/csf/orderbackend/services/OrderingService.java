package ibf2022.batch3.assessment.csf.orderbackend.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.OrdersRepository;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.PendingOrdersRepository;

@Service
public class OrderingService {

	// API URL
	@Value("${pizzaprice.api.url}")
	private String pizzaPriceApiUrl;

	@Autowired
	private OrdersRepository ordersRepo;

	@Autowired
	private PendingOrdersRepository pendingOrdersRepo;

	// TODO: Task 5
	// WARNING: DO NOT CHANGE THE METHOD'S SIGNATURE
	public PizzaOrder placeOrder(PizzaOrder order) throws OrderException {
		String priceUrl = UriComponentsBuilder
				.fromUriString(pizzaPriceApiUrl)
				.queryParam("name", order.getName())
				.queryParam("email", order.getEmail())
				.queryParam("sauce", order.getSauce())
				.queryParam("size", order.getSize())
				.queryParam("thickCrust", order.getThickCrust())
				.queryParam("toppings", order.getTopplings())
				.queryParam("comments", order.getComments())
				.toUriString();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
				priceUrl,
				HttpMethod.POST,
				requestEntity,
				String.class);
		// response in string
		String responseBody = response.getBody();
		// delimit the string into array
		String[] responseArray = responseBody.split(",");
		// responseArray is split into [orderid] [orderdate in mili] [price]
		// id
		order.setOrderId(responseArray[0]);
		// convert to date then set
		long orderDateMillis = Long.parseLong(responseArray[1]);
		Date orderDate = new Date(orderDateMillis);
		order.setDate(orderDate);
		// convert to string then date
		float total = Float.parseFloat(responseArray[2]);
		order.setTotal(total);

		return order;
	}

	// For Task 6
	// WARNING: Do not change the method's signature or its implemenation
	public List<PizzaOrder> getPendingOrdersByEmail(String email) {
		
	
		return ordersRepo.getPendingOrdersByEmail(email);
	}

	// For Task 7
	// WARNING: Do not change the method's signature or its implemenation
	public boolean markOrderDelivered(String orderId) {
		return ordersRepo.markOrderDelivered(orderId) && pendingOrdersRepo.delete(orderId);
	}

}
