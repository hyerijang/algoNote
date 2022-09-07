package com.jhr.algoNote;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

public class Practice {
    
    @DisplayName("스터디 만들기")
    @RepeatedTest(5)
    void repeatTest(RepetitionInfo repetitionInfo) {
        int currentRepetition = repetitionInfo.getCurrentRepetition();
        System.out.println("currentRepetition = " + currentRepetition);
    }

}
