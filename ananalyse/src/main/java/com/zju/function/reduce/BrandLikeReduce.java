package com.zju.function.reduce;

import com.zju.object.BrandLikeDo;
import org.apache.flink.api.common.functions.ReduceFunction;

/**
 * @author zhangqi
 * @create 2019/7/15
 */
public class BrandLikeReduce implements ReduceFunction<BrandLikeDo> {
    @Override
    public BrandLikeDo reduce(BrandLikeDo brandLike, BrandLikeDo t1) throws Exception {
        String brand = brandLike.getBrand();
        long count1 = brandLike.getCount();
        long count2 = t1.getCount();
        BrandLikeDo brandLikefinal = new BrandLikeDo();
        brandLikefinal.setBrand(brand);
        brandLikefinal.setCount(count1+count2);
        return brandLikefinal;
    }
}
