package com.wurmcraft.serveressentials.api.models;

import java.util.Objects;

public class Donator {

  public String store;
  public String transactionID;
  public Double amount;
  public String uuid;
  public Long timestamp;
  public String type;
  public String typeData;

  /**
   * @param store name of the store for this donation
   * @param transactionID id of the transaction from the given store
   * @param amount amount of the donation
   * @param uuid uuid of the person doing the donation
   * @param timestamp unix timestamp when the donation was received
   * @param type type of the donation, "Package", "Rank" etc...
   * @param typeData Full details about the donation
   */
  public Donator(
      String store,
      String transactionID,
      double amount,
      String uuid,
      long timestamp,
      String type,
      String typeData) {
    this.store = store;
    this.transactionID = transactionID;
    this.amount = amount;
    this.uuid = uuid;
    this.timestamp = timestamp;
    this.type = type;
    this.typeData = typeData;
  }

  public Donator() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Donator)) return false;
    Donator donator = (Donator) o;
    return Objects.equals(store, donator.store)
        && Objects.equals(transactionID, donator.transactionID)
        && Objects.equals(amount, donator.amount)
        && Objects.equals(uuid, donator.uuid)
        && Objects.equals(timestamp, donator.timestamp)
        && Objects.equals(type, donator.type)
        && Objects.equals(typeData, donator.typeData);
  }
}
