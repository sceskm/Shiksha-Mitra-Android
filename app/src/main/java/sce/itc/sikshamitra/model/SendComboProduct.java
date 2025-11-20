package sce.itc.sikshamitra.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendComboProduct {
    @Expose
    private int comboId;
    @Expose
    private int productId;
    @Expose
    @SerializedName("received")
    private boolean receiveStatus;

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

    public boolean isReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(boolean receiveStatus) {
        this.receiveStatus = receiveStatus;
    }
}
