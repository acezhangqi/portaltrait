package com.zju.task;

import com.zju.function.map.SexPreMap;
import com.zju.function.map.SexPresaveMap;
import com.zju.function.reduce.SexpreReduce;
import com.zju.object.SexPreInfo;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.utils.ParameterTool;

import java.util.*;

/**
 * @author zhangqi
 * @create 2019/7/16
 */
public class SexPreTask {
    public static void main(String[] args) {
        final ParameterTool params = ParameterTool.fromArgs(args);

        // set up the execution environment
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);

        // get input data
        DataSet<String> text = env.readTextFile(params.get("input"));

        DataSet<SexPreInfo> mapresult = text.map(new SexPreMap());
        DataSet<ArrayList<Double>> reduceresutl = mapresult.groupBy("groupfield").reduceGroup(new SexpreReduce());
        try {
            List<ArrayList<Double>> reusltlist = reduceresutl.collect();
            int groupsize = reusltlist.size();
            Map<Integer, Double> summap = new TreeMap<Integer, Double>(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            for (ArrayList<Double> array : reusltlist) {

                for (int i = 0; i < array.size(); i++) {
                    double pre = summap.get(i) == null ? 0d : summap.get(i);
                    summap.put(i, pre + array.get(i));
                }
            }
            ArrayList<Double> finalweight = new ArrayList<Double>();
            Set<Map.Entry<Integer, Double>> set = summap.entrySet();
            for (Map.Entry<Integer, Double> mapentry : set) {
                Integer key = mapentry.getKey();
                Double sumvalue = mapentry.getValue();
                double finalvalue = sumvalue / groupsize;
                finalweight.add(finalvalue);
            }

            DataSet<String> text2 = env.readTextFile(params.get("input2"));
            text2.map(new SexPresaveMap(finalweight));

            env.execute("sexPreTask analy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
