package com.example.jason.finalproj.MainInterface;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jason.finalproj.MyArticle.ArticleActivity;
import com.example.jason.finalproj.MyArticle.ArticleAdapter;
import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.R;
import com.example.jason.finalproj.Users;
import com.example.jason.finalproj.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {
    /* 主活动实例 */
    private MainActivity mainActivity;

    /* 滑动布局 */
    private SlidingUpPanelLayout slidingUpPanelLayout;

    /* Toolbar */
    private Toolbar toolbar;

    /* 轮播图 */
    private ViewPager Viewpager;    //轮播图
    private int[] ImageIds = new int[]{R.drawable.intro1,R.drawable.intro2,R.drawable.intro3,R.drawable.intro4};//轮播图片id数组
    private LinearLayout tipsBox;   //存放提示点的容器
    private ImageView[] tips;//提示点数组
    private int currentPage=0;//当前展示的页码
    private boolean isAutoPlay = true; //是否自动播放

    private DB db = new DB(); //数据库操作实例
    private Connection con;

    /* 用户Id */
    private int user_id;
    Users users;
    boolean article_set = false;
    /* 招募列表 */
    RecyclerView recyclerView;
    private List<Integer> a_idList = new ArrayList<>();
    private List<Map<String, Object>> article_datalist = new ArrayList<>();
    private List<Map<String, Bitmap>> article_imagelist = new ArrayList<>();
    ArticleAdapter articleAdapter;

    boolean add_flag = true; //关注文章是否成功

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 将布局文件转换成View
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        /* Toolbar */
        //toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        /* 滑动布局 */
        slidingUpPanelLayout = (SlidingUpPanelLayout) view.findViewById(R.id.sliding_layout);

        /* 轮播图控件 */
        Viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        tipsBox = (LinearLayout)view.findViewById(R.id.tipsBox);

        /* 招募文章列表 */
        recyclerView = (RecyclerView) view.findViewById(R.id.recruit_list);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setViewPager();     //ViewPager设置
        setSlidingUpPanelLayout();  //滑动布局设置
        setRecyclerView();  //招募文章列表设置
    }

    /**
     * 获取碎片滑动布局
     */
    public SlidingUpPanelLayout getSlidingUpPanelLayout() {
        return slidingUpPanelLayout;
    }

    /**
     * 滑动布局设置函数
     */
    public void setSlidingUpPanelLayout() {
        /*
         * 滑动面板监听事件
         * 1.onPanelSilde -- 滑动面板位置改变
         *  @param panel 面板
         *  @param sildOffset 偏移量
         * 2.onPanelStateChanged -- 滑动面板状态改变
         *  @param panel 面板
         *  @param previousState 旧状态
         *  @param newState 新状态
         */
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        });

        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
    }



    /**
     * 招募文章列表ListView设置函数
     * 绑定适配器
     */
    public void setRecyclerView() {
        /* 文章列表数据 */
        user_id = mainActivity.getUser_id();
        a_idList = mainActivity.getA_idList();
        article_datalist = mainActivity.getArticle_datalist();
        article_imagelist = mainActivity.getArticle_imagelist();

        /* ArticleAdapter适配器 */
        articleAdapter = new ArticleAdapter(mainActivity, R.layout.article_item, article_datalist, article_imagelist);
        /* 设置适配器 */
        recyclerView.setAdapter(articleAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(mainActivity, 1);//网格布局，每行1列
        recyclerView.setLayoutManager(layoutManager);
        /* 单项点击监听器 */
        articleAdapter.setonItemClickListener(new ArticleAdapter.OnItemClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Intent intent = new Intent(mainActivity, ArticleActivity.class);
                intent.putExtra("a_id", a_idList.get(position));
                intent.putExtra("u_id", user_id);
                startActivityForResult(intent,1);
                Toast.makeText(mainActivity, String.valueOf(article_datalist.get(position).get("title")) , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void LongClick(View view, int position) {

            }

            @Override
            public void AttentionOnClick(View view, int position) {
                addAttention(a_idList.get(position));
            }
        });
    }



    public void addAttention(final int a_id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    con = db.getConnection();
                    add_flag = true;
                    List<Integer> Attention_aid = db.geta_idFromAttentionByu_id(user_id, con);
                    for(int i = 0; i < Attention_aid.size(); i++) {
                        if(a_id == Attention_aid.get(i)) add_flag = false;
                    }
                    if(add_flag) {
                        add_flag = db.addattention(user_id, a_id, con);
                    }
                    handler.obtainMessage(100).sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: //添加关注
                    if(add_flag) {
                        Toast.makeText(mainActivity, "关注成功", Toast.LENGTH_SHORT).show();
                        mainActivity.getAttention_fragment().setAttentionArticle();
                    } else Toast.makeText(mainActivity, "关注失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 轮播图ViewPager设置函数
     * 点击图片可以跳转到招募文章详情
     */
    public void setViewPager() {
        //初始化提示点
        tips_initial();

        // 声明ViewPager的自定义适配器
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mainActivity, ImageIds);
        Viewpager.setAdapter(viewPagerAdapter);

        //自动无限轮播
        infinite_carrousel();
    }
    /**
     * 初始化提示点操作
     */
    private void tips_initial() {
        //提示点初始化，把提示点添加到容器中
        tips=new ImageView[ImageIds.length];
        for(int i=0;i<tips.length;i++){
            ImageView img = new ImageView(mainActivity); //实例化提示点
            img.setLayoutParams(new ViewGroup.LayoutParams(10,10));
            tips[i] = img;
            if(i == 0) img.setBackgroundResource(R.drawable.shape_point_selected);//蓝色背景
            else img.setBackgroundResource(R.drawable.shape_point_normal);//灰色背景
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            params.leftMargin=2;
            params.rightMargin=2;
            tipsBox.addView(img, params); //把提示点添加到容器中
        }
    }

    /**
     * 自动无限轮播
     */
    private void infinite_carrousel() {
        //ViewPager的页面切换监听器
        Viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrollStateChanged(int state) {
                // TODO Auto-generated method stub
                switch (state) {
                    // 闲置中
                    case ViewPager.SCROLL_STATE_IDLE:
                        isAutoPlay = true;
                        break;
                    // 拖动中取消自动轮播
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        isAutoPlay = false;
                        break;
                    // 设置中
                    case ViewPager.SCROLL_STATE_SETTLING:
                        isAutoPlay = true;
                        break;
                }
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                // 设置提示点的背景
                tips[currentPage].setBackgroundResource(R.drawable.shape_point_normal);
                position = position % ImageIds.length;
                currentPage=position;
                tips[position].setBackgroundResource(R.drawable.shape_point_selected);
            }
        });
        //不断给自身发消息处理，实现无限轮播
        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAutoPlay) {
                    int currentPosition = Viewpager.getCurrentItem();

                    if (currentPosition == Viewpager.getAdapter().getCount() - 1) {
                        // 最后一页
                        Viewpager.setCurrentItem(0);
                    } else {
                        Viewpager.setCurrentItem(currentPosition + 1);
                    }

                    // 一直给自己发消息
                    mHandler.postDelayed(this, 5000); //5s
                } else {
                    // 一直给自己发消息
                    mHandler.postDelayed(this, 5000); //5s
                }
            }
        },5000); //5s
    }


    public void update() {
        mainActivity.setNavHeader();
        mainActivity.setToolbarPhoto();
        mainActivity.setAllArticle();

        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (article_set) {
                    article_set = false;
                    user_id = mainActivity.getUser_id();
                    a_idList = mainActivity.getA_idList();
                    article_datalist = mainActivity.getArticle_datalist();
                    article_imagelist = mainActivity.getArticle_imagelist();
                    articleAdapter.notifyDataSetChanged();
                    Log.d("MainActivity", "更新文章");
                } else {
                    article_set = mainActivity.getArticle_set();
                    // 一直给自己发消息
                    mHandler.postDelayed(this, 10); //10ms
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCode", String.valueOf(requestCode));
        Log.d("resultCode", String.valueOf(resultCode));
        if(requestCode == 1 && resultCode == 2) {
            update();
            Log.d("test", "homeOnA");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.closeConnection(con);
    }



}
