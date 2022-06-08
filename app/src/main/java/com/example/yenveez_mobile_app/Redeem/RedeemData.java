package com.example.yenveez_mobile_app.Redeem;

public class RedeemData {
    private String redeemItemName;
    private String redeemItemIconUrl;
    private String redeemItemDescription;

    public RedeemData(String redeemItemName, String redeemItemIconUrl, String redeemItemDescription) {
        this.redeemItemName = redeemItemName;
        this.redeemItemIconUrl = redeemItemIconUrl;
        this.redeemItemDescription = redeemItemDescription;
    }

    public RedeemData() {
    }

    public String getRedeemItemName() {
        return redeemItemName;
    }

    public void setRedeemItemName(String redeemItemName) {
        this.redeemItemName = redeemItemName;
    }

    public String getRedeemItemIconUrl() {
        return redeemItemIconUrl;
    }

    public void setRedeemItemIconUrl(String redeemItemIconUrl) {
        this.redeemItemIconUrl = redeemItemIconUrl;
    }

    public String getRedeemItemDescription() {
        return redeemItemDescription;
    }

    public void setRedeemItemDescription(String redeemItemDescription) {
        this.redeemItemDescription = redeemItemDescription;
    }
}