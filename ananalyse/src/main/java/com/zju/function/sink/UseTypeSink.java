package com.zju.function.sink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import com.zju.object.BrandLikeDo;
import com.zju.object.UseTypeInfo;
import com.zju.utils.EsUtils;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by li on 2019/1/6.
 */
public class UseTypeSink implements SinkFunction<UseTypeInfo> {
    @Override
    public void invoke(UseTypeInfo value, Context context) throws Exception {
        String brand = value.getUsetype();
        long count = value.getCount();
        String source = JSONObject.toJSONString(buildMap(value),SerializerFeature.WriteMapNullValue);
        HttpEntity entity = new NStringEntity(source,ContentType.APPLICATION_JSON);
        EsUtils.performRequest("POST",String.format("/%s/%s/%s/_update","portaltrait","userTypestatics",value.getUsetype().hashCode()),Collections.emptyMap(),entity);
    }

    private Map<String,Object> buildMap(UseTypeInfo useTypeInfo){
        Map<String,Object> map = new HashMap<>();
        map.put("script","ctx_.source.count+="+useTypeInfo.getCount());
        //构建upsert语句
        JSONObject upsert = new JSONObject();
        upsert.put("userYtpye",useTypeInfo.getUsetype());
        upsert.put("count",useTypeInfo.getCount());
        map.put("upsert", JSON.toJSONString(upsert));
        return map;
    }
}
