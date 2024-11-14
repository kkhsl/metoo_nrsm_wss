package com.metoo.nrsm.core.service.impl;

import com.metoo.nrsm.entity.User;
import com.metoo.nrsm.core.mapper.UserMapper;
import com.metoo.nrsm.core.service.IRoleService;
import com.metoo.nrsm.core.service.IUserRoleService;
import com.metoo.nrsm.core.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IUserRoleService userRoleService;
    @Autowired
    private IRoleService roleService;

    @Override
    public User selectByName(String username) {
        return this.userMapper.selectByName(username);
    }

    @Override
    public User selectObjById(Long id) {
        return this.userMapper.selectObjById(id);
    }


}
