package com.sky.skyai.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 校区信息表
 * </p>
 *
 * @author yoke
 * @since 2025-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("school")
public class School implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 校区ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 校区名称
     */
    private String name;

    /**
     * 所在城市
     */
    private String city;


}
