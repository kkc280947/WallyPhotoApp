package r.c.wallyphotoapp.di.modules;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;
import r.c.wallyphotoapp.ui.photogallery.PhotoGalleryViewModel;
import r.c.wallyphotoapp.ui.profile.ProfileViewModel;

@Module
public abstract class ViewModelModule {

    @Documented
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    private @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }

    @Binds
    @IntoMap
    @ViewModelKey(PhotoGalleryViewModel.class)
    abstract ViewModel bindMainViewModel(PhotoGalleryViewModel photoGalleryViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileViewModel(ProfileViewModel profileViewModel);
}
