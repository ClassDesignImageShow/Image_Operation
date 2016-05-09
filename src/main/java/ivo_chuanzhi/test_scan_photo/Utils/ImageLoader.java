package ivo_chuanzhi.test_scan_photo.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by chenjiacheng on 2016/4/20.
 */
public class ImageLoader {
    /**
     * 图片缓存的核心类
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池（为了节约内存）
     */
    private ExecutorService mThreadPool;
    /**
     * 线程池的线程数量，默认为1
     */
    private int mThreadCount = 1;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;

    /**
     * 任务队列
     * 为什么不适用ArrayList呢，
     * 主要是因为，LinkedList可以尾部或头部取一个对象，内部实现是链表，不需要连续的内存
     */
    private LinkedList<Runnable> mTaskQueue;

    /**
     * 轮询的线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHander;

    /**
     * 运行在UI线程的handler，用于给ImageView设置图片
     */
    private Handler mHandler;

    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     * 使用信号量处理Task，让任务存贮在TaskQueue中，一个一个执行
     */
    private volatile Semaphore mPoolSemaphore;

    private static ImageLoader mInstance;

    /**
     * 队列的调度方式
     * 枚举类型
     *
     * @author zhy
     */
    public enum Type {
        FIFO, LIFO
    }


    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static ImageLoader getInstance() {

        if (mInstance == null) {
            //使用同步，主要是为了效率，
            // 比如两个线程同时达到，同步快就起作用了，第一个new了之后，第二个就new不了了，所以就有了二重判断
            //第一重没有使用同步，但是可以过滤大部分的代码
            synchronized (ImageLoader.class) {
                //二次判断
                if (mInstance == null) {
                    mInstance = new ImageLoader(1, Type.LIFO);
                }
            }
        }
        return mInstance;
    }

    /**
     * 单例获得该实例对象
     *
     * @return
     */
    public static ImageLoader getInstance(int threadCount, Type type) {

        if (mInstance == null) {
            synchronized (ImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    //不允许外界创建实例
    private ImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    /**
     * 初始化
     *
     * @param threadCount
     * @param type
     */
    private void init(int threadCount, Type type) {
        // loop thread 后台轮询线程
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池去取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // 释放一个信号量
                mSemaphore.release();
                //在后台不断进行轮询
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 3;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //每一行占据的字节数乘以它的高度
                return value.getRowBytes() * value.getHeight();
            }
        };

        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);

        mPoolSemaphore = new Semaphore(threadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type == null ? Type.LIFO : type;

    }

    /**
     * 根据path为imageView设置图片
     *
     * @param path
     * @param imageView
     */
    public void loadImage(final String path, final ImageView imageView) {
        // set tag  //防止调用多次，造成混乱
        imageView.setTag(path);
        // UI线程
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    //获取得到图片，为imageView 回调设置图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    ImageView imageView = holder.imageView;
                    Bitmap bm = holder.bitmap;
                    String path = holder.path;
                    //将path与getTag()存储路径进行比较
                    if (imageView.getTag().toString().equals(path)) {
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }

        //根据path缓存中获取bitmap
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null) {
            // 发送消息给mUIHandler,去更新图片
            ImgBeanHolder holder = new ImgBeanHolder();
            holder.bitmap = bm;
            holder.imageView = imageView;
            holder.path = path;
            Message message = Message.obtain();
            message.obj = holder;
            mHandler.sendMessage(message);
        } else {
            addTask(new Runnable() {
                @Override
                public void run() {

                    //加载图片
                    //压缩图片
                    //1、获得图片需要显示的大小
                    ImageSize imageSize = getImageViewWidth(imageView);

                    int reqWidth = imageSize.width;
                    int reqHeight = imageSize.height;

                    //2、压缩图片
                    Bitmap bm = decodeSampledBitmapFromResource(path, reqWidth,
                            reqHeight);
                    //3、把图片加入到缓存
                    addBitmapToLruCache(path, bm);

                    //4、 发送消息给mUIHandler,去更新图片
                    ImgBeanHolder holder = new ImgBeanHolder();
                    holder.bitmap = getBitmapFromLruCache(path);
                    holder.imageView = imageView;
                    holder.path = path;
                    Message message = Message.obtain();
                    message.obj = holder;
                    // Log.e("TAG", "mHandler.sendMessage(message);");
                    mHandler.sendMessage(message);

                    //释放信号
                    mPoolSemaphore.release();
                }
            });
        }

    }

    /**
     * 添加一个任务
     * <p/>
     * 向请求队列增加一条请求，并发送消息
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {  //需要同步块，保证线程一条条进来
        try {
            // 请求信号量，防止mPoolThreadHander为null
            //要确保mPoolThreadHandler被初始化后才执行addTask
            //if(mPoolThreadHandler == null){wait();}
            if (mPoolThreadHander == null)
                mSemaphore.acquire();//请求时，因为默认为0，没有得到信号量时，会被阻塞
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mTaskQueue.add(runnable);

        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    /**
     * 取出一个任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTaskQueue.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTaskQueue.removeLast();
        }
        return null;
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
     * 从LruCache中获取一张图片，如果不存在就返回null。
     */
    private Bitmap getBitmapFromLruCache(String key) {
        return mLruCache.get(key);
    }

    /**
     * 往LruCache中添加一张图片
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null)
                mLruCache.put(key, bitmap);
        }
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
        return inSampleSize;
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
     * ========================  内部类  =================
     */


    /**
     * 需要一个图片大小的对象
     */
    private class ImageSize {
        int width;
        int height;
    }

    /**
     * 将对应的图片和ImageView封装在一个对象里，防止图片错位
     */
    private class ImgBeanHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;
    }

}
