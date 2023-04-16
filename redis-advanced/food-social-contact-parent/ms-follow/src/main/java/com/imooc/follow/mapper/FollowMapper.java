package com.imooc.follow.mapper;

import com.imooc.follow.entity.Follow;
import org.apache.ibatis.annotations.*;

/**
 * @author E.T
 * @date 2023/4/16
 */
@Mapper
public interface FollowMapper {
    @Select("SELECT id, diner_id, follow_diner_id, is_valid FROM t_follow WHERE diner_id = #{dinerId} AND follow_diner_id = #{followDinerId}")
    Follow selectFollow(@Param("dinerId") Integer DinerId, @Param("followDinerId") Integer followDinerId);

    // save follow
    @Insert("INSERT INTO t_follow (diner_id, follow_diner_id, is_valid, create_date) VALUES (#{e.dinerId}, #{e.followDinerId}, 1, now())")
    @Options(useGeneratedKeys = true, keyProperty = "e.id", keyColumn = "id")
    int save(@Param("e") Follow follow);
    // update follow info
    @Update("UPDATE t_follow SET is_valid = #{isValid}, update_date = now() WHERE id =  #{id}")
    int update(@Param("id") Integer id, @Param("isValid") int isValid);
    @Delete("DELETE FROM t_follow WHERE id = #{id}")
    int delete(@Param("id") Integer id);
}
