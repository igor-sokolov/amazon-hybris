# Hybris modules for Amazon web services infrustructure

Currently it sonsists only of one module with aim to add centralized session repository based on Spring Session. AWS provides a service ElastiCache which can be Redis-compatible. This should offer following benefits:
- provide full session fail-over
- refuse from sticky session necessity
- add dynamic scale-up and down for Application layer

## Installation
The extension is addon so you need to install it to each storefront extension:
It can be done through ant command ```addoninstall```:
```
ant addoninstall -Daddonnames="redissessionaddon" -DaddonStorefront.yacceleratorstorefront="yacceleratorstorefront"
```
