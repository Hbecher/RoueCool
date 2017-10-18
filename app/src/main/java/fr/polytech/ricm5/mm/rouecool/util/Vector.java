package fr.polytech.ricm5.mm.rouecool.util;

public class Vector
{
	private static final double TWO_PI = 6.283185307179586;
	private final Point origin, point;

	public Vector(Point p)
	{
		this(new Point(0.0, 0.0), p);
	}

	public Vector(Point origin, Point point)
	{
		if(origin == null || point == null)
		{
			throw new NullPointerException();
		}

		this.origin = origin;
		this.point = point;
	}

	public Point getOrigin()
	{
		return origin;
	}

	public Point getPoint()
	{
		return point;
	}

	public double angle()
	{
		return Math.atan2(origin.getY() - point.getY(), point.getX() - origin.getX());
	}

	public double angle(Vector v)
	{
		if(v == null)
		{
			throw new NullPointerException();
		}

		double d = v.angle() - angle();

		if(d < -Math.PI)
		{
			do
			{
				d += TWO_PI;
			}
			while(d < -Math.PI);
		}
		else if(d > Math.PI)
		{
			do
			{
				d -= TWO_PI;
			}
			while(d > Math.PI);
		}

		return d;
	}

	public void scale(double scale)
	{
		throw new UnsupportedOperationException("NIY");
	}

	public void add(Vector v)
	{
		throw new UnsupportedOperationException("NIY");
	}

	public void dot(Vector v)
	{
		throw new UnsupportedOperationException("NIY");
	}

	public void rotate(double d)
	{
		throw new UnsupportedOperationException("NIY");
	}

	public void translate(double x, double y)
	{
		throw new UnsupportedOperationException("NIY");
	}

	public Vector copy()
	{
		return new Vector(origin.copy(), point.copy());
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}

		if(!(o instanceof Vector))
		{
			return false;
		}

		Vector that = (Vector) o;

		return origin.equals(that.origin) && point.equals(that.point);
	}

	@Override
	public int hashCode()
	{
		int result = origin.hashCode();
		result = 31 * result + point.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return String.format("[%s -> %s]", origin, point);
	}
}
