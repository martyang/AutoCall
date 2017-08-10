package com.gionee.autocall.bean;


public class ResultBean {
    public int count_total = 0;
    public int count_success = 0;
    public int count_fail = 0;
    public int call_times = 0;
    public String networkType = "";
    public String testTime = "";
    private String deviceID;

    public ResultBean setCount_total(int count_total) {
        this.count_total = count_total;
        return this;
    }

    public ResultBean setCount_success(int count_success) {
        this.count_success = count_success;
        return this;
    }

    public ResultBean setCount_fail(int count_fail) {
        this.count_fail = count_fail;
        return this;
    }

    public ResultBean setCall_times(int call_times) {
        this.call_times = call_times;
        return this;
    }

    public ResultBean setNetworkType(String networkType) {
        this.networkType = networkType;
        return this;
    }

    public ResultBean setTestTime(String testTime) {
        this.testTime = testTime;
        return this;
    }

    public String getResult() {
        return "测试总数: " + count_total + "轮" + call_times + "通" + "\n"
                + "成功个数: " + count_success + "\n"
                + "失败个数: " + count_fail + "\n"
                + "测试时间" + ": " + testTime + "\n"
                + "测试卡类型" + ": " + networkType + "\n"
                + "测试机IMEI：" + deviceID + "\r\n";
    }

    public ResultBean setDeviceID(String deviceID) {
        this.deviceID = deviceID;
        return this;
    }
}
