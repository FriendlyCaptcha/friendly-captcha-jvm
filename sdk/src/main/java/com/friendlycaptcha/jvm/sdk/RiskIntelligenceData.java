package com.friendlycaptcha.jvm.sdk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * RiskIntelligenceData contains all risk intelligence information.
 *
 * Field availability depends on enabled modules.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RiskIntelligenceData {
    /**
     * Risk scores from various signals, these summarize the risk intelligence
     * assessment.
     *
     * Available when the Risk Scores module is enabled.
     * Null when the Risk Scores module is not enabled.
     */
    @JsonProperty("risk_scores")
    private RiskScoresData riskScores;

    /**
     * Network-related risk intelligence.
     */
    private NetworkData network;

    /**
     * Client/device risk intelligence.
     */
    private ClientData client;

    public RiskScoresData getRiskScores() {
        return riskScores;
    }

    public NetworkData getNetwork() {
        return network;
    }

    public ClientData getClient() {
        return client;
    }

    @Override
    public String toString() {
        return "RiskIntelligenceData{riskScores=" + riskScores + ", network=" + network + ", client=" + client + "}";
    }

    /**
     * Risk score value ranging from 1 to 5.
     *
     * 0 = Unknown or missing,
     * 1 = Very low,
     * 2 = Low,
     * 3 = Medium,
     * 4 = High,
     * 5 = Very high.
     */
    public enum RiskScore {
        /** Unknown or missing risk score. */
        UNKNOWN(0),
        /** Very low risk score (1/5). */
        VERY_LOW(1),
        /** Low risk score (2/5). */
        LOW(2),
        /** Medium risk score (3/5). */
        MEDIUM(3),
        /** High risk score (4/5). */
        HIGH(4),
        /** Very high risk score (5/5). */
        VERY_HIGH(5);

        private final int value;

        RiskScore(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return value;
        }

        @JsonCreator
        public static RiskScore fromValue(Integer value) {
            if (value == null) {
                return UNKNOWN;
            }
            switch (value) {
                case 1:
                    return VERY_LOW;
                case 2:
                    return LOW;
                case 3:
                    return MEDIUM;
                case 4:
                    return HIGH;
                case 5:
                    return VERY_HIGH;
                default:
                    return UNKNOWN;
            }
        }
    }

    /**
     * Summarized risk intelligence scores per category.
     *
     * Available when the Risk Scores module is enabled for your account.
     * Null when the Risk Scores module is not enabled for your account.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RiskScoresData {
        /** Overall risk score combining all signals. */
        private RiskScore overall = RiskScore.UNKNOWN;

        /**
         * Network-related risk score.
         * Captures likelihood of automation/malicious activity based on IP address,
         * ASN, reputation, geolocation, past abuse from this network, and other
         * network signals.
         */
        private RiskScore network = RiskScore.UNKNOWN;

        /**
         * Browser-related risk score.
         * Captures likelihood of automation, malicious activity or browser spoofing
         * based on user-agent consistency, automation traces, past abuse, and browser
         * characteristics.
         */
        private RiskScore browser = RiskScore.UNKNOWN;

        public RiskScore getOverall() {
            return overall;
        }

        public RiskScore getNetwork() {
            return network;
        }

        public RiskScore getBrowser() {
            return browser;
        }

        @Override
        public String toString() {
            return "RiskScoresData{overall=" + overall + ", network=" + network + ", browser=" + browser + "}";
        }
    }

    /**
     * Information about the autonomous system (AS) that owns the IP.
     *
     * Available when the IP Intelligence module is enabled.
     * Null when the IP Intelligence module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkAutonomousSystemData {
        /** Autonomous System Number (ASN) identifier. Example: 3209. */
        private int number;

        /** Autonomous system short name/handle. Example: "VODANET". */
        private String name;

        /** Organization name that owns the ASN. Example: "Vodafone GmbH". */
        private String company;

        /** Description of the ASN owner organization. */
        private String description;

        /** Domain associated with the ASN. Example: "vodafone.de". */
        private String domain;

        /** Two-letter ISO 3166-1 alpha-2 country code of ASN registration. */
        private String country;

        /** Regional Internet Registry that allocated the ASN. Example: "RIPE". */
        private String rir;

        /** IP route in CIDR notation. Example: "88.64.0.0/12". */
        private String route;

        /** Autonomous system type. Example: "isp". */
        private String type;

        public int getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public String getCompany() {
            return company;
        }

        public String getDescription() {
            return description;
        }

        public String getDomain() {
            return domain;
        }

        public String getCountry() {
            return country;
        }

        public String getRir() {
            return rir;
        }

        public String getRoute() {
            return route;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "NetworkAutonomousSystemData{number=" + number + ", name='" + name + "', company='" + company
                    + "', description='" + description + "', domain='" + domain + "', country='" + country
                    + "', rir='" + rir + "', route='" + route + "', type='" + type + "'}";
        }
    }

    /**
     * Detailed country metadata for geolocation.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkGeolocationCountryData {
        /** Two-letter ISO 3166-1 alpha-2 country code. Example: "DE". */
        private String iso2;

        /** Three-letter ISO 3166-1 alpha-3 country code. Example: "DEU". */
        private String iso3;

        /** English country name. Example: "Germany". */
        private String name;

        /** Native country name. Example: "Deutschland". */
        @JsonProperty("name_native")
        private String nameNative;

        /** Major world region. Example: "Europe". */
        private String region;

        /** More specific region. Example: "Western Europe". */
        private String subregion;

        /** ISO 4217 currency code. Example: "EUR". */
        private String currency;

        /** Currency name. Example: "Euro". */
        @JsonProperty("currency_name")
        private String currencyName;

        /** International dialing code. Example: "49". */
        @JsonProperty("phone_code")
        private String phoneCode;

        /** Capital city name. Example: "Berlin". */
        private String capital;

        public String getIso2() {
            return iso2;
        }

        public String getIso3() {
            return iso3;
        }

        public String getName() {
            return name;
        }

        public String getNameNative() {
            return nameNative;
        }

        public String getRegion() {
            return region;
        }

        public String getSubregion() {
            return subregion;
        }

        public String getCurrency() {
            return currency;
        }

        public String getCurrencyName() {
            return currencyName;
        }

        public String getPhoneCode() {
            return phoneCode;
        }

        public String getCapital() {
            return capital;
        }

        @Override
        public String toString() {
            return "NetworkGeolocationCountryData{iso2='" + iso2 + "', iso3='" + iso3 + "', name='" + name
                    + "', nameNative='" + nameNative + "', region='" + region + "', subregion='" + subregion
                    + "', currency='" + currency + "', currencyName='" + currencyName + "', phoneCode='"
                    + phoneCode + "', capital='" + capital + "'}";
        }
    }

    /**
     * Geographic location for the IP address.
     *
     * Available when the IP Intelligence module is enabled.
     * Null when the IP Intelligence module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkGeolocationData {
        /** Country information. */
        private NetworkGeolocationCountryData country;

        /** City name. Empty string if unknown. */
        private String city;

        /** State/region/province name. Empty string if unknown. */
        private String state;

        public NetworkGeolocationCountryData getCountry() {
            return country;
        }

        public String getCity() {
            return city;
        }

        public String getState() {
            return state;
        }

        @Override
        public String toString() {
            return "NetworkGeolocationData{country=" + country + ", city='" + city + "', state='" + state + "'}";
        }
    }

    /**
     * Abuse-contact details for the IP network owner.
     *
     * Available when the IP Intelligence module is enabled.
     * Null when the IP Intelligence module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkAbuseContactData {
        /** Postal address of the abuse contact. */
        private String address;

        /** Name of the abuse contact person/team. */
        private String name;

        /** Abuse contact email address. */
        private String email;

        /** Abuse contact phone number. */
        private String phone;

        public String getAddress() {
            return address;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        @Override
        public String toString() {
            return "NetworkAbuseContactData{address='" + address + "', name='" + name + "', email='" + email
                    + "', phone='" + phone + "'}";
        }
    }

    /**
     * VPN/proxy/anonymization detection details.
     *
     * Available when the Anonymization Detection module is enabled.
     * Null when the Anonymization Detection module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkAnonymizationData {
        /** Likelihood that the IP belongs to a VPN service. */
        @JsonProperty("vpn_score")
        private RiskScore vpnScore = RiskScore.UNKNOWN;

        /** Likelihood that the IP belongs to a proxy service. */
        @JsonProperty("proxy_score")
        private RiskScore proxyScore = RiskScore.UNKNOWN;

        /** Whether the IP is a Tor exit node. */
        private boolean tor;

        /** Whether the IP is from iCloud Private Relay. */
        @JsonProperty("icloud_private_relay")
        private boolean iCloudPrivateRelay;

        public RiskScore getVpnScore() {
            return vpnScore;
        }

        public RiskScore getProxyScore() {
            return proxyScore;
        }

        public boolean isTor() {
            return tor;
        }

        public boolean isICloudPrivateRelay() {
            return iCloudPrivateRelay;
        }

        @Override
        public String toString() {
            return "NetworkAnonymizationData{vpnScore=" + vpnScore + ", proxyScore=" + proxyScore + ", tor=" + tor
                    + ", iCloudPrivateRelay=" + iCloudPrivateRelay + "}";
        }
    }

    /**
     * Network information for the challenge request context.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NetworkData {
        /** IP address used when requesting the challenge. */
        private String ip;

        /**
         * Autonomous system information.
         *
         * Available when the IP Intelligence module is enabled.
         * Null when the IP Intelligence module is not enabled.
         */
        @JsonProperty("as")
        private NetworkAutonomousSystemData as;

        /**
         * Geolocation information.
         *
         * Available when the IP Intelligence module is enabled.
         * Null when the IP Intelligence module is not enabled.
         */
        private NetworkGeolocationData geolocation;

        /**
         * Abuse contact information.
         *
         * Available when the IP Intelligence module is enabled.
         * Null when the IP Intelligence module is not enabled.
         */
        @JsonProperty("abuse_contact")
        private NetworkAbuseContactData abuseContact;

        /**
         * Anonymization information.
         *
         * Available when the Anonymization Detection module is enabled.
         * Null when the Anonymization Detection module is not enabled.
         */
        private NetworkAnonymizationData anonymization;

        public String getIp() {
            return ip;
        }

        public NetworkAutonomousSystemData getAs() {
            return as;
        }

        public NetworkGeolocationData getGeolocation() {
            return geolocation;
        }

        public NetworkAbuseContactData getAbuseContact() {
            return abuseContact;
        }

        public NetworkAnonymizationData getAnonymization() {
            return anonymization;
        }

        @Override
        public String toString() {
            return "NetworkData{ip='" + ip + "', as=" + as + ", geolocation=" + geolocation
                    + ", abuseContact=" + abuseContact + ", anonymization=" + anonymization + "}";
        }
    }

    /**
     * IANA time zone data from the browser.
     *
     * Available when the Browser Identification module is enabled.
     * Null when the Browser Identification module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientTimeZoneData {
        /** IANA time zone name reported by the browser. */
        private String name;

        /**
         * Two-letter ISO 3166-1 alpha-2 country code derived from the time zone.
         * "XU" if timezone is missing or cannot be mapped to a country.
         */
        @JsonProperty("country_iso2")
        private String countryIso2;

        public String getName() {
            return name;
        }

        public String getCountryIso2() {
            return countryIso2;
        }

        @Override
        public String toString() {
            return "ClientTimeZoneData{name='" + name + "', countryIso2='" + countryIso2 + "'}";
        }
    }

    /**
     * Detected browser details.
     *
     * Available when the Browser Identification module is enabled.
     * Null when the Browser Identification module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientBrowserData {
        /** Browser identifier. Empty if browser could not be identified. */
        private String id;

        /** Human-readable browser name. Empty if unknown. */
        private String name;

        /** Browser version. Empty if unknown. */
        private String version;

        /** Browser release date (YYYY-MM-DD). Empty if unknown. */
        @JsonProperty("release_date")
        private String releaseDate;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        @Override
        public String toString() {
            return "ClientBrowserData{id='" + id + "', name='" + name + "', version='" + version
                    + "', releaseDate='" + releaseDate + "'}";
        }
    }

    /**
     * Detected browser engine details.
     *
     * Available when the Browser Identification module is enabled.
     * Null when the Browser Identification module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientBrowserEngineData {
        /** Rendering engine identifier. Empty if unknown. */
        private String id;

        /** Human-readable rendering engine name. Empty if unknown. */
        private String name;

        /** Rendering engine version. Empty if unknown. */
        private String version;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "ClientBrowserEngineData{id='" + id + "', name='" + name + "', version='" + version + "'}";
        }
    }

    /**
     * Detected device details.
     *
     * Available when the Browser Identification module is enabled.
     * Null when the Browser Identification module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientDeviceData {
        /** Device type. Example: desktop, mobile, tablet. */
        private String type;

        /** Device brand. */
        private String brand;

        /** Device model name. */
        private String model;

        public String getType() {
            return type;
        }

        public String getBrand() {
            return brand;
        }

        public String getModel() {
            return model;
        }

        @Override
        public String toString() {
            return "ClientDeviceData{type='" + type + "', brand='" + brand + "', model='" + model + "'}";
        }
    }

    /**
     * Detected operating system details.
     *
     * Available when the Browser Identification module is enabled.
     * Null when the Browser Identification module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientOSData {
        /** Operating system identifier. Empty if unknown. */
        private String id;

        /** Human-readable operating system name. Empty if unknown. */
        private String name;

        /** Operating system version. */
        private String version;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "ClientOSData{id='" + id + "', name='" + name + "', version='" + version + "'}";
        }
    }

    /**
     * TLS client-hello signatures.
     *
     * Available when the Bot Detection module is enabled.
     * Null when the Bot Detection module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TLSSignatureData {
        /** JA3 hash. */
        private String ja3;

        /** JA3N hash. */
        private String ja3n;

        /** JA4 signature. */
        private String ja4;

        public String getJa3() {
            return ja3;
        }

        public String getJa3n() {
            return ja3n;
        }

        public String getJa4() {
            return ja4;
        }

        @Override
        public String toString() {
            return "TLSSignatureData{ja3='" + ja3 + "', ja3n='" + ja3n + "', ja4='" + ja4 + "'}";
        }
    }

    /**
     * Detected known-bot details.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientAutomationKnownBotData {
        /** Whether a known bot was detected. */
        private boolean detected;

        /** Bot identifier. Empty when not detected. */
        private String id;

        /** Human-readable bot name. Empty when not detected. */
        private String name;

        /** Bot type classification. Empty when not detected. */
        private String type;

        /** Link to bot documentation. Empty when not detected. */
        private String url;

        public boolean isDetected() {
            return detected;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return "ClientAutomationKnownBotData{detected=" + detected + ", id='" + id + "', name='" + name
                    + "', type='" + type + "', url='" + url + "'}";
        }
    }

    /**
     * Detected automation-tool details.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientAutomationToolData {
        /** Whether an automation tool was detected. */
        private boolean detected;

        /** Automation-tool identifier. Empty when not detected. */
        private String id;

        /** Human-readable automation-tool name. Empty when not detected. */
        private String name;

        /** Automation-tool type. Empty when not detected. */
        private String type;

        public boolean isDetected() {
            return detected;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return "ClientAutomationToolData{detected=" + detected + ", id='" + id + "', name='" + name
                    + "', type='" + type + "'}";
        }
    }

    /**
     * Automation detection data.
     *
     * Available when the Bot Detection module is enabled.
     * Null when the Bot Detection module is not enabled.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientAutomationData {
        /** Detected automation tool information. */
        @JsonProperty("automation_tool")
        private ClientAutomationToolData automationTool;

        /** Detected known bot information. */
        @JsonProperty("known_bot")
        private ClientAutomationKnownBotData knownBot;

        public ClientAutomationToolData getAutomationTool() {
            return automationTool;
        }

        public ClientAutomationKnownBotData getKnownBot() {
            return knownBot;
        }

        @Override
        public String toString() {
            return "ClientAutomationData{automationTool=" + automationTool + ", knownBot=" + knownBot + "}";
        }
    }

    /**
     * User-agent and device/client information.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClientData {
        /** User-Agent header value. */
        @JsonProperty("header_user_agent")
        private String headerUserAgent;

        /**
         * Time zone information.
         *
         * Available when the Browser Identification module is enabled.
         * Null when the Browser Identification module is not enabled.
         */
        @JsonProperty("time_zone")
        private ClientTimeZoneData timeZone;

        /**
         * Browser information.
         *
         * Available when the Browser Identification module is enabled.
         * Null when the Browser Identification module is not enabled.
         */
        private ClientBrowserData browser;

        /**
         * Browser engine information.
         *
         * Available when the Browser Identification module is enabled.
         * Null when the Browser Identification module is not enabled.
         */
        @JsonProperty("browser_engine")
        private ClientBrowserEngineData browserEngine;

        /**
         * Device information.
         *
         * Available when the Browser Identification module is enabled.
         * Null when the Browser Identification module is not enabled.
         */
        private ClientDeviceData device;

        /**
         * Operating system information.
         *
         * Available when the Browser Identification module is enabled.
         * Null when the Browser Identification module is not enabled.
         */
        @JsonProperty("os")
        private ClientOSData os;

        /**
         * TLS signature information.
         *
         * Available when the Bot Detection module is enabled.
         * Null when the Bot Detection module is not enabled.
         */
        @JsonProperty("tls_signature")
        private TLSSignatureData tlsSignature;

        /**
         * Automation detection data.
         *
         * Available when the Bot Detection module is enabled.
         * Null when the Bot Detection module is not enabled.
         */
        private ClientAutomationData automation;

        public String getHeaderUserAgent() {
            return headerUserAgent;
        }

        public ClientTimeZoneData getTimeZone() {
            return timeZone;
        }

        public ClientBrowserData getBrowser() {
            return browser;
        }

        public ClientBrowserEngineData getBrowserEngine() {
            return browserEngine;
        }

        public ClientDeviceData getDevice() {
            return device;
        }

        public ClientOSData getOs() {
            return os;
        }

        public TLSSignatureData getTlsSignature() {
            return tlsSignature;
        }

        public ClientAutomationData getAutomation() {
            return automation;
        }

        @Override
        public String toString() {
            return "ClientData{headerUserAgent='" + headerUserAgent + "', timeZone=" + timeZone + ", browser="
                    + browser + ", browserEngine=" + browserEngine + ", device=" + device + ", os=" + os
                    + ", tlsSignature=" + tlsSignature + ", automation=" + automation + "}";
        }
    }
}
