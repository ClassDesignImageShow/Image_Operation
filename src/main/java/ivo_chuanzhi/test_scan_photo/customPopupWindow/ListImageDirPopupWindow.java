package ivo_chuanzhi.test_scan_photo.customPopupWindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import ivo_chuanzhi.test_scan_photo.FolderBean.FolderBean;
import ivo_chuanzhi.test_scan_photo.R;
import ivo_chuanzhi.test_scan_photo.Utils.ImageLoader;


/**
 * Created by chenjiacheng on 2016/4/20.
 */
public class ListImageDirPopupWindow extends PopupWindow {

    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private ListView mListView;
    private List<FolderBean> mDatas;

    /**
     * 回调接口
     */
    public interface OnDirSelectedListener {
        void onSelected(FolderBean folderBean);
    }

    /**
     * 声明回调接口
     */
    public OnDirSelectedListener listener;

    /**
     * 设置回调接口
     *
     * @param listener
     */
    public void setOnDirSelectedListener(OnDirSelectedListener listener) {
        this.listener = listener;
    }


    /**
     * 构造方法
     *
     * @param context
     * @param datas
     */
    public ListImageDirPopupWindow(Context context, final List<FolderBean> datas) {

        calWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.list_dir, null);
        mDatas = datas;

        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);

        //设置点击外面可以消失
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }

                return false;
            }
        });

        initView(context);
        initEvent();

    }

    /**
     * 初始化event
     */
    private void initEvent() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    listener.onSelected(mDatas.get(position));
                }
            }
        });

    }

    /**
     * 初始化控件
     */
    private void initView(Context context) {
        mListView = (ListView) mConvertView.findViewById(R.id.id_list_dir);
        mListView.setAdapter(new ListDirAdapter(context, 0, mDatas));
    }

    /**
     * 计算popupWindow的宽度（满屏）和高度（60%）
     *
     * @param context
     */
    private void calWidthAndHeight(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMertrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMertrics);

        mWidth = outMertrics.widthPixels;
        mHeight = (int) (outMertrics.heightPixels * 0.6);
    }

    /**
     * ==================  适配器  =================
     */
    private class ListDirAdapter extends ArrayAdapter<FolderBean> {

        private LayoutInflater mInflater;
        private List<FolderBean> mDatas;

        public ListDirAdapter(Context context, int resource, List<FolderBean> objects) {
            super(context, 0, objects);

            mInflater = LayoutInflater.from(context);

        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {

                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_dir_item, parent, false);

                viewHolder.mImg = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
                viewHolder.mDirName = (TextView) convertView.findViewById(R.id.id_dir_item_name);
                viewHolder.mDirCount = (TextView) convertView.findViewById(R.id.id_dir_item_count);

                convertView.setTag(viewHolder);

            } else {

                viewHolder = (ViewHolder) convertView.getTag();
            }

            FolderBean bean = getItem(position);

            viewHolder.mImg.setImageResource(R.mipmap.pictures_no);

            //设置图片
            ImageLoader.getInstance().loadImage(bean.getFirstImgPath(), viewHolder.mImg);
            viewHolder.mDirCount.setText(bean.getCount() + "");
            viewHolder.mDirName.setText(bean.getName());

            return convertView;
        }

        private class ViewHolder {
            ImageView mImg;
            TextView mDirName;
            TextView mDirCount;
        }

    }

}


















