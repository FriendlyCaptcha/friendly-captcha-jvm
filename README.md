# Friendly Captcha JVM SDK

[![Maven Central Version](https://img.shields.io/maven-central/v/com.friendlycaptcha.jvm/sdk)
](https://central.sonatype.com/artifact/com.friendlycaptcha.jvm/sdk) [![javadoc](https://javadoc.io/badge2/com.friendlycaptcha.jvm/sdk/javadoc.svg)](https://javadoc.io/doc/com.friendlycaptcha.jvm/sdk) 


A Java SDK for [Friendly Captcha](https://friendlycaptcha.com) that can be used from JVM languages like Java, Kotlin, Scala, Clojure, Groovy, etc. This SDK provides a simple way to interact with the Friendly Captcha API. It is compatible with Java 8 and later.

> This library is for [Friendly Captcha v2 only](https://developer.friendlycaptcha.com/). If you are looking for a v1 library, we recommend using [dheid/friendlycaptcha](https://github.com/dheid/friendlycaptcha).

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.friendlycaptcha.jvm</groupId>
    <artifactId>sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

First we create a client - you usually only create one and re-use it.
```java
FriendlyCaptchaClient client = new FriendlyCaptchaClient(
    new FriendlyCaptchaClientOptions()
                        .setApiKey(apiKey)
                        .setSitekey(sitekey)
);
```

Then we can use the client to verify the captcha response (which is sent in the `frc-captcha-response` form field by default) in a request handler or middleware:
```java
// ...
String responseToken = formData.get("frc-captcha-response");
VerifyResult result = client.verifyCaptchaResponse(responseToken).get();

if (!result.wasAbleToVerify()) {
    // Alert yourself: something went wrong, we weren't able to verify the captcha.
    // Maybe the API is down, or your credentials are incorrect.
    System.out.println("COULD NOT VERIFY FRIENDLY CAPTCHA RESPONSE!\n" + 
    "> Error Code: " + result.getErrorCode() + "\n" +
    "> Response Error: " + result.getResponseError());
}

if (!result.shouldAccept()) {
    // The captcha should be rejected.
    // Alert the user that they should try again.

    model.put("message", "❌ Captcha verification failed! Please try again.");
    exchange.sendResponseHeaders(400, 0);
    return;
}

// Captcha verification successful, continue with your application logic.
// ...
```

## Development
You can build the SDK using 

```bash
./gradlew :sdk:build
```

and run the tests using

```bash
docker run -p 1090:1090 friendlycaptcha/sdk-testserver:latest
./gradlew :sdk:test
```

Make sure you are running the SDK Testserver first

### Minting a release

Bump the version in `sdk/build.gradle`, run `./gradlew :sdk:build`, merge the changes to `main` and create a new release on GitHub.

## Example

A standalone example can be found in [Example.java](examples/src/main/java/com/friendlycaptcha/jvm/examples/Example.java).

This example serves a HTML form with a Friendly Captcha widget and validates the user's response.

![Screenshot](example.png)

To run the example, execute the following commands:

```shell
FRC_SITEKEY=<your sitekey> FRC_APIKEY=<your api key> ./gradlew :examples:run
```

Then open [http://localhost:8080](http://localhost:8080) in your browser.

## License
This is open-source software licensed under the [MIT license](LICENSE).
