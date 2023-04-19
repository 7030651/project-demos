package com.imooc.feeds.dto;

import com.imooc.commons.model.BaseResponse;
import com.imooc.feeds.entity.Feeds;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author E.T
 * @date 2023/4/19
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FeedsResponse extends BaseResponse {
    private Feeds data;
}
