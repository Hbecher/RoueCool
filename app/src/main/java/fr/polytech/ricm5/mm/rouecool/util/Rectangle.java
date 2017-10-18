package fr.polytech.ricm5.mm.rouecool.util;

import android.graphics.RectF;

public class Rectangle
{
	private float width, height;
	private final RectF rect;

	public Rectangle()
	{
		this(0.0F, 0.0F, 0.0F, 0.0F);
	}

	public Rectangle(float x, float y, float width, float height)
	{
		this.width = width;
		this.height = height;
		rect = new RectF(x, y, x + width, y + height);
	}

	public RectF rect()
	{
		return rect;
	}

	public void setX(float x)
	{
		rect.left = x;
		rect.right = x + width;
	}

	public float getX()
	{
		return rect.left;
	}

	public void setY(float y)
	{
		rect.top = y;
		rect.bottom = y + height;
	}

	public float getY()
	{
		return rect.top;
	}

	public void setWidth(float width)
	{
		this.width = width;
		rect.right = rect.left + width;
	}

	public float getWidth()
	{
		return width;
	}

	public void setHeight(float height)
	{
		this.height = height;
		rect.bottom = rect.top + height;
	}

	public float getHeight()
	{
		return height;
	}
}
