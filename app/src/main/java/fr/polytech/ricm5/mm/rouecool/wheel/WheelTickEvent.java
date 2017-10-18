package fr.polytech.ricm5.mm.rouecool.wheel;

public class WheelTickEvent
{
	private final int direction;
	private final int amount;

	WheelTickEvent(int direction, int amount)
	{
		this.direction = direction;
		this.amount = amount;
	}

	public int getDirection()
	{
		return direction;
	}

	public int getAmount()
	{
		return amount;
	}
}
