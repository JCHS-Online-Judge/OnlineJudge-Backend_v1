package com.github.ioloolo.onlinejudge.judge.container.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JudgeLanguage {
    C("Solution.c", "gcc Solution.c -o Solution -O2 -w -lm -std=c11 -DONLINE_JUDGE", "./Solution"),
    CPP("Solution.cpp", "g++ Solution.cpp -o Solution -O2 -w -lm -std=c++17 -DONLINE_JUDGE", "./Solution"),
    PYTHON("Solution.py", null, "python Solution.py"),
    JAVA("Solution.java", "javac Solution.java", "java Solution"),
    ;
    
    private final String fileName;
    private final String compile;
    private final String run;
}

