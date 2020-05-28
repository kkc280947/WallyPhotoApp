package r.c.wallyphotoapp.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import r.c.wallyphotoapp.R;
import r.c.wallyphotoapp.base.BaseActivity;
import r.c.wallyphotoapp.ui.main.MainFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(getContainerViewId(), MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public int getContainerViewId() {
        return R.id.container;
    }
}
