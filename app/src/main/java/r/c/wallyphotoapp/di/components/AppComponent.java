package r.c.wallyphotoapp.di.components;

import javax.inject.Singleton;

import dagger.Component;
import r.c.wallyphotoapp.di.modules.ContextModule;
import r.c.wallyphotoapp.di.modules.DataModule;
import r.c.wallyphotoapp.di.modules.NetworkModule;
import r.c.wallyphotoapp.di.modules.ViewModelModule;
import r.c.wallyphotoapp.service.ImageDownloadService;
import r.c.wallyphotoapp.ui.login.LoginActivity;
import r.c.wallyphotoapp.ui.photogallery.PhotoGalleryFragment;
import r.c.wallyphotoapp.ui.profile.ProfileFragment;

@Singleton
@Component(modules = {NetworkModule.class, ViewModelModule.class,
        ContextModule.class, DataModule.class})
public interface AppComponent {

    void inject(PhotoGalleryFragment mainFragment);

    void inject(ImageDownloadService backgroundNotificationService);

    void inject(LoginActivity loginActivity);

    void inject(ProfileFragment profileFragment);
}
