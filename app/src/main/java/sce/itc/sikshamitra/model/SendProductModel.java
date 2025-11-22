package sce.itc.sikshamitra.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendProductModel {
    @Expose
    @SerializedName("productId")
    private int productId;
    @Expose
    @SerializedName("isDistributed")
    private int isDistributed;

    public SendProductModel(int productId, int isDistributed) {
        this.productId = productId;
        this.isDistributed = isDistributed;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getIsDistributed() {
        return isDistributed;
    }

    public void setIsDistributed(int isDistributed) {
        this.isDistributed = isDistributed;
    }
}
