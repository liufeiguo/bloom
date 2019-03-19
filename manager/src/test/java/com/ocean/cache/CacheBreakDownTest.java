package com.ocean.cache;
 
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
 
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
 
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
 
import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.ocean.dao.UserDao;
import com.ocean.dto.UserDto;
 
/**
 * 缓存击穿
 * @author 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/spring/spring-dao.xml",
		"classpath:config/spring/spring-bean.xml",
		"classpath:config/spring/spring-redis.xml"})
public class CacheBreakDownTest {
	private static final Logger logger = LoggerFactory.getLogger(CacheBreakDownTest.class);
	
	private static final int THREAD_NUM = 100;//线程数量
	
	@Resource
	private UserDao UserDao;
	
	@Resource
	private RedisTemplate redisTemplate;
	
	private int count = 0;
	
	//初始化一个计数器
	private CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);
	
	private BloomFilter<String> bf;
	
	List<UserDto> allUsers;
	
	@PostConstruct
	public void init(){
		//将数据从数据库导入到本地
		allUsers = UserDao.getAllUser();
		if(allUsers == null || allUsers.size()==0){
			return;
		}
		//创建布隆过滤器(默认3%误差)
		bf = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), allUsers.size());
		//将数据存入布隆过滤器
		for(UserDto userDto : allUsers){
			bf.put(userDto.getUserName());
		}
	}
	
	@Test
	public void cacheBreakDownTest(){
		for(int i=0;i<THREAD_NUM;i++){
			new Thread(new MyThread()).start();
			//计数器减一
			countDownLatch.countDown();
		}
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	class MyThread implements Runnable{
 
		@Override
		public void run() {
			try {
				//所有子线程等待，当子线程全部创建完成再一起并发执行后面的代码
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//随机产生一个字符串
			//String randomUser = UUID.randomUUID().toString();
			String randomUser = allUsers.get(new Random().nextInt(allUsers.size())).getUserName();
			String key = "Key:"+randomUser;
			
			//如果布隆过滤器中不存在这个用户直接返回，将流量挡掉
			if(!bf.mightContain(randomUser)){
				System.out.println("bloom filter don't has this user");
				return;
			}
			//查询缓存，如果缓存中存在直接返回缓存数据
			ValueOperations<String,String> operation = (ValueOperations<String, String>) redisTemplate.opsForValue();
			synchronized (countDownLatch) {
				Object cacheUser = operation.get(key);
				if(cacheUser!=null){
					System.out.println("return user from redis");
					return;
				}
				//如果缓存不存在查询数据库
				List<UserDto> user = UserDao.getUserByUserName(randomUser);
				if(user == null || user.size() == 0){
					return;
				}
				//将mysql数据库查询到的数据写入到redis中
				System.out.println("write to redis");
				operation.set("Key:"+user.get(0).getUserName(), user.get(0).getUserName());
			}
		}
		
	}
}
 
 
