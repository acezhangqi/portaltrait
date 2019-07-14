package com.zju.function.map;

import com.zju.common.CarrierEnum;
import com.zju.object.BaiJiaInfoDO;
import com.zju.object.StaticsEntity;
import com.zju.utils.DataUtils;
import com.zju.utils.HbaseUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangqi
 * @create 2019/7/14
 */
public class BaijiaMap implements MapFunction<String,BaiJiaInfoDO> {
    @Override
    public BaiJiaInfoDO map(String s) throws Exception {
        if (StringUtils.isBlank(s)){
            return null;
        }
        String[] orderinfos = s.split(",");
        String id= orderinfos[0];
        String productId = orderinfos[1];
        String productTypeId = orderinfos[2];
        String createTime = orderinfos[3];
        String amount = orderinfos[4];
        String payType = orderinfos[5];
        String payTime = orderinfos[6];
        String payStatus = orderinfos[7];
        String couponAmount = orderinfos[8];
        String totalAmount = orderinfos[9];
        String refundAmount = orderinfos[10];
        String num = orderinfos[11];
        String userId = orderinfos[12];

        BaiJiaInfoDO baiJiaInfo = new BaiJiaInfoDO();
        baiJiaInfo.setUserid(userId);
        baiJiaInfo.setCreatetime(createTime);
        baiJiaInfo.setAmount(amount);
        baiJiaInfo.setPaytype(payType);
        baiJiaInfo.setPaytime(payTime);
        baiJiaInfo.setPaystatus(payStatus);
        baiJiaInfo.setCouponamount(couponAmount);
        baiJiaInfo.setTotalamount(totalAmount);
        baiJiaInfo.setRefundamount(refundAmount);
        String groupfield = "baijia=="+userId;
        baiJiaInfo.setGroupfield(groupfield);
        return baiJiaInfo;


    }
}
