# cloud-crawler

**cloud-crawler** is a distributed crawler developed on the basis of [crawler4j](https://github.com/yasserg/crawler4j).
> see project group @ [https://team.oschina.net/cloudwalk](https://team.oschina.net/cloudwalk "workgroup page")

## Basic structure
In current design, the cloud-crawler has one master-node and multiple slave-nodes which would all be deployed on cloud servers(such as [AWS](http://aws.amazon.com/) and [AliYun](https://www.aliyun.com)). The graph below shows a sketch of the basic project structure, note that **the structure may be modified  at any time if we find it not applicable or better design comes out**.

![](http://i.imgur.com/vzyrvtg.jpg)

- The master-node contains the coordinator module and databases. The coordinator accesses the crawling queue in Key-Value database and dispatches work tasks to the crawlers on slave-nodes, this module is designed to handle multiple slave-nodes' requests(which means the slave-node should be easily added or removed to ensure flexible extension).
- There are two kinds of databases installed on master-node, in current design they are [Redis](http://redis.io/) and [MySQL](https://www.mysql.com/). while Redis-DB serves as the working queue and replaces the Berkeley-DB in the originate crawler4j project, MySQL-DB stores relatively fixed data such as slave-nodes' status, crawling task status, etc.
- The crawlers on slave-nodes are originated from crawler4j project, with their controller and database access changed in order to work as part of the distributed system. A communication module should be implemented so that the crawlers can link up with the coordinator on the master-node.
- There are FTP servers on slave-nodes so that the user may get the crawling products(the pages crawled will be processed on local slave-node, and if there are any processing results, they will be push to the FTP server).

## Optional objectives
These following features might be implemented if possible:

- Create a [docker](http://www.docker.com/) image containing the environment of slave-node for fast deployment and extension.
- Use web pages to show the current status and progress of the crawlers
- ...(more to be added)
 