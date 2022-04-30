package com.glureau.tigrou.xml

actual fun provideXmlDeserializer(): XmlDeserializer = JvmXmlDeserializer()