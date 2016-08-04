package demo.queue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import demo.queue.util.JMSQueueUtil;

public class ReciveMess {
	private Log log = LogFactory.getLog(ReciveMess.class);
	private ConnectionFactory cf;
	private Connection conn;
	private Session session;
	private Destination dest;
	private MessageConsumer reciver;

	public ReciveMess() {
		try {
			init();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() throws JMSException {
		cf = JMSQueueUtil.getConnectionFactory();
		conn = cf.createConnection();
		conn.start();
		session = conn.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
		if (log.isDebugEnabled()) {
			log.debug("创建会话成功");
		}

		dest = session.createQueue(JMSQueueUtil.MESS_QUEUE_NAME);
		if (log.isDebugEnabled()) {
			log.debug("连接到队列[" + JMSQueueUtil.MESS_QUEUE_NAME + "]成功");
		}
	}

	public void doBusiness() {
		try {
			reciver = session.createConsumer(dest);// 获取消息接收器
			reciveMess();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reciver != null) {
				try {
					reciver.close();
				} catch (JMSException e) {
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (JMSException e) {
				}
			}
		}
	}

	private void reciveMess() throws JMSException {
		TextMessage mess;
		while (true) {
			mess = (TextMessage) reciver.receive();
			if (mess != null) {
				String messStr = mess.getText();
				if (log.isDebugEnabled()) {
					log.debug("从队列[" + JMSQueueUtil.MESS_QUEUE_NAME + "]接受到消息[" + messStr + "]");
				}
				if (messStr.equals(JMSQueueUtil.CLOSE_COMMAND)) {
					break;
				}
			} else {
				break;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("接收到关闭指令，停止接受消息");
		}
	}

	public static void main(String[] args) {
		ReciveMess reciveMess = new ReciveMess();
		reciveMess.doBusiness();
	}
}
