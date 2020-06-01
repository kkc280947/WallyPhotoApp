package r.c.wallyphotoapp.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import r.c.wallyphotoapp.api.UnsplashApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static r.c.wallyphotoapp.utils.WallyConstants.BASE_URL;

@Module
public class NetworkModule {

    @Singleton
    @Provides
    Retrofit providesRetrofitClient() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
    }

    @Singleton
    @Provides
    UnsplashApi provideUnsplashApi(Retrofit retrofit) {
        return retrofit.create(UnsplashApi.class);
    }
}
