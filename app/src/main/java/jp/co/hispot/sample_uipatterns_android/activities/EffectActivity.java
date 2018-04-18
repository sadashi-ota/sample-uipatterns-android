package jp.co.hispot.sample_uipatterns_android.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.hispot.sample_uipatterns_android.R;

public class EffectActivity extends AppCompatActivity {

    private static final int REVEAL_ANIMATION_DURATION = 500;
    private boolean changeReveal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect);

        TextView nonEffect = findViewById(R.id.non_effect);
        nonEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EffectActivity.this, "Tap non effect text", Toast.LENGTH_SHORT).show();
            }
        });

        TextView rippleEffect = findViewById(R.id.ripple_effect);
        rippleEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EffectActivity.this, "Tap ripple effect text", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.start_reveal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealAnimation();
            }
        });
    }

    private void revealAnimation() {
        final View base = findViewById(R.id.base);
        final View reveal = findViewById(R.id.reveal);
        FloatingActionButton fab = findViewById(R.id.start_reveal);
        int cx = (fab.getLeft() + fab.getRight()) / 2;
        int cy = (fab.getTop() + fab.getBottom()) / 2;

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int radius = point.y;

        if (changeReveal) {
            reveal.setBackgroundResource(R.color.colorNormal);
        } else {
            reveal.setBackgroundResource(R.color.colorReveal);
        }

        reveal.setVisibility(View.VISIBLE);
        Animator animator = ViewAnimationUtils.createCircularReveal(reveal, cx, cy, 0, radius);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reveal.setVisibility(View.INVISIBLE);
                if (changeReveal) {
                    base.setBackgroundResource(R.color.colorNormal);
                } else {
                    base.setBackgroundResource(R.color.colorReveal);
                }
                changeReveal = !changeReveal;
            }
        });
        animator.setDuration(REVEAL_ANIMATION_DURATION);
        animator.start();
    }
}
