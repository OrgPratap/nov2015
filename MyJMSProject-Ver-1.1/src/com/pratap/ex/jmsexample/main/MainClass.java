package com.pratap.ex.jmsexample.main;

import java.sql.Connection;
import java.sql.SQLException;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.sql.DataSource;

import oracle.jms.AQjmsFactory;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.pratap.ex.jmsexample.db.MyDbFactory;

public class MainClass {

	public static void main(String[] args) throws SQLException {
		
		MyDbFactory myDbFactory = new MyDbFactory();
		myDbFactory.createOracleDataSource("jdbc:oracle:thin:@localhost:1521:XE", "system", "root");
		DataSource dataSource = myDbFactory.getOracleDataSource();
		Connection connection = dataSource.getConnection();
		//create random unique table name
		String priorityQueueAndTableName = RandomStringUtils.random( 16, true, true );
		System.out.println("-----> priorityQueueAndTableName : [ "+ priorityQueueAndTableName +" ]");
		
		//Create a queue table
		connection.prepareCall("{call dbms_aqadm.create_queue_table ( queue_table => '"+ priorityQueueAndTableName +"', sort_list => 'PRIORITY,ENQ_TIME', queue_payload_type => 'sys.aq$_jms_map_message', compatible => '8.1.0')}").execute();

		//create a queue
		connection.prepareCall("{call dbms_aqadm.create_queue ( queue_name => '"+ priorityQueueAndTableName +"', queue_table => '"+ priorityQueueAndTableName +"')}").execute();

		//Start the queue
		connection.prepareCall("{call dbms_aqadm.start_queue (queue_name => '"+ priorityQueueAndTableName +"')}").execute();
		
		//-----------------------------------------------------------------------------------------
		
		try{
			
			ConnectionFactory connectionFactory = null;
			try {
				connectionFactory = AQjmsFactory.getQueueConnectionFactory(dataSource);
			} catch (JMSException e) {
				e.printStackTrace();
			}
			
			//Below code is for creating the messages
			for(int i=1;i<=10;i++)
			{
				Thread.currentThread().sleep(3000);
				final int priority = RandomUtils.nextInt(7);
				JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
				jmsTemplate.setExplicitQosEnabled(true);
				MessageCreator messageCreator = new MessageCreator() {
				        @Override
				        public Message createMessage(Session session) throws JMSException {
				            MapMessage message = session.createMapMessage();
				            /* set some properties*/
				            message.setJMSPriority(priority);
				            return message;
				        }
				};
				jmsTemplate.setPriority(priority);
				jmsTemplate.setDefaultDestinationName(priorityQueueAndTableName);
				jmsTemplate.setConnectionFactory(connectionFactory);
				jmsTemplate.send(priorityQueueAndTableName, messageCreator);
			}
			
			//Below code is for consuming the messages
			javax.jms.Connection con = connectionFactory.createConnection();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue loggerQueue = session.createQueue( priorityQueueAndTableName );
			MessageConsumer consumer = session.createConsumer(loggerQueue);

			int count = 0;
			while (true) {
				Thread.currentThread().sleep(3000);
			    con.start();
			    Message message = consumer.receive(1000);
			    if (message == null) {
			        break;
			    }
			    count++;
			    System.out.println(count + ".\tPriority [" + message.getJMSPriority() + "]");
			}
			con.close();
			
			
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			Thread.currentThread().sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//-----------------------------------------------------------------------------------------		
		try {
			connection.prepareCall("{call dbms_aqadm.stop_queue(queue_name => '"+ priorityQueueAndTableName +"')}").execute();
			connection.prepareCall("{call DBMS_AQADM.DROP_QUEUE(queue_name => '"+ priorityQueueAndTableName +"')}").execute();
			connection.prepareCall("{call DBMS_AQADM.DROP_QUEUE_TABLE(queue_table => '"+ priorityQueueAndTableName +"')}").execute();
		} finally {
			connection.close();
			System.out.println("-->FINISHED..");
		}
		
		

	}

}
