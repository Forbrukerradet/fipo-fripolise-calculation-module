#Fripolisekalkulator
Finansportalen.no's calculation module for Fripolisekalkulator.

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