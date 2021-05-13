package io.wurmatron.serveressentials.models;

import io.wurmatron.serveressentials.ServerEssentialsRest;
import io.wurmatron.serveressentials.models.transfer.ItemWrapper;

import java.util.Objects;

public class MarketEntry {
    public String serverID;
    public String sellerUUID;
    public ItemWrapper item;
    public String currencyName;
    public double currencyAmount;
    public long timestamp;
    public String marketType;
    public String marketData;
    public String transferID;

    /**
     * @param serverID       id of the server, where the trade started
     * @param sellerUUID     uuid of the seller
     * @param item           json data of the item to be sold
     * @param currencyName   name of the currency being used by the entry
     * @param currencyAmount current amount of the currency being used by this entry
     * @param timestamp      unix timestamp when the entry was created
     * @param marketType     type of the market for this entry
     * @param marketData     data related to the market entry
     * @param transferID     server's transferID, for use with multi-server market's
     */
    public MarketEntry(String serverID, String sellerUUID, ItemWrapper item, String currencyName, double currencyAmount, long timestamp, String marketType, String marketData, String transferID) {
        this.serverID = serverID;
        this.sellerUUID = sellerUUID;
        this.item = item;
        this.currencyName = currencyName;
        this.currencyAmount = currencyAmount;
        this.timestamp = timestamp;
        this.marketType = marketType;
        this.marketData = marketData;
        this.transferID = transferID;
    }

    public MarketEntry() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketEntry)) return false;
        MarketEntry that = (MarketEntry) o;
        return Double.compare(that.currencyAmount, currencyAmount) == 0 && timestamp == that.timestamp && Objects.equals(serverID, that.serverID) && Objects.equals(sellerUUID, that.sellerUUID) && Objects.equals(item, that.item) && Objects.equals(currencyName, that.currencyName) && Objects.equals(marketType, that.marketType) && Objects.equals(marketData, that.marketData) && Objects.equals(transferID, that.transferID);
    }

    @Override
    public Rank clone() {
        String json = ServerEssentialsRest.GSON.toJson(this);
        return ServerEssentialsRest.GSON.fromJson(json, Rank.class);
    }
}
