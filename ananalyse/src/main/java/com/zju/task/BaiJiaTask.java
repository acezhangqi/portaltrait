package com.zju.task;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zju.function.map.BaijiaMap;
import com.zju.function.map.CarrierMap;
import com.zju.function.reduce.BaijiaReduce;
import com.zju.function.reduce.BaseStaticsReduce;
import com.zju.object.BaiJiaInfoDO;
import com.zju.object.StaticsEntity;
import com.zju.utils.DataUtils;
import com.zju.utils.EsUtils;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.GroupReduceOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author zhangqi
 * @create 2019/7/14
 * 败家指数计算
 */
public class BaiJiaTask {
    public static void main(String[] args) throws Exception {
        final ParameterTool param =ParameterTool.fromArgs(args);
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        List<Tuple2<String, List<BaiJiaInfoDO>>> res = env.readTextFile(param.get("input")).map(new BaijiaMap()).groupBy("groupFiled").reduceGroup(new BaijiaReduce()).collect();
        for (Tuple2<String,List<BaiJiaInfoDO>> tuple2:res){
            String userId = tuple2.f0;
            List<BaiJiaInfoDO> baiJiaInfoDOS = tuple2.f1;
            //个人订单排个序
            Collections.sort(baiJiaInfoDOS,(o1, o2) -> {
                String timeo1 = o1.getCreatetime();
                String timeo2 = o2.getCreatetime();
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd hhmmss");
                Date now = new Date();
                Date time1 = now;
                Date time2 = now;
                try {
                    time1 = dateFormat.parse(timeo1);
                    time2 = dateFormat.parse(timeo2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return time1.compareTo(time2);
            });

        }






        for (StaticsEntity staticsEntity:staticsEntityList){
            String source = JSONObject.toJSONString(DataUtils.buildMap(staticsEntity),SerializerFeature.WriteMapNullValue);
            HttpEntity entity = new NStringEntity(source,ContentType.APPLICATION_JSON);
            EsUtils.performRequest("POST",String.format("/%s/%s/%s/_update","portaltrait","carrierStatics",staticsEntity.getType()),Collections.emptyMap(),entity);
        }
        env.execute("carrierbaseTask");
    }


}
