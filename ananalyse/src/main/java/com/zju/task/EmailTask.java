package com.zju.task;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zju.function.map.CarrierMap;
import com.zju.function.map.EmailMap;
import com.zju.function.reduce.BaseStaticsReduce;
import com.zju.object.StaticsEntity;
import com.zju.utils.DataUtils;
import com.zju.utils.EsUtils;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;

import java.util.Collections;
import java.util.List;

/**
 * @author zhangqi
 * @create 2019/7/14
 */
public class EmailTask {
    public static void main(String[] args) throws Exception {
        final ParameterTool param =ParameterTool.fromArgs(args);
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        List<StaticsEntity> staticsEntityList = env.readTextFile(param.get("input")).map(new EmailMap()).groupBy("groupFiled").reduce(new BaseStaticsReduce()).collect();
        for (StaticsEntity staticsEntity:staticsEntityList){
            String source = JSONObject.toJSONString(DataUtils.buildMap(staticsEntity),SerializerFeature.WriteMapNullValue);
            HttpEntity entity = new NStringEntity(source,ContentType.APPLICATION_JSON);
            EsUtils.performRequest("POST",String.format("/%s/%s/%s/_update","portaltrait","emailStatics",staticsEntity.getType()),Collections.emptyMap(),entity);
        }
        env.execute("emailbaseTask");
    }


}
