package com.zju.function.map;

import com.alibaba.fastjson.JSONObject;

import com.zju.kafka.KafkaEvent;
import com.zju.log.ScanProductLog;
import com.zju.object.BrandLikeDo;
import com.zju.utils.HbaseUtils;
import com.zju.utils.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by li on 2019/1/6.
 */
public class BrandLikeMap implements FlatMapFunction<KafkaEvent, BrandLikeDo> {


    @Override
    public void flatMap(KafkaEvent kafkaEvent, Collector<BrandLikeDo> collector) throws Exception {
        String data = kafkaEvent.getWord();
        ScanProductLog scanProductLog = JSONObject.parseObject(data,ScanProductLog.class);
        int userid = scanProductLog.getUserid();
        String brand = scanProductLog.getBrand();
        String tablename = "userflaginfo";
        String rowkey = userid+"";
        String famliyname = "userbehavior";
        String colum = "brandlist";//运营
        String mapdata = HbaseUtils.getdata(tablename,rowkey,famliyname,colum);
        Map<String,Long> map = new HashMap<String,Long>();
        if(StringUtils.isNotBlank(mapdata)){
            map = JSONObject.parseObject(mapdata,Map.class);
        }
        //获取之前的品牌偏好
        String maxprebrand = MapUtils.getmaxbyMap(map);

        long prebarnd = map.get(brand)==null?0l:map.get(brand);
        map.put(brand,prebarnd+1);
        String finalstring = JSONObject.toJSONString(map);
        HbaseUtils.putdata(tablename,rowkey,famliyname,colum,finalstring);

        String maxbrand = MapUtils.getmaxbyMap(map);
        if(StringUtils.isNotBlank(maxbrand)&&!maxprebrand.equals(maxbrand)){
            BrandLikeDo brandLike = new BrandLikeDo();
            brandLike.setBrand(maxprebrand);
            brandLike.setCount(-1L);
            brandLike.setGroupbyfield("==brandlik=="+maxprebrand);
            collector.collect(brandLike);
        }

        BrandLikeDo brandLike = new BrandLikeDo();
        brandLike.setBrand(maxbrand);
        brandLike.setCount(1L);
        collector.collect(brandLike);
        brandLike.setGroupbyfield("==brandlik=="+maxbrand);
        colum = "brandlike";
        HbaseUtils.putdata(tablename,rowkey,famliyname,colum,maxbrand);
    }
}
