package com.metoo.nrsm.core.service.impl;

import com.metoo.nrsm.entity.RoleRes;
import com.metoo.nrsm.core.mapper.RoleResMapper;
import com.metoo.nrsm.core.service.IRoleResService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleResServiceImpl implements IRoleResService {

    @Autowired
    private RoleResMapper roleResMapper;

    @Override
    public boolean batchAddRoleRes(List<RoleRes> roleRes) {
        try {
            this.roleResMapper.insert(roleRes);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteRoleResByRoleId(Long role_id) {
        try {
            this.roleResMapper.delete(role_id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
