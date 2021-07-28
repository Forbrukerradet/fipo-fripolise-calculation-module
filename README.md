# Fripolisekalkulator

This is finansportalen.no calculation library for Fripolisekalkulator https://www.finansportalen.no/pensjon/fripolisekalkulator

For any questions related this code contact Finansportalen https://www.finansportalen.no/andre-valg/kontaktskjema

The library is hosted on the maven central repository https://search.maven.org/search?q=a:fipo-fripolise-calculation-module

# Maven
To include the library to your Maven project. Add a dependency:

  ```
  <dependency>
    <groupId>no.finansportalen</groupId>
    <artifactId>fipo-fripolise-calculation-module</artifactId>
    <version>1.0.3</version>
  </dependency>
  ```

# Tests
Tests require a Java Scripting API compliant JS engine. Current setup uses Nashorn which is packaged with JDK. 
When using Nashorn, use JDK versions 1.8.0_77 and up.
