package r.c.wallyphotoapp.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import r.c.wallyphotoapp.api.UnsplashApi;
import r.c.wallyphotoapp.repository.ImageFileRepository;

@Module
public class DataModule {

    @Provides
    @Singleton
    ImageFileRepository getImageFileRepo(UnsplashApi api){
        return new ImageFileRepository(api);
    }
}
