/** 
 * Project Name:manager 
 * File Name:UserDao.java 
 * Package Name:com.ocean.dao 
 * Date:2019年3月19日上午9:46:56 
 * Copyright (c) 2019, chenzhou1025@126.com All Rights Reserved. 
 * 
*/  
  
package com.ocean.dao;

import java.util.List;

import com.ocean.dto.UserDto;

/** 
 * ClassName:UserDao <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2019年3月19日 上午9:46:56 <br/> 
 * @author   liu-guofei 
 * @version   
 * @since    JDK 1.8 
 * @see       
 */
public interface UserDao {

	
  List<UserDto>	getAllUser();
  
  List<UserDto> getUserByUserName(String userName);

}
 