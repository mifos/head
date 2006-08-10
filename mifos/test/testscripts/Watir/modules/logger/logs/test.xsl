<?xml version="1.0" encoding="utf-8"?>
<!-- test.xsl - Transform for self-explanatory XSLT exercise -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  version="1.0">  
<xsl:template match="/">
<html>
  <body>
    <h2>Results</h2>
    <table border="1">
    <tr bgcolor="#9acd32">
      <th align="left">Testcase</th>
      <th align="left">Input</th>
      <th align="Left">Expected</th>
      <th align="Left">Result</th>
    </tr>
    <xsl:for-each select="results/result">
    <tr>
      <td><xsl:value-of select="testcase"/></td>
      <td><xsl:value-of select="Input"/></td>
      <td><xsl:value-of select="Expected"/></td>
      <td><xsl:value-of select="Status"/></td>
    </tr>
    </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>