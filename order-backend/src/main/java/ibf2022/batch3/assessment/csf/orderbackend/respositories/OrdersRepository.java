package ibf2022.batch3.assessment.csf.orderbackend.respositories;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;

public class OrdersRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	// TODO: Task 3
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	// Native MongoDB query here for add()
	public void add(PizzaOrder order) {
		
		// creating document for mongo insertion
		Document document = new Document()
				.append("_id", order.getOrderId())
				.append("date", order.getDate())
				.append("total", order.getTotal())
				.append("name", order.getName())
				.append("email", order.getEmail())
				.append("sauce", order.getSauce())
				.append("size", order.getSize())
				.append("comment", order.getComments())
				.append("toppings", order.getTopplings());


		//db.orders.insertOne(document)
		mongoTemplate.insert(document, "orders");

	}

	// TODO: Task 6
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	// Native MongoDB query here for getPendingOrdersByEmail()
	public List<PizzaOrder> getPendingOrdersByEmail(String email) {
	// 	db.orders.find(
	//   {
	//     email: "email",
	//     delivered: false
	//   }
	// ).sort({ date: -1 })

		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(email).and("delivered").is(false));
		query.with(Sort.by(Direction.DESC, "date"));

		List<PizzaOrder> pendingOrders = mongoTemplate.find(query, PizzaOrder.class, "orders");
		return pendingOrders;

	}

	// TODO: Task 7
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	// Native MongoDB query here for markOrderDelivered()
	public boolean markOrderDelivered(String orderId) {
		// db.orders.updateOne(
		// { _id: "orderId" },
		// { $set: { delivered: true } })

		Query query = new Query();
		query.addCriteria(Criteria.where("_id").is(orderId));

		Update update = new Update();
		update.set("delivered", true);

		UpdateResult result = mongoTemplate.updateFirst(query, update, PizzaOrder.class, "orders");

		return result.wasAcknowledged();
	}

}
