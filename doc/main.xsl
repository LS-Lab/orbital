<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- common font properties -->
<xsl:import href="special.xsl"/>
<!-- transformation for common elements, (images, text, etc.) -->
<xsl:import href="common.xsl"/> 



<!-- main structure of page -->
<xsl:template match="html">

   <html> 
     <head>
  	  <title>
             <xsl:apply-templates select="/html/head/title" mode="header"/>
          </title>
          <xsl:apply-templates select="head"/>
	  

          <xsl:variable name="homepath">
             <xsl:call-template name="pathToHome"/>
          </xsl:variable>

          <link rel="stylesheet" type="text/css"> 
             <xsl:attribute name="href">
                <xsl:value-of select="concat($homepath, 'layout.css')"/>
             </xsl:attribute>
          </link>

	  <xsl:call-template name="stampHead"/>
     </head>
    
     <xsl:apply-templates select="body"/>
  </html>
</xsl:template>
<!-- end of main page structure -->


<!-- copy everything == ID-abbildung --> 
<xsl:template match="@*|node()" priority="-100">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

<!-- the date of the last modification -->
<xsl:template match="date">
    <xsl:variable name="text"><xsl:value-of select="."/></xsl:variable>
    <xsl:variable name="datum">
    	<xsl:value-of select="translate(substring-before(substring-after($text, 'Date: '), ' '), '/', '-')"/>
    </xsl:variable>
    <xsl:if test="string-length($datum) > 0">
    	   Last modified:
           <xsl:value-of select="$datum"/>
    </xsl:if>
</xsl:template>




</xsl:stylesheet>

