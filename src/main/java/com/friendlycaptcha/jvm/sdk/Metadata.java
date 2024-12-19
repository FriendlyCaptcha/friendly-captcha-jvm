package com.friendlycaptcha.jvm.sdk;

import java.util.Properties;

public final class Metadata {
  private static final Metadata INSTANCE = new Metadata();

  private Properties properties = new Properties();

  public final String sdkVersion = sdkVersion();
  public final String sdkIdentifier = sdkIdentifier();

  /**
   * Returns the SDK version, e.g. "1.2.3"
   * 
   * @return
   */
  private String sdkVersion() {
    try {
      this.properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
      String version = properties.getProperty("sdk.version");
      return (version == null) ? "unknown" : version;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "unknown";
  }

  /**
   * Returns the SDK identifier, e.g. "friendly-captcha-jvm@1.2.3"
   * 
   * @return
   */
  private String sdkIdentifier() {
    try {
      this.properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
      String name = properties.getProperty("artifact.id");
      String version = properties.getProperty("sdk.version");

      name = name != null ? name : "friendly-captcha-jvm";
      version = version != null ? version : "unknown";
      return name + "@" + version;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "unknown";
  }

  public static Metadata getInstance() {
    return INSTANCE;
  }
}