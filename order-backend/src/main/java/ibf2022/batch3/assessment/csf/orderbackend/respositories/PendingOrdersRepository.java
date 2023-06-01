package ibf2022.batch3.assessment.csf.orderbackend.respositories;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class PendingOrdersRepository {

	// Autowired for Task 3 - Redis
	@Autowired
    private RedisTemplate<String, String> template;

	// TODO: Task 3
	// WARNING: Do not change the method's signature.
	public void add(PizzaOrder order) {

		//set date format
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//json
		JsonObject pendingOrderJs = Json.createObjectBuilder()
				.add("orderID", order.getOrderId())
				.add("date", dateFormat.format(order.getDate()))
				.add("total", order.getTotal())
				.add("name", order.getName())
				.add("email", order.getEmail())
				.build();
		//stringify
		String jsonString = pendingOrderJs.toString();
		//push to redis
		template.opsForValue().set(order.getOrderId(), jsonString);
	}

	// TODO: Task 7
	// WARNING: Do not change the method's signature.
	public boolean delete(String orderId) {
		template.delete(orderId);
		return  template.hasKey(orderId);
	}

}
