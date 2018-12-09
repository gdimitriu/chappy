<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0" exclude-result-prefixes="xs " xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema">
    <!-- Template for '/' -->
    <xsl:template match="/">
        <xsl:apply-templates select="processing"/>
    </xsl:template>
    <!-- Template for 'processing' -->
    <xsl:template match="processing">
        <processing>
            <first>
                <xsl:value-of select="(second,'')[. ne ''][1]"/>
            </first>
            <second>
                <xsl:value-of select="(first,'')[. ne ''][1]"/>
            </second>
            <number>
                <xsl:value-of select="(number,'')[. ne ''][1]"/>
            </number>
        </processing>
    </xsl:template>
</xsl:stylesheet>
