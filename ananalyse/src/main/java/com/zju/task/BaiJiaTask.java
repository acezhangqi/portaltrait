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
import com.zju.utils.HbaseUtils;
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
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

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

           //计算购买频率
           BaiJiaInfoDO first = baiJiaInfoDOS.get(0);
           BaiJiaInfoDO last = baiJiaInfoDOS.get(baiJiaInfoDOS.size()-1);
           int days = DataUtils.getDaysBetweenbyStartAndend(first.getCreatetime(),last.getCreatetime(),"yyyyMMdd hhmmss");
           int avrDays = days/baiJiaInfoDOS.size();


            DoubleStream doubleStream = baiJiaInfoDOS.stream().mapToDouble(t -> Double.valueOf(t.getTotalamount()));

            //计算订单平均值
            double average = doubleStream.average().getAsDouble();
            //计算订单中最大值
            double maxAmount = doubleStream.max().getAsDouble();

            int baijiaScore = computeBaijiaScore(avrDays,average,maxAmount);

            String tablename = "userflaginfo";
            String rowkey = userId;
            String famliyname = "baseinfo";
            String colum = "baijiasoce";
            HbaseUtils.putdata(tablename,rowkey,famliyname,colum,baijiaScore+"");
       }

        env.execute("baijiaTask execute");
    }


    /**
     *  败家指数 = 支付金额平均值*0.3、最大支付金额*0.3、下单频率*0.4
     *  支付金额平均值30分（0-20 5 20-60 10 60-100 20 100-150 30 150-200 40 200-250 60 250-350 70 350-450 80 450-600 90 600以上 100  ）
     *  最大支付金额30分（0-20 5 20-60 10 60-200 30 200-500 60 500-700 80 700 100）
     *  下单平率30分 （0-5 100 5-10 90 10-30 70 30-60 60 60-80 40 80-100 20 100以上的 10）
     *
     * @param avrDays
     * @param average
     * @param maxAmount
     * @return
     */
    private static int computeBaijiaScore(int avrDays, double avramount, double maxamount) {
        int avraoumtsoce = 0;
        if(avramount>=0 && avramount < 20){
            avraoumtsoce = 5;
        }else if (avramount>=20 && avramount < 60){
            avraoumtsoce = 10;
        }else if (avramount>=60 && avramount < 100){
            avraoumtsoce = 20;
        }else if (avramount>=100 && avramount < 150){
            avraoumtsoce = 30;
        }else if (avramount>=150 && avramount < 200){
            avraoumtsoce = 40;
        }else if (avramount>=200 && avramount < 250){
            avraoumtsoce = 60;
        }else if (avramount>=250 && avramount < 350){
            avraoumtsoce = 70;
        }else if (avramount>=350 && avramount < 450){
            avraoumtsoce = 80;
        }else if (avramount>=450 && avramount < 600){
            avraoumtsoce = 90;
        }else if (avramount>=600){
            avraoumtsoce = 100;
        }

        int maxaoumtscore = 0;
        if(maxamount>=0 && maxamount < 20){
            maxaoumtscore = 5;
        }else if (maxamount>=20 && maxamount < 60){
            maxaoumtscore = 10;
        }else if (maxamount>=60 && maxamount < 200){
            maxaoumtscore = 30;
        }else if (maxamount>=200 &&maxamount < 500){
            maxaoumtscore = 60;
        }else if (maxamount>=500 && maxamount < 700){
            maxaoumtscore = 80;
        }else if (maxamount>=700){
            maxaoumtscore = 100;
        }

        // 下单平率30分 （0-5 100 5-10 90 10-30 70 30-60 60 60-80 40 80-100 20 100以上的 10）
        int avrdaysscore = 0;
        if(avrDays>=0 && avrDays < 5){
            avrdaysscore = 100;
        }else if (avrDays>=5 && avrDays < 10){
            avrdaysscore = 90;
        }else if (avrDays>=10 && avrDays < 30){
            avrdaysscore = 70;
        }else if (avrDays>=30 && avrDays < 60){
            avrdaysscore = 60;
        }else if (avrDays>=60 && avrDays < 80){
            avrdaysscore = 40;
        }else if (avrDays>=80 && avrDays < 100){
            avrdaysscore = 20;
        }else if (avrDays>=100){
            avrdaysscore = 10;
        }
        return (avraoumtsoce/100)*30+(maxaoumtscore/100)*30+(avrdaysscore/100)*40;


    }


}
