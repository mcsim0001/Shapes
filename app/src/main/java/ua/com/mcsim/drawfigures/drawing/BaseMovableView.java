package ua.com.mcsim.drawfigures.drawing;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public interface BaseMovableView {

    public void setImageMatrix(Matrix matrix);

    public Matrix getImageMatrix();

    public Bitmap getBitmap();

}
