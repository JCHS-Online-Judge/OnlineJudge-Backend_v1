package com.github.ioloolo.onlinejudge.common.validation;

import com.github.ioloolo.onlinejudge.common.validation.group.LengthGroup;
import com.github.ioloolo.onlinejudge.common.validation.group.MinMaxGroup;
import com.github.ioloolo.onlinejudge.common.validation.group.NotBlankGroup;
import com.github.ioloolo.onlinejudge.common.validation.group.PatternGroup;
import jakarta.validation.GroupSequence;

@GroupSequence(value = {NotBlankGroup.class, LengthGroup.class, PatternGroup.class, MinMaxGroup.class})
public interface OrderChecks {
}
