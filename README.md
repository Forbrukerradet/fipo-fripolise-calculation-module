# Fripolisekalkulator
Finansportalen.no's calculation module for Fripolisekalkulator.
https://www.finansportalen.no/pensjon/fripolisekalkulator/

# Maven
To include `fripolise-calculation-module` library to your Maven project:

* Add following repository to your Maven configuration:

    ```
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-finansportalen-maven</id>
        <name>bintray</name>
        <url>http://dl.bintray.com/finansportalen/maven</url>
    </repository>
    ```

* Add dependency to `fripolise-calculation-module`:

    ```
    <dependency>
        <groupId>no.finansportalen</groupId>
        <artifactId>fripolise-calculation-module</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```
# Tests
Tests require a Java Scripting API compliant JS engine. Current setup uses Nashorn which is packageg with JDK. 
When using Nashorn, use JDK versions 1.8.0_77 and up.