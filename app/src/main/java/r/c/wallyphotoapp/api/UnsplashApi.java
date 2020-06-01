package r.c.wallyphotoapp.api;

import java.util.List;

import okhttp3.ResponseBody;
import r.c.wallyphotoapp.repository.datamodels.imagemodels.PhotoModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface UnsplashApi {

    @GET("photos/?client_id=YdfnemQFtzbB4YRoD40mpPwLMb0Y6SXFnxWmCeWLloA&per_page=30")
    Call<List<PhotoModel>> getPhotos();

    @Streaming
    @GET()
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String url);
}
