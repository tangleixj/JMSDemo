package demo.queue.util;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSQueueUtil {
	public static final String MESS_QUEUE_NAME = "TEST_QUEUE";
	public static final String CLOSE_COMMAND = "close";

	private static ConnectionFactory cf;

	public static ConnectionFactory getConnectionFactory() {
		if (cf == null) {
			cf = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
					"tcp://localhost:61616");
		}
		return cf;
	}
}
