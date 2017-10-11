package fr.polytech.ricm5.mm.rouecool;

import android.graphics.RectF;

class Rectangle
{
	private float width, height;
	private final RectF rect;

	Rectangle()
	{
		this(0.0F, 0.0F, 0.0F, 0.0F);
	}

	Rectangle(float x, float y, float width, float height)
	{
		this.width = width;
		this.height = height;
		rect = new RectF(x, y, x + width, y + height);
	}

	RectF rect()
	{
		return rect;
	}

	void setX(float x)
	{
		rect.left = x;
		rect.right = x + width;
	}

	float getX()
	{
		return rect.left;
	}

	void setY(float y)
	{
		rect.top = y;
		rect.bottom = y + height;
	}

	float getY()
	{
		return rect.top;
	}

	void setWidth(float width)
	{
		this.width = width;
		rect.right = rect.left + width;
	}

	float getWidth()
	{
		return width;
	}

	void setHeight(float height)
	{
		this.height = height;
		rect.bottom = rect.top + height;
	}

	float getHeight()
	{
		return height;
	}
}
