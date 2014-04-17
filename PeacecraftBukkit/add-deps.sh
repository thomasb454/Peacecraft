#!/bin/sh
mvn install:install-file -Dfile=lib/DonationPoints.jar -DgroupId=donationpoints -DartifactId=donationpoints -Dversion=latest -Dpackaging=jar
mvn install:install-file -Dfile=lib/Essentials.jar -DgroupId=essentials -DartifactId=essentials -Dversion=latest -Dpackaging=jar
mvn install:install-file -Dfile=lib/LWC.jar -DgroupId=lwc -DartifactId=lwc -Dversion=latest -Dpackaging=jar
mvn install:install-file -Dfile=lib/WorldEdit.jar -DgroupId=com.sk89q -DartifactId=worldedit -Dversion=latest -Dpackaging=jar
mvn install:install-file -Dfile=lib/Vault.jar -DgroupId=net.milkbowl.vault -DartifactId=Vault -Dversion=latest -Dpackaging=jar
