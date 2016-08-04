package demo.queue;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import demo.queue.util.JMSQueueUtil;

/**
 * 向MQ发送消息
 * 
 * @author 小磊子
 *
 */
public class SendMess {
	private Log log = LogFactory.getLog(SendMess.class);
	private ConnectionFactory conFactory;
	private Connection conn;
	private Session session;
	private Destination dest;
	private MessageProducer sender;

	public SendMess() {
		try {
			init();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() throws JMSException {
		conFactory = JMSQueueUtil.getConnectionFactory();
		conn = conFactory.createConnection();//创建连接
		conn.start();//启动连接
		session = conn.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);//创建会话
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
			sender = session.createProducer(dest);// 获取消息发送器
			sender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);// 设置不持久化
			sendMess();
			session.commit();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (sender != null) {
				try {
					sender.close();
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

	private void sendMess() throws JMSException {
		Scanner scanner = new Scanner(System.in);
		TextMessage mess = null;
		while (true) {
			String str = scanner.nextLine();
			mess = session.createTextMessage(str);
			sender.send(mess);
			if (log.isDebugEnabled()) {
				log.debug("发送消息[" + str + "]到队列[" + JMSQueueUtil.MESS_QUEUE_NAME + "]");
			}

			if (str.equals(JMSQueueUtil.CLOSE_COMMAND)) {
				break;
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("接受到关闭指令，停止发送");
		}
		scanner.close();
	}

	public static void main(String[] args) {
		SendMess send = new SendMess();
		send.doBusiness();
	}
}
