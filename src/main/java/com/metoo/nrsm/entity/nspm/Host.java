package com.metoo.nrsm.entity.nspm;

import com.metoo.nrsm.core.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Host extends IdEntity {

    private String name;
    private String uuid;
    private String ip1;
    private String ip2;
    private String description;
    private Long userId;
    private String userName;
}
