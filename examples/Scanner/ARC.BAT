@ECHO Off
md orbital
xcopy %JAVA_HOME%\classes\orbital\moon\awt\Demonstratos*.class orbital\moon\awt\
xcopy %JAVA_HOME%\classes\orbital\moon\awt\TextAreaOutput*.class orbital\moon\awt\
xcopy %JAVA_HOME%\classes\orbital\awt\Closer*.class orbital\awt\
xcopy %JAVA_HOME%\classes\orbital\moon\awt\SystemRequestor.class orbital\moon\awt\
xcopy %JAVA_HOME%\classes\orbital\io orbital\io\
xcopy %JAVA_HOME%\classes\orbital\io\parsing orbital\io\parsing\ /S
xcopy %JAVA_HOME%\classes\orbital\logic orbital\logic\
xcopy %JAVA_HOME%\classes\orbital\logic\functor orbital\logic\functor\
xcopy %JAVA_HOME%\classes\orbital\util\Suspicious*.class orbital\util\
jar cfm Browser.jar manifest.mf Browser*.class DocCanvas*.class Html*.class html.* orbital
rd orbital /S /Q
