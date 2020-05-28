package r.c.wallyphotoapp.api;


import retrofit2.Call;
import retrofit2.http.POST;

public interface UnsplashApi {


    @POST("photos/?client_id=YdfnemQFtzbB4YRoD40mpPwLMb0Y6SXFnxWmCeWLloA")
    Call<Void> getPhotos();

}
