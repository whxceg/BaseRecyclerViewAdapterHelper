package com.chad.baserecyclerviewadapterhelper;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.baserecyclerviewadapterhelper.adapter.PullToRefreshAdapter;
import com.chad.baserecyclerviewadapterhelper.base.BaseActivity;
import com.chad.baserecyclerviewadapterhelper.data.DataServer;
import com.chad.baserecyclerviewadapterhelper.loadmore.CustomLoadMoreView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class PullToRefreshUseStaggeredActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerView;
    private PullToRefreshAdapter pullToRefreshAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int TOTAL_COUNTER = 180;

    private static final int PAGE_SIZE = 7;

    private int delayMillis = 3000;

    private int mCurrentCounter = 0;

    private boolean isErr;
    private boolean mLoadMoreEndGone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        setTitle("Pull TO Refresh Use");
        setBackBtn();
        initAdapter();
        addHeadView();
    }

    private void addHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.head_view, (ViewGroup) mRecyclerView.getParent(), false);
        headView.findViewById(R.id.iv).setVisibility(View.GONE);
        ((TextView) headView.findViewById(R.id.tv)).setText("change load view");
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadMoreEndGone = true;
                pullToRefreshAdapter.setLoadMoreView(new CustomLoadMoreView());
                mRecyclerView.setAdapter(pullToRefreshAdapter);
                Toast.makeText(PullToRefreshUseStaggeredActivity.this, "change complete", Toast.LENGTH_LONG).show();
            }
        });
        pullToRefreshAdapter.addHeaderView(headView);
//        pullToRefreshAdapter.setPreLoadNumber();
    }

    @Override
    public void onLoadMoreRequested() {
        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMore();
            }
        },3000);
    }

    private void loadMore(){

        mSwipeRefreshLayout.setEnabled(false);
        if (pullToRefreshAdapter.getData().size() < PAGE_SIZE) {
            pullToRefreshAdapter.loadMoreEnd(false);
        } else {
            if (mCurrentCounter >= TOTAL_COUNTER) {
//                    pullToRefreshAdapter.loadMoreEnd();//default visible
                pullToRefreshAdapter.loadMoreEnd(mLoadMoreEndGone);//true is gone,false is visible
            } else {
                isErr = !isErr;
                if (isErr) {
                    pullToRefreshAdapter.addData(DataServer.getSampleData(PAGE_SIZE));
                    mCurrentCounter = pullToRefreshAdapter.getData().size();
                    pullToRefreshAdapter.loadMoreComplete();
                } else {
//                    isErr = true;
//                    Toast.makeText(PullToRefreshUseActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                    pullToRefreshAdapter.loadMoreFail();

                }
            }
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    @Override
    public void onRefresh() {
        pullToRefreshAdapter.setEnableLoadMore(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshAdapter.setNewData(DataServer.getSampleData(PAGE_SIZE));
                isErr = false;
                mCurrentCounter = PAGE_SIZE;
                mSwipeRefreshLayout.setRefreshing(false);
                pullToRefreshAdapter.setEnableLoadMore(true);
            }
        }, delayMillis);
    }

    private void initAdapter() {
        pullToRefreshAdapter = new PullToRefreshAdapter();
        pullToRefreshAdapter.setOnLoadMoreListener(this, mRecyclerView);
//        pullToRefreshAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        pullToRefreshAdapter.setPreLoadNumber(3);
        mRecyclerView.setAdapter(pullToRefreshAdapter);
//        pullToRefreshAdapter.bindToRecyclerView(mRecyclerView);
        mCurrentCounter = pullToRefreshAdapter.getData().size();

        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(final BaseQuickAdapter adapter, final View view, final int position) {
                Toast.makeText(PullToRefreshUseStaggeredActivity.this, Integer.toString(position), Toast.LENGTH_LONG).show();
            }
        });
    }


}
