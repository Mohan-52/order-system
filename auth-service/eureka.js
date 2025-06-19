const { Eureka } = require("eureka-js-client");

const eurekaClient = new Eureka({
  instance: {
    app: "AUTH-SERVICE",
    instanceId: "auth-service-instance-1",
    hostName: "localhost",
    ipAddr: "127.0.0.1",
    statusPageUrl: "http://localhost:8085",
    port: {
      $: 8085,
      "@enabled": true,
    },
    vipAddress: "auth-service",
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn",
    },
  },
  eureka: {
    host: "localhost", // Eureka server hostname
    port: 8761, // Eureka server port
    servicePath: "/eureka/apps/",
  },
});

module.exports = eurekaClient;
