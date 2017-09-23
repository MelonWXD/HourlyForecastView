package com.dongua.hourlyforecastview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.dongua.hourlyforecastview.bean.HourlyWeatherBean;
import com.dongua.hourlyforecastview.view.HourlyForecastView;
import com.dongua.hourlyforecastview.view.ScrollWatched;
import com.dongua.hourlyforecastview.view.ScrollWatcher;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    HorizontalScrollView horizontalScrollView;
    HourlyForecastView hourlyForecastView;
    ScrollWatched watched;
    List<ScrollWatcher> watcherList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initObserver();
        initView();
    }

    private void initObserver() {
        watcherList = new ArrayList<>();
        watched = new ScrollWatched() {
            @Override
            public void addWatcher(ScrollWatcher watcher) {
                watcherList.add(watcher);
            }

            @Override
            public void removeWatcher(ScrollWatcher watcher) {
                watcherList.remove(watcher);
            }

            @Override
            public void notifyWatcher(int x) {
                for (ScrollWatcher watcher : watcherList) {
                    watcher.update(x);
                }
            }
        };
    }

    private void initView() {
        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.hsv);
        hourlyForecastView = (HourlyForecastView)findViewById(R.id.hourly);

        List<HourlyWeatherBean> hourlyWeatherList = new ArrayList<>();
        Gson gson = new Gson();
        JsonObject jo = new JsonParser().parse(jsonData).getAsJsonObject();
        JsonArray ja = jo.getAsJsonArray("hourly");

        for (JsonElement element : ja) {
            HourlyWeatherBean bean = gson.fromJson(element, new TypeToken<HourlyWeatherBean>() {
            }.getType());
            hourlyWeatherList.add(bean);
        }


        //设置当天的最高最低温度
        hourlyForecastView.setHighestTemp(27);
        hourlyForecastView.setLowestTemp(16);
        hourlyForecastView.initData(hourlyWeatherList);
        watched.addWatcher(hourlyForecastView);

        horizontalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                watched.notifyWatcher(scrollX);
            }
        });


    }


    String jsonData = " {\"hourly\":[" +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"17\",\"time\":\"02:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"17\",\"time\":\"03:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"17\",\"time\":\"04:00\"}," +
            "{\"text\":\"多云\",\"code\":\"4\",\"temperature\":\"16\",\"time\":\"05:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"17\",\"time\":\"06:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"18\",\"time\":\"07:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"19\",\"time\":\"08:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"20\",\"time\":\"09:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"22\",\"time\":\"10:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"24\",\"time\":\"11:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"25\",\"time\":\"12:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"26\",\"time\":\"13:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"27\",\"time\":\"14:00\"}," +
            "{\"text\":\"晴\",\"code\":\"0\",\"temperature\":\"26\",\"time\":\"15:00\"}," +
            "{\"text\":\"多云\",\"code\":\"4\",\"temperature\":\"26\",\"time\":\"16:00\"}," +
            "{\"text\":\"多云\",\"code\":\"4\",\"temperature\":\"25\",\"time\":\"17:00\"}," +
            "{\"text\":\"多云\",\"code\":\"4\",\"temperature\":\"24\",\"time\":\"18:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"23\",\"time\":\"19:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"21\",\"time\":\"20:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"21\",\"time\":\"21:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"21\",\"time\":\"22:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"21\",\"time\":\"23:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"21\",\"time\":\"00:00\"}," +
            "{\"text\":\"晴\",\"code\":\"1\",\"temperature\":\"20\",\"time\":\"01:00\"}]}";
}
