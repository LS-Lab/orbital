@echo off
set assert=-ea
echo recompiling library with "assert" set to "-ea"
pushd %home%\Java\orbital
call mkall
popd
call j c *
rem now call all testing applications in this directory with "-ea"
java -ea SearchTest
java -ea FunctionTest