package fr.polytech.ricm5.mm.rouecool.util;

public class Point
{
	private double x, y;

	public Point(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public static Point origin()
	{
		return new Point(0.0, 0.0);
	}

	public double getX()
	{
		return x;
	}

	public float getXf()
	{
		return (float) x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public float getYf()
	{
		return (float) y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public void set(double x, double y)
	{
		setX(x);
		setY(y);
	}

	public void set(Point p)
	{
		set(p.x, p.y);
	}

	public void translate(double dx, double dy)
	{
		set(x + dx, y + dy);
	}

	public double distance(Point p)
	{
		return distance(p.x, p.y);
	}

	public double distance(double x, double y)
	{
		return Math.sqrt(Math.pow(x - this.x, 2.0) + Math.pow(y - this.y, 2.0));
	}

	public Vector createVector(Point p)
	{
		return new Vector(this, p);
	}

	public Point copy()
	{
		return new Point(x, y);
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}

		if(!(o instanceof Point))
		{
			return false;
		}

		Point that = (Point) o;

		return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0;
	}

	@Override
	public int hashCode()
	{
		int result;
		long temp;

		temp = Double.doubleToLongBits(x);
		result = (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = 31 * result + (int) (temp ^ (temp >>> 32));

		return result;
	}

	@Override
	public String toString()
	{
		return String.format("(%s, %s)", x, y);
	}
}
