package jp.co.hispot.sample_uipatterns_android.activities;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import jp.co.hispot.sample_uipatterns_android.R;

public class AnimationIconActivity extends AppCompatActivity {

    private boolean menuFlag = true;
    private AnimatedVectorDrawable menuDrawable;
    private AnimatedVectorDrawable arrowDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_icon);

        menuDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_menu_24dp);
        arrowDrawable = (AnimatedVectorDrawable) getDrawable(R.drawable.ic_arrow_24dp);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(menuDrawable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menuFlag) {
                    toolbar.setNavigationIcon(menuDrawable);
                    menuDrawable.start();
                } else {
                    toolbar.setNavigationIcon(arrowDrawable);
                    arrowDrawable.start();
                }
                menuFlag = !menuFlag;
            }
        });
    }
}
