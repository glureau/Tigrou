package com.glureau.tigrou.xml

import com.glureau.tigrou.sitemap.Sitemap
import com.glureau.tigrou.sitemap.SitemapImage
import com.glureau.tigrou.sitemap.SitemapIndex
import com.glureau.tigrou.sitemap.SitemapUrl
import com.glureau.tigrou.sitemap.SitemapUrlSet
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.ByteArrayInputStream
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

class JvmXmlDeserializer : XmlDeserializer {
    private val dbf = DocumentBuilderFactory.newInstance().apply {
        setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }

    override fun parseSitemapIndex(data: String): SitemapIndex {
        val doc = dbf.newDocumentBuilder().parse(ByteArrayInputStream(data.toByteArray()))
        assert(doc.documentElement.tagName == "sitemapindex")
        val sitemaps = mutableListOf<Sitemap>()
        doc.documentElement.getElementsByTagName("sitemap").forEach { sitemap ->
            sitemaps.add(
                Sitemap(
                    loc = sitemap.childNodes.filterName("loc")!!.textContent,
                    lastmod = sitemap.childNodes.filterName("lastmod")?.textContent
                )
            )
        }
        return SitemapIndex(sitemaps)
    }

    override fun parseUrlSet(data: String): SitemapUrlSet {
        val doc = dbf.newDocumentBuilder().parse(ByteArrayInputStream(data.toByteArray()))
        assert(doc.documentElement.tagName == "urlset")
        val urls = mutableListOf<SitemapUrl>()
        doc.documentElement.getElementsByTagName("url").forEach { url ->
            val images = url.childNodes.filter { it.nodeName == "image:image" }.map { img ->
                SitemapImage(
                    loc = img.childNodes.filterName("image:loc")!!.textContent,
                    title = img.childNodes.filterName("image:title")?.textContent,
                    geo_location = img.childNodes.filterName("geo_location")?.textContent,
                    caption = img.childNodes.filterName("caption")?.textContent,
                    license = img.childNodes.filterName("license")?.textContent,
                )
            }
            urls.add(
                SitemapUrl(
                    loc = url.childNodes.filterName("loc")!!.textContent,
                    lastmod = url.childNodes.filterName("lastmod")?.textContent,
                    changefreq = url.childNodes.filterName("changefreq")?.textContent,
                    priority = url.childNodes.filterName("priority")?.textContent,
                    image = images,
                )
            )
        }
        return SitemapUrlSet(urls)
    }

    private fun NodeList.filterName(nodeName: String): Node? =
        filter { it.nodeName == nodeName }.firstOrNull()

    private fun NodeList.filter(condition: (node: Node) -> Boolean): List<Node> {
        val newList = mutableListOf<Node>()
        forEach {
            if (condition(it)) newList.add(it)
        }
        return newList
    }

    private fun <T> NodeList.map(transform: (node: Node) -> T): List<T> {
        val newList = mutableListOf<T>()
        forEach {
            newList.add(transform(it))
        }
        return newList
    }

    private fun NodeList.forEach(block: (node: Node) -> Unit) {
        for (i in 0 until this.length) {
            this.item(i)?.let(block)
        }
    }
}