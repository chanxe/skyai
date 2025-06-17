package com.sky.skyai.entity.po;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 学科主表
 * </p>
 *
 * @author yoke
 * @since 2025-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 学科名称
     */
    private String name;

    /**
     * 学历要求：0-无,1-初中,2-高中,3-大专,4-本科以上
     */
    private Integer edu;

    /**
     * 课程类型
     */
    private String type;

    /**
     * 课程价格(单位：元)
     */
    private BigDecimal price;

    /**
     * 学习时长(天)
     */
    private Integer duration;


}
