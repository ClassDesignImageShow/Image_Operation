package ivo_chuanzhi.test_scan_photo.customPopupWindow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import ivo_chuanzhi.test_scan_photo.R;
import ivo_chuanzhi.test_scan_photo.Utils.ImageHelper;
import ivo_chuanzhi.test_scan_photo.activity.ShowImageActivity;
import ivo_chuanzhi.test_scan_photo.activity.TestActivity;
import ivo_chuanzhi.test_scan_photo.view.ZoomImageView;

/**
 * Created by chenjiacheng on 2016/5/6.
 */
public class PhotoEditPopupWindow extends PopupWindow{


    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private Context mContext;
    private Bitmap mBitmap;
    private ZoomImageView zoomImageView;
    private LinearLayout cutLinearLayout, passLinearLayout,colorLinearLayout,mosaicLinearLayout;

    public static PhotoColorPopupWindow  photoColorPopupWindow;


    public PhotoEditPopupWindow(){}
    public PhotoEditPopupWindow(Context context, Bitmap bitmap,ZoomImageView zoomImageView){

        this.mContext = context;
        if(bitmap == null){
            System.out.println("PhotoEditPopupWindow : bitmap == null");
        }
        this.mBitmap = bitmap;
        this.zoomImageView = zoomImageView;
        getWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.popupwindow_photo_edit,null);
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
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    dismiss();
                    //       ZoomImageView.LISTENER = 0;
                    return true;
                }
                return false;
            }
        });

        initView();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        cutLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.id_photoCut);
        cutLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "剪辑", Toast.LENGTH_SHORT).show();
               /* Intent intent = new Intent(mContext, TestActivity.class);
                mContext.startActivity(intent);*/
            }
        });

        passLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.id_photoPass);
        passLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "滤镜", Toast.LENGTH_SHORT).show();
            }
        });

        colorLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.id_photoColor);
        colorLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "色彩", Toast.LENGTH_SHORT).show();
                PhotoOperationPopupWindow.photoEditPopupWindow.dismiss();
             //   ShowImageActivity.pageText.setVisibility(View.VISIBLE);

/*                PhotoOperationPopupWindow pw = new PhotoOperationPopupWindow(mContext,mBitmap);
                pw.showAsDropDown(ShowImageActivity.mShowText,0,0);*/

                photoColorPopupWindow = new PhotoColorPopupWindow(mContext,mBitmap);
                photoColorPopupWindow.setVerticalSeekBarOnClickListener(new PhotoColorPopupWindow.VerticalSeekBarOnClickListener() {
                    @Override
                    public void setPrimaryColor(float hum, float saturation, float lum) {
                        System.out.println("设置图片的三个调色");
                        zoomImageView.setImageBitmap(ImageHelper.handleImageEffect(mBitmap,hum,saturation,lum));
                    }
                });
               photoColorPopupWindow.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        System.out.println("消失？？？？？？？？？？");
                        ShowImageActivity.pageText.setVisibility(View.VISIBLE);
                    }
                });
                System.out.println("展现photoColorPopupWindow");
                photoColorPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
                photoColorPopupWindow.showAsDropDown(ShowImageActivity.mShowText, 0, 0);
                ShowImageActivity.pageText.setVisibility(View.GONE);
                System.out.println("展现？？？？？？？？？？");

            }
        });

        mosaicLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.id_photoMosaic);
        mosaicLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "马赛克", Toast.LENGTH_SHORT).show();
            }
        });


    }


    /**
     * 取得PopupWindow的宽高
     * @param context
     */
    private void getWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mWidth = outMetrics.widthPixels;
        mHeight = (int) (outMetrics.heightPixels * 0.08);
    }


}
