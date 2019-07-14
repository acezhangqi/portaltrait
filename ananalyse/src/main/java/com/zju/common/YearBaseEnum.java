package com.zju.common;

import java.util.Arrays;

/**
 * @author zhangqi
 * @create 2019/7/13
 * 年代人枚举
 */
public enum YearBaseEnum {

    _40("40后", 1),
    _50("50后", 2),
    _60("60后", 3),
    _70("70后", 4),
    _80("80后", 5),
    _90("90后", 6),
    _00("00后", 7),
    _2010("2010后", 8);

    private String type;

    private Integer id;

    YearBaseEnum(String type, Integer id) {
        this.type = type;
        this.id = id;
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

    public static Integer getIdByType(String type) {
        YearBaseEnum yearBaseEnum = Arrays.stream(YearBaseEnum.values()).filter(t -> t.type.equals(type)).findAny().orElse(null);
        return yearBaseEnum != null ? yearBaseEnum.getId() : 0;
    }

}
