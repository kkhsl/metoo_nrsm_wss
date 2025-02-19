package com.metoo.nrsm.entity.nspm;

import com.metoo.nrsm.core.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Order extends IdEntity {

    private String orderNo;
    private String orderId;
    private String userName;
    private Long userId;
    private String branchName;
    private String branchLevel;
}
