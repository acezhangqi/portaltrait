package com.zju.common;

import java.util.Arrays;

/**
 * @author zhangqi
 * @create 2019/7/13
 * 手机号枚举
 */
public enum CarrierEnum {

    CHINA_MOBILE("移动", 1,"(^1(33|53|77|73|99|8[019])\\d{8}$)|(^1700\\d{7}$)"),
    CHINA_UNICOM("联通", 2,"(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)"),
    CHINA_TELECO("电信", 3,"(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$");

    private String type;

    private Integer id;

    private String reg;

    CarrierEnum(String type, Integer id, String reg) {
        this.type = type;
        this.id = id;
        this.reg = reg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public static Integer getIdByReg(String tel) {
        CarrierEnum carrierEnum = Arrays.stream(CarrierEnum.values()).filter(t -> tel.matches(t.reg)).findAny().orElse(null);
        return carrierEnum != null ? carrierEnum.getId() : 0;
    }

    public static String getTagById(Integer id) {
        CarrierEnum carrierEnum = Arrays.stream(CarrierEnum.values()).filter(t -> t.id.equals(id)).findAny().orElse(null);
        return carrierEnum != null ? carrierEnum.getType() : "未知";
    }

}
