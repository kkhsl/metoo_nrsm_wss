package com.metoo.nrsm.entity.nspm;

import com.metoo.nrsm.core.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatherAlarm extends IdEntity {

    private String deviceUuid;
    private String deviceInterface;
    private String ip;
    private String mac;
    private Integer type;

    private String remoteDeviceUuid;
    private String remoteDeviceInterface;

}
