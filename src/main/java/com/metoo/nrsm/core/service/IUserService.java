package com.metoo.nrsm.core.service;

import com.metoo.nrsm.entity.User;

public interface IUserService {

    /**
     * 根据Username 查询一个User 对象
     * @param username
     * @return
     */
    User selectByName(String username);

    User selectObjById(Long id);

}
