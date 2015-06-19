Peacecraft
==========

Code formerly used to power [Peacecraft](http://peacecraft-ec.com)'s Bukkit server.


PeacecraftCore
--------------

PeacecraftCore provides the plugin's foundation, including a module system to prevent a single error from bringing down the entire plugin, database access, flatfile storage, schematics, a translation system, and backend code for Peacecraft's various web systems, such as stats and web chat.


PeacecraftBukkit
----------------

PeacecraftBukkit provides the various modules used on the Peacecraft Bukkit server, such as world backups, chat management, an economy, towns, permission groups, portals, block protection, game restrictions, stats tracking, and world management. All modules can be enabled or disabled in the plugin's central configuration file. Modules that make use of the economy or permissions system can also use other plugins that support the Vault API, and vice versa. It runs on top of PeacecraftCore.

Requires Vault to run, and most functionality requires a Redis server for data storage.


Building
--------

To build all projects, run "mvn clean install" in the root directory of this repository. Each project can be individually built by running "mvn clean install" in its respective folder.


License
-------

The code contained in this repository is free to use under the MIT License. See LICENSE.txt for more information. While it is not strictly required by the license, credit would be much appreciated if you make use of this project.