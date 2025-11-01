package sce.itc.sikshamitra.model;

import com.google.gson.annotations.Expose;

public class Image {
    @Expose
    private int imageDefinitionId;
    @Expose
    private String imageName;
    @Expose
    private String imageFileExt;

    public int getImageDefinitionId() {
        return imageDefinitionId;
    }

    public void setImageDefinitionId(int imageDefinitionId) {
        this.imageDefinitionId = imageDefinitionId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageFileExt() {
        return imageFileExt;
    }

    public void setImageFileExt(String imageFileExt) {
        this.imageFileExt = imageFileExt;
    }
}
