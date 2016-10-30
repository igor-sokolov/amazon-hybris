# Hybris modules for Amazon web services infrustructure

Currently it sonsists only of one module with aim to add centralized session repository based on Spring Session. AWS provides a service ElastiCache which can be Redis-compatible. This should offer following benefits:
- provide full session fail-over
- refuse from sticky session necessity
- add dynamic scale-up and down for Application layer
