package com.pmmq.pmmqproject.ui.picture;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 可移动、缩放、旋转的ImageView
 *
 * @author
 * @ClassName: TouchImageView
 * @Description: TODO
 * @date 2014年11月28日 下午4:52:44
 */
public class TouchImageView extends ImageView {

    float x_down = 0;
    float y_down = 0;
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    float oldRotation = 0;
    Matrix matrix = new Matrix();
    Matrix matrix1 = new Matrix();
    Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    int mode = NONE;

    boolean matrixCheck = false;

    int widthScreen;
    int heightScreen;

    Bitmap gintama;

    public TouchImageView(Activity activity, Bitmap resBitmap) {
        super(activity);
        //        gintama = BitmapFactory.decodeResource(getResources(), resId);
        gintama = resBitmap;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        widthScreen = dm.widthPixels;
        heightScreen = dm.heightPixels;

        matrix = new Matrix();
    }

    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawBitmap(gintama, matrix, null);
        canvas.restore();
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    matrix1.set(savedMatrix);
                    float rotation = rotation(event) - oldRotation;
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;
                    matrix1.postScale(scale, scale, mid.x, mid.y);// 縮放
                    matrix1.postRotate(rotation, mid.x, mid.y);// 旋轉
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                } else if (mode == DRAG) {
                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                    matrixCheck = matrixCheck();
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        matrix.set(matrix1);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        return true;
    }

    private boolean matrixCheck() {  
        /*float[] f = new float[9];  
        matrix1.getValues(f);  
        // 图片4个顶点的坐标  
        float x1 = f[0] * 0 + f[1] * 0 + f[2];  
        float y1 = f[3] * 0 + f[4] * 0 + f[5];  
        float x2 = f[0] * gintama.getWidth() + f[1] * 0 + f[2];  
        float y2 = f[3] * gintama.getWidth() + f[4] * 0 + f[5];  
        float x3 = f[0] * 0 + f[1] * gintama.getHeight() + f[2];  
        float y3 = f[3] * 0 + f[4] * gintama.getHeight() + f[5];  
        float x4 = f[0] * gintama.getWidth() + f[1] * gintama.getHeight() + f[2];  
        float y4 = f[3] * gintama.getWidth() + f[4] * gintama.getHeight() + f[5];  
        // 图片现宽度  
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));  
        // 缩放比率判断  
        if (width < widthScreen / 3 || width > widthScreen * 3) {  
            return true;  
        }  
        // 出界判断  
        if ((x1 < widthScreen / 3 && x2 < widthScreen / 3  
                && x3 < widthScreen / 3 && x4 < widthScreen / 3)  
                || (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3  
                        && x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)  
                || (y1 < heightScreen / 3 && y2 < heightScreen / 3  
                        && y3 < heightScreen / 3 && y4 < heightScreen / 3)  
                || (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3  
                        && y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {  
            return true;  
        }  */
        return false;
    }

    // 触碰两点间距离  
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        //        return FloatMath.sqrt(x * x + y * y);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点  
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取旋转角度  
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 将移动，缩放以及旋转后的图层保存为新图片  
    // 本例中沒有用到該方法，需要保存圖片的可以參考  
    public Bitmap CreatNewPhoto() {
        Bitmap bitmap = Bitmap.createBitmap(widthScreen, widthScreen,
                Config.ARGB_8888); // 背景图片  
        Canvas canvas = new Canvas(bitmap); // 新建画布  
        canvas.drawBitmap(gintama, matrix, null); // 画图片  
        canvas.save(Canvas.ALL_SAVE_FLAG); // 保存画布  
        canvas.restore();
        return bitmap;
    }


}
