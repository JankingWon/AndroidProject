package com.sysu.janking.httpapi.BILIBILI;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.sysu.janking.httpapi.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<RecyclerObj> mData;
    private Context context;
    private String baseURL = "https://api.bilibili.com/";
    public static final int GET_DATA_SUCCESS = 1;
    public static final int NETWORK_ERROR = 2;
    public static final int SERVER_ERROR = 3;

    public RecyclerAdapter(Context context){
        mData = new ArrayList<>();
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // 实例化展示的view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        // 实例化viewholder
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //获取屏幕宽度
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams lp = holder.result_cover.getLayoutParams();
        //宽度为屏幕宽度
        lp.width = screenWidth;
        //高度自适应
        lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        holder.result_cover.setLayoutParams(lp);
        //最大允许宽度和高度
        holder.result_cover.setMaxWidth(screenWidth);
        holder.result_cover.setMaxHeight(screenWidth);
        // 绑定数据
        holder.result_title.setText(mData.get(position).getIdata().getTitle());
        holder.result_content.setText(mData.get(position).getIdata().getContent());
        holder.result_create.setText(mData.get(position).getIdata().getCreate());
        holder.result_duration.setText(mData.get(position).getIdata().getDuration());
        holder.result_play.setText(mData.get(position).getIdata().getPlay());
        holder.result_review.setText(mData.get(position).getIdata().getVideo_review());
        //得到毫秒数,设置为拖动条最大值
        //但是我感觉不需要这么精确
        String total = mData.get(position).getIdata().getDuration();
        Date date = new Date();
        Date start = new Date();
        try{
            date = new SimpleDateFormat("mm:ss").parse(total);
            start = new SimpleDateFormat("mm:ss").parse("00:00");
        }catch (ParseException e){
            Log.d("getMs", "onBindViewHolder: " + e.toString());
        }
        Log.d("getMs", "onBindViewHolder: " + date.toString());
        //当加载完成时，设置拖动条可拖动
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_DATA_SUCCESS:
                        //设置封面
                        holder.result_cover.setImageBitmap(holder.result_bitmap);
                        //隐藏进度条
                        holder.result_progressBar.setVisibility(View.GONE);
                        //显示封面
                        holder.result_cover.setVisibility(View.VISIBLE);
                        //设置拖动条可拖动
                        //防止缺失预览图报异常
                        if(!holder.preview_bitmaps.isEmpty())
                            holder.seekBar.setEnabled(true);
                        break;
                }

            }
        };
        //RXJAVA
        //获取预览图的网页JSON数据
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        Observable<PreviewObj> observableGetImageUrl = Observable.create(new ObservableOnSubscribe<PreviewObj>() {
            @Override
            public void subscribe(ObservableEmitter<PreviewObj> observableEmitter) throws Exception {
                URL url = new URL(baseURL + "pvideo?aid=" + mData.get(position).getIdata().getAid());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                //将得到的数据转为字符串，最后解析json为PreviewObj
                if(conn.getResponseCode() == 200){
                    BufferedInputStream bis = new BufferedInputStream((InputStream)conn.getContent());
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int result = bis.read();
                    while(result != -1) {
                        byteArrayOutputStream.write((byte) result);
                        result = bis.read();
                    }
                    observableEmitter.onNext(new Gson().fromJson(byteArrayOutputStream.toString(), PreviewObj.class));
                }
                observableEmitter.onComplete();
            }
        });
        DisposableObserver<PreviewObj> disposableObserver = new DisposableObserver<PreviewObj>() {
            @Override
            public void onNext(final PreviewObj previewObj) {
                //新线程获取图片
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //获取封面的图片
                            holder.result_bitmap = getBitmap(mData.get(position).getIdata().getCover());
                            //获取预览图的的图片，并解析为bitmap保存到数组里
                            holder.preview_bitmaps = cutBitmap(
                                    getBitmap(previewObj.getIdata().getImage().get(0)),
                                    previewObj.getIdata().getImg_x_len(),
                                    previewObj.getIdata().getIndex().size() - 2,
                                    previewObj.getIdata().getImg_x_size(),
                                    previewObj.getIdata().getImg_y_size());
                        }catch (NullPointerException e){
                            Log.d("NULLE", "onNext: " + e.toString());
                        }
                        catch (IOException e){
                            Log.d("IOE", "onNext: " + e.toString());
                        }finally {
                            //利用Message把图片发给Handler
                            Message msg = Message.obtain();
                            msg.what = GET_DATA_SUCCESS;
                            handler.sendMessage(msg);
                        }
                    }
                }.start();
                Log.d ("Adapter: Next", "");
            }
            @Override
            public void onComplete() {
                Log.d ("Adapter: Complete", "");
            }

            @Override
            public void onError(Throwable e) {
                Log.d ("Adapter: Next", e.toString());
            }
        };
        //在新线程监听
        observableGetImageUrl.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        //管理DisposableObserver的容器
        mCompositeDisposable.add(disposableObserver);

        //设置进度条(总数为秒数)
        holder.seekBar.setMax((int)(date.getTime() - start.getTime()) / 1000);
        holder.seekBar.setProgress(0);
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                holder.result_cover.setImageBitmap(holder.preview_bitmaps.get( progress * (holder.preview_bitmaps.size()-1) / holder.seekBar.getMax()));
                Log.d("test", "onProgressChanged: " +  progress * (holder.preview_bitmaps.size()-1) / holder.seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                holder.result_cover.setImageBitmap(holder.result_bitmap);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(0);
                holder.result_cover.setImageBitmap(holder.result_bitmap);
            }
        });
        //提示
        Toast.makeText(context, "加载完成", Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public void addItem(RecyclerObj recyclerObj){
        if(mData != null){
            mData.add(recyclerObj);
            notifyItemInserted(mData.size() - 1);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  result_title, result_duration, result_review, result_create, result_play, result_content;
        ImageView result_cover;
        ProgressBar result_progressBar;
        SeekBar seekBar;
        //保存封面
        Bitmap result_bitmap;
        //保存预览图
        ArrayList<Bitmap> preview_bitmaps;
        public ViewHolder(View itemView) {
            super(itemView);
            result_cover = itemView.findViewById(R.id.result_cover);
            result_title = itemView.findViewById(R.id.result_title);
            result_content = itemView.findViewById(R.id.result_content);
            result_create = itemView.findViewById(R.id.result_create);
            result_play = itemView.findViewById(R.id.result_play);
            result_duration = itemView.findViewById(R.id.result_duration);
            result_review = itemView.findViewById(R.id.result_review);
            result_progressBar = itemView.findViewById(R.id.progress_bar);
            seekBar = itemView.findViewById(R.id.seek_bar);
            preview_bitmaps = new ArrayList<>();
            //默认不可拖动
            seekBar.setEnabled(false);
        }
    }
    //通过url得到bitmap文件
    public static Bitmap getBitmap(String path) throws IOException, ProtocolException {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 200){
            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return bitmap;
        }
        return null;
    }
    //裁剪图片
    public static ArrayList<Bitmap> cutBitmap(Bitmap bitmap, int img_x_len, int total_len, int img_x_size, int img_y_size){
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        //竖直方向取图，total_len / img_x_len + 1, 比如有27张图片，每行10张，那么就有3行
        //x表示横坐标（从左上角水平方向算）
        for(int i = 0; i < total_len / img_x_len + 1 && i < 10; i++){
            //水平方向取图，水平方向即每行有img_x_len张图片
            //y表示纵坐标（从左上角竖直方向算）
            for(int j = 0; j < img_x_len && i * img_x_len + j < total_len; j++){
                //基于原图，取正方形左上角x坐标
                int retX = img_x_size * j;
                int retY = img_y_size * i;
                bitmaps.add(Bitmap.createBitmap(bitmap, retX, retY, img_x_size, img_y_size, null, false));
            }
        }
        return bitmaps;
    }

}