package com.team1.kingofhonor.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.team1.kingofhonor.HeroAdd;
import com.team1.kingofhonor.HeroDetail;
import com.team1.kingofhonor.R;
import com.team1.kingofhonor.adapter.HeroAdapter;
import com.team1.kingofhonor.model.Hero;
import com.team1.kingofhonor.sqlite.HeroSQLiteHelper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


public class Fragment1 extends Fragment {
    private View view;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private HeroAdapter mAdapter;
    private int clickPosition;
    /* 英雄类别选择 */
    private RadioGroup hero_category_select;
    //搜索提示消息
    private ArrayAdapter<String> searchAdapter;
    //picture pager
    private ViewPager imageViewPager;
    private String[] imageArr={"android.resource://com.team1.kingofhonor/" + R.mipmap.juyoujing,
            "android.resource://com.team1.kingofhonor/" + R.mipmap.libai};
    private ArrayList<ImageView> imgList;
    private int lastPointPosition;//上一个页面的位置索引
    private LinearLayout pointGroup;//滑动的指示
    private boolean isRunning = true;
    /**
     * 用于实现自动滑动
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1 && isRunning){
                //收到消息,开始滑动
                if(imageViewPager != null){
                    int currentItem = imageViewPager.getCurrentItem();//获取当前显示的界面的索引
                    //如果当前显示的是最后一个页面,就显示第一张,否则显示下一张
                    if(currentItem==imgList.size()-1){
                        imageViewPager.setCurrentItem(0);
                    }else{
                        imageViewPager.setCurrentItem(currentItem+1);
                    }
                }

                //2ms后再发送消息,实现循环
                handler.sendEmptyMessageDelayed(1, 3000);
            }
        }
    };
    //database
    private HeroSQLiteHelper mySQLiteHelper;
    //EventBus
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Hero h) {
        if(h.getAdded()){
            h.setAdded(false);
            searchAdapter.add(h.getName());
            searchAdapter.notifyDataSetChanged();
            mAdapter.addNewItem(h);
            mySQLiteHelper.addHero(h);
            //更新当前选中的英雄类型的列表
            mAdapter.updateWithCategory(mySQLiteHelper.getAllHeroes(), ((RadioButton)view.findViewById(hero_category_select.getCheckedRadioButtonId())).getText().toString());
            Log.d("添加英雄", "onMessageEvent: add" + h.getName());
        } else if(h.getDeleted()){
                h.setDeleted(false);
                if(mAdapter != null) {
                    mAdapter.deleteItem(clickPosition);
                    searchAdapter.remove(h.getName());
                    searchAdapter.notifyDataSetChanged();
                }
                mySQLiteHelper.deleteHero(h);
                Log.d("删除英雄", "onMessageEvent: delete" + h.getName());
            }else if(h.getModified()){
                h.setModified(false);
                if(mAdapter != null) {
                    mAdapter.updateSingleHero(clickPosition, h);
                }
                mySQLiteHelper.deleteHero(h);
                mySQLiteHelper.addHero(h);
                Log.d("修改英雄", "onMessageEvent: modify" + h.getName());
            }

    };

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment1, container, false);
        mySQLiteHelper= new HeroSQLiteHelper(getContext());

        //设置宽度固定，高度自适应
        //获取屏幕宽度
        //int screenHeight = getResources().getDisplayMetrics().heightPixels;


        //hero list
        mLayoutManager = new GridLayoutManager(getActivity(), 5);
        mAdapter = new HeroAdapter(mySQLiteHelper.getAllHeroes());
        mRecyclerView = view.findViewById(R.id.hero_recyclerview);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //设置英雄列表的边距
        //mRecyclerView.addItemDecoration(new SpaceItemDecoration(40));
        mAdapter.setOnItemClickListener(new HeroAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), HeroDetail.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("Click_Hero", mAdapter.getItem(position));
                intent.putExtras(bundle);
                clickPosition = position;
                startActivityForResult(intent, 0);
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        //获取ToolBar控件
        android.support.v7.widget.Toolbar toolbar = view.findViewById(R.id.hero_toolbar);
        toolbar.inflateMenu(R.menu.hero_menu);//设置右上角的填充菜单
        toolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    //添加英雄
                    case R.id.action_hero_add:
                        Intent intent = new Intent(getActivity(), HeroAdd.class);
                        intent.putStringArrayListExtra("hero_names", mAdapter.getAllNames());
                        startActivityForResult(intent, 1);
                        break;
                }
                return true;
            }
        });

        //search
        final SearchView searchView = view.findViewById(R.id.hero_edit_search);
        searchView.setIconified(false);//设置searchView处于展开状态
        searchView.onActionViewExpanded();// 当展开无输入内容的时候，没有关闭的图标
        searchView.setQueryHint("输入查找的英雄名");//设置默认无内容时的文字提示
        searchView.setIconifiedByDefault(false);//默认为true在框内，设置false则在框外
        searchView.setIconified(false);//展开状态
        searchView.clearFocus();//清除焦点
        searchView.isSubmitButtonEnabled();//键盘上显示搜索图标

        AutoCompleteTextView completeText = searchView.findViewById(R.id.search_src_text) ;
        completeText.setTextColor(getResources().getColor(android.R.color.black));//设置内容文字颜色
        completeText.setHintTextColor(getResources().getColor(R.color.white));//设置提示文字颜色
        completeText.setThreshold(0);
        searchAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mAdapter.getAllNames());
        completeText.setAdapter(searchAdapter);
        completeText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position,long id){
                searchView.setQuery(searchAdapter.getItem(position),true);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){

                Hero temp_hero = mAdapter.getItemByName(query);
                if(temp_hero != null){
                    Intent intent = new Intent(getActivity(), HeroDetail.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("Click_Hero", temp_hero);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);
                }else{
                    Toast.makeText(getActivity(),"该英雄不存在" ,Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                return false;
            }
        });

        //hero category
        //改变显示的英雄类别
        hero_category_select = view.findViewById(R.id.hero_category);
        hero_category_select.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mAdapter.updateWithCategory(mySQLiteHelper.getAllHeroes(), ((RadioButton)view.findViewById(checkedId)).getText().toString());
            }
        });
        return view;

    }


    @Override
    public void onResume() {
        super.onResume();
        lastPointPosition = 0;
        isRunning = true;
        //view pager
        pointGroup = view.findViewById(R.id.point_group);
        imageViewPager = view.findViewById(R.id.hero_upper_pager);
        //添加图片列表
        imageArr = mAdapter.getAllFavoriteHeroes(mySQLiteHelper);
        imgList= new ArrayList<>();
        for (int i=0;i<imageArr.length;i++) {
            //初始化图片
            ImageView image=new ImageView(getActivity());
            image.setImageURI(Uri.parse(imageArr[i]));
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            imgList.add(image);
            //添加图片的指示点
            ImageView point=new ImageView(getActivity());
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(30,30);//布局参数,point的布局宽与高
            params.rightMargin = 40;//右边距
            point.setLayoutParams(params);//设置布局参数
            point.setBackgroundResource(R.drawable.point_bg);//point_bg是根据setEnabled的值来确定形状的
            if(i==0){
                point.setEnabled(true);//初始化的时候设置第一张图片的形状
            }else{
                point.setEnabled(false);//根据该属性来确定这个图片的显示形状
            }
            pointGroup.addView(point);//将该指示的图片添加到布局中
        }
        imageViewPager.setAdapter(new ImagePagerAdapter());
        //为viewPager设置监听
        imageViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //页面改变的时候调用(稳定),positon表示被选中的索引
            @Override
            public void onPageSelected(int position) {

                if(pointGroup.getChildAt(lastPointPosition)!=null)
                    pointGroup.getChildAt(lastPointPosition).setEnabled(false);//将上一个点设置为false
                else
                    Log.e("PointGroupRrror:", "onPageSelected: "+ lastPointPosition);
                lastPointPosition=position;
                //改变指示点的状态
                pointGroup.getChildAt(position).setEnabled(true);//将当前点enbale设置为true
            }
            //页面正在滑动的时候调用,position指的是左侧页面的索引,positionOffset代表偏移量[0,1]的范围,positionOffsetPixels也是偏移量,不过是像素点的偏移量 范围[0,显示的控件的绝对长度]
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }
            @Override
            //页面滚动状态发送改变的时候回调
            public void onPageScrollStateChanged(int state) {
                //当手指点击屏幕滚动的时状态码为1,当手指离开viewpager自动滚动的状态码为2,自动滚动选中了显示了页面的时候状态码为0
            }

        });
        handler.sendEmptyMessageDelayed(1, 3000);
    }

    //图片轮换的适配器
    private class ImagePagerAdapter extends PagerAdapter {
        /**
         * 获得页面的总数
         */
        @Override
        public int getCount() {
            return imageArr.length;
        }
        /**
         * 判断view和object的对应关系,如果当前要显示的控件是来之于instantiateItem方法创建的就显示,否则不显示
         * object 为instantiateItem方法返回的对象
         * 如果为false就不会显示该视图
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }
        /**
         * 实例化下一个要显示的子条目,获取相应位置上的view,这个为当前显示的视图的下一个需要显示的控件
         * container  view的容器,其实就是viewager自身
         * position   ViewPager相应的位置
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imgList.get(position));
            return imgList.get(position);
        }
        /**
         * 销毁一个子条目,object就为instantiateItem方法创建的返回的对象,也是滑出去需要销毁了的视图对象
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            object=null;
        }
    }
    //recyclerview居中的间距设置
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            outRect.left = space;
            outRect.bottom = space;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) %3==0) {
                outRect.left = 0;
            }
        }

    }
    //下拉框选择事件
    private class HeroCategorySelected implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            mAdapter.updateWithCategory(mySQLiteHelper.getAllHeroes(),parent.getItemAtPosition(position).toString());


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }

    }
    //一定要记得取消注册，不然经常创建页面会显示重复注册
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
        pointGroup.removeAllViews();
        imageViewPager.removeAllViews();
        imageViewPager.clearOnPageChangeListeners();
    }
}