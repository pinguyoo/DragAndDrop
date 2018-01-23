package ncu.a106hci.draganddrop;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView image;
    private TextView text;
    private Matrix matrix, currentMatrix;
    private PointF startPoint, middlePoint, textPoint;
    float scale, imageDistance;
    int shiftPercentX, shiftPercentY;
    private LinearLayout linearLayout;
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = (ImageView) findViewById(R.id.image);
        image.setOnTouchListener(onTouchListener);
        text = (TextView) findViewById(R.id.text);
        initialValue();
    }

    private void initialValue() {
        scale = 0;
        imageDistance = 0;
        shiftPercentX = 0;
        shiftPercentY = 0;
        matrix = new Matrix();
        currentMatrix = new Matrix();
        startPoint = new PointF();
        textPoint = deriveCenter(currentMatrix);
        params.leftMargin = 128;
        params.topMargin  = 128;
        text.setLayoutParams(params);
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction() & motionEvent.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    currentMatrix.set(image.getImageMatrix());
                    startPoint.set(motionEvent.getX(), motionEvent.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    int fingerNumber = motionEvent.getPointerCount();
                    if (fingerNumber < 2) {
                        float x = motionEvent.getX() - startPoint.x;
                        float y = motionEvent.getY() - startPoint.y;
                        matrix.set(currentMatrix);
                        matrix.postTranslate(x, y);
                    } else {
                        float endDistance = distance(motionEvent);
                        if (endDistance > 10f) {
                            scale = endDistance / imageDistance;
                            matrix.set(currentMatrix);
                            PointF centerPoint = deriveCenter(matrix);
                            matrix.postScale(scale, scale, centerPoint.x, centerPoint.y);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    PointF centerPoint = deriveCenter(matrix);
                    params.leftMargin = (int)centerPoint.x;
                    params.topMargin = (int)centerPoint.y;
                    shiftPercentX = (int) ((centerPoint.x / 1280) * 100);
                    shiftPercentY = (int) ((centerPoint.y / 728) * 100);
                    System.out.println("x=" + shiftPercentX + "%and y=" + shiftPercentY + "%");
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    System.out.println("scale=" + scale);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    imageDistance = distance(motionEvent);

                    if (imageDistance > 10f) {
                        middlePoint = middle(motionEvent);
                        currentMatrix.set(image.getMatrix());
                    }
                    break;
            }
            image.setImageMatrix(matrix);
            image.invalidate();
            text.setLayoutParams(params);
            text.invalidate();
            return true;
        }
    };

    private float distance(MotionEvent event) {
        float dis_x = event.getX(1) - event.getX(0);
        float dis_y = event.getY(1) - event.getY(0);

        return (float) Math.sqrt(dis_x*dis_x + dis_y*dis_y);
    }

    private PointF middle(MotionEvent event) {
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);

        return new PointF(x/2, y/2);
    }

    private PointF deriveCenter(Matrix matrix) {
        PointF point = new PointF();
        float[] values = new float[9];
        matrix.getValues(values);
        int viewWidth = image.getDrawable().getBounds().width();
        int viewHeight = image.getDrawable().getBounds().height();
        point.set(values[Matrix.MSCALE_X] * ( viewWidth / 2 ) + values[Matrix.MTRANS_X], values[Matrix.MSCALE_Y] * ( viewHeight / 2) + values[Matrix.MTRANS_Y]);

        return point;
    }

}
