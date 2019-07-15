package com.zju.object;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangqi
 * @create 2019/7/15
 */
@Data
@NoArgsConstructor
public class BrandLikeDo {
    private String brand;
    private long count;
    private String groupbyfield;

}
