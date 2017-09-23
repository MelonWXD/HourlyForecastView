package com.dongua.hourlyforecastview.bean;


public class HourlyWeatherBean {

    /**
     * text : 多云
     * code : 4
     * temperature : 28
     * time : 02:00
     */

    private String text;
    private String code;
    private String temperature;
    private String time;

    public HourlyWeatherBean(String text, String code, String temperature, String time) {
        this.text = text;
        this.code = code;
        this.temperature = temperature;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public int getIntCode() {
        return Integer.parseInt(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
