package fr.polytech.ricm5.mm.rouecool;

class Vector
{
	private static final double TWO_PI = 6.283185307179586;
	private Point origin, point;

	Vector(Point p)
	{
		this(new Point(0.0, 0.0), p);
	}

	Vector(Point origin, Point point)
	{
		setOrigin(origin);
		setPoint(point);
	}

	Point getOrigin()
	{
		return origin;
	}

	void setOrigin(Point origin)
	{
		if(origin == null)
		{
			throw new NullPointerException();
		}

		this.origin = origin;
	}

	Point getPoint()
	{
		return point;
	}

	void setPoint(Point point)
	{
		if(point == null)
		{
			throw new NullPointerException();
		}

		this.point = point;
	}

	double angle()
	{
		return Math.atan2(origin.getY() - point.getY(), point.getX() - origin.getX());
	}

	double angle(Vector v)
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

	void scale(double scale)
	{
		throw new UnsupportedOperationException("NIY");
	}

	void add(Vector v)
	{
		throw new UnsupportedOperationException("NIY");
	}

	void dot(Vector v)
	{
		throw new UnsupportedOperationException("NIY");
	}

	void rotate(double d)
	{
		throw new UnsupportedOperationException("NIY");
	}

	Vector copy()
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
