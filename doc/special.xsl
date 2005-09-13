<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- specific parameters templates, different for different pages -->


<!-- path to cvs repository -->
<xsl:variable name="CVSPATH">C:\\Personal\\repository/Web/src</xsl:variable>

<!-- name + path of the picture for the left border -->
<xsl:variable name="randBild"></xsl:variable>

<!-- background color for whole page -->
<xsl:variable name="wholeBGColor">#f0ffff<!--#0202cc--></xsl:variable>

<!-- background color for body -->
<xsl:variable name="bodyBGColor">#ffffff</xsl:variable>

<!-- background color for left border -->
<xsl:variable name="randBGColor"></xsl:variable>

<!-- foreground color for footer text -->
<xsl:variable name="footerColor">#1193CF<!--#E3F0FD--></xsl:variable>

<!-- foreground color for title text -->
<xsl:variable name="titleColor">#1193CF<!--#2CFAEE--></xsl:variable>

<!-- the title to be displayed in the document -->
<!-- MUST be surrounded by tr tags -->
<xsl:template match="title" mode="mainpage">
   <tr><td width="100%" height="50" align="center" valign="center" 
           style="padding:6pt 0pt;">
       <h1><font style="font-size: 24pt;" face="Sans-Serif">
           <xsl:attribute name="color"><xsl:value-of select="$titleColor"/></xsl:attribute>
           <xsl:apply-templates/>
       </font></h1>
   </td></tr>
</xsl:template>

<!-- a table row with a logo inside -->
<!-- MUST be surrounded by tr tags -->
<xsl:template name="logo">
   <tr>
      <td height="80" align="center" valign="center" style="padding:6pt 0pt;">
<!--<i><b><font color="#2CAFEE">functologic</font></b></i>-->
      </td>
   </tr>
</xsl:template>


<!-- the e-mail address of the webmaster -->
<!-- MUST be surrounded by TD tags -->
<xsl:template name="webmaster">
    <td width="50" align="right">
      <a href="http://www.functologic.com/feedback.html">
      <!-- image content will not get forwarded -->
        <font><xsl:attribute name="color"><xsl:value-of select="$footerColor"/></xsl:attribute>
	  <img src="http://www.uni-karlsruhe.de/~Andre.Platzer/mailto.gif" height="26" width="30" alt="send mail" />
        </font>
      </a>
    </td>
</xsl:template>

<!-- invoked within html/head in order to stamp user-specific information -->
<xsl:template name="stampHead">
  <meta name="author" content="Andre Platzer" />
  <meta name="copyright" content="&#169; 1996-2003 Andr&#233; Platzer" />
  <!-- <meta name="robots" content="INDEX, FOLLOW" /> -->
  <link rel="start" href="http://www.functologic.com/" type="text/html" title="functologic.com" />
</xsl:template>


<!-- the separator bar between title and main page content -->
<!-- MUST be surrounded by td tag -->
<xsl:template name="separator">
    <td></td> 
</xsl:template>

</xsl:stylesheet>