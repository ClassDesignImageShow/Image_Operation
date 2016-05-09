package ivo_chuanzhi.test_scan_photo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import ivo_chuanzhi.test_scan_photo.Adapter.ImageAdapter;
import ivo_chuanzhi.test_scan_photo.R;
import ivo_chuanzhi.test_scan_photo.Utils.ImageLoader;
import ivo_chuanzhi.test_scan_photo.customPopupWindow.PhotoOperationPopupWindow;
import ivo_chuanzhi.test_scan_photo.view.ZoomImageView;

/**
 * Created by chenjiacheng on 2016/4/28.
 */
public class ShowImageActivity extends Activity implements ViewPager.OnPageChangeListener {

    // private ZoomImageView mZoomImageView;

    /**
     * 用于管理图片的滑动
     */
    private ViewPager viewPager;

    private  ViewPagerAdapter adapter;

    /**
     * 显示当前图片的页数
     */
    public static TextView pageText;
    public static TextView mShowText;

    /**
     * 图片数据集
     */
    private List<String> mImgePaths;

    private int imagePosition;

    private String mDirPath;

    private Bitmap mBitmap;

    //弹出菜单
    public static PhotoOperationPopupWindow mPhotoOperationPopupWindow;

  //  private RelativeLayout mRelativeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_show_image);


        mDirPath = getIntent().getStringExtra(ImageAdapter.IMAGE_DIRPATH);
        imagePosition = getIntent().getIntExtra(ImageAdapter.IMAGE_POSITION, 0);
        mImgePaths = ImageAdapter.getImgPaths();

