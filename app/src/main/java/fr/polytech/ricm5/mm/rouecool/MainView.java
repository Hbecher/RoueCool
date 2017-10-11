package fr.polytech.ricm5.mm.rouecool;

import static android.content.Context.AUDIO_SERVICE;

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

public class MainView extends View
{
	private final SoundPool sounds;
	private final int pap;
	private final Paint circle, target, filled, text, pen;
	private final Point pos = Point.origin(), prevPos = Point.origin(), wheel = Point.origin();
	private final Vector v = new Vector(wheel, pos), prevV = new Vector(wheel, prevPos);
	private final Rectangle r = new Rectangle(8.0F, 280.0F, 128.0F, 384.0F), sel = new Rectangle(r.getX(), r.getY(), r.getWidth(), 16.0F);
	private float posCircleRadius, wheelRadius = 200.0F;
	private double angle;
	private double speed;
	private int direction;
	private boolean touching, rolling, scrolling;
	private boolean papLoaded;

	public MainView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MainView, 0, 0);

		float circleThickness;

		try
		{
			posCircleRadius = a.getDimension(R.styleable.MainView_circleRadius, 256.0F);
			circleThickness = a.getDimension(R.styleable.MainView_circleThickness, 1.0F);
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

		text = new Paint(Paint.SUBPIXEL_TEXT_FLAG);
		text.setStyle(Paint.Style.STROKE);
		text.setColor(Color.BLACK);
		text.setTextSize(32.0F);

		pen = new Paint(Paint.ANTI_ALIAS_FLAG);
		pen.setStyle(Paint.Style.STROKE);
		pen.setStrokeWidth(2.0F);
		pen.setColor(Color.BLACK);
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

	private float mod(float f, float m)
	{
		float mod = f % m;

		return mod < 0.0F ? mod + m : mod;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		drawTarget(canvas);

		if(touching)
		{
			canvas.drawCircle(pos.getXf(), pos.getYf(), posCircleRadius, circle);
		}

		canvas.drawText(String.format("pos = %s", pos), 8.0F, 40.0F, text);
		canvas.drawText(String.format("roll = %s", rolling), 8.0F, 80.0F, text);
		canvas.drawText(String.format("scroll = %s", scrolling), 8.0F, 120.0F, text);
		canvas.drawText(String.format("angle = %s", angle), 8.0F, 160.0F, text);
		canvas.drawText(String.format("speed = %s", speed), 8.0F, 200.0F, text);
		canvas.drawText(String.format("direction = %s", direction), 8.0F, 240.0F, text);
		canvas.drawRect(r.rect(), pen);
		canvas.drawRect(sel.rect(), pen);
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
		super.onTouchEvent(event);

		switch(event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				touching = true;

				pos.set(event.getX(), event.getY());

				if(isInWheel(event.getX(), event.getY()))
				{
					rolling = true;

					invalidate();
				}

				break;
			}

			case MotionEvent.ACTION_MOVE:
			{
				prevPos.set(pos.getX(), pos.getY());
				pos.set(event.getX(), event.getY());

				if(rolling)
				{
					angle = v.angle(prevV);
					scrolling = isInWheel(event.getX(), event.getY());

					if(scrolling)
					{
						speed += Math.abs(angle) * 2.0D;
						direction = Double.compare(angle, 0.0);

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

							sel.setY(mod((sel.getY() - r.getY() + (int) (speed) * direction * sel.getHeight()), r.getHeight()) + r.getY());

							speed = 0.0;
						}
					}
				}

				invalidate();

				break;
			}

			case MotionEvent.ACTION_UP:
			{
				touching = false;

				if(rolling)
				{
					rolling = false;
					scrolling = false;

					prevPos.set(0.0, 0.0);
					pos.set(0.0, 0.0);

					angle = 0.0;
					speed = 0.0;
					direction = 0;
				}

				invalidate();

				break;
			}

			default:
			{
				return false;
			}
		}

		return true;
	}
}
