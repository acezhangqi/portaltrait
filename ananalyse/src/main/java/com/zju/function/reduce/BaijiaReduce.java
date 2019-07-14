package com.zju.function.reduce;

import com.zju.object.BaiJiaInfoDO;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhangqi
 * @create 2019/7/14
 */
public class BaijiaReduce implements GroupReduceFunction<BaiJiaInfoDO,Tuple2<String,List<BaiJiaInfoDO>>> {

    private List<BaiJiaInfoDO> baiJiaInfoDOS = new CopyOnWriteArrayList<>();
    private String userId;
    @Override
    public void reduce(Iterable<BaiJiaInfoDO> iterable, Collector<Tuple2<String, List<BaiJiaInfoDO>>> collector) throws Exception {
          for (BaiJiaInfoDO baiJiaInfoDO:iterable){
              userId = baiJiaInfoDO.getUserid();
              baiJiaInfoDOS.add(baiJiaInfoDO);
          }
          collector.collect(new Tuple2<>(userId,baiJiaInfoDOS));

    }
}
