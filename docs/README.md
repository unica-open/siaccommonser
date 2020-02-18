# Configurations
All configuration MUST be set in the `buildfiles/<env>.properties` file used for compilation
- nome.ambiente = the name of the environment
- flag-debug = the Java compiler flag to activate debug symbols (on/off)
- flag-optimize = the Java compiler flag to activate compile-time optimizations (on/off)
- flag-compress = the Java packager flag to activate packaging compressions for
    WAR/EAR/JAR files (on/off)
- current.env = the currently executing environment
