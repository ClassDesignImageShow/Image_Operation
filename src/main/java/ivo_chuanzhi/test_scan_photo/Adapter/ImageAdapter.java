package ivo_chuanzhi.test_scan_photo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ivo_chuanzhi.test_scan_photo.R;
import ivo_chuanzhi.test_scan_photo.Utils.ImageLoader;
import ivo_chuanzhi.test_scan_photo.activity.ShowImageActivity;


/**
 * Created by chenjiacheng on 2016/4/20.
 */
public class ImageAdapter extends BaseAdapter {

    /**
     * 保存选中状态
     */
    private  Set<String> mSelectItem = new HashSet<String>();

    private String mDirPath;
    private static List<String> mImgPaths;

    public static List<String> getImgPaths() {
        return mImgPaths;
    }

    private LayoutInflater mInflater;

    private int mScreenWidth;

    private Context context;

    public static final String IMAGE_PATH = "image_path";//具体某一张图片
    public static final String IMAGE_PATH_LIST = "image_path_list";//图片数据集
    public static final String IMAGE_POSITION = "image_position";//在数据集的位置
    public static final String IMAGE_DIRPATH = "image_dirPath";//数据集的父路经

    public ImageAdapter(){

    }

    //上下文对象，图片名称，图片路径（不传图片的路径加名称，为了节约内存，因为一个路径下可以有好几张图片）
    public ImageAdapter(Context context, List<String> mDatas, String path) {
        this.context = context;
        this.mDirPath = path;
        this.mImgPaths = mDatas;
        mInflater = LayoutInflater.from(context);

        //最后为节省内存所做的优化，在一开始的时候，只取屏幕的三分之一
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }

    @Override
    public int getCount() {
        return mImgPaths.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgPaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_item_image);
            viewHolder.mSelect = (ImageButton) convertView.findViewById(R.id.id_item_select);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //重置状态
        viewHolder.mImg.setImageResource(R.mipmap.pictures_no);
        viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);
        viewHolder.mImg.setColorFilter(null);

        viewHolder.mImg.setMaxWidth(mScreenWidth / 3);

        //加载图片
        ImageLoader.getInstance(5, ImageLoader.Type.LIFO)
                .loadImage(mDirPath + "/" + mImgPaths.get(position), viewHolder.mImg);


        final String filePath = mDirPath + "/" + mImgPaths.get(position);

        //监听点击事件
        viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.putExtra(IMAGE_PATH, filePath);
                intent.putExtra(IMAGE_DIRPATH,mDirPath);
                intent.putExtra(IMAGE_POSITION, position);

                //不知道为毛这一句会出错，只好直接从getter拿了
             //   intent.putStringArrayListExtra(IMAGE_PATH_LIST, ((ArrayList<String>)mImgPaths));
                context.startActivity(intent);
                //参数一是下一个Activity的进入动画，参数二是当前Activity的退出动画
                ((Activity) context).overridePendingTransition(R.anim.activity_zoom_in, R.anim.activiy_zoom_out);

            }
        });
        viewHolder.mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //已经选择
                if (mSelectItem.contains(filePath)) {
                    mSelectItem.remove(filePath);
                    viewHolder.mImg.setColorFilter(null);
                    viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);
                    //     viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);

                } else {//未被选择
                    mSelectItem.add(filePath);
                    viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
                    viewHolder.mSelect.setImageResource(R.mipmap.pictures_selected);

                }
            }
        });

        //监听长按事件
        viewHolder.mImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //已经选择
                if (mSelectItem.contains(filePath)) {
                    mSelectItem.remove(filePath);
                    viewHolder.mImg.setColorFilter(null);
                    viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);
                    //     viewHolder.mSelect.setImageResource(R.mipmap.picture_unselected);

                } else {//未被选择
                    mSelectItem.add(filePath);
                    viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
                    viewHolder.mSelect.setImageResource(R.mipmap.pictures_selected);

                }

                //点击事件不会再次传递给短按
                return true;
            }
        });



        if (mSelectItem.contains(filePath)) {
            viewHolder.mImg.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.mSelect.setImageResource(R.mipmap.pictures_selected);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView mImg;
        ImageButton mSelect;
    }
}

