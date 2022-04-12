# Friendly Captcha Verification API Client

This client library allows JVM-based applications to
verify [Friendly Captcha](https://www.friendlycaptcha.com) puzzle solutions. It wraps the necessary
call and interprets the result.

## Usage

Include the dependency using Maven

```xml

<dependency>
  <groupId>org.drjekyll</groupId>
  <artifactId>friendlycaptcha</artifactId>
  <version>1.0.0</version>
</dependency>
```

or Gradle:

```groovy
implementation 'org.drjekyll:friendlycaptcha:1.0.0'
```

Run your build tool and you can include the verifier as follows:

```java
import org.drjekyll.friendlycaptcha.FriendlyCaptchaVerifier;

public class FriendlyCaptchaExample {

  private final FriendlyCaptchaVerifier friendlyCaptchaVerifier = FriendlyCaptchaVerifier
    .builder()
    .apiKey("YOUR_API_KEY")
    .sitekey("AN_OPTIONAL_SITE_KEY")
    .build();

  public void checkSolution(String solution) {

    boolean success = friendlyCaptchaVerifier.verify(solution);
    if (success) {
      // continue
    } else {
      // stop processing
    }

  }

}
```

## Parameters

The Friendly Captcha Verifier currently supports the following builder methods:

* `.apiKey(...)` An API key that proves it's you, create one on the Friendly Captcha website
* `.objectMapper(...)` If you would like to use an existing or custom object mapper
* `.verificationEndpoint(...)` An `URI` object that can point to another verification endpoint (for example if you would like to use EU hosts). Default is: https://api.friendlycaptcha.com/api/v1/siteverify
* `.connectTimeout(...)` allows you to change the default connection timeout of 10 seconds. 0 is interpreted as infinite, null uses the system default
* `.socketTimeout(...)` allows you to change the default socket timeout of 10 seconds. 0 is interpreted as infinite, null uses the system default
* `.sitekey(...)` is an optional sitekey that you want to make sure the puzzle was generated from.
* `.proxy(...)` allows you to use a proxy to connect to the verification API

## Development

To build and locally install the library and run the tests, just call

  mvn install

## Contributing

Please read [the contribution document](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/dheid/friendlycaptcha/tags).

## License

This project is licensed under the LGPL License - see the [license](LICENSE) file for details.

## Release Notes

### 1.0.0

* Initial version