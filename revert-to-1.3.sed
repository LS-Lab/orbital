s-assert -//assert -g
s-.initCause[^}]*}-;}-g
s-.initCause-;//.initCause-g
s-throw new AssertionError-throw new InternalError-g
s-throw (AssertionError) new AssertionError-throw new InternalError-g
s-throw new java.lang.AssertionError-throw new InternalError-g
s-throws AssertionError- -g
#s-new FileHandler[^;]*;-new StreamHandler();-g
s-IdentityHashMap-HashMap-g
s-LinkedHashMap-HashMap-g
s-LinkedHashSet-HashSet-g
s-java.util.logging-orbital.util.logging-g
# replacements required by jikes (cannot handle unicode):
s-×-x-g
s-°-o-g
s-ö-\\224-g
##must be done before deletion of multiline comments: 
#/\/\*\/\//D
##deletion of multi-line comments within single lines 
##(not absolutely required...)
#s/\/\*[^(\*\/)]*\*\// /g
#s/\*\//\*\/\
#/g
