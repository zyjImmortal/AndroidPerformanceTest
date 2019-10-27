package com.zyj.android.performance.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.zhangyue.we.x2c.ano.Xml;
import com.zyj.android.performance.test.adapter.NewsAdapter;
import com.zyj.android.performance.test.adapter.OnFeedShowCallBack;

@Xml(layouts = "activity_main")
public class MainActivity extends AppCompatActivity implements OnFeedShowCallBack {

    private RecyclerView mRecyclerView;  // 列表控件
    private NewsAdapter newsAdapter;  // 为列表控件指定一个路由器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onFeedShow() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
