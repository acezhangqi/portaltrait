package com.zju.function.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zju.object.BrandLikeDo;
import com.zju.utils.DataUtils;
import com.zju.utils.EsUtils;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangqi
 * @create 2019/7/15
 */
public class BrandLikeSink implements SinkFunction<BrandLikeDo> {

    @Override
    public void invoke(BrandLikeDo value, Context context) throws Exception {
        String brand = value.getBrand();
        long count = value.getCount();
        String source = JSONObject.toJSONString(buildMap(value),SerializerFeature.WriteMapNullValue);
        HttpEntity entity = new NStringEntity(source,ContentType.APPLICATION_JSON);
        EsUtils.performRequest("POST",String.format("/%s/%s/%s/_update","portaltrait","brandlikestatics",value.getBrand().hashCode()),Collections.emptyMap(),entity);
    }

    private Map<String,Object> buildMap(BrandLikeDo brandLikeDo){
        Map<String,Object> map = new HashMap<>();
        map.put("script","ctx_.source.count+="+brandLikeDo.getCount());
        //构建upsert语句
        JSONObject upsert = new JSONObject();
        upsert.put("brandLike",brandLikeDo.getBrand());
        upsert.put("count",brandLikeDo.getCount());
        map.put("upsert", JSON.toJSONString(upsert));
        return map;
    }
}
