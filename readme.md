Spring-cloud app with distributed lock
=

This demo application is intended to use as a proxy for __non-threadsafe-service__ to organize synchronized access for clients in clustered configuration.
It uses Zookeeper as a distributed lock provider. So Zookeeper should be started in standalone mode.