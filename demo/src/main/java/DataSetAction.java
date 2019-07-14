import com.sun.tools.corba.se.idl.constExpr.Or;
import org.apache.commons.compress.utils.Lists;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.GroupReduceOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhangqi
 * @create 2019/7/14
 */
public class DataSetAction {
    public static void main(String[] args) throws Exception {
        final ParameterTool param =ParameterTool.fromArgs(args);
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        List<Order> orders = mockList();

        DataSet<Tuple2<String, List<Order>>> res = env.fromCollection(orders).groupBy("userId").
                reduceGroup(new GroupReduceFunction<Order, Tuple2<String, List<Order>>>() {
                    List<Order> orders1 = new ArrayList<>();
                    Order order = null;
            @Override
            public void reduce(Iterable<Order> iterable, Collector<Tuple2<String, List<Order>>> collector) throws Exception {
                for (Order order:iterable){
                    orders1.add(order);
                    this.order = order;
                }
                collector.collect(new Tuple2<String,List<Order>>(order.getUserId(),orders1));
            }
        });
        List<Tuple2<String, List<Order>>> collect = res.collect();
        System.out.println(collect);

        env.execute("carrierbaseTask");
    }

    private static List<Order> mockList() {
        Order _1order = new Order("1",1d);
        Order _1order1 = new Order("1",2d);
        Order _2order1 = new Order("2",3d);
        Order _2order2 = new Order("2",4d);
        Order _2order3 = new Order("2",7d);
        List<Order> orders = new ArrayList<>();
        orders.add(_1order);
        orders.add(_1order1);
        orders.add(_2order1);
        orders.add(_2order2);
        orders.add(_2order3);
        return orders;
    }
}

