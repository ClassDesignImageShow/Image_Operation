package ivo_chuanzhi.test_scan_photo.customPopupWindow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import ivo_chuanzhi.test_scan_photo.R;
import ivo_chuanzhi.test_scan_photo.view.VerticalSeekbar;

/**
 * Created by chenjiacheng on 2016/5/7.
 */
public class PhotoColorPopupWindow extends PopupWindow implements SeekBar.OnSeekBarChangeListener {


    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private Context mContext;
    private Bitmap mBitmap;

    //保存参数
    private float mHue, mSaturatiom, mLum;
    private  int MAX_VALUES = 255;
    private  int MID_VALUES = 127;

    private VerticalSeekbar humSeekBar,saturationSeekBar,lumSeekBar;
    private TextView humTextView,saturationTextView,lumTextView;

    public interface VerticalSeekBarOnClickListener{
        void setPrimaryColor(float hum,float saturation,float lum);
    }

    private VerticalSeekBarOnClickListener listener;

    public void setVerticalSeekBarOnClickListener(VerticalSeekBarOnClickListener listener){
        this.listener = listener;
    }


    public PhotoColorPopupWindow(){}
    public PhotoColorPopupWindow(Context context,Bitmap bitmap){
        this.mContext = context;
        this.mBitmap = bitmap;
        getWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.popupwindow_photo_color,null);
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

    private void initView() {

        humSeekBar = (VerticalSeekbar) mConvertView.findViewById(R.id.id_seekbar_hue);
        humSeekBar.setOnSeekBarChangeListener(this);
        humTextView = (TextView) mConvertView.findViewById(R.id.id_textview_hue);

        saturationSeekBar = (VerticalSeekbar) mConvertView.findViewById(R.id.id_seekbar_saturation);
        saturationSeekBar.setOnSeekBarChangeListener(this);
        saturationTextView = (TextView) mConvertView.findViewById(R.id.id_textview_saturation);

        lumSeekBar = (VerticalSeekbar) mConvertView.findViewById(R.id.id_seekbar_lum);
        lumSeekBar.setOnSeekBarChangeListener(this);
        lumTextView = (TextView) mConvertView.findViewById(R.id.id_textview_lum);


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
        mHeight = (int) (outMetrics.heightPixels * 0.5);
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {

            case R.id.id_seekbar_hue:
                mHue = (progress - MID_VALUES) * 1.0F / MID_VALUES * 180;
                humTextView.setText("色调："+mHue);
                break;

            case R.id.id_seekbar_saturation:
                mSaturatiom = progress * 1.0F / MID_VALUES;
                saturationTextView.setText("饱和度："+mSaturatiom);
                break;

            case R.id.id_seekbar_lum:
                mLum = progress * 1.0F / MID_VALUES;
                lumTextView.setText("亮度："+mLum);
                break;
        }

        // System.out.println("mHue = " + mHue + ";" + "mSaturation = " + mSaturatiom + ";" + "mLum = " + mLum );

        if(listener != null){
            listener.setPrimaryColor(mHue,mSaturatiom,mLum);
            System.out.println("mHue = " + mHue + ";" + "mSaturation = " + mSaturatiom + ";" + "mLum = " + mLum );
        }

    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}


}
