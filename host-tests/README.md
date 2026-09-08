# Car service Java host regression

Run small production algorithms and their real unit tests without an Android
platform checkout or device. This entry point compiles the original service
files directly and reuses the existing tests from `tests/carservice_unit_test`.

## Run

Install JDK 17 and Apache Maven 3.9.9, then run from the repository root:

```sh
mvn -f host-tests/pom.xml clean test
mvn -f host-tests/pom.xml package
```

The first run downloads the versioned Maven plugins and test-only dependencies
from Maven Central. Production compilation uses only the Java standard library,
with Java 11 bytecode compatibility. No Android SDK or substitute platform
classes are used. The POM pins the direct test dependencies and build plugins.
To inspect the complete resolved test dependency tree:

```sh
mvn -f host-tests/pom.xml dependency:tree
```

JUnit XML and text reports are in `host-tests/target/surefire-reports`.
Compiled classes are in `host-tests/target/classes`; `package` also creates
`host-tests/target/car-service-host-regression-1.0-SNAPSHOT.jar`.

## What runs

- `WindowDumpParser`: window selection used by the package manager's dialog and
  driving-distraction checks; reuses `WindowDumpParserTest`.
- `SetMultimap`: profile-inhibit records used by Bluetooth; reuses `SetMultimapTest`.
- `SlidingWindow`: bounded I/O sample retention and threshold counting used by
  storage monitoring; reuses `SlidingWindowTest`.
- `TimeSource`: UTC date/time boundaries used by watchdog storage and retention;
  `TimeSourceTest` exercises deterministic boundary instants through the real
  abstraction. The same test source belongs to the existing Android watchdog
  unit-test source glob.

This is a local regression target for those four Java units. It does not build
CarService, its Binder/HAL integration, generated platform code, or Android
instrumentation tests. Device and full-platform verification still use the
existing Soong test targets. No CI workflow is added by this entry point.
