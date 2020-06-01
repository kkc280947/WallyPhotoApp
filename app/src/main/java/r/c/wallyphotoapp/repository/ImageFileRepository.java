package r.c.wallyphotoapp.repository;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import r.c.wallyphotoapp.api.UnsplashApi;
import r.c.wallyphotoapp.repository.datamodels.imagemodels.ListResult;
import r.c.wallyphotoapp.repository.datamodels.imagemodels.PhotoModel;
import retrofit2.Response;

/**
 * ImageFileRepository acts as data source to provide json data using retrofit from server.
* */
public class ImageFileRepository {

    @Inject
    UnsplashApi unsplashApi;

    @Inject
    public ImageFileRepository(UnsplashApi api) {
        this.unsplashApi = api;
    }

    public ListResult getImages(){
        ListResult listResult = new ListResult();
            try {
                Response<List<PhotoModel>> listResponse = unsplashApi.getPhotos().execute();
                if(listResponse.isSuccessful()){
                    if (listResponse.body() != null) {
                        listResult.setCode(listResponse.code());
                        listResult.setPhotoModelList(listResponse.body());
                    }
                }else {
                    listResult.setCode(listResponse.code());
                    if (listResponse.errorBody() != null) {
                        listResult.setError(listResponse.errorBody().string());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                listResult.setError(e.getMessage());
            }
        return listResult;
    }

    public Response<ResponseBody> downloadFile(String url) throws IOException {
        return unsplashApi.downloadFileWithDynamicUrlSync(url).execute();
    }
}
