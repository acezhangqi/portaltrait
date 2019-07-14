package com.zju.common;

import java.util.Arrays;

/**
 * @author zhangqi
 * @create 2019/7/14
 */
public enum  EmailEnum {
    NET_EASY("网易邮箱",1,".*@163\\.com$ || .*@126\\.com$"),
    YIDONG("移动邮箱",2,".*@139\\.com$"),
    SOHU("搜狐邮箱",3,".*@sohu\\.com$$"),
    QQ("QQ邮箱",4,".*qq\\.com$"),
    TOM("tom邮箱",5,".*tom\\.com$"),
    ALIYUN("阿里邮箱",6,".*aliyun\\.com$"),
    SINA("新浪邮箱",7,".*sina\\.com$");

    private String type;
    private Integer id;
    private String reg;

    EmailEnum(String type, Integer id, String reg) {
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
    public static Integer getIdByReg(String email) {
        EmailEnum emailEnum = Arrays.stream(EmailEnum.values()).filter(t -> email.matches(t.reg)).findAny().orElse(null);
        return emailEnum != null ? emailEnum.id : 0;
    }

    public static String getTagById(Integer id) {
        EmailEnum emailEnum = Arrays.stream(EmailEnum.values()).filter(t -> t.id.equals(id)).findAny().orElse(null);
        return emailEnum != null ? emailEnum.type : "未知";
    }
}
