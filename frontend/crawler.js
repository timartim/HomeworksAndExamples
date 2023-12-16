const linkRegex = new RegExp(/href="(http[^"]+)"/g);


class CrawlData {
    constructor(url, depth, content = "", links = []) {
        this.url = url;
        this.depth = depth;
        this.content = content;
        this.links = links;
    }
}
async function executeLink(url, deph) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            return new CrawlData(url, deph, "", []);
        }
        const html = await response.text();
        let match;
        let currentElement = new CrawlData(url, deph, html, [])
        while ((match = linkRegex.exec(html)) !== null) {
            currentElement.links.push(match[1]);
        }
        currentElement.depth = deph
        return currentElement;
    } catch (error) {
        return new CrawlData(url, deph, "", [])
    }
}

async function crawl(url, depth, concurrency) {
    var activeTasks = []
    activeTasks.push(new CrawlData(url, depth, "", []));
    const crawlAnswer = []
    while (activeTasks.length > 0){
        let packet = activeTasks.slice(0, Math.min(activeTasks.length, concurrency))
        activeTasks = activeTasks.slice(Math.min(activeTasks.length, concurrency))
        const activeTasksPromises = await Promise.all(packet.map(async (data) => {
            return await (executeLink(data.url, data.depth))
        }))
        for(let currentElement of activeTasksPromises){
            if(currentElement.depth >= 1){
                crawlAnswer.push(currentElement)
                for(let link of currentElement.links){
                    activeTasks.push(new CrawlData(link, currentElement.depth - 1, "", []))
                }
            }
        }
    }
    return crawlAnswer;
}

module.exports = crawl;