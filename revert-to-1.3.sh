#!/bin/csh -f
# 
#  revert-to-1.3.sh     
#  (c) 2002 Ute Platzer
#

if ($#argv == 0) then 
echo ""
echo ""
echo " This script can revert orbital sources for java 1.4 to java 1.3 version."
echo " To work, the following requirements must be fulfilled:"
echo ""
echo " 1. orbital sources must be in a subdirectory of the current directory" 
echo "    calles j4-src/ (if j4-src does not exist, we assume that the sources"
echo "	  are still in src/ and therefore the script moves src/ to j4-src/)"
echo "" 
echo " 2. special modified java.util.logging sources must be in a directory"
echo "	   called resources/java/util/logging"
echo "" 
echo " 3. the sed-script revert-to-1.3.sed must be in the current directory"
echo ""
echo " (4. for ant to run properly, a directory lib/ mut be present)"
echo ""  
echo " If these requirements are met, just call this script and it will"  
echo " generate  Java 1.3 compatible source code in a directory called src/" 
echo " After that, call 'ant' to compile the new sources, and if " 
echo " compilation is successful, call 'ant jar' to create the orbital.jar"
echo " file in the dist/ directory." 
echo ""
echo " To run the script, pass a parameter to indicte your acceptance."
echo ""
echo ""
exit(-1)
endif

# remove old versions: 
if ( -e j4-src ) then
  echo removing old src directory...
  rm -rf src/
else
  echo moving sources to j4-src/
  mv src/ j4-src
endif

# create the directory tree structure: 
echo creating new src directory...
mkdir src

#of the orbital library
echo creating directory structure for orbital library...
cd j4-src
find -type d -exec mkdir ../src/\{\} \;


# determine names of java files ot be modified and 
# write them into a file which can then be executed 
# to make the modification and copying: 
echo preparing to sed orbital source files...
rm -f t
find -name "*.java" -exec echo sed -f ../revert-to-1.3.sed \{\} \> ../src/\{\} >> t \;
# execute the file: 
echo making replacements... 
 source t

#do the same for jdk1.4 files:

#directory structure for necessary java source files
cd ..
mkdir src/orbital/util/logging

echo preparing replacements for jkd1.4 sources...
cd resources/java/util/logging
rm -f t
find -name "*.java" -exec echo sed -f ../../../../revert-to-1.3.sed \{\} \> ../../../../src/orbital/util/logging/\{\} >> t \;
# execute the file: 
echo making replacements... 
source t

#back to orbital project directory: 
cd ../../../../

# remove aspects, which cannot be compiled:
# (they are not needed)
echo removing aspects...
rm -rf src/orbital/moon/aspects/*
# remove deprecated files:
#first rescue used files
echo rescuing deprecated-used...
mv src/orbital/moon/deprecated/used/* src/orbital/
echo removing  deprecated...
rm -rf src/orbital/moon/deprecated/*

# remove unwanted constructor call in InnerCheckedException: 
sed -e 's/super(message, cause)/super(message)/' src/orbital/util/InnerCheckedException.java > t
mv -f t src/orbital/util/InnerCheckedException.java

# remove unwanted instantiation of InternalError: 
sed -e 's/throw new InternalError(asserted)/throw new InternalError(asserted.toString())/' src/orbital/algorithm/evolutionary/Gene.java > t
mv -f t src/orbital/algorithm/evolutionary/Gene.java

# copy LogicParser.jj
cp j4-src/orbital/moon/logic/LogicParser.jj src/orbital/moon/logic/

#
