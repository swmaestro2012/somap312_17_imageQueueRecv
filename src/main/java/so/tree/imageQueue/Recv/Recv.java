package so.tree.imageQueue.Recv;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class Recv {

    private final static String QUEUE_NAME = "imageUploadQueue";

    public static void main(String[] argv) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println("Waiting for messages. To exit press CTRL+C");
    
    QueueingConsumer consumer = new QueueingConsumer(channel);
    channel.basicConsume(QUEUE_NAME, true, consumer);
    
    while (true) {
    	try{
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String key = new String(delivery.getBody());
			
			System.out.println(key.substring(0,5));
			S3UploadModule s3Upload = new S3UploadModule(Settings.S3_ACCESS_KEY, Settings.S3_SECRET_KEY, Settings.S3_BUCKET_NAME, key.substring(5), true);
			s3Upload.upload();

			if(key.substring(0,5).equals("user|")){
				s3Upload.updateUserLookEntity();
			}else if(key.substring(0,5).equals("look|")){
				s3Upload.updateLookEntity();
			}
			
			
    	}catch(Exception e){
    		e.printStackTrace();
    	}
      
    }
  }
}