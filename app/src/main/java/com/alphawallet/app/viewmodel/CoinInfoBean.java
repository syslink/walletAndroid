package com.alphawallet.app.viewmodel;

import com.google.gson.annotations.SerializedName;

public class CoinInfoBean {

    private String id;
    private String symbol;
    private String name;
    private PlatformsBean platforms;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlatformsBean getPlatforms() {
        return platforms;
    }

    public void setPlatforms(PlatformsBean platforms) {
        this.platforms = platforms;
    }

    @Override
    public String toString() {
        return "CoinInfoBean{" +
                "id='" + id + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static class PlatformsBean {

        private String ethereum;
        @SerializedName("binance-smart-chain")
        private String binancesmartchain;
        @SerializedName("huobi-token")
        private String huobitoken;
        @SerializedName("polygon-pos")
        private String polygonpos;

        public String getEthereum() {
            return ethereum;
        }

        public void setEthereum(String ethereum) {
            this.ethereum = ethereum;
        }

        public String getBinancesmartchain() {
            return binancesmartchain;
        }

        public void setBinancesmartchain(String binancesmartchain) {
            this.binancesmartchain = binancesmartchain;
        }

        public String getHuobitoken() {
            return huobitoken;
        }

        public void setHuobitoken(String huobitoken) {
            this.huobitoken = huobitoken;
        }

        public String getPolygonpos() {
            return polygonpos;
        }

        public void setPolygonpos(String polygonpos) {
            this.polygonpos = polygonpos;
        }
    }
}
