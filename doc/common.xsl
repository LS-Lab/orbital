<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- transform a html-xml file into a real html file -->
<xsl:output method="html"/>


<xsl:template match="body">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match="ujap">
<i><b><font color="green"><font size="+3">U</font><font size="+2">J</font>ap</font></b></i>
</xsl:template>
  

<xsl:template match="ujapref">
  <a href="{@target}.html"><xsl:apply-templates/></a>
</xsl:template>

<xsl:template match="ujapmail">
  <a href="javascript:to('{@target}@SPAMPROT.ujap.de')"><xsl:value-of select="@target"/> @ ujap.de</a>
</xsl:template>

<!-- for the list of new features in the download page -->

<xsl:template match="newslist">
    <hr/>
    <h2>New features</h2>
    <p>These features are available only fron the <tt>ujap.jar</tt> file, not from the all-in-one zip archive. In order to get the newest version if you don't have a previous version of ujap already, you'll have to download the all-in one zip file and unpack it. Then download the ujap.jar file and copy it to the <tt>lib/</tt> directory inside the ujap directory.</p>
    <ul>
    <xsl:for-each select="new">
       <xsl:sort select="@date"/>
       <li><tt><xsl:value-of select="@date"/></tt> <xsl:apply-templates/></li>
    </xsl:for-each>
    </ul>
</xsl:template>


<!-- copy everything == ID-abbildung --> 
<xsl:template match="@*|node()" priority="-100">
  <xsl:copy>
    <xsl:apply-templates select="@*|node()"/>
  </xsl:copy>
</xsl:template>

<!-- do nothing with the menu -->
<xsl:template match="menupunkt"/>

<!-- for the links page -->
<xsl:template match="linklist">
   <table border="1">
   <xsl:for-each select="link">
      <tr>

      <td valign="top"><a><xsl:attribute name="href"><xsl:value-of select="url"/>
      </xsl:attribute><xsl:value-of select="name"/></a></td>

      <td valign="top"><xsl:value-of select="text"/></td>

      </tr>
   </xsl:for-each>
   </table>
</xsl:template>

<!--
<xsl:template match="date">
<p align="right" valign="bottom">
<xsl:variable name="text">
<xsl:value-of select="."/>
</xsl:variable>
<xsl:value-of select="translate(substring-before(substring-after($text, 'Date: '), ' '), '/', '-')"/>
</p>
</xsl:template>
-->

<!-- absolute path to file, without ending 
     (absolute means from base web directory, "/")  -->
<xsl:template name="filepath">
   <xsl:variable name="tmp"><xsl:value-of select="substring-after(/html/file, $CVSPATH)"/></xsl:variable>
   <xsl:value-of select="substring-before($tmp, '.xml')"/>
</xsl:template>

<!-- only filename (without ending) -->
<xsl:template name="filename">
    <xsl:call-template name="recfilename">
       <xsl:with-param name="file">
           <xsl:call-template name="filepath"/>
       </xsl:with-param>
    </xsl:call-template>
</xsl:template>

<!-- recursive helper template for 'filename' template -->
<xsl:template name="recfilename">
    <xsl:param name="file"></xsl:param>
    <xsl:choose>
    <xsl:when test="contains($file, '/')">
       <xsl:call-template name="recfilename">
          <xsl:with-param name="file">
          <xsl:value-of select="substring-after($file, '/')"/>
          </xsl:with-param>
       </xsl:call-template>
    </xsl:when>
    <xsl:otherwise> 
       <xsl:value-of select="$file"/>
    </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- path from this file to root directory -->
<!-- e.g. "../../"                         -->
<xsl:template name="pathToHome">
    <xsl:call-template name="createdots">
       <xsl:with-param name="file">
          <xsl:variable name="tmp"><xsl:call-template name="filepath"/>
          </xsl:variable>
          <xsl:value-of select="substring-after($tmp, '/')"/>
       </xsl:with-param>
    </xsl:call-template>
</xsl:template>

<!-- recursive helper template for relpath  and pathToHome templates -->
<!-- for each slash in the filename, write "../" -->
<xsl:template name="createdots">
    <xsl:param name="file"></xsl:param>
    <xsl:choose>
    <xsl:when test="contains($file, '/')">../<xsl:call-template name="createdots">
               <xsl:with-param name="file">
                   <xsl:value-of select="substring-after($file, '/')"/>
               </xsl:with-param>
           </xsl:call-template></xsl:when>
    <xsl:otherwise>
       <!-- do nothing, return  -->
    </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet> 
