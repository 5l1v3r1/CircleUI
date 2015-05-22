package com.aqnichol.circleui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A CircleActivity allows navigation through menus in a cool, circular way.
 */
public class CircleActivity extends Activity {

    private CirclesView circlesView;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        circlesView = new CirclesView(this);
        setContentView(circlesView, params);
    }

    public void showMenu(CircleFragment f) {
        circlesView.pushTransition(new InstantTransition(f));
    }

    private static CircleDrawInfo[] layoutForFragment(CircleFragment fragment) {
        int count = 1;
        if (fragment.isMenu()) {
            count += fragment.getMenuOptions().length;
        }
        CircleDrawInfo[] info = new CircleDrawInfo[count];
        info[0] = new CircleDrawInfo(fragment, 0, 0, 1);
        for (int i = 1; i < count; ++i) {
            float angle = 2 * (float)Math.PI * (float)(i - 1) / (float)(count - 1);
            info[i] = new CircleDrawInfo(fragment.getMenuOptions()[i - 1], 1, angle, 1);
        }
        return info;
    }

    private static class CirclesView extends View {

        private float CIRCLE_RADIUS_RATIO = 1.0f/8.0f;

        private long transitionStartTime = 0;
        private Transition transition = null;
        private ArrayList<Transition> pending = new ArrayList<>();
        private Rect reuseRect = new Rect();

        public CirclesView(Context context) {
            super(context);
        }

        public void pushTransition(Transition t) {
            if (transition == null || transition.isDone(getProgress())) {
                transitionStartTime = SystemClock.elapsedRealtime();
                transition = t;
                postInvalidateOnAnimation();
            } else {
                pending.add(t);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.transition == null) {
                return;
            }

            float progress = getProgress();
            CircleDrawInfo infos[] = transition.layoutForProgress(progress);

            Rect contentBounds = this.getContentBounds();

            float circleRadius = (float)contentBounds.width() * CIRCLE_RADIUS_RATIO;
            float distScale = ((float)contentBounds.width() / 2) - circleRadius;
            int circleSize = Math.round(circleRadius * 2);
            for (CircleDrawInfo info : infos) {
                float circleCenterX = contentBounds.exactCenterX() + ((float)Math.cos(info.angle) *
                        distScale * info.radialDistance);
                float circleCenterY = contentBounds.exactCenterY() + ((float)Math.sin(info.angle) *
                        distScale * info.radialDistance);
                int x = Math.round(circleCenterX - circleRadius);
                int y = Math.round(circleCenterY - circleRadius);
                reuseRect.set(x, y, x + circleSize, y + circleSize);
                info.circle.draw(canvas, reuseRect, info.alpha);
            }

            if (!transition.isDone(progress)) {
                postInvalidateOnAnimation();
            } else if (pending.size() > 0) {
                Transition t = pending.get(0);
                pending.remove(0);
                this.pushTransition(t);
            }
        }

        private Rect getContentBounds() {
            if (this.getWidth() < this.getHeight()) {
                float top = (float)(this.getHeight() - this.getWidth()) / 2;
                return new Rect(0, Math.round(top), this.getWidth(),
                        this.getWidth() + Math.round(top));
            } else {
                float left = (float)(this.getWidth() - this.getHeight()) / 2;
                return new Rect(Math.round(left), 0, Math.round(left) + this.getHeight(),
                        this.getHeight());
            }
        }

        private float getProgress() {
            long duration = SystemClock.elapsedRealtime() - transitionStartTime;
            return (float)duration / 1000.0f;
        }

    }

    private static class CircleDrawInfo {

        public CircleFragment circle;
        public float radialDistance;
        public float angle;
        public float alpha;

        public CircleDrawInfo(CircleFragment circle, float radialDistance, float angle,
                              float alpha) {
            this.circle = circle;
            this.radialDistance = radialDistance;
            this.angle = angle;
            this.alpha = alpha;
        }

        public CircleDrawInfo changeAlpha(float newAlpha) {
            return new CircleDrawInfo(circle, radialDistance, angle, newAlpha);
        }

        public CircleDrawInfo rotate(float radians) {
            return new CircleDrawInfo(circle, radialDistance, angle + radians, alpha);
        }

        public CircleDrawInfo clone() {
            return new CircleDrawInfo(circle, radialDistance, angle, alpha);
        }

        public static CircleDrawInfo intermediate(CircleDrawInfo i1, CircleDrawInfo i2, float p) {
            return new CircleDrawInfo(i1.circle, i1.radialDistance + (i2.radialDistance -
                    i1.radialDistance) * p, i1.angle + (i2.angle - i1.angle) * p,
                    i1.alpha + (i2.alpha - i1.alpha) * p);
        }

        public static CircleDrawInfo[] intermediateList(CircleDrawInfo[] l1, CircleDrawInfo[] l2,
                                                        float p) {
            CircleDrawInfo[] result = new CircleDrawInfo[l1.length];
            for (int i = 0; i < l1.length; ++i) {
                result[i] = intermediate(l1[i], l2[i], p);
            }
            return result;
        }

    }

    private interface Transition {

        boolean isDone(float progress);
        CircleDrawInfo[] layoutForProgress(float progress);

    }

    private static class InstantTransition implements Transition {

        private CircleDrawInfo[] info;

        public InstantTransition(CircleFragment f) {
            info = layoutForFragment(f);
        }

        public boolean isDone(float progress) {
            return true;
        }

        public CircleDrawInfo[] layoutForProgress(float progress) {
            return info;
        }

    }

    private static class ForwardTransition implements Transition {

        private final float PHASE1_DURATION = 0.3f;
        private final float PHASE2_DURATION = 0.3f;

        public CircleDrawInfo[] phase1Start;
        public CircleDrawInfo[] phase1End;
        public CircleDrawInfo[] phase2Start;
        public CircleDrawInfo[] phase2End;

        public ForwardTransition(CircleFragment start, CircleFragment end) {
            phase1Start = layoutForFragment(start);
            phase1End = new CircleDrawInfo[phase1Start.length];
            for (int i = 0; i < phase1Start.length; ++i) {
                if (phase1Start[i].circle == end) {
                    phase1End[i] = phase1Start[i].clone();
                    phase1End[i].radialDistance = 0;
                } else {
                    phase1End[i] = phase1Start[i].rotate((float)Math.PI).changeAlpha(0);
                    phase1End[i].alpha = 0;
                }
            }

            phase2End = layoutForFragment(end);
            phase2Start = new CircleDrawInfo[phase2End.length];
            for (int i = 0; i < phase2End.length; ++i) {
                if (phase2End[i].circle == end) {
                    continue;
                }
                phase2Start[i] = phase2End[i].rotate((float)Math.PI).changeAlpha(0);
            }
        }

        public boolean isDone(float progress) {
            return progress >= PHASE1_DURATION + PHASE2_DURATION;
        }

        public CircleDrawInfo[] layoutForProgress(float progress) {
            if (progress >= PHASE1_DURATION + PHASE2_DURATION) {
                return phase2End;
            } else if (progress <= 0) {
                return phase1Start;
            }

            if (progress <= PHASE1_DURATION) {
                return CircleDrawInfo.intermediateList(phase1Start, phase1End, progress /
                        PHASE1_DURATION);
            } else {
                return CircleDrawInfo.intermediateList(phase2Start, phase2End, (progress -
                        PHASE1_DURATION) / PHASE2_DURATION);
            }
        }

    }

}
