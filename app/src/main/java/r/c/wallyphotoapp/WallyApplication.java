package r.c.wallyphotoapp;

import android.app.Application;

import r.c.wallyphotoapp.di.components.AppComponent;
import r.c.wallyphotoapp.di.components.DaggerAppComponent;
import r.c.wallyphotoapp.di.modules.ContextModule;

public class WallyApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = getAppComponent();
    }

    public AppComponent getAppComponent(){
        if(appComponent == null){
            appComponent = DaggerAppComponent.builder().
                    contextModule(new ContextModule(getApplicationContext())).build();
        }
        return appComponent;
    }
}
