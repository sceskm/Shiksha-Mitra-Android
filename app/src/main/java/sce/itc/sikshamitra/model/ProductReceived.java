package sce.itc.sikshamitra.model;

import com.google.gson.annotations.SerializedName;

public class ProductReceived {
    @SerializedName("comboId")
    private int comboId;

    @SerializedName("productId")
    private int productId;

    @SerializedName("received")
    private boolean received;

    public int getComboId() {
        return comboId;
    }

    public void setComboId(int comboId) {
        this.comboId = comboId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }
}
