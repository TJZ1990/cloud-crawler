# cloud-crawler

**cloud-crawler** is a simple web crawler designed to be deployed on cloud servers. 
> see project group @ [https://team.oschina.net/cloudwalk](https://team.oschina.net/cloudwalk "workgroup page")

## Basic structure
In current design, the cloud-crawler has one master-node and multiple slave-nodes which would all be deployed on cloud servers(such as [AWS](http://aws.amazon.com/) and [AliYun](https://www.aliyun.com)).

The graph below shows a sketch of the basic project structure, note that the structure may be modified if we find it not applicable or better design comes out.

![](http://i.imgur.com/vzyrvtg.jpg)

- The master-node contains the coordinator module and databases. The coordinator accesses the crawling queue in Key-Value database and dispatches work tasks to the crawlers on slave-nodes, this module is designed to handle multiple slave-nodes' requests(which means the slave-node should be easily added or removed to ensure flexible extension).
- There are two kinds of databases installed on master-node, in current design they are [Redis](http://redis.io/) and [MySQL](https://www.mysql.com/). While Redis-DB serves as the working queue and replaces the Berkeley-DB in the originate crawler4j project, MySQL-DB stores relatively fixed data such as slave-nodes' status, crawling task status, etc.
- The crawler on slave-nodes uses a few modules from the [crawler4j](https://github.com/yasserg/crawler4j) project on github, with its controller and database access changed in order to work as part of the distributed system. A communication module is implemented so that the crawlers can link up with the coordinator on the master-node.
- There are FTP servers on slave-nodes so that the user may get the crawling products(the pages crawled will be processed on local slave-node, and if there are any processing results, they will be pushed to the FTP server waiting for download).

## Brief deployment Guide(On Linux server)
###Normal way
1. Install java runtime and add environment variables.
2. (Ignore this step on slave-node) Install and configure Redis and MySQL services.
3. Set up FTP service so that file can be transferred.
4. Upload the project's jar file(**The program for master-node and slave-node will be packaged in one jar file**) and "cloud-crawler.sh"(under "./deployment/shell" folder) to servers, execute "cloud-crawler.sh" with **different arguments** in Linux shell to start each service.


###Using docker
1. Get docker image at [release](https://github.com/TJZ1990/cloud-crawler/releases) page.
2. Deploy.

## Optional objectives
These following features might be implemented if possible:

- Create a [docker](http://www.docker.com/) image containing the environment of slave-node for fast deployment and extension.
- Use web pages to show the current status and progress of the crawlers
- ...(more to be added)
 