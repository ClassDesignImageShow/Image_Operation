package ivo_chuanzhi.test_scan_photo.customPopupWindow;

import android.content.Context;
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
import ivo_chuanzhi.test_scan_photo.activity.ShowImageActivity;
import ivo_chuanzhi.test_scan_photo.view.ZoomImageView;

/**
 * Created by chenjiacheng on 2016/5/4.
 */
public class PhotoOperationPopupWindow extends PopupWindow{

    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private Context mContext;

    private LinearLayout shareLinearLayout, deleteLinearLayout,editLinearLayout,menuLinearLayout;
    private Bitmap mBitmap;
    private ZoomImageView zoomImageView;
    public static PhotoEditPopupWindow photoEditPopupWindow;

    public PhotoOperationPopupWindow(Context context, Bitmap bitmap, ZoomImageView zoomImageView){

        this.mContext = context;
        if(bitmap == null){
            System.out.println("PhotoOperationPopupWindow : bitmap == null");
        }
        this.mBitmap = bitmap;
        this.zoomImageView = zoomImageView;
        getWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.popupwindow_photo_operation,null);
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
        shareLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.id_share);
        shareLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show();
            }
        });

        deleteLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.id_delete);
        deleteLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
            }
        });

        editLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.id_edit);
        editLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(mContext, "编辑", Toast.LENGTH_SHORT).show();

                ShowImageActivity.mPhotoOperationPopupWindow.dismiss();

                photoEditPopupWindow = new PhotoEditPopupWindow(mContext,mBitmap,zoomImageView);
                photoEditPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //         ZoomImageView.LISTENER = 0;
                        ShowImageActivity.pageText.setVisibility(View.VISIBLE);
                    }
                });
                photoEditPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
                photoEditPopupWindow.showAsDropDown(ShowImageActivity.mShowText, 0, 0);
                ShowImageActivity.pageText.setVisibility(View.GONE);

            }
        });

        menuLinearLayout = (LinearLayout) mConvertView.findViewById(R.id.id_menu);
        menuLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "菜单", Toast.LENGTH_SHORT).show();
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
