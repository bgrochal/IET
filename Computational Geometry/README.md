#GeomVisualiser
Planar geometry visualiser, integrated with [GeoGebra](https://www.geogebra.org/) open source software, and some computational geometry algorithms written within the "Computational Geometry" classes held at AGH UST. Technology stack: Java.

#Installation

##Prerequisites
It is required to have following tools installed:
- Java 1.8 (tested on 1.8.0_102) with JavaFX support.
- Maven (tested on 3.3.9)
- Gradle (tested on 3.1.)

##Installation for development purposes
###GeoGebra setup
1. Clone latest version of [GeoGebra repository](https://github.com/geogebra/geogebra).
2. Build GeoGebra ```*.jar``` by typing following commands:  
```
cd [directory containing repository]
gradle jar
```  
###Application setup
1. Clone this repository.
2. Include following ```*.jar``` files located in GeoGebra's directory as libraries to the project:  
```
common.jar
common-jre.jar
desktop.jar
editor-desktop.jar
giac-jni.jar
impl.jar
jogl2.jar
```
3. Run ```com.github.bgrochal.geomvisualiser.app.GeomVisualiser``` class.
