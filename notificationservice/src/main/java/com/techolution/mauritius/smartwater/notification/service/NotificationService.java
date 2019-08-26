package com.techolution.mauritius.smartwater.notification.service;

import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.techolution.mauritius.smartwater.notification.domain.MeterConnection;
import com.techolution.mauritius.smartwater.notification.domain.NotificationDetails;
import com.techolution.mauritius.smartwater.notification.domain.NotificationStatusStatistics;
import com.techolution.mauritius.smartwater.notification.repository.ConnectionDetailsRepository;
import com.techolution.mauritius.smartwater.notification.repository.NotificationDetailsRepository;

@Component
public class NotificationService {
	
	private static String OPEN="OPEN";
	private static String HIGH="HIGH";
	private static String MEDIUM="MEDIUM";
	private static String LOW="LOW";
	
	
	private Log log = LogFactory.getLog(NotificationService.class);
	
	@Autowired
	private NotificationDetailsRepository notificationDetailsRepository;
	
	@Autowired
	private ConnectionDetailsRepository connectionDetailsRepository;
	
	 @PersistenceContext
	  private EntityManager em;
	
	private static String ISSUE_STATUS_OPEN="OPEN";
	
	@Autowired
	RedisProperties redisProperties;
	
	@Autowired
    private RedisAutoConfiguration redisAutoConfiguration;
	
	@Bean
	 JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory connectionFactory=new JedisConnectionFactory();
		connectionFactory.setHostName(redisProperties.getHost());
		connectionFactory.setPort(redisProperties.getPort());
		connectionFactory.setPassword(redisProperties.getPassword());
	  return connectionFactory;
	 }
	
	public List<NotificationDetails> getAllOpenNotifcations(){
		
		log.info("Entering NotificationService.getAllOpenNotifcations");
		
		List<NotificationDetails> results=notificationDetailsRepository.findByIssuestatus(ISSUE_STATUS_OPEN);
		//List<NotificationDetails> results=(List<NotificationDetails>)notificationDetailsRepository.findAll();
		
		if(results!=null){
			log.debug("Result size is:"+results.size());	
		}
		
		
		log.info("Exiting NotificationService.getAllOpenNotifcations");
		
		return results;
	}
	
  public NotificationStatusStatistics getCountBasedOnStatus() throws UnknownHostException{
		
		log.info("Entering NotificationService.getAllOpenNotifcations");
		
		
		NotificationStatusStatistics result=new NotificationStatusStatistics();
		List<NotificationDetails> notificationDetails=(List<NotificationDetails>)notificationDetailsRepository.findAll();
		
		List<NotificationDetails> listOpenIssues=notificationDetails.parallelStream().filter( notification -> notification.getIssuestatus().equalsIgnoreCase(OPEN)).collect(Collectors.toList());
		
		
		List<NotificationDetails> highpriroitylist=listOpenIssues.parallelStream().filter( notification -> notification.getPriority().equalsIgnoreCase(HIGH)).collect(Collectors.toList());
		List<NotificationDetails> medium=listOpenIssues.parallelStream().filter( notification -> notification.getPriority().equalsIgnoreCase(MEDIUM)).collect(Collectors.toList());
		List<NotificationDetails> low=listOpenIssues.parallelStream().filter( notification -> notification.getPriority().equalsIgnoreCase(LOW)).collect(Collectors.toList());
		
		List<MeterConnection> connections=getFromRedis();
		int numMeter=0;
		if(connections !=null){
			numMeter=connections.size();
		}
		
		result.setNumberOfMeters(numMeter);
		if(highpriroitylist!=null){
			result.setHighPriorityCount(highpriroitylist.size());
		}
		if(medium!=null){
			result.setMediumPriorityCount(medium.size());
		}
		if(low!=null){
			result.setLowPriorityCount(low.size());
		}
		
		
		/*EntityManagerFactory emf =
				Persistence.createEntityManagerFactory("notificationdetails");
				EntityManager em = emf.createEntityManager();
*/

				// JPA Query Language is executed on your entities (Java Classess), not on your database tables;

				/*Query query = em.createQuery("SELECT nt.priority,count(*) FROM NotificationDetails nt WHERE nt.issuestatus='OPEN' GROUP BY nt.priority ");
				List resultList=(ArrayList)query.getResultList();
				//query.
				System.out.println("Name is:"+resultList.getClass().getName());
				
				int size=resultList.size();
				NotificationStatusStatistics notificationStatusStatistics=new NotificationStatusStatistics();
				for(int index=0;index<size;index++){
					
					
					System.out.println("Index is:"+index);
					System.out.println("classname is:"+resultList.get(index).getClass().getName());
					System.out.println(resultList.get(index).toString());
					String val=resultList.get(index).toString();
					System.out.println("Val is:"+val);
					Array array=(Array)resultList.get(index);
					String type=val.split(",")[0];
					String count =val.split(",")[1];
					
					if("HIGH".equalsIgnoreCase(type)){
						notificationStatusStatistics.setHighPriorityCount(Integer.valueOf(count));
					}else if("MEDIUM".equalsIgnoreCase(type)){
						notificationStatusStatistics.setMediumPriorityCount(Integer.valueOf(count));
					}else{
						notificationStatusStatistics.setLowPriorityCount(Integer.valueOf(count));
					}
					List val=(List)resultList.get(index);
					System.out.println("Inner list size is:"+val.size());
					//String obj=(String)resultList.get(index);
					
					//ArrayList vals=(ArrayList)resultList.get(index);
					
				}
				
*/
		
		log.info("Exiting NotificationService.getAllOpenNotifcations");
		
		return result;
	}
  
  private List<MeterConnection> getFromRedis() throws UnknownHostException {
		List<MeterConnection> connections;
		RedisTemplate<Object,Object> template=redisAutoConfiguration.redisTemplate(jedisConnectionFactory());
		
		if(template ==null || template.opsForValue().get("ALL_CONNECTIONS_LIST_NOTIFICATION")==null){
		
			log.info("Data not present in redis. Populating it");
			connections= (List<MeterConnection>)connectionDetailsRepository.findAll();
			log.info("Exiting ConsolidatedDataService.getAllConnections ");
			if(connections == null) {
				log.debug("returnList size is null");
			}else{
				log.debug("List size is:"+connections.size());	
			}
			
			
			 template.opsForValue().set("ALL_CONNECTIONS_LIST_NOTIFICATION", connections);
			
			
			
		}else{
			
			log.info("Data IS present in redis for all connections");
			connections=(List < MeterConnection>)template.opsForValue().get("ALL_CONNECTIONS_LIST_NOTIFICATION");
		}
		return connections;
	}

}


