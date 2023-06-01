package ibf2022.batch3.assessment.csf.orderbackend.controllers;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.service.annotation.DeleteExchange;

import com.fasterxml.jackson.databind.ObjectMapper;


import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.OrdersRepository;
import ibf2022.batch3.assessment.csf.orderbackend.services.OrderingService;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Controller
@RequestMapping(path = "/api")
@CrossOrigin(origins = "*")
public class OrderController {

	@Autowired
	private OrderingService orderSvc;

	// @Autowired
	// private OrdersRepository ordersRepo;

	// TODO: Task 3 - POST /api/order
	@PostMapping(path = "/order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> postOrder(@RequestBody MultiValueMap<String, String> form) {
		PizzaOrder pizzaOrder = new PizzaOrder();
		try {

			String name = form.getFirst("name");
			if (name == null || name.isEmpty()) {
				return ResponseEntity.badRequest().body("Missing 'name' field in the request body");
			}
			pizzaOrder.setName(name);

			String email = form.getFirst("email");
			if (email == null || email.isEmpty()) {
				return ResponseEntity.badRequest().body("Missing 'email' field in the request body");
			}
			pizzaOrder.setEmail(email);

			String sizeStr = form.getFirst("size");
			if (sizeStr == null || sizeStr.isEmpty()) {
				return ResponseEntity.badRequest().body("Missing 'size' field in the request body");
			}
			try {
				Integer size = Integer.parseInt(sizeStr);
				pizzaOrder.setSize(size);
			} catch (NumberFormatException e) {
				return ResponseEntity.badRequest().body("Invalid 'size' value in the request body");
			}

			String sauce = form.getFirst("sauce");
			if (sauce == null || sauce.isEmpty()) {
				return ResponseEntity.badRequest().body("Missing 'sauce' field in the request body");
			}
			pizzaOrder.setSauce(sauce);

			String base = form.getFirst("base");
			if (base == null || base.isEmpty()) {
				return ResponseEntity.badRequest().body("Missing 'base' field in the request body");
			}
			pizzaOrder.setThickCrust(base.equals("thick"));

			List<String> toppings = form.get("toppings");
			if (toppings == null || toppings.isEmpty()) {
				return ResponseEntity.badRequest().body("Missing 'toppings' field in the request body");
			}
			pizzaOrder.setTopplings(toppings);

			String comments = form.getFirst("comments");
			pizzaOrder.setComments(comments);

			orderSvc.placeOrder(pizzaOrder);

		} catch (Exception e) {
			// Handle any JSON parsing or processing errors
			return ResponseEntity.badRequest().body("Invalid request");
		}

		// json
		JsonObject pendingOrderJs = Json.createObjectBuilder()
				.add("orderID", pizzaOrder.getOrderId())
				.add("date", pizzaOrder.getDate().getTime())
				.add("total", pizzaOrder.getTotal())
				.add("name", pizzaOrder.getName())
				.add("email", pizzaOrder.getEmail())
				.build();
		// stringify
		String jsonString = pendingOrderJs.toString();

		return ResponseEntity.ok(jsonString);

		/*
		 * public ResponseEntity<String> postOrder(@RequestBody MultiValueMap<String,
		 * String> form) {
		 * PizzaOrder pizzaOrder = new PizzaOrder();
		 * try {
		 * 
		 * return ResponseEntity.ok("{}");
		 * } catch (Exception e) {
		 * // Handle any JSON parsing or processing errors
		 * return ResponseEntity.badRequest().body("Invalid JSON format");
		 * }
		 */

	}

	// TODO: Task 6 - GET /api/orders/<email>

	@GetMapping(path = "/order/{email}")
	@ResponseBody
	public ResponseEntity<String> getOrderFromEmail(@PathVariable String email) {
		try {
			List<PizzaOrder> listOrder = orderSvc.getPendingOrdersByEmail(email);

			JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			for (PizzaOrder order : listOrder) {
				JsonObject pendingOrderJs = Json.createObjectBuilder()
						.add("orderID", order.getOrderId())
						.add("date", dateFormat.format(order.getDate()))
						.add("total", order.getTotal())
						.build();
	
				jsonArrayBuilder.add(pendingOrderJs);
			}
			// Create a JSON response containing the order data
			JsonArray jsonArray = jsonArrayBuilder.build();
			String jsonResponse = jsonArray.toString();
			// Return the JSON response
			return ResponseEntity.ok(jsonResponse);
		} catch (Exception e) {
			// Handle any errors or exceptions
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while retrieving orders.");
		}
	}


	// TODO: Task 7 - DELETE /api/order/<orderId>


	@DeleteMapping(path = "/order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> deleteOrder(@RequestBody MultiValueMap<String, String> form) {
		try {
			String id = form.getFirst("id");
			if (id == null || id.isEmpty()) {
				return ResponseEntity.badRequest().body("Missing 'id' field in the request body");
			}
			
			if (!orderSvc.markOrderDelivered(id)) {
				return ResponseEntity.notFound().build();
			}
	
		} catch (Exception e) {
			// Handle any JSON parsing or processing errors
			return ResponseEntity.badRequest().body("Invalid request");
		}
	
		return ResponseEntity.ok("{}");

	}
		/*
		 * public ResponseEntity<String> postOrder(@RequestBody MultiValueMap<String,
		 * String> form) {
		 * PizzaOrder pizzaOrder = new PizzaOrder();
		 * try {
		 * 
		 * return ResponseEntity.ok("{}");
		 * } catch (Exception e) {
		 * // Handle any JSON parsing or processing errors
		 * return ResponseEntity.badRequest().body("Invalid JSON format");
		 * }
		 */

	}


