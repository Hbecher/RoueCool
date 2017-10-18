package fr.polytech.ricm5.mm.rouecool.wheel;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import fr.polytech.ricm5.mm.rouecool.R;
import fr.polytech.ricm5.mm.rouecool.util.Point;
import fr.polytech.ricm5.mm.rouecool.util.Vector;

public class Wheel extends View
{
	private final Set<WheelTickListener> tickListeners = new HashSet<>();
	private final Set<WheelClickListener> clickListeners = new HashSet<>();
	private final Paint circle, target, filled;
	private final Point pos = Point.origin(), prevPos = Point.origin(), initPos = Point.origin(), wheel = Point.origin();
	private final Vector v = new Vector(wheel, pos), prevV = new Vector(wheel, prevPos);
	private final float clickMargin = 8.0F;
	private float posCircleRadius, wheelRadius = 200.0F;
	private double wheelRotation = 0.0;
	private State state;
	private double nextTick;

	public Wheel(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Wheel, 0, 0);

		float circleThickness;

		try
		{
			posCircleRadius = a.getDimension(R.styleable.Wheel_circleRadius, 256.0F);
			circleThickness = a.getDimension(R.styleable.Wheel_circleThickness, 1.0F);
		}
		finally
		{
			a.recycle();
		}

		setState(State.IDLE);

		circle = new Paint(Paint.ANTI_ALIAS_FLAG);
		circle.setStyle(Paint.Style.STROKE);
		circle.setStrokeWidth(circleThickness);
		circle.setColor(Color.BLUE);

		target = new Paint(Paint.ANTI_ALIAS_FLAG);
		target.setStyle(Paint.Style.STROKE);
		target.setStrokeWidth(circleThickness);
		target.setColor(Color.RED);

		filled = new Paint(Paint.ANTI_ALIAS_FLAG);
		filled.setStyle(Paint.Style.FILL);
		filled.setColor(Color.RED);
	}

	private boolean isInWheel()
	{
		return isInWheel(pos.getX(), pos.getY());
	}

	private boolean isInWheel(double x, double y)
	{
		return wheel.distance(x, y) <= wheelRadius;
	}

	private double speed(double angle)
	{
		return Math.abs(angle) * 2.0 * Math.exp(2.0 * (wheelRadius - pos.distance(wheel)) / wheelRadius);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		float x = wheel.getXf(), y = wheel.getYf();

		canvas.drawCircle(x, y, wheelRadius, target);
		canvas.drawCircle(x, y, 20, filled);

		float wrf = (float) Math.toDegrees(wheelRotation);

		canvas.rotate(wrf, x, y);
		canvas.drawLine(x, y, x, y, filled);
		canvas.drawRect(x - 2, y - wheelRadius, x + 2, y, filled);
		canvas.rotate(-wrf, x, y);

		if(state == State.CLICK || state == State.ROLL || state == State.MOVE)
		{
			circle.setColor(state == State.CLICK ? Color.BLUE : Color.GREEN);
			canvas.drawCircle(pos.getXf(), pos.getYf(), posCircleRadius, circle);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		wheel.set(w - wheelRadius - 50, h - wheelRadius - 50);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch(state)
		{
			case IDLE:
			{
				if(event.getAction() == MotionEvent.ACTION_DOWN && isInWheel(event.getX(), event.getY()))
				{
					setState(State.CLICK);

					updatePosition(event.getX(), event.getY());
					initPos.set(pos);

					invalidate();

					return true;
				}

				break;
			}

			case CLICK:
			{
				switch(event.getAction())
				{
					case MotionEvent.ACTION_MOVE:
					{
						updatePosition(event.getX(), event.getY());

						if(initPos.distance(pos) > clickMargin)
						{
							setState(State.ROLL);
						}

						invalidate();

						return true;
					}

					case MotionEvent.ACTION_UP:
					{
						dispatchWheelClick();

						reset();

						return true;
					}
				}

				break;
			}

			case ROLL:
			{
				switch(event.getAction())
				{
					case MotionEvent.ACTION_MOVE:
					{
						updatePosition(event.getX(), event.getY());

						if(isInWheel())
						{
							double angle = v.angle(prevV);
							wheelRotation += angle;
							nextTick += speed(angle);
							int direction = Double.compare(angle, 0.0);

							if(nextTick >= 1.0)
							{
								dispatchWheelTick(direction, (int) nextTick);

								nextTick = 0.0;
							}
						}
						else
						{
							wheel.translate(pos.getX() - prevPos.getX(), pos.getY() - prevPos.getY());
							// setState(State.OUT);
						}

						invalidate();

						return true;
					}

					case MotionEvent.ACTION_UP:
					{
						reset();

						return true;
					}
				}

				break;
			}

			case MOVE:
			{
				switch(event.getAction())
				{
					case MotionEvent.ACTION_MOVE:
					{
						updatePosition(event.getX(), event.getY());

						wheel.translate(pos.getX() - prevPos.getX(), pos.getY() - prevPos.getY());

						if(isInWheel())
						{
							setState(State.ROLL);
						}

						invalidate();

						return true;
					}

					case MotionEvent.ACTION_UP:
					{
						reset();

						return true;
					}
				}

				break;
			}

			case OUT:
			{
				switch(event.getAction())
				{
					case MotionEvent.ACTION_MOVE:
					{
						updatePosition(event.getX(), event.getY());

						if(isInWheel())
						{
							setState(State.ROLL);
						}

						invalidate();

						return true;
					}

					case MotionEvent.ACTION_UP:
					{
						reset();

						return true;
					}
				}

				break;
			}
		}

		return false;
	}

	private void updatePosition(float x, float y)
	{
		prevPos.set(pos.getX(), pos.getY());
		pos.set(x, y);
	}

	private void reset()
	{
		setState(State.IDLE);

		prevPos.set(0.0, 0.0);
		pos.set(0.0, 0.0);
		initPos.set(0.0, 0.0);

		nextTick = 0.0;

		wheel.set(getWidth() - wheelRadius - 50, getHeight() - wheelRadius - 50);

		invalidate();
	}

	private void setState(State state)
	{
		this.state = state;

		Log.d("Wheel State", state.name());
	}

	public void addWheelTickListener(WheelTickListener l)
	{
		tickListeners.add(l);
	}

	public void removeWheelTickListener(WheelTickListener l)
	{
		tickListeners.remove(l);
	}

	public void addWheelClickListener(WheelClickListener l)
	{
		clickListeners.add(l);
	}

	public void removeWheelClickListener(WheelClickListener l)
	{
		clickListeners.remove(l);
	}

	private void dispatchWheelTick(int direction, int amount)
	{
		WheelTickEvent e = new WheelTickEvent(direction, amount);

		for(WheelTickListener l : tickListeners)
		{
			l.onWheelTick(e);
		}
	}

	private void dispatchWheelClick()
	{
		for(WheelClickListener l : clickListeners)
		{
			l.onWheelClick();
		}
	}
}
