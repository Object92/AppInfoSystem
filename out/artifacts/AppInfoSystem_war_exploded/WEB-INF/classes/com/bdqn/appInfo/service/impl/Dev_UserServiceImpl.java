package com.bdqn.appInfo.service.impl;

import com.bdqn.appInfo.dao.Dev_UserMapper;
import com.bdqn.appInfo.exception.BusinessExcpetion;
import com.bdqn.appInfo.pojo.Dev_User;
import com.bdqn.appInfo.service.Dev_UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName: Dev_UserServiceImpl
 * @Description: 开发用户业务接口实现类
 * @Author: xyf
 * @Date 2019/7/18 15:20
 */
@Service
@Transactional
public class Dev_UserServiceImpl implements Dev_UserService {

    @Autowired
    private Dev_UserMapper dev_userMapper;

    /**
     * @param dev_userId
     * @Description: 查找到DevUser
     * @param: [dev_userId]
     * @return: com.bdqn.appInfo.pojo.Dev_User
     * @Date: 2019/07/18 15:26
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Dev_User findDevUserById(Long dev_userId) throws BusinessExcpetion {
        Dev_User dev_user = dev_userMapper.selectByPrimaryKey(dev_userId);
        if (dev_user == null) {
            return null;
        }
        return dev_user;
    }

    /**
     * @param devcode
     * @param devpassword
     * @Description: 开发者登录的方法
     * @param: [devcode, devpassword]
     * @return: com.bdqn.appInfo.pojo.Dev_User
     * @Date: 2019/07/19 14:09
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Dev_User login(String devcode, String devpassword) throws BusinessExcpetion {
        Dev_User user=dev_userMapper.selectByDevCodeAndPwd(devcode,devpassword);
        if (user==null){
            return null;
        }
        return user;
    }

}
