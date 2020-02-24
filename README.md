# moneytransfer project

## Notes

This is my first Quarkus project.

For constructor injection no-args constructor is needed, this is different than Spring.

https://quarkus.io/guides/cdi-reference#simplified-constructor-injection

(so a no-args constructor should be generated for an application scoped bean class that does not extend any other class)
https://github.com/quarkusio/quarkus/issues/6722


## API

It is described by ... TODO: add swagger.

## Framework

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application is packageable using `./mvnw package`.
It produces the executable `moneytransfer-1.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/moneytransfer-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or you can use Docker to build the native executable using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/moneytransfer-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .
