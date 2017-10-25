package fr.polytech.ricm5.mm.rouecool.res.images;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.RawRes;

public class ScaledBitmap
{
	private final Bitmap image;
	private final Matrix matrix = new Matrix();
	private float scale, scaledWidth, scaledHeight, centerX, centerY;

	public ScaledBitmap(Resources resources, @RawRes int id)
	{
		this(BitmapFactory.decodeResource(resources, id));
	}

	public ScaledBitmap(Resources resources, @RawRes int id, float scale)
	{
		this(BitmapFactory.decodeResource(resources, id), scale);
	}

	public ScaledBitmap(Bitmap image)
	{
		this(image, 1.0F);
	}

	public ScaledBitmap(Bitmap image, float scale)
	{
		if(image == null)
		{
			throw new NullPointerException();
		}

		this.image = image;
		setScale(scale);
	}

	public Bitmap getImage()
	{
		return image;
	}

	public Matrix getScaleMatrix()
	{
		return matrix;
	}

	public float getScale()
	{
		return scale;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
		scaledWidth = image.getWidth() * scale;
		scaledHeight = image.getHeight() * scale;
		centerX = scaledWidth / 2.0F;
		centerY = scaledHeight / 2.0F;

		matrix.reset();
		matrix.preScale(scale, scale);
	}

	public float getScaledWidth()
	{
		return scaledWidth;
	}

	public float getScaledHeight()
	{
		return scaledHeight;
	}

	public int getWidth()
	{
		return image.getWidth();
	}

	public int getHeight()
	{
		return image.getHeight();
	}

	public float getCenterX()
	{
		return centerX;
	}

	public float getCenterY()
	{
		return centerY;
	}
}
