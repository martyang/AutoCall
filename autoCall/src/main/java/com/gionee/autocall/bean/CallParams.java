package com.gionee.autocall.bean;


public class CallParams {
    public String number = "10086,10010";
    public String[] numbers = new String[]{"10086", "10010"};
    public int count = 3;
    public int call_time = 10;
    public int gap_time = 20;
    public int call_time_sum = 60;
    public boolean isSpeakOn = false;

    public CallParams setNumber(String number) {
        this.number = number;
        return this;
    }

    public CallParams setGap_time(int gap_time) {
        this.gap_time = gap_time;
        return this;
    }

    public CallParams setNumbers(String[] numbers) {
        this.numbers = numbers;
        return this;
    }

    public CallParams setCount(int count) {
        this.count = count;
        return this;
    }

    public CallParams setCall_time(int call_time) {
        this.call_time = call_time;
        return this;
    }

    public CallParams setCall_time_sum(int call_time_sum) {
        this.call_time_sum = call_time_sum;
        return this;
    }


    public CallParams setSpeakOn(boolean speakOn) {
        this.isSpeakOn = speakOn;
        return this;
    }
}
