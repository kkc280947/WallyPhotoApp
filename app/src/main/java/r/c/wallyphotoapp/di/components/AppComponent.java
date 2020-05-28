package r.c.wallyphotoapp.di.components;

import dagger.Component;
import r.c.wallyphotoapp.di.modules.NetworkModule;
import r.c.wallyphotoapp.di.modules.ViewModelModule;
import r.c.wallyphotoapp.ui.main.MainFragment;

@Component(modules = {NetworkModule.class, ViewModelModule.class})
public interface AppComponent {

    public void inject(MainFragment mainFragment);
}
