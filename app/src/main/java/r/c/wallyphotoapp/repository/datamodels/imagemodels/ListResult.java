package r.c.wallyphotoapp.repository.datamodels.imagemodels;

import java.util.ArrayList;
import java.util.List;


public class ListResult {
    private String error;
    private int code;
    private List<PhotoModel> photoModelList = new ArrayList<>();

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PhotoModel> getPhotoModelList() {
        return photoModelList;
    }

    public void setPhotoModelList(List<PhotoModel> photoModelList) {
        this.photoModelList = photoModelList;
    }
}
