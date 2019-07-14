package com.zju.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zju.common.CarrierEnum;
import com.zju.common.EmailEnum;
import com.zju.common.YearBaseEnum;
import com.zju.object.StaticsEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataUtils {

    /**
     * 根据年龄获取标签
     * @param age
     * @return
     */
    public static String getYearbasebyAge(String age){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR,-Integer.valueOf(age));
        Date newdate = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        String newdatestring = dateFormat.format(newdate);
        Integer newdateinteger = Integer.valueOf(newdatestring);
        String yearbasetype = "未知";
        if(newdateinteger >= 1940 && newdateinteger < 1950){
            yearbasetype = YearBaseEnum._40.getType();
        }else if (newdateinteger >= 1950 && newdateinteger < 1960){
            yearbasetype = YearBaseEnum._50.getType();
        }else if (newdateinteger >= 1960 && newdateinteger < 1970){
            yearbasetype = YearBaseEnum._60.getType();
        }else if (newdateinteger >= 1970 && newdateinteger < 1980){
            yearbasetype = YearBaseEnum._70.getType();
        }else if (newdateinteger >= 1980 && newdateinteger < 1990){
            yearbasetype = YearBaseEnum._80.getType();
        }else if (newdateinteger >= 1990 && newdateinteger < 2000){
            yearbasetype = YearBaseEnum._90.getType();
        }else if (newdateinteger >= 2000 && newdateinteger < 2010){
            yearbasetype = YearBaseEnum._00.getType();
        }else if (newdateinteger >= 2010 ){
            yearbasetype = YearBaseEnum._2010.getType();
        }
        return yearbasetype;
    }

    /**
     * 根据电话获取运营商
     * @param tel
     * @return
     */
    public static Integer getCarrierByTel(String tel){
        return CarrierEnum.getIdByReg(tel);
    }

    /**
     * 根据邮箱获取运营商
     */
    public static Integer getEmailIdByEmail(String email){
        return EmailEnum.getIdByReg(email);
    }



    /**
     * 创建es的map
     * @param staticsEntity
     * @return
     */
    public static Map<String,Object> buildMap(StaticsEntity staticsEntity) {
        Map<String,Object> map = new HashMap<>();
        map.put("script","ctx_.source.count+="+staticsEntity.getCount());
        //构建upsert语句
        JSONObject upsert = new JSONObject();
        upsert.put("yearType",staticsEntity.getType());
        upsert.put("count",staticsEntity.getCount());
        map.put("upsert", JSON.toJSONString(upsert));
        return map;
    }

    /**
     * 计算两个时间点之间的差距天数
     * @param startTime
     * @param endTime
     * @param dateFormatString
     * @return
     * @throws ParseException
     */
    public static int getDaysBetweenbyStartAndend(String startTime,String endTime,String dateFormatString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        Date start = dateFormat.parse(startTime);
        Date end = dateFormat.parse(endTime);
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.setTime(start);
        endCalendar.setTime(end);
        int days = 0;
        while(startCalendar.before(endCalendar)){
            startCalendar.add(Calendar.DAY_OF_YEAR,1);
            days += 1;
        }
        return days;
    }


}
