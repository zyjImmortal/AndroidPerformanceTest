package com.zyj.android.performance.test;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.zhangyue.we.x2c.ano.Xml;
import com.zyj.android.performance.test.adapter.NewsAdapter;
import com.zyj.android.performance.test.adapter.OnFeedShowCallBack;
import com.zyj.android.performance.test.bean.NewsItem;
import com.zyj.android.performance.test.net.RetrofitNewsUtils;
import com.zyj.android.performance.test.utils.LaunchTimer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Xml(layouts = "activity_main")
public class MainActivity extends AppCompatActivity implements OnFeedShowCallBack {

    private RecyclerView mRecyclerView;  // 列表控件
    public List<NewsItem> mItems = new ArrayList<>();
    private NewsAdapter mNewsAdapter;// 为列表控件指定一个路由器
    private String mStringIds = "20190220005233,20190220005171,20190220005160,20190220005146,20190220001228," +
            "20190220001227,20190219006994,20190219006839,20190219005350,20190219005343,20190219004522,20190219004520," +
            "20190219000132,20190219000118,20190219000119,20190218009367,20190218009078,20190218009075,20190218008572," +
            "20190218008496,20190218006078,20190218006156,20190218006190,20190218006572,20190218006235,20190218006284," +
            "20190218006571,20190218006283,20190218006191,20190218005733,20190217004740,20190218001891,20190218001889," +
            "20190217004183,20190217004019,20190217004011,20190217003152,20190217002757,20190217002249,20190217000954," +
            "20190217000957,20190217000953,20190216004269,20190216003721,20190216003720,20190216003351,20190216003364," +
            "20190216002989,20190216002613,20190216000044,20190216000043,20190216000042,20190215007933,20190215008945," +
            "20190215007932,20190215007090,20190215005473,20190215005469,20190215005313,20190215004868,20190215004299," +
            "20190215001233,20190215001229,20190215001226,20190214008652,20190214008429,20190214009262,20190214008347," +
            "20190214008345,20190214007362,20190214006949,20190214006948,20190214006588,20190214006270,20190214006102," +
            "20190214005769,20190214005583,20190214005581,20190214005484,20190214005466,20190214005303,20190214004660," +
            "20190213009703,20190213009285,20190214002912,20190213007775,20190213007461,20190213007049,20190213007047," +
            "20190213006228,20190213006050,20190213005767,20190213005738,20190213005641,20190213005512,20190213004174," +
            "20190212007918,20190212007914,20190212007913,20190212007696,20190212007369,20190212007361,20190212006921," +
            "20190212006007,20190212005954,20190212005925,20190212005924,20190212005923,20190212005922,20190212005428," +
            "20190212005427,20190212005426,20190212005226,20190212004916,20190212004422,20190212004355,20190212004351," +
            "20190212000989,20190212000994,20190212000991,20190211005672,20190211004121,20190211004049,20190211003973," +
            "20190211003434,20190211003199,20190211005392,20190211003179,20190211000956,20190211000955,20190211003203," +
            "20190211003206,20190210004201,20190210003934,20190210004067,20190210003683,20190210003685,20190210003684," +
            "20190210003682,20190210003281,20190210002944,20190210002936,20190210003308,20190210002745,20190210002634," +
            "20190210002893,20190210002315,20190210001977,20190210002046,20190210001663,20190209004408,20190209003643," +
            "20190209003582,20190209003401,20190209003193,20190209002777,20190209002664,20190209002724,20190209002723," +
            "20190209002119,20190208001691,20190208004370,20190208000203,20190208004129,20190208003560,20190208002739," +
            "20190208002661,20190208000144,20190208000194,20190208002671,20190208003081,20190208002398,20190208000184," +
            "20190208001943,20190208000074,20190208000051,20190208000121,20190207003938,20190207003939,20190208002394," +
            "20190207003698,20190207001759,20190207003882,20190207003424,20190207002872,20190207003101,20190207002873," +
            "20190207002772,20190207002036,20190207001888,20190207000695,20190206004239,20190206004172,20190206002264," +
            "20190206002238,20190206002237,20190206004192,20190206004176,20190206003738,20190206003028";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new AsyncLayoutInflater(MainActivity.this).inflate(R.layout.activity_main, null, new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(@NonNull View view, int i, @Nullable ViewGroup viewGroup) {
                setContentView(view);
                mRecyclerView = findViewById(R.id.recycler_view);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                mRecyclerView.setAdapter(mNewsAdapter);
                mNewsAdapter.setOnFeedShowCallBack(MainActivity.this);
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewsAdapter = new NewsAdapter(mItems);

        getNews();
    }

    @Override
    public void onFeedShow() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void getNews() {
        RetrofitNewsUtils.getApiService().getNBANews("banner", mStringIds)
                // enqueue执行异步请求
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String json = response.body().string();
                            JSONObject jsonObject = new JSONObject(json);
                            JSONObject data = jsonObject.getJSONObject("data");
                            Iterator<String> keys = data.keys();
                            while (keys.hasNext()) {
                                String next = keys.next();
                                JSONObject itemJO = data.getJSONObject(next);
                                NewsItem newsItem = JSON.parseObject(itemJO.toString(), NewsItem.class);
                                mItems.add(newsItem);
                            }
                            mNewsAdapter.setItems(mItems);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // activity的第一帧
        super.onWindowFocusChanged(hasFocus);
        LaunchTimer.endRecord("onWindowFocusChanged");
    }
}
