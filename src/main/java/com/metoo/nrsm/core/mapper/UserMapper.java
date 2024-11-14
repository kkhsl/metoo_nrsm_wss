package com.metoo.nrsm.core.mapper;

import com.metoo.nrsm.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    /**
     * 根据Username 查询一个User 对象
     * @param username
     * @return
     */
    User selectByName(String username);

    User selectObjById(Long id);

}
