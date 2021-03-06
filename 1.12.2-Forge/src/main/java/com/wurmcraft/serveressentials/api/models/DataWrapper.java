package com.wurmcraft.serveressentials.api.models;

public class DataWrapper {

  public String type;
  public String data;

  /**
   * Wrapper to hold an json object along with its type
   *
   * @param type name of the data, provided
   * @param data json instance of the data, of the type
   * @see WSWrapper
   */
  public DataWrapper(String type, String data) {
    this.type = type;
    this.data = data;
  }
}
