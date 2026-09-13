package com.example.myhabits;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class ProgressRing extends View {

    private final Tracker tracker;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF circle = new RectF();
    private final ThemeColors colors;

    private float buttonRadius;
    private float centerX;
    private float centerY;
    private OnAddClickListener listener;

    public interface OnAddClickListener {
        void onAddClick();
    }

    public ProgressRing(Context context, Tracker tracker) {
        super(context);
        this.tracker = tracker;
        this.colors = new ThemeColors(context);
        setClickable(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setOnAddClickListener(OnAddClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        centerX = getWidth() / 2f;
        centerY = getHeight() / 2f;
        float size = Math.min(getWidth(), getHeight());
        float radius = size * 0.39f;
        buttonRadius = size * 0.22f;

        circle.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(18f, size * 0.075f));
        paint.setColor(colors.border);
        canvas.drawArc(circle, -90, 360, false, paint);

        if (tracker.target > 0) {
            float completed = Math.min(tracker.current, tracker.target);
            float angle = completed / (float) tracker.target * 360f;
            paint.setColor(tracker.ringColor);
            canvas.drawArc(circle, -90, angle, false, paint);

            if (tracker.current > tracker.target) {
                float excess = tracker.current - tracker.target;
                float excessAngle = Math.min(360f, excess / (float) tracker.target * 360f);
                paint.setColor(colors.limit);
                canvas.drawArc(circle, -90, excessAngle, false, paint);
            }
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colors.primary);
        canvas.drawCircle(centerX, centerY, buttonRadius, paint);

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(7f);
        canvas.drawLine(centerX - buttonRadius * 0.35f, centerY,
                centerX + buttonRadius * 0.35f, centerY, paint);
        canvas.drawLine(centerX, centerY - buttonRadius * 0.35f,
                centerX, centerY + buttonRadius * 0.35f, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float dx = event.getX() - centerX;
            float dy = event.getY() - centerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance <= buttonRadius) {
                if (listener != null) {
                    listener.onAddClick();
                }
                performClick();
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
