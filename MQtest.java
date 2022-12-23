import com.ibm.mq.jms.*;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;



public class MQtest {

    public static void main(String[] args) {
        try {
            MQQueueConnection mqConn;
            MQQueueConnectionFactory mqCF;
            final MQQueueSession mqQSession;
            MQQueue mqIn;
            MQQueue mqOut;

            MQQueueReceiver mqReceiver;
			MQQueueSender mqSender;

            mqCF = new MQQueueConnectionFactory();
            mqCF.setHostName("localhost");

            mqCF.setPort(1418);

            mqCF.setQueueManager("ADMIN");
         

            mqConn = (MQQueueConnection) mqCF.createQueueConnection();
            mqQSession = (MQQueueSession) mqConn.createQueueSession(true, Session.AUTO_ACKNOWLEDGE);

            mqIn = (MQQueue) mqQSession.createQueue("MQ.IN");
            mqOut = (MQQueue) mqQSession.createQueue("MQ.OUT"); // выход

            mqReceiver = (MQQueueReceiver) mqQSession.createReceiver(mqIn);
			mqSender = (MQQueueSender) mqQSession.createSender(mqOut);
			
		
			   
            MessageListener ListenerIn = msg -> {
                System.out.println("Got Message in MQ.IN");
                if (msg instanceof TextMessage) {
                    try {
                        TextMessage tMsg = (TextMessage) msg;
                        System.out.println("MQ.IN: " + tMsg.getText());
                        mqSender.send(msg);
                        mqQSession.commit();
						System.out.println("Message moved to MQ.OUT");
					} catch (JMSException e) {
                        e.printStackTrace();
                    }
                }

            };

            mqReceiver.setMessageListener(ListenerIn);
			mqConn.start();
            System.out.println("Stub Started");

		} catch (JMSException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}