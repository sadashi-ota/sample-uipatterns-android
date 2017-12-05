package jp.co.hispot.sample_uipatterns_android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import jp.co.hispot.sample_uipatterns_android.R;

public class MainActivity extends AppCompatActivity {

    private static final String DRAWER_LAYOUT_SAMPLE = "DrawerLayout & NavigationView";
    private static final String BUTTONS_SAMPLE = "Buttons";
    private static final String ANIMATION_ICON_SAMPLE = "Animation Icon";
    private static final String EFFECT_SAMPLE = "Effect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] uiSamples = {
                DRAWER_LAYOUT_SAMPLE,
                BUTTONS_SAMPLE,
                ANIMATION_ICON_SAMPLE,
                EFFECT_SAMPLE,
        };

        ListView listSamples = findViewById(R.id.list_samples);
        listSamples.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, uiSamples));
        listSamples.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                switch (name) {
                    case DRAWER_LAYOUT_SAMPLE:
                        startActivity(new Intent(MainActivity.this, DrawerLayoutActivity.class));
                        break;
                    case BUTTONS_SAMPLE:
                        startActivity(new Intent(MainActivity.this, ButtonsActivity.class));
                        break;
                    case ANIMATION_ICON_SAMPLE:
                        startActivity(new Intent(MainActivity.this, AnimationIconActivity.class));
                        break;
                    case EFFECT_SAMPLE:
                        startActivity(new Intent(MainActivity.this, EffectActivity.class));
                        break;
                }
            }
        });
    }
}
