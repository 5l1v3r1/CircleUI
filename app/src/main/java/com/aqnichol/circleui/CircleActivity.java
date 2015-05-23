package com.aqnichol.circleui;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A CircleActivity allows navigation through menus in a cool, circular way.
 */
public class CircleActivity extends Activity {

    private CirclesView circlesView;
    private OnChooseListener onChooseListener;
    private Map<String, CircleFragment> uniqueIdToFragment;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        circlesView = new CirclesView(this);
        setContentView(circlesView, params);
    }

    public void showMenu(CircleFragment f) {
        findFragments(f);
        circlesView.goToNextPage(f);
    }

    public OnChooseListener getOnChooseListener() {
        return onChooseListener;
    }

    public void setOnChooseListener(OnChooseListener onChooseListener) {
        this.onChooseListener = onChooseListener;
    }

    private void findFragments(CircleFragment f) {
        uniqueIdToFragment = new HashMap<>();
        LinkedList<CircleFragment> queue = new LinkedList<>();
        queue.push(f);
        while (queue.size() > 0) {
            CircleFragment node = queue.getFirst();
            queue.removeFirst();
            if (uniqueIdToFragment.containsKey(node.getUniqueName())) {
                continue;
            }
            uniqueIdToFragment.put(node.getUniqueName(), node);
            if (!node.isMenu()) {
                continue;
            }
            for (int i = 0; i < node.getMenuOptions().length; ++i) {
                queue.push(node.getMenuOptions()[i]);
            }
        }
    }

    public interface OnChooseListener {
        void onChoose(CircleActivity activity, CircleFragment fragment);
    }

    private static class CircleLayout {

        public CircleFragment circle;
        public float radius;
        public float angle;
        public float alpha;

        public CircleLayout(CircleFragment circle, float radius, float angle, float alpha) {
            this.circle = circle;
            this.radius = radius;
            this.angle = angle;
            this.alpha = alpha;
        }

        public CircleLayout alpha(float newAlpha) {
            return new CircleLayout(circle, radius, angle, newAlpha);
        }

        public CircleLayout radius(float newRadius) {
            return new CircleLayout(circle, newRadius, angle, alpha);
        }

        public CircleLayout rotate(float newAngle) {
            return new CircleLayout(circle, radius, newAngle, alpha);
        }

    }

    private static class PageLayout {

        private CircleLayout[] circleLayouts;

        public PageLayout(CircleFragment root) {
            if (BuildConfig.DEBUG && !root.isMenu()) {
                throw new AssertionError();
            }
            CircleFragment[] options = root.getMenuOptions();
            int count = 1 + options.length;
            circleLayouts = new CircleLayout[count];
            circleLayouts[0] = new CircleLayout(root, 0, 0, 1);
            for (int i = 1; i < count; ++i) {
                float angle = 2 * (float)Math.PI * (float)(i - 1) / (float)(count - 1);
                circleLayouts[i] = new CircleLayout(options[i - 1], 1, angle, 1);
            }
        }

        public PageLayout(CircleLayout[] layouts) {
            circleLayouts = layouts;
        }

        public int getCircleCount() {
            return circleLayouts.length;
        }

        public CircleLayout[] getCircleLayouts() {
            return circleLayouts;
        }

        public CircleLayout getMain() {
            return circleLayouts[0];
        }

        public static PageLayout intermediate(PageLayout start, PageLayout end, float progress) {
            if (BuildConfig.DEBUG && start.circleLayouts.length != end.circleLayouts.length) {
                throw new AssertionError();
            }
            CircleLayout[] layouts = new CircleLayout[start.circleLayouts.length];
            for (int i = 0; i < start.circleLayouts.length; ++i) {
                CircleLayout l1 = start.circleLayouts[i];
                CircleLayout l2 = end.circleLayouts[i];
                if (BuildConfig.DEBUG && l1.circle != l2.circle) {
                    throw new AssertionError();
                }
                layouts[i] = new CircleLayout(l1.circle, intermediateFloat(l1.radius, l2.radius,
                        progress), intermediateFloat(l1.angle, l2.angle, progress),
                        intermediateFloat(l1.alpha, l2.alpha, progress));
            }
            return new PageLayout(layouts);
        }

        private static float intermediateFloat(float f1, float f2, float progress) {
            return f1 + (f2 - f1) * progress;
        }

    }

    private interface ChangeablePage {

        boolean isDoneChanging();
        PageLayout currentFrame();

    }

    private static class UnchangingChangeableLayouts implements ChangeablePage {

        private PageLayout layout;

        public UnchangingChangeableLayouts(CircleFragment menu) {
            layout = new PageLayout(menu);
        }

        @Override
        public boolean isDoneChanging() {
            return true;
        }

        @Override
        public PageLayout currentFrame() {
            return layout;
        }

    }

    abstract private static class Transition implements ChangeablePage {

        private long start;

        public Transition() {
            start = SystemClock.elapsedRealtime();
        }

        protected float getSecondsElapsed() {
            return (float)(SystemClock.elapsedRealtime() - start) / 1000.0f;
        }

    }

    private static class SpinTransition extends Transition {

        private final float PHASE1_DURATION = 0.3f;
        private final float PHASE2_DURATION = 0.3f;
        private final float ANGLE_FACTOR = 0.2f;
        private final float ANGLE_OFFSET = 0.3f;
        private final float FORWARD_ANGLE_OFFSET = 0.7f;

        public PageLayout phase1Start;
        public PageLayout phase1End;
        public PageLayout phase2Start;
        public PageLayout phase2End;

        public SpinTransition(CircleFragment start, CircleFragment end, boolean forwards) {
            phase1Start = new PageLayout(start);
            CircleLayout[] endLayouts = new CircleLayout[phase1Start.getCircleCount()];
            for (int i = 0; i < endLayouts.length; ++i) {
                CircleLayout x = phase1Start.getCircleLayouts()[i];
                if (x.circle == end) {
                    endLayouts[i] = x.radius(0);
                } else {
                    if (forwards) {
                        endLayouts[i] = x.rotate(x.angle + FORWARD_ANGLE_OFFSET)
                                .alpha(0);
                    } else {
                        endLayouts[i] = x.rotate(x.angle * (1.0f - ANGLE_FACTOR) - ANGLE_OFFSET)
                                .alpha(0);
                    }
                }
            }
            phase1End = new PageLayout(endLayouts);

            phase2End = new PageLayout(end);
            CircleLayout[] startLayouts = new CircleLayout[phase2End.getCircleCount()];
            for (int i = 0; i < startLayouts.length; ++i) {
                CircleLayout x = phase2End.getCircleLayouts()[i];
                if (x.circle == end) {
                    startLayouts[i] = x;
                } else {
                    if (forwards) {
                        startLayouts[i] = x.rotate(x.angle * (1.0f - ANGLE_FACTOR) - ANGLE_OFFSET)
                                .alpha(0);
                    } else {
                        startLayouts[i] = x.rotate(x.angle * (1.0f + ANGLE_FACTOR) + ANGLE_OFFSET)
                                .alpha(0);
                    }
                }
            }
            phase2Start = new PageLayout(startLayouts);
        }

        public boolean isDoneChanging() {
            return getSecondsElapsed() >= PHASE1_DURATION + PHASE2_DURATION;
        }

        public PageLayout currentFrame() {
            float progress = getSecondsElapsed();

            if (progress >= PHASE1_DURATION + PHASE2_DURATION) {
                return phase2End;
            } else if (progress <= 0) {
                return phase1Start;
            }

            if (progress <= PHASE1_DURATION) {
                return PageLayout.intermediate(phase1Start, phase1End, progress /
                        PHASE1_DURATION);
            } else {
                return PageLayout.intermediate(phase2Start, phase2End, (progress -
                        PHASE1_DURATION) / PHASE2_DURATION);
            }
        }

    }

    private class CirclesView extends View implements View.OnTouchListener,
            FragmentManager.OnBackStackChangedListener {

        private final float MARGIN = 10.0f;
        private final float RELATIVE_CIRCLE_RADIUS = 1.0f/8.0f;
        private final float RELATIVE_PRESS_INSET = 1.0f/100.0f;

        private ChangeablePage page = null;
        private ArrayList<ChangeablePage> pending = new ArrayList<>();
        private Rect reuseRect = new Rect();
        private Paint backgroundPaint = new Paint();

        private CircleFragment highlighted = null;
        private CircleFragment root = null;
        private CircleFragment currentFragment = null;

        public CirclesView(Context context) {
            super(context);
            backgroundPaint.setColor(Color.BLACK);
            this.setOnTouchListener(this);
            getFragmentManager().addOnBackStackChangedListener(this);
        }

        public void goToNextPage(CircleFragment f) {
            if (currentFragment == null) {
                push(new UnchangingChangeableLayouts(f));
                currentFragment = f;
                root = f;
            } else {
                push(new SpinTransition(currentFragment, f, true));
                currentFragment = f;
                getFragmentManager().beginTransaction()
                        .add(f, null)
                        .addToBackStack(f.getUniqueName())
                        .commit();
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (page == null) {
                return;
            }

            canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

            boolean isDone = page.isDoneChanging();
            PageLayout pageLayout = page.currentFrame();
            Rect bounds = this.getContentBounds();

            float radius = (float)bounds.width() * RELATIVE_CIRCLE_RADIUS;
            float coordinateScale = ((float)bounds.width() / 2) - radius;
            int diameter = Math.round(radius * 2);

            for (CircleLayout layout : pageLayout.getCircleLayouts()) {
                float x = bounds.exactCenterX() + ((float)Math.cos(layout.angle) *
                        coordinateScale * layout.radius);
                float y = bounds.exactCenterY() + ((float)Math.sin(layout.angle) *
                        coordinateScale * layout.radius);
                int boundsX = Math.round(x - radius);
                int boundsY = Math.round(y - radius);
                reuseRect.set(boundsX, boundsY, boundsX + diameter, boundsY + diameter);
                if (layout.circle == highlighted) {
                    int inset = Math.round((float) bounds.width() * RELATIVE_PRESS_INSET);
                    reuseRect.inset(inset, inset);
                }
                layout.circle.draw(canvas, reuseRect, layout.alpha);
            }

            if (!isDone) {
                postInvalidateOnAnimation();
            } else if (pending.size() > 0) {
                ChangeablePage t = pending.get(0);
                pending.remove(0);
                this.push(t);
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            if (pending.size() > 0 || !page.isDoneChanging()) {
                return true;
            }
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                highlighted = circleForPoint((int)e.getX(), (int)e.getY());
                invalidate();
            } else if (e.getAction() == MotionEvent.ACTION_UP) {
                highlighted = null;
                invalidate();
                CircleFragment f = circleForPoint((int)e.getX(), (int)e.getY());
                if (f != null) {
                    if (f.isMenu()) {
                        goToNextPage(f);
                    } else if (getOnChooseListener() != null) {
                        getOnChooseListener().onChoose(CircleActivity.this, f);
                    }
                }
            }
            return true;
        }

        @Override
        public void onBackStackChanged() {
            CircleFragment f = root;
            int count = getFragmentManager().getBackStackEntryCount();
            if (count > 0) {
                String name = getFragmentManager().getBackStackEntryAt(count - 1).getName();
                f = uniqueIdToFragment.get(name);
                if (BuildConfig.DEBUG && f == null) {
                    throw new AssertionError();
                }
            }
            if (currentFragment == f) {
                return;
            } else {
                push(new SpinTransition(currentFragment, f, false));
            }
            currentFragment = f;
        }

        private void push(ChangeablePage p) {
            if (page == null || page.isDoneChanging()) {
                if (currentFragment == null) {
                    currentFragment = p.currentFrame().getMain().circle;
                }
                highlighted = null;
                page = p;
                postInvalidateOnAnimation();
            } else {
                pending.add(p);
            }
        }

        private Rect getContentBounds() {
            float margin = getResources().getDisplayMetrics().density * MARGIN;

            float top = margin;
            float bottom = (float)this.getHeight() - margin;
            float left = margin;
            float right = (float)this.getWidth() - margin;

            float sizeDiff = Math.abs((right - left) - (bottom - top)) / 2;

            if ((right - left) > (bottom - top)) {
                right -= sizeDiff;
                left += sizeDiff;
            } else {
                top += sizeDiff;
            }

            int size = Math.round(right - left);
            int x = Math.round(left);
            int y = Math.round(top);
            return new Rect(x, y, x + size, y + size);
        }

        @Nullable
        private CircleFragment circleForPoint(int touchX, int touchY) {
            PageLayout pageLayout = page.currentFrame();
            Rect bounds = this.getContentBounds();

            float radius = (float)bounds.width() * RELATIVE_CIRCLE_RADIUS;
            float coordinateScale = ((float)bounds.width() / 2) - radius;

            for (CircleLayout layout : pageLayout.getCircleLayouts()) {
                // Do not allow the user to tap the center circle.
                if (layout.radius == 0) {
                    continue;
                }

                float x = bounds.exactCenterX() + ((float)Math.cos(layout.angle) *
                        coordinateScale * layout.radius);
                float y = bounds.exactCenterY() + ((float)Math.sin(layout.angle) *
                        coordinateScale * layout.radius);
                float dist = (float)Math.sqrt(Math.pow(x - (float)touchX, 2) +
                        Math.pow(y - (float)touchY, 2));
                if (dist <= radius) {
                    return layout.circle;
                }
            }
            return null;
        }

    }

}
