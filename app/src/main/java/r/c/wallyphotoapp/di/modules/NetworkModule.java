package r.c.wallyphotoapp.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import r.c.wallyphotoapp.api.UnsplashApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Singleton
    @Provides
    GsonConverterFactory getGsonConverterFactory(){
        return GsonConverterFactory.create();
    }

    @Singleton
    @Provides
    UnsplashApi getRestApiClient(Retrofit retrofit){
         retrofit.newBuilder()
                .addConverterFactory(getGsonConverterFactory())
                .baseUrl("https://api.unsplash.com/")
                .build();
         return retrofit.create(UnsplashApi.class);

    }
}
