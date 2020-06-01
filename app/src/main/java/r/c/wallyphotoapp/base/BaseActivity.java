package r.c.wallyphotoapp.base;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Base activity which works as abstraction layer over user defined activities
 * */
public abstract class BaseActivity extends AppCompatActivity{
    abstract public int getContainerViewId();
}