#x
s/^ \* @(#)\([A-Za-z0-9_]+\)\.java [ \t]*\([0-9.]+\) [ \t]*\([0-9\/]+\) [ \t]*Andr‚ Platzer/ \* @(#)\1\.java \2 19\3 Andre Platzer/
s/^ \* Copyright (c) 19\([0-9]+\) Andr‚ Platzer\. All Rights Reserved\./ \* Copyright (c) 19\1 Andre Platzer. All Rights Reserved./
s/^ \* Copyright (c) 19\([0-9]+\) Andr\&eacute; Platzer\. All Rights Reserved\./ * Copyright (c) 19\1 Andre Platzer. All Rights Reserved./
s/^ \* @version [ \t]*/ \* @version /
s/^ \* @author [ \t]*Andre Platzer/ \* @author  Andr\&eacute; Platzer/
s/^ \* @author [ \t]*Andr‚ Platzer/ \* @author  Andr\&eacute; Platzer/
s/^\/\*--- formatted by Jindent 2\.1, \(www\.c-lab\.de\/~jindent\) ---\*\/\n\n//
#s/^ \* @(#)\([A-Za-z0-9_]+\)\.java [ \t]*\([0-9.]+\) [ \t]*95\([0-9\/]+\) [ \t]*Andre Platzer/ \* @(#)\1\.java \2 1995\3 Andre Platzer/
#s/^ \* @(#)\([A-Za-z0-9_]+\)\.java [ \t]*\([0-9.]+\) [ \t]*96\([0-9\/]+\) [ \t]*Andre Platzer/ \* @(#)\1\.java \2 1996\3 Andre Platzer/
#s/^ \* @(#)\([A-Za-z0-9_]+\)\.java [ \t]*\([0-9.]+\) [ \t]*97\([0-9\/]+\) [ \t]*Andre Platzer/ \* @(#)\1\.java \2 1997\3 Andre Platzer/
#s/^ \* @(#)\([A-Za-z0-9_]+\)\.java [ \t]*\([0-9.]+\) [ \t]*98\([0-9\/]+\) [ \t]*Andre Platzer/ \* @(#)\1\.java \2 1998\3 Andre Platzer/
#s/^ \* @(#)\([A-Za-z0-9_]+\)\.java [ \t]*\([0-9.]+\) [ \t]*99\([0-9\/]+\) [ \t]*Andre Platzer/ \* @(#)\1\.java \2 1999\3 Andre Platzer/
# Only in comments:
# s/\<tt\>/\<code\>/
# s/\</tt\>/\<\/code\>/