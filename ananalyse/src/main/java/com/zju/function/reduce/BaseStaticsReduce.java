package com.zju.function.reduce;

import com.zju.object.StaticsEntity;
import org.apache.flink.api.common.functions.ReduceFunction;

/**
 * @author zhangqi
 * @create 2019/7/14
 */
public class BaseStaticsReduce implements ReduceFunction<StaticsEntity> {


    @Override
    public StaticsEntity reduce(StaticsEntity t1, StaticsEntity t2) throws Exception {
        String type = t1.getType();
        Long count1 = t1.getCount();
        Long count2 = t2.getCount();

        StaticsEntity staticsEntityfinal = new StaticsEntity();
        staticsEntityfinal.setType(type);
        staticsEntityfinal.setCount(count1+count2);
        return staticsEntityfinal;
    }
}
