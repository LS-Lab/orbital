#!/bin/csh -f
# also !/bin/tcsh -f works
# 
#  revert-to-1.3.sh     
#  (c) 2002 Ute Platzer
#

# directories to be processed

setenv DIRS "src examples test"

if ($#argv == 0) then 
    echo ""
    echo ""
    echo " This script can revert orbital sources for Java 1.4 back to Java 1.3 version."
    echo " To work, the following requirements must be fulfilled:"
    echo ""
    echo " 1. orbital sources must be in subdirectories of the current directory" 
    echo "    called $DIRS"
    echo "" 
    echo " 2. special modified java.util.logging sources must be in a directory"
    echo "	   called resources/java/util/logging"
    echo "" 
    echo " 3. the sed-script revert-to-1.3.sed must be in the current directory"
    echo ""
    echo " 4. the javac.source=1.3 option should be put into build.properties"
    echo ""  
    echo " If these requirements are met, just call this script and it will"  
    echo " generate  Java 1.3 compatible source code in the directories called" 
    echo " $DIRS"
    echo " After that, call 'ant' to compile the new sources, and if " 
    echo " compilation is successful, call 'ant jar' to create the orbital.jar"
    echo " file in the dist/ directory." 
    echo ""
    echo " To run the script, pass a parameter to indicte your acceptance."
    echo ""
    echo ""
    exit(-1)
endif



foreach SOURCE ($DIRS)
    # $SOURCE is "where are the files to be converted"
    
    # where to put the "old" files during conversion
    # (the old files are moved, and the new, converted files are put
    #  into the tempdir)
    setenv TEMPDIR j4-$SOURCE
    
    # remove old versions: 
    if ( -e $TEMPDIR ) then
      echo removing old $SOURCE directory keeping only $TEMPDIR ...
      rm -rf $SOURCE
    else
      echo moving sources $SOURCE to $TEMPDIR
      mv $SOURCE $TEMPDIR
    endif
    
    echo create new $SOURCE directory...
    echo "and copy the nontransformed files (or simply all files)"
    cp -r $TEMPDIR $SOURCE
    ## create the directory tree structure: 
    #echo creating new $SOURCE directory...
    #mkdir $SOURCE
    ## of the orbital library
    #echo creating directory structure for orbital library...
    cd $TEMPDIR
    #find -type d -exec mkdir ../{$SOURCE}/\{\} \;
    
    
    # determine names of java files ot be modified and 
    # write them into a file which can then be executed 
    # to make the modification and copying: 
    echo preparing to sed orbital source files...
    rm -f t
    find -name "*.jj" -exec echo sed -f ../revert-to-1.3.sed \{\} \> ../{$SOURCE}/\{\} >> t \;
    find -name "*.java" -exec echo sed -f ../revert-to-1.3.sed \{\} \> ../{$SOURCE}/\{\} >> t \;
    # execute the file: 
    echo making replacements... 
    source t
    rm -f t
    
    #do the same for jdk1.4 files:
    
    cd ..
    if ($SOURCE == src) then 
        #directory structure for necessary java source files
        mkdir {$SOURCE}/orbital/util/logging
        
        echo preparing replacements for jkd1.4 sources...
        cd resources/java/util/logging
        rm -f t
        find -name "*.jj" -exec echo sed -f ../../../../revert-to-1.3.sed \{\} \> ../../../../{$SOURCE}/orbital/util/logging/\{\} >> t \;
        find -name "*.java" -exec echo sed -f ../../../../revert-to-1.3.sed \{\} \> ../../../../{$SOURCE}/orbital/util/logging/\{\} >> t \;
        # execute the file: 
        echo making replacements... 
        source t
        
        #back to orbital project directory: 
        cd ../../../../
        
        # remove aspects, which cannot be compiled:
        # (they are not needed)
        echo removing aspects...
        rm -rf {$SOURCE}/orbital/moon/aspects/*
        # remove deprecated files:
        # first rescue used files
        echo rescuing deprecated-used...
        mv {$SOURCE}/orbital/moon/deprecated/used/* {$SOURCE}/orbital/
        echo removing deprecated...
        rm -rf {$SOURCE}/orbital/moon/deprecated/*
        
        # remove unwanted constructor call in InnerCheckedException: 
        sed -e 's/super(message, cause)/super(message)/' {$SOURCE}/orbital/util/InnerCheckedException.java > t
        mv -f t {$SOURCE}/orbital/util/InnerCheckedException.java
        
        # remove unwanted constructor call in orbital.logic.sign.ParseException: 
        sed -e 's/super(message, cause)/super(message)/' {$SOURCE}/orbital/logic/sign/ParseException.java > t
        mv -f t {$SOURCE}/orbital/logic/sign/ParseException.java

        # remove unwanted instantiation of InternalError: 
        sed -e 's/throw new InternalError(asserted)/throw new InternalError(asserted.toString())/' {$SOURCE}/orbital/algorithm/evolutionary/Gene.java > t
        mv -f t {$SOURCE}/orbital/algorithm/evolutionary/Gene.java

    endif

    if ($SOURCE == test) then 
        # remove unwanted catch AssertionError:
        sed -e 's/catch (AssertionError/catch (InternalError/' {$SOURCE}/orbital/math/functional/FunctionTest.java > t
        mv -f t {$SOURCE}/orbital/math/functional/FunctionTest.java
    endif

end
#
