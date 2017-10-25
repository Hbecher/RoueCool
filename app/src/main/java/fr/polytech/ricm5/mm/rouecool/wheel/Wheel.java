package fr.polytech.ricm5.mm.rouecool.wheel;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import fr.polytech.ricm5.mm.rouecool.R;
import fr.polytech.ricm5.mm.rouecool.res.images.ScaledBitmap;
import fr.polytech.ricm5.mm.rouecool.util.Point;
import fr.polytech.ricm5.mm.rouecool.util.Vector;

public class Wheel extends View
{
	private final Set<WheelTickListener> tickListeners = new HashSet<>();
	private final Set<WheelClickListener> clickListeners = new HashSet<>();
	private final Point pos = Point.origin(), prevPos = Point.origin(), initPos = Point.origin(), center = Point.origin();
	private final Vector v = new Vector(center, pos), prevV = new Vector(center, prevPos);
	private final float wheelRadius, wheelMargin, clickPlay, touchRadius, touchThickness;
	private final boolean drawWheelImage, drawOutline, drawTouchPos, snapBack;
	private final Paint touchCircle, wheelCircle, wheelOutline;
	private final ScaledBitmap staticWheel, rotatingWheel;
	private double wheelRotation;
	private State state;
	private double nextTick;

	public Wheel(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Wheel, 0, 0);

		try
		{
			wheelRadius = a.getDimension(R.styleable.Wheel_radius, 200.0F);
			wheelMargin = a.getDimension(R.styleable.Wheel_margin, 40.0F);
			clickPlay = a.getDimension(R.styleable.Wheel_clickPlay, 20.0F);
			touchRadius = a.getDimension(R.styleable.Wheel_touchRadius, 40.0F);
			touchThickness = a.getDimension(R.styleable.Wheel_touchThickness, 4.0F);
			drawWheelImage = a.getBoolean(R.styleable.Wheel_drawImage, true);
			drawOutline = a.getBoolean(R.styleable.Wheel_drawOutline, false);
			drawTouchPos = a.getBoolean(R.styleable.Wheel_drawTouch, false);
			snapBack = a.getBoolean(R.styleable.Wheel_snapBack, false);
		}
		finally
		{
			a.recycle();
		}

		setState(State.IDLE);

		touchCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		touchCircle.setStyle(Paint.Style.STROKE);
		touchCircle.setStrokeWidth(touchThickness);
		touchCircle.setColor(Color.BLUE);

		wheelCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		wheelCircle.setStyle(Paint.Style.STROKE);
		wheelCircle.setStrokeWidth(touchThickness);
		wheelCircle.setColor(Color.RED);

		wheelOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
		wheelOutline.setStyle(Paint.Style.STROKE);
		wheelOutline.setStrokeWidth(1.0F);
		wheelOutline.setColor(Color.GRAY & 0x3f << 24);

		staticWheel = new ScaledBitmap(context.getResources(), R.raw.wheel);
		staticWheel.setScale((2.0F * wheelRadius) / Math.max(staticWheel.getWidth(), staticWheel.getHeight()));
		rotatingWheel = new ScaledBitmap(staticWheel.getImage());
		rotatingWheel.setScale((2.0F * wheelRadius) / Math.max(rotatingWheel.getWidth(), rotatingWheel.getHeight()));
	}

	private boolean isInWheel()
	{
		return isInWheel(pos.getX(), pos.getY());
	}

	private boolean isInWheel(double x, double y)
	{
		return center.distance(x, y) <= wheelRadius;
	}

	private double speed(double angle)
	{
		return Math.abs(angle) * 2.0 * Math.exp(2.0 * (1.0 - pos.distance(center) / wheelRadius));
	}

	private void drawWheel(Canvas canvas)
	{
		float rotation = (float) Math.toDegrees(wheelRotation);
		float x = center.getXf(), y = center.getYf();

		if(drawWheelImage)
		{
			ScaledBitmap img = state == State.ROLL ? rotatingWheel : staticWheel;
			float dx = img.getScaledWidth() / 2.0F, dy = img.getScaledHeight() / 2.0F;

			canvas.translate(x, y);
			canvas.rotate(rotation);
			canvas.translate(-dx, -dy);

			canvas.drawBitmap(img.getImage(), img.getScaleMatrix(), wheelCircle);

			canvas.translate(dx, dy);
			canvas.rotate(-rotation);
			canvas.translate(-x, -y);
		}
		else
		{
			wheelCircle.setStyle(Paint.Style.STROKE);

			canvas.drawCircle(x, y, wheelRadius, wheelCircle);

			wheelCircle.setStyle(Paint.Style.FILL);

			canvas.drawCircle(x, y, wheelRadius / 10.0F, wheelCircle);

			canvas.rotate(rotation, x, y);

			canvas.drawLine(x, y, x, y, wheelCircle);
			canvas.drawRect(x - wheelRadius / 100.0F, y - wheelRadius, x + wheelRadius / 100.0F, y, wheelCircle);

			canvas.rotate(-rotation, x, y);
		}

		if(drawOutline)
		{
			wheelOutline.setStyle(Paint.Style.STROKE);

			canvas.drawCircle(x, y, wheelRadius, wheelOutline);

			wheelOutline.setStyle(Paint.Style.FILL);

			canvas.drawCircle(x, y, wheelRadius / 10.0F, wheelOutline);
		}
	}

	private void drawTouchPos(Canvas canvas)
	{
		if(drawTouchPos && (state == State.CLICK || state == State.ROLL || state == State.MOVE))
		{
			touchCircle.setColor(state == State.CLICK ? Color.BLUE : Color.GREEN);
			canvas.drawCircle(pos.getXf(), pos.getYf(), touchRadius, touchCircle);
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		drawWheel(canvas);
		drawTouchPos(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		center.set(w - wheelRadius - wheelMargin, h - wheelRadius - wheelMargin);
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

						if(initPos.distance(pos) > clickPlay)
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
							center.translate(pos.getX() - prevPos.getX(), pos.getY() - prevPos.getY());
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

						center.translate(pos.getX() - prevPos.getX(), pos.getY() - prevPos.getY());

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

		if(snapBack)
		{
			center.set(getWidth() - wheelRadius - wheelMargin, getHeight() - wheelRadius - wheelMargin);
		}

		invalidate();
	}

	private void setState(State state)
	{
		this.state = state;
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
