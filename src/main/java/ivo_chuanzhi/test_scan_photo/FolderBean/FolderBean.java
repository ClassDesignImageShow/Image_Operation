package ivo_chuanzhi.test_scan_photo.FolderBean;

/**
 * Created by chenjiacheng on 2016/4/20.
 */
public class FolderBean {

    /**
     * 当前文件夹的路径
     */
    private String dir;
    private String firstImgPath;
    private String name;

    public String getName() {
        return name;
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        //自动为文件夹赋名
        int lastIndexOf = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(lastIndexOf);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;

}
