package com.github.ioloolo.onlinejudge.domain.rank.data;

import com.github.ioloolo.onlinejudge.domain.user.data.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Rank {

    private User user;

    private int solved;
}
