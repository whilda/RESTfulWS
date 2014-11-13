package connector;

import java.net.UnknownHostException;
import com.mongodb.MongoClient;

public class MONGODB {
	public static MongoClient GetMongoClient(){
		try {
			return new MongoClient("localhost",27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
}
