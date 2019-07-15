package com.zju.task;

import com.zju.function.map.BrandLikeMap;
import com.zju.function.reduce.BrandLikeReduce;
import com.zju.function.sink.BrandLikeSink;
import com.zju.kafka.KafkaEvent;
import com.zju.kafka.KafkaEventSchema;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import javax.annotation.Nullable;
import java.util.Properties;

/**
 * @author zhangqi
 * @create 2019/7/15
 * 计算品牌偏好
 */
public class BrandLikeTask {
    public static void main(String[] args) throws Exception {
        args = new String[]{"--input-topic","scanProductLog","--bootstrap.servers","192.168.80.134:9092","--zookeeper.connect","192.168.80.134:2181","--group.id","youfan"};
        final ParameterTool parameterTool = ParameterTool.fromArgs(args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().disableSysoutLogging();
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));
        env.enableCheckpointing(5000); // create a checkpoint every 5 seconds
        env.getConfig().setGlobalJobParameters(parameterTool); // make parameters available in the web interface
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<KafkaEvent> input =env.addSource(new FlinkKafkaConsumer011<KafkaEvent>(parameterTool.getRequired("input-topic"),new KafkaEventSchema(),parameterTool.getProperties())
                                            .assignTimestampsAndWatermarks(new CustomWatermarkExtractor()));
        input.flatMap(new BrandLikeMap()).keyBy("groupbyfield").timeWindowAll(Time.seconds(2)).reduce(new BrandLikeReduce()).addSink(new BrandLikeSink());
        env.execute("brandLike analy");

    }

    private static class CustomWatermarkExtractor implements AssignerWithPeriodicWatermarks<KafkaEvent> {

        private static final long serialVersionUID = -742759155861320823L;

        private long currentTimestamp = Long.MIN_VALUE;

        @Override
        public long extractTimestamp(KafkaEvent event, long previousElementTimestamp) {
            // the inputs are assumed to be of format (message,timestamp)
            this.currentTimestamp = event.getTimestamp();
            return event.getTimestamp();
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(currentTimestamp == Long.MIN_VALUE ? Long.MIN_VALUE : currentTimestamp - 1);
        }
    }

}
