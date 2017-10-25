package fr.polytech.ricm5.mm.rouecool.wheel;

public class WheelRollEvent
{
	private final double angle;

	WheelRollEvent(double angle)
	{
		this.angle = angle;
	}

	public double getAngle()
	{
		return angle;
	}
}