        pageText = (TextView) findViewById(R.id.id_page_text);
        mShowText = (TextView) findViewById(R.id.id_show_text);
        viewPager = (ViewPager) findViewById(R.id.id_viewpager);


    }




    @Override
    protected void onResume() {
        super.onResume();

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(imagePosition);
        viewPager.addOnPageChangeListener(this);
        //设定当前的页数和总页数
        pageText.setText((imagePosition + 1) + "/" + mImgePaths.size());

        //initPhotoOperationPopupWindow();



    }

    private void initPhotoOperationPopupWindow(ZoomImageView zoomImageView){
        if(mBitmap != null){
            System.out.println("ShowImageActivity : mBitmap != null");
        }else {
            System.out.println("ShowImageActivity : mBitmap == null");
        }
        mPhotoOperationPopupWindow = new PhotoOperationPopupWindow(this,mBitmap,zoomImageView);
        mPhotoOperationPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
       //         ZoomImageView.LISTENER = 0;
                pageText.setVisibility(View.VISIBLE);
            }
        });
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 每当页数发生改变时重新设定一遍当前的页数和总页数
        pageText.setText((position + 1) + "/" + mImgePaths.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * 需要一个图片大小的对象
     */
    class ImageSize {
        int width;
        int height;
    }


    /**
     * ============= ViewPagerAdapter ===============
     */

    class ViewPagerAdapter extends PagerAdapter{



        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            String imagePath = mDirPath + "/" + mImgePaths.get(position);

            View view = LayoutInflater.from(ShowImageActivity.this)
                    .inflate(R.layout.zoom_image_layout, null);
             final ZoomImageView zoomImageView = (ZoomImageView) view.findViewById(R.id.id_ZoomImageView);

            zoomImageView.setZooomImageOnClickListener(new ZoomImageView.ZooomImageOnClickListener() {
                @Override
                public void zoomImageCallBack() {

                  //  System.out.println("打开PopupWindow");
                 //   Toast.makeText(ShowImageActivity.this, "打开PopupWindow", Toast.LENGTH_SHORT).show();

                //    initPhotoOperationPopupWindow(zoomImageView);

                    if(mBitmap != null){
                        System.out.println("ShowImageActivity : mBitmap != null");
                    }else {
                        System.out.println("ShowImageActivity : mBitmap == null");
                    }
                    mPhotoOperationPopupWindow = new PhotoOperationPopupWindow(ShowImageActivity.this,mBitmap,zoomImageView);
                    mPhotoOperationPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            //         ZoomImageView.LISTENER = 0;
                            pageText.setVisibility(View.VISIBLE);
                        }
                    });

                    mPhotoOperationPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
                    mPhotoOperationPopupWindow.showAsDropDown(mShowText, 0, 0);
                    pageText.setVisibility(View.GONE);
                }
            });

            ImageSize  imageSize = getImageViewWidth(zoomImageView);


            mBitmap = decodeSampledBitmapFromResource(imagePath
                    ,imageSize.width, imageSize.height);
            if(mBitmap == null){
                mBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.pictures_no);
            }
            zoomImageView.setImageBitmap(mBitmap);

            imagePath = mDirPath + "/" + mImgePaths.get(position-1);
            mBitmap = decodeSampledBitmapFromResource(imagePath
                    ,imageSize.width, imageSize.height);

            container.addView(view);


            return view;
        }



        /**
         * 根据ImageView获得适当的压缩的宽和高
         *
         * @param imageView
         * @return
         */
        private ImageSize getImageViewWidth(ImageView imageView) {
            ImageSize imageSize = new ImageSize();
            final DisplayMetrics displayMetrics = imageView.getContext()
                    .getResources().getDisplayMetrics();//取得屏幕
            final LayoutParams params = imageView.getLayoutParams();

            int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
                    .getWidth(); // Get actual image width  获取imageView的实际宽度
            //由于刚刚new出来，宽度可能为0，或者种种原因
            if (width <= 0)
                width = params.width; // Get layout width parameter  获取imageView在layout中声明的宽度
            //有可能声明为wrap_content
            if (width <= 0)
                width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check检查最大值
            // maxWidth
            // parameter
            //如果没有设置，依然<=0
            if (width <= 0)
                width = displayMetrics.widthPixels;//最不幸，只能获取屏幕的宽度了

            int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
                    .getHeight(); // Get actual image height
            if (height <= 0)
                height = params.height; // Get layout height parameter
            if (height <= 0)
                height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
            // maxHeight
            // parameter
            if (height <= 0)
                height = displayMetrics.heightPixels;
            imageSize.width = width;
            imageSize.height = height;
            return imageSize;

        }

        /**
         * 反射获得ImageView设置的最大宽度和高度
         *
         * @param object
         * @param fieldName
         * @return
         */
        private int getImageViewFieldValue(Object object, String fieldName) {
            int value = 0;
            try {
                Field field = ImageView.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                int fieldValue = (Integer) field.get(object);
                if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                    value = fieldValue;

                    //          Log.e("TAG", value + "");
                }
            } catch (Exception e) {
            }
            return value;
        }


        /**
         * 根据计算的inSampleSize，得到压缩后图片
         *
         * @param pathName
         * @param reqWidth
         * @param reqHeight
         * @return
         */
        private Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {
            // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
            //获取图片原来实际的宽和高，并不把图片加载到内存中
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(pathName, options);
            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            // 使用获取到的inSampleSize值再次解析图片,并加载到内存
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

            return bitmap;
        }


        /**
         * 计算inSampleSize，用于压缩图片
         *
         * @param options
         * @param reqWidth
         * @param reqHeight
         * @return
         */
        private int calculateInSampleSize(BitmapFactory.Options options,
                                          int reqWidth, int reqHeight) {
            // 源图片的宽度
            int width = options.outWidth;
            int height = options.outHeight;
            int inSampleSize = 1;

            if (width > reqWidth && height > reqHeight) {
                // 计算出实际宽度和目标宽度的比率
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) width / (float) reqWidth);
                //取两者的大值,会压缩得比较小，可以根据项目实际更改策略
                inSampleSize = Math.max(widthRatio, heightRatio);
            }
       //     System.out.println("压缩比 = " + (inSampleSize+1));
            return inSampleSize;
        }

        @Override
        public int getCount() {
            return mImgePaths.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*for(int i = 0; i<mImgePaths.size();i++){
            mImgePaths.remove(i);
        }*/
        mImgePaths = null;
        adapter = null;
        viewPager = null;
        pageText = null;
        imagePosition = 10;
        mDirPath = null;


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            mImgePaths = null;
            adapter = null;
            viewPager = null;
            pageText = null;
            imagePosition = 10;
            mDirPath = null;

            finish();
            overridePendingTransition(R.anim.activity_zoom_in,R.anim.activiy_zoom_out);

            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


}
