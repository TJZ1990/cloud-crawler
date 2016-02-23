# cloud-crawler

**cloud-crawler** is a simple web crawler designed to be deployed on cloud servers. 
> see project group @ [https://team.oschina.net/cloudwalk](https://team.oschina.net/cloudwalk "workgroup page")

## Basic structure
In current design, the cloud-crawler has one master-node and multiple slave-nodes which would all be deployed on cloud servers(such as [AWS](http://aws.amazon.com/) and [AliYun](https://www.aliyun.com)).

The graph below shows a sketch of the basic project structure, note that the structure may be modified if we find it not applicable or a better design comes out.

![](http://i.imgur.com/vzyrvtg.jpg)

- The master-node contains the coordinator module and databases. The coordinator accesses the crawling queue in Key-Value database and dispatches work tasks to the crawlers on slave-nodes, this module is designed to handle multiple slave-nodes' requests(which means the slave-node should be easily added or removed to ensure flexible extension).
- There are two kinds of databases installed on master-node, in current design they are [Redis](http://redis.io/) and [MySQL](https://www.mysql.com/). While Redis-DB serves as the working queue and replaces the Berkeley-DB in the original crawler4j project, MySQL-DB stores relatively fixed data such as slave-nodes' status, crawling task status, etc.
- The crawler on slave-nodes uses a few modules from the [crawler4j](https://github.com/yasserg/crawler4j) project on github, with its controller and database access changed in order to work as part of the distributed system. A communication module is implemented so that the crawlers can link up with the coordinator on the master-node.
- There are FTP servers on slave-nodes so that the user may get the crawling products(the pages crawled will be processed on local slave-node, and if there are any processing results, they will be pushed to the FTP server waiting for download).

## Brief deployment guide(On Linux server)
### Normal way

See complete version [here](https://github.com/TJZ1990/cloud-crawler/blob/master/deployment/full-deployment-report.md).
- Set up FTP service so that file can be transferred.
- Install java runtime environment and add environment variables.
- (Ignore this step on slave-node) Install and configure Redis and MySQL services.
- Upload the project's jar file(**The program for master-node and slave-node will be packaged in one jar file**) and "cloud-crawler.sh"(under "deployment/shell/") to servers, execute "cloud-crawler.sh" with **different arguments** in Linux shell to start each service.  
`$ mv -iuv /usr/rootftp/cloud-crawler-jar-with-dependencies.jar /usr/crawler`  
`$ chmod +x cloud-crawler.sh`  
`$ ./cloud-crawler.sh -m 或 cloud-crawler.sh -s 或 ./cloud-crawler.sh -s IP PORT CODE`  

### Using docker(NOT AVAILABLE YET)
- Set up master node as mentioned above.
- Get slave's docker image at [release](https://github.com/TJZ1990/cloud-crawler/releases) page or under "deployment/app/".
- Deploy slave-nodes on using the docker image on as many servers as you want.

## Optional objectives
These following features might be implemented if possible:
- Create a [docker](http://www.docker.com/) image containing the environment of slave-node for fast deployment and extension.
- ~~Use web pages to show the current status and progress of the crawlers.~~√
- ...(more to be added)

## Development guide
### API guide (Request from Front-end)
Request: /slave.json

Response:

    {
      "hostname": "localhost",
      "ip": "127.0.0.1",
      "port": -1,
      "name": "slave0",
      "number": 0
    }

Request: /slaves.json

Response:

    {
      "slave0": {
        "hostname": "localhost",
        "ip": "127.0.0.1",
        "port": -1,
        "name": "slave0",
        "number": 0
      },
      "slave1": {
        "hostname": "localhost",
        "ip": "127.0.0.1",
        "port": -1,
        "name": "slave1",
        "number": 0
      },
      "slave2": {
        "hostname": "remote",
        "ip": "127.0.0.1",
        "port": -1,
        "name": "slave0",
        "number": 0
      },
      "slave3": {
        "hostname": "remote",
        "ip": "127.0.0.1",
        "port": -1,
        "name": "slave1",
        "number": 0
       }
    }

Request: /number.json

Response:

    {
      "slave0": 1,
      "slave1": 2,
      "slave2": 3,
      "slave3": 4
    }

