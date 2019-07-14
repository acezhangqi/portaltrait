package com.zju.function.map;

import com.zju.object.StaticsEntity;
import com.zju.utils.DataUtils;
import com.zju.utils.HbaseUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author zhangqi
 * @create 2019/7/14
 */
public class YearBaseMap implements MapFunction<String,StaticsEntity> {
    @Override
    public StaticsEntity map(String s) throws Exception {
        if (StringUtils.isBlank(s)){
            return null;
        }
        String[] userInfos = s.split(",");
        String userId = userInfos[0];
        String userName = userInfos[1];
        String sex = userInfos[2];
        String mobile = userInfos[3];
        String email = userInfos[4];
        String age = userInfos[5];
        String registerTime = userInfos[6];
        String termination = userInfos[7];
        String yearbaseType = DataUtils.getYearbasebyAge(age);
        String tablename = "userflagInfo";
        String rowkey  = userId;
        String familyname = "baseInfo";
        String colum = "yearbase";
        HbaseUtils.putdata(tablename,rowkey,familyname,colum,yearbaseType);
        HbaseUtils.putdata(tablename,rowkey,familyname,"age",age);
        StaticsEntity yearBase = new StaticsEntity();
        String groupfield = "yearbase=="+yearbaseType;
        yearBase.setType(yearbaseType);
        yearBase.setCount(1L);
        yearBase.setGroupFiled(groupfield);
        return yearBase;


    }
}
