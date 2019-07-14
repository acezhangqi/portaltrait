package com.zju.object;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by li on 2019/1/5.
 */
@Data
@NoArgsConstructor
public class BaiJiaInfoDO {
    private String baijiatype;//败家指数区段：0-20 、20-50 、50-70、70-80、80-90、90-100
    private String userid;
    private String createtime;
    private String amount ;
    private String paytype ;
    private String paytime;
    private String paystatus;//0、未支付 1、已支付 2、已退款
    private String couponamount;
    private String totalamount;
    private String refundamount;
    private Long count;//数量
    private String groupfield;//分组

    private List<BaiJiaInfoDO> list;





}
