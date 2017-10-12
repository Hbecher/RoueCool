package fr.polytech.ricm5.mm.rouecool;

import static android.content.Context.AUDIO_SERVICE;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Wheel extends View
{
	private final Set<WheelTickListener> listeners = new HashSet<>();
	private final SoundPool sounds;
	private final int pap;
	private final Paint circle;
	private final Paint target;
	private final Paint filled;
	private final Point pos = Point.origin(), prevPos = Point.origin(), wheel = Point.origin();
	private final Vector v = new Vector(wheel, pos), prevV = new Vector(wheel, prevPos);
	private float posCircleRadius, wheelRadius = 200.0F;
	private double speed;
	private boolean rolling;
	private boolean papLoaded;

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

		if(isInEditMode())
		{
			sounds = null;
			pap = -1;
			papLoaded = false;
		}
		else
		{
			sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
			sounds.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
			{
				@Override
				public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
				{
					papLoaded = true;
				}
			});
			pap = sounds.load(context, R.raw.pap, 1);
		}

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

	private void drawTarget(Canvas canvas)
	{
		float x = wheel.getXf(), y = wheel.getYf();

		canvas.drawCircle(x, y, wheelRadius, target);
		canvas.drawCircle(x, y, 20, filled);
	}

	private boolean isInWheel(double x, double y)
	{
		return wheel.distance(x, y) <= wheelRadius;
	}

	private int mod(int f, int m)
	{
		int mod = f % m;

		return mod < 0 ? mod + m : mod;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		drawTarget(canvas);

		if(rolling)
		{
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
		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				if(isInWheel(event.getX(), event.getY()))
				{
					rolling = true;

					pos.set(event.getX(), event.getY());

					invalidate();

					return true;
				}

				break;
			}

			case MotionEvent.ACTION_MOVE:
			{
				if(rolling)
				{
					prevPos.set(pos.getX(), pos.getY());
					pos.set(event.getX(), event.getY());

					double angle = v.angle(prevV);
					boolean scrolling = isInWheel(event.getX(), event.getY());

					if(scrolling)
					{
						speed += Math.abs(angle) * 2.0 * (1.0 + 5.0 * (wheelRadius - pos.distance(wheel)) / wheelRadius);
						int direction = Double.compare(angle, 0.0);

						if(speed >= 1.0)
						{
							if(papLoaded)
							{
								AudioManager audioManager = (AudioManager) getContext().getSystemService(AUDIO_SERVICE);
								float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
								float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
								float volume = actualVolume / maxVolume;
								sounds.play(pap, volume, volume, 1, 0, 1.0F);
							}

							dispatchWheelTick(direction);

							speed = 0.0;
						}
					}

					invalidate();

					return true;
				}

				break;
			}

			case MotionEvent.ACTION_UP:
			{
				if(rolling)
				{
					rolling = false;

					prevPos.set(0.0, 0.0);
					pos.set(0.0, 0.0);

					speed = 0.0;

					invalidate();

					return true;
				}

				break;
			}
		}

		return false;
	}

	public void addWheelTickListener(WheelTickListener l)
	{
		listeners.add(l);
	}

	public void removeWheelTickListener(WheelTickListener l)
	{
		listeners.remove(l);
	}

	private void dispatchWheelTick(int direction)
	{
		WheelTickEvent e = new WheelTickEvent(direction);

		for(WheelTickListener l : listeners)
		{
			l.onWheelTick(e);
		}
	}

	public interface WheelTickListener
	{
		void onWheelTick(WheelTickEvent e);
	}

	public static class WheelTickEvent
	{
		public static final int UP = -1, DOWN = 1;
		private final int direction;

		private WheelTickEvent(int direction)
		{
			this.direction = direction;
		}

		public int getDirection()
		{
			return direction;
		}
	}
}
